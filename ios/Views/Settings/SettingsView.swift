//
//  SettingsView.swift
//  BarberPro iOS - Barbearia Gomes
//

import SwiftUI

public struct SettingsView: View {
    @EnvironmentObject var store: BarberStore

    public var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Header
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Ajustes & Perfil")
                            .font(.system(size: 24, weight: .bold))
                            .foregroundColor(BarberTheme.textWhite)
                        Text("Configurações do salão e segurança")
                            .font(.system(size: 13))
                            .foregroundColor(BarberTheme.textGray)
                    }
                    Spacer()
                }
                .padding(.horizontal, 16)
                .padding(.top, 12)

                // Barbershop Card
                VStack(spacing: 12) {
                    Image("app_logo")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 72, height: 72)
                        .clipShape(Circle())
                        .overlay(Circle().stroke(BarberTheme.accentGold, lineWidth: 2))

                    Text(store.profile.name)
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(BarberTheme.textWhite)

                    Text("\(store.profile.managerName) • \(store.profile.role)")
                        .font(.system(size: 13))
                        .foregroundColor(BarberTheme.accentGoldLight)

                    HStack(spacing: 6) {
                        Image(systemName: "mappin.and.ellipse")
                            .foregroundColor(BarberTheme.textMuted)
                        Text(store.profile.address)
                            .font(.system(size: 12))
                            .foregroundColor(BarberTheme.textGray)
                    }
                }
                .frame(maxWidth: .infinity)
                .padding(20)
                .barberCard()
                .padding(.horizontal, 16)

                // Security & Credentials Information
                VStack(alignment: .leading, spacing: 14) {
                    Text("CREDENCIAS E SEGURANÇA")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(BarberTheme.textMuted)

                    VStack(spacing: 10) {
                        infoRow(label: "Identificador Oficial", value: store.configuredIdentifier, icon: "person.text.rectangle")
                        Divider().background(BarberTheme.borderDark)
                        infoRow(label: "PIN Rápido Ativo", value: "•••• (\(store.configuredPin))", icon: "key.fill")
                        Divider().background(BarberTheme.borderDark)
                        infoRow(label: "Senha do Gerente", value: "•••••••• (Gomesbarber)", icon: "lock.shield.fill")
                        Divider().background(BarberTheme.borderDark)
                        infoRow(label: "Criação de Contas", value: "Desativado (Acesso Pessoal)", icon: "nosign")
                    }
                    .padding(14)
                    .barberCard()
                }
                .padding(.horizontal, 16)

                // Lock & Logout Actions
                VStack(spacing: 10) {
                    Button(action: { store.lockSession() }) {
                        HStack(spacing: 8) {
                            Image(systemName: "lock.fill")
                            Text("Bloquear Tela com PIN")
                        }
                        .font(.system(size: 15, weight: .semibold))
                        .foregroundColor(BarberTheme.accentGold)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 14)
                        .background(BarberTheme.surfaceElevated)
                        .cornerRadius(12)
                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(BarberTheme.accentGold.opacity(0.4), lineWidth: 1))
                    }

                    Button(action: { store.logout() }) {
                        HStack(spacing: 8) {
                            Image(systemName: "power")
                            Text("Encerrar Sessão")
                        }
                        .font(.system(size: 15, weight: .semibold))
                        .foregroundColor(BarberTheme.negativeRed)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 14)
                        .background(BarberTheme.surfaceElevated)
                        .cornerRadius(12)
                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(BarberTheme.negativeRed.opacity(0.3), lineWidth: 1))
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 10)

                Spacer().frame(height: 40)
            }
        }
        .background(BarberTheme.bgBlack.ignoresSafeArea())
    }

    private func infoRow(label: String, value: String, icon: String) -> some View {
        HStack {
            Image(systemName: icon)
                .font(.system(size: 14))
                .foregroundColor(BarberTheme.accentGold)
                .frame(width: 24)

            Text(label)
                .font(.system(size: 13))
                .foregroundColor(BarberTheme.textWhite)

            Spacer()

            Text(value)
                .font(.system(size: 12, weight: .medium))
                .foregroundColor(BarberTheme.textGray)
        }
    }
}
