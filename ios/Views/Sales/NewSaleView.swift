//
//  NewSaleView.swift
//  BarberPro iOS - Barbearia Gomes
//

import SwiftUI

public struct NewSaleView: View {
    @EnvironmentObject var store: BarberStore
    @Environment(\.dismiss) var dismiss

    @State private var clientName: String = ""
    @State private var selectedService: ServiceItem?
    @State private var selectedPayment: PaymentMethod = .multicaixa
    @State private var showSuccessNotice: Bool = false

    public var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 20) {
                    // Client Selection / Input
                    VStack(alignment: .leading, spacing: 8) {
                        Text("CLIENTE")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(BarberTheme.textMuted)

                        TextField("Nome do cliente (ou deixe vazio para Balcão)", text: $clientName)
                            .foregroundColor(BarberTheme.textWhite)
                            .padding(14)
                            .background(BarberTheme.surfaceDark)
                            .cornerRadius(12)
                            .overlay(RoundedRectangle(cornerRadius: 12).stroke(BarberTheme.borderDark, lineWidth: 1))

                        // Quick pick from existing
                        if !store.clients.isEmpty {
                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 8) {
                                    ForEach(store.clients.prefix(5)) { client in
                                        Button(action: { clientName = client.name }) {
                                            Text(client.name)
                                                .font(.system(size: 12, weight: .medium))
                                                .foregroundColor(clientName == client.name ? BarberTheme.bgBlack : BarberTheme.textGray)
                                                .padding(.horizontal, 10)
                                                .padding(.vertical, 6)
                                                .background(clientName == client.name ? BarberTheme.accentGold : BarberTheme.surfaceElevated)
                                                .cornerRadius(16)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Service Selection
                    VStack(alignment: .leading, spacing: 8) {
                        Text("SELECIONE O SERVIÇO")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(BarberTheme.textMuted)

                        VStack(spacing: 8) {
                            ForEach(store.services) { service in
                                Button(action: { selectedService = service }) {
                                    HStack {
                                        VStack(alignment: .leading, spacing: 2) {
                                            Text(service.name)
                                                .font(.system(size: 15, weight: .semibold))
                                                .foregroundColor(BarberTheme.textWhite)
                                            Text(BarberTheme.formatKz(service.pricePerService))
                                                .font(.system(size: 13))
                                                .foregroundColor(BarberTheme.accentGold)
                                        }

                                        Spacer()

                                        if selectedService?.id == service.id {
                                            Image(systemName: "checkmark.circle.fill")
                                                .font(.system(size: 20))
                                                .foregroundColor(BarberTheme.accentGold)
                                        } else {
                                            Circle()
                                                .stroke(BarberTheme.borderDark, lineWidth: 2)
                                                .frame(width: 20, height: 20)
                                        }
                                    }
                                    .padding(14)
                                    .barberCard()
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 16)
                                            .stroke(selectedService?.id == service.id ? BarberTheme.accentGold : Color.clear, lineWidth: 1.5)
                                    )
                                }
                            }
                        }
                    }

                    // Payment Method
                    VStack(alignment: .leading, spacing: 8) {
                        Text("FORMA DE PAGAMENTO")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(BarberTheme.textMuted)

                        HStack(spacing: 8) {
                            ForEach(PaymentMethod.allCases) { method in
                                Button(action: { selectedPayment = method }) {
                                    VStack(spacing: 4) {
                                        Image(systemName: paymentIcon(for: method))
                                            .font(.system(size: 16))
                                        Text(method.rawValue)
                                            .font(.system(size: 11, weight: .semibold))
                                            .multilineTextAlignment(.center)
                                    }
                                    .foregroundColor(selectedPayment == method ? BarberTheme.bgBlack : BarberTheme.textWhite)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 12)
                                    .background(selectedPayment == method ? BarberTheme.accentGold : BarberTheme.surfaceDark)
                                    .cornerRadius(12)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 12)
                                            .stroke(BarberTheme.borderDark, lineWidth: 1)
                                    )
                                }
                            }
                        }
                    }

                    // Confirm Action Button
                    Button(action: confirmSale) {
                        HStack(spacing: 8) {
                            Image(systemName: "checkmark.circle.fill")
                                .font(.system(size: 18, weight: .bold))
                            Text("Confirmar Atendimento")
                                .font(.system(size: 16, weight: .bold))
                        }
                        .foregroundColor(BarberTheme.bgBlack)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(selectedService != nil ? BarberTheme.accentGold : BarberTheme.surfaceElevated)
                        .cornerRadius(14)
                    }
                    .disabled(selectedService == nil)
                    .padding(.top, 10)
                }
                .padding(20)
            }
            .background(BarberTheme.bgBlack.ignoresSafeArea())
            .navigationTitle("Novo Atendimento")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancelar") { dismiss() }
                        .foregroundColor(BarberTheme.textGray)
                }
            }
        }
    }

    private func confirmSale() {
        guard let service = selectedService else { return }
        store.registerSale(clientName: clientName, service: service, paymentMethod: selectedPayment)
        dismiss()
    }

    private func paymentIcon(for method: PaymentMethod) -> String {
        switch method {
        case .dinheiro: return "banknote.fill"
        case .multicaixa: return "creditcard.fill"
        case .transferencia: return "arrow.left.arrow.right"
        }
    }
}

public struct AddClientView: View {
    @EnvironmentObject var store: BarberStore
    @Environment(\.dismiss) var dismiss

    @State private var name: String = ""
    @State private var phone: String = "+244 9"
    @State private var initialService: String = "Corte de cabelo"

    public var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                VStack(alignment: .leading, spacing: 6) {
                    Text("Nome Completo")
                        .font(.system(size: 13, weight: .medium))
                        .foregroundColor(BarberTheme.textGray)

                    TextField("Ex: Mário Silva", text: $name)
                        .foregroundColor(BarberTheme.textWhite)
                        .padding(14)
                        .background(BarberTheme.surfaceDark)
                        .cornerRadius(12)
                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(BarberTheme.borderDark, lineWidth: 1))
                }

                VStack(alignment: .leading, spacing: 6) {
                    Text("Número de Telefone")
                        .font(.system(size: 13, weight: .medium))
                        .foregroundColor(BarberTheme.textGray)

                    TextField("Ex: +244 923 000 000", text: $phone)
                        .foregroundColor(BarberTheme.textWhite)
                        .padding(14)
                        .background(BarberTheme.surfaceDark)
                        .cornerRadius(12)
                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(BarberTheme.borderDark, lineWidth: 1))
                        .keyboardType(.phonePad)
                }

                Button(action: saveClient) {
                    Text("Salvar Cliente")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(BarberTheme.bgBlack)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(!name.trimmingCharacters(in: .whitespaces).isEmpty ? BarberTheme.accentGold : BarberTheme.surfaceElevated)
                        .cornerRadius(12)
                }
                .disabled(name.trimmingCharacters(in: .whitespaces).isEmpty)
                .padding(.top, 10)

                Spacer()
            }
            .padding(20)
            .background(BarberTheme.bgBlack.ignoresSafeArea())
            .navigationTitle("Novo Cliente")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancelar") { dismiss() }
                        .foregroundColor(BarberTheme.textGray)
                }
            }
        }
    }

    private func saveClient() {
        guard !name.isEmpty else { return }
        store.addClient(name: name, phone: phone, initialService: initialService)
        dismiss()
    }
}
