//
//  DashboardView.swift
//  BarberPro iOS - Barbearia Gomes
//

import SwiftUI

public struct DashboardView: View {
    @EnvironmentObject var store: BarberStore
    @Binding var showNewSaleSheet: Bool
    @Binding var showNewClientSheet: Bool

    public var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Top Header
                headerView

                // Metrics Grid
                metricsGridView

                // Quick Action Bar
                quickActionsBar

                // Services Breakdown
                servicesBreakdownView

                // Recent Sales
                recentSalesView

                Spacer().frame(height: 40)
            }
            .padding(.horizontal, 16)
            .padding(.top, 12)
        }
        .background(BarberTheme.bgBlack.ignoresSafeArea())
    }

    // MARK: - Header
    private var headerView: some View {
        HStack(spacing: 12) {
            Image("app_logo")
                .resizable()
                .scaledToFit()
                .frame(width: 48, height: 48)
                .clipShape(Circle())
                .overlay(Circle().stroke(BarberTheme.accentGold, lineWidth: 1.5))

            VStack(alignment: .leading, spacing: 2) {
                Text(store.profile.name)
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(BarberTheme.textWhite)
                Text("Olá, \(store.profile.managerName)")
                    .font(.system(size: 13))
                    .foregroundColor(BarberTheme.accentGoldLight)
            }

            Spacer()

            Button(action: {
                store.lockSession()
            }) {
                HStack(spacing: 4) {
                    Image(systemName: "lock.shield.fill")
                        .foregroundColor(BarberTheme.accentGold)
                    Text("Bloquear")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(BarberTheme.textGray)
                }
                .padding(.horizontal, 10)
                .padding(.vertical, 6)
                .background(BarberTheme.surfaceElevated)
                .cornerRadius(20)
                .overlay(RoundedRectangle(cornerRadius: 20).stroke(BarberTheme.borderDark, lineWidth: 1))
            }
        }
        .padding(.vertical, 6)
    }

    // MARK: - Metrics Grid
    private var metricsGridView: some View {
        VStack(spacing: 12) {
            // Main Revenue Card
            VStack(alignment: .leading, spacing: 8) {
                HStack {
                    Text("FATURAMENTO HOJE")
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundColor(BarberTheme.textMuted)
                    Spacer()
                    HStack(spacing: 4) {
                        Image(systemName: "arrow.up.right")
                        Text("+\(store.metrics.revenueChangePct)%")
                    }
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(BarberTheme.positiveGreen)
                }

                Text(BarberTheme.formatKz(store.metrics.revenueKz))
                    .font(.system(size: 32, weight: .heavy))
                    .foregroundColor(BarberTheme.accentGold)

                HStack {
                    Text("Ontem: \(BarberTheme.formatKz(store.metrics.yesterdayRevenueKz))")
                        .font(.system(size: 12))
                        .foregroundColor(BarberTheme.textGray)
                    Spacer()
                }
            }
            .padding(16)
            .barberCard()

            // 2 Column Mini Cards
            HStack(spacing: 12) {
                // Clients
                VStack(alignment: .leading, spacing: 6) {
                    Text("CLIENTES")
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundColor(BarberTheme.textMuted)

                    Text("\(store.metrics.clientsCount)")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(BarberTheme.textWhite)

                    HStack(spacing: 2) {
                        Image(systemName: "arrow.up.right")
                        Text("+\(store.metrics.clientsChangePct)% vs ontem")
                    }
                    .font(.system(size: 11, weight: .medium))
                    .foregroundColor(BarberTheme.positiveGreen)
                }
                .padding(14)
                .frame(maxWidth: .infinity, alignment: .leading)
                .barberCard()

                // Ticket Médio
                VStack(alignment: .leading, spacing: 6) {
                    Text("TICKET MÉDIO")
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundColor(BarberTheme.textMuted)

                    Text(BarberTheme.formatKz(store.metrics.avgTicketKz))
                        .font(.system(size: 22, weight: .bold))
                        .foregroundColor(BarberTheme.textWhite)

                    HStack(spacing: 2) {
                        Image(systemName: "arrow.up.right")
                        Text("+\(store.metrics.avgTicketChangePct)%")
                    }
                    .font(.system(size: 11, weight: .medium))
                    .foregroundColor(BarberTheme.positiveGreen)
                }
                .padding(14)
                .frame(maxWidth: .infinity, alignment: .leading)
                .barberCard()
            }
        }
    }

    // MARK: - Quick Actions
    private var quickActionsBar: some View {
        HStack(spacing: 12) {
            Button(action: { showNewSaleSheet = true }) {
                HStack(spacing: 8) {
                    Image(systemName: "plus.circle.fill")
                        .font(.system(size: 16, weight: .bold))
                    Text("Nova Venda")
                        .font(.system(size: 14, weight: .bold))
                }
                .foregroundColor(BarberTheme.bgBlack)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 14)
                .background(BarberTheme.accentGold)
                .cornerRadius(12)
            }

            Button(action: { showNewClientSheet = true }) {
                HStack(spacing: 8) {
                    Image(systemName: "person.crop.circle.badge.plus")
                        .font(.system(size: 16, weight: .semibold))
                    Text("Novo Cliente")
                        .font(.system(size: 14, weight: .semibold))
                }
                .foregroundColor(BarberTheme.textWhite)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 14)
                .background(BarberTheme.surfaceElevated)
                .cornerRadius(12)
                .overlay(RoundedRectangle(cornerRadius: 12).stroke(BarberTheme.borderDark, lineWidth: 1))
            }
        }
    }

    // MARK: - Services Breakdown
    private var servicesBreakdownView: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text("SERVIÇOS REALIZADOS")
                    .font(.system(size: 13, weight: .bold))
                    .foregroundColor(BarberTheme.textGray)
                Spacer()
                Text("\(store.services.reduce(0) { $0 + $1.count }) no total")
                    .font(.system(size: 12))
                    .foregroundColor(BarberTheme.accentGoldLight)
            }

            VStack(spacing: 8) {
                ForEach(store.services) { item in
                    HStack {
                        Image(systemName: iconName(for: item.iconType))
                            .foregroundColor(BarberTheme.accentGold)
                            .frame(width: 28, height: 28)
                            .background(BarberTheme.surfaceElevated)
                            .clipShape(Circle())

                        Text(item.name)
                            .font(.system(size: 14, weight: .medium))
                            .foregroundColor(BarberTheme.textWhite)

                        Spacer()

                        Text("\(item.count)x")
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(BarberTheme.textGray)

                        Text(BarberTheme.formatKz(item.totalKz))
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(BarberTheme.textWhite)
                            .frame(minWidth: 80, alignment: .trailing)
                    }
                    .padding(.vertical, 6)

                    if item.id != store.services.last?.id {
                        Divider().background(BarberTheme.borderDark)
                    }
                }
            }
            .padding(14)
            .barberCard()
        }
    }

    // MARK: - Recent Sales
    private var recentSalesView: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("ÚLTIMOS ATENDIMENTOS")
                .font(.system(size: 13, weight: .bold))
                .foregroundColor(BarberTheme.textGray)

            VStack(spacing: 8) {
                ForEach(store.recentSales.prefix(5)) { sale in
                    HStack {
                        VStack(alignment: .leading, spacing: 2) {
                            Text(sale.clientName)
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundColor(BarberTheme.textWhite)
                            Text("\(sale.serviceName) • \(sale.paymentMethod.rawValue)")
                                .font(.system(size: 12))
                                .foregroundColor(BarberTheme.textGray)
                        }

                        Spacer()

                        VStack(alignment: .trailing, spacing: 2) {
                            Text(BarberTheme.formatKz(sale.valueKz))
                                .font(.system(size: 14, weight: .bold))
                                .foregroundColor(BarberTheme.positiveGreen)
                            Text(sale.formattedTime)
                                .font(.system(size: 11))
                                .foregroundColor(BarberTheme.textMuted)
                        }
                    }
                    .padding(.vertical, 6)

                    if sale.id != store.recentSales.prefix(5).last?.id {
                        Divider().background(BarberTheme.borderDark)
                    }
                }
            }
            .padding(14)
            .barberCard()
        }
    }

    private func iconName(for type: String) -> String {
        switch type {
        case "hair": return "scissors"
        case "beard": return "mustache"
        case "hair_beard": return "comb.fill"
        default: return "sparkles"
        }
    }
}
