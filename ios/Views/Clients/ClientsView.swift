//
//  ClientsView.swift
//  BarberPro iOS - Barbearia Gomes
//

import SwiftUI

public struct ClientsView: View {
    @EnvironmentObject var store: BarberStore
    @Binding var showAddClientSheet: Bool

    @State private var searchText: String = ""

    var filteredClients: [Client] {
        if searchText.isEmpty {
            return store.clients
        } else {
            return store.clients.filter {
                $0.name.localizedCaseInsensitiveContains(searchText) ||
                $0.phone.contains(searchText) ||
                $0.lastService.localizedCaseInsensitiveContains(searchText)
            }
        }
    }

    public var body: some View {
        VStack(spacing: 16) {
            // Header
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Clientes")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(BarberTheme.textWhite)
                    Text("\(store.clients.count) clientes cadastrados")
                        .font(.system(size: 13))
                        .foregroundColor(BarberTheme.textGray)
                }

                Spacer()

                Button(action: { showAddClientSheet = true }) {
                    Image(systemName: "plus")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(BarberTheme.bgBlack)
                        .frame(width: 40, height: 40)
                        .background(BarberTheme.accentGold)
                        .clipShape(Circle())
                }
            }
            .padding(.horizontal, 16)
            .padding(.top, 12)

            // Search Bar
            HStack(spacing: 8) {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(BarberTheme.textGray)

                TextField("Buscar por nome ou telefone...", text: $searchText)
                    .foregroundColor(BarberTheme.textWhite)

                if !searchText.isEmpty {
                    Button(action: { searchText = "" }) {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(BarberTheme.textGray)
                    }
                }
            }
            .padding(12)
            .background(BarberTheme.surfaceDark)
            .cornerRadius(12)
            .overlay(RoundedRectangle(cornerRadius: 12).stroke(BarberTheme.borderDark, lineWidth: 1))
            .padding(.horizontal, 16)

            // Clients List
            ScrollView {
                LazyVStack(spacing: 10) {
                    ForEach(filteredClients) { client in
                        clientRow(client)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 40)
            }
        }
        .background(BarberTheme.bgBlack.ignoresSafeArea())
    }

    private func clientRow(_ client: Client) -> some View {
        HStack(spacing: 12) {
            // Initials Avatar
            Text(client.initials)
                .font(.system(size: 14, weight: .bold))
                .foregroundColor(BarberTheme.accentGold)
                .frame(width: 44, height: 44)
                .background(BarberTheme.surfaceElevated)
                .clipShape(Circle())
                .overlay(Circle().stroke(BarberTheme.accentGold.opacity(0.3), lineWidth: 1))

            VStack(alignment: .leading, spacing: 3) {
                Text(client.name)
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundColor(BarberTheme.textWhite)

                Text(client.phone)
                    .font(.system(size: 13))
                    .foregroundColor(BarberTheme.textGray)

                Text("Último: \(client.lastService) (\(client.lastServiceDate))")
                    .font(.system(size: 11))
                    .foregroundColor(BarberTheme.textMuted)
            }

            Spacer()

            // Quick Call & WhatsApp Actions
            HStack(spacing: 8) {
                if let callUrl = URL(string: "tel:\(client.phone.filter { "0123456789+".contains($0) })") {
                    Link(destination: callUrl) {
                        Image(systemName: "phone.fill")
                            .font(.system(size: 13))
                            .foregroundColor(BarberTheme.accentGold)
                            .frame(width: 34, height: 34)
                            .background(BarberTheme.surfaceElevated)
                            .clipShape(Circle())
                    }
                }

                let cleanPhone = client.phone.filter { "0123456789".contains($0) }
                if let waUrl = URL(string: "https://wa.me/\(cleanPhone)") {
                    Link(destination: waUrl) {
                        Image(systemName: "message.fill")
                            .font(.system(size: 13))
                            .foregroundColor(BarberTheme.positiveGreen)
                            .frame(width: 34, height: 34)
                            .background(BarberTheme.surfaceElevated)
                            .clipShape(Circle())
                    }
                }
            }
        }
        .padding(14)
        .barberCard()
    }
}
