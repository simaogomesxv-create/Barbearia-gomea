//
//  ReportsView.swift
//  BarberPro iOS - Barbearia Gomes
//

import SwiftUI

public struct ReportsView: View {
    @EnvironmentObject var store: BarberStore

    let chartData: [(hour: String, value: Int64, height: CGFloat)] = [
        ("09h", 4000, 0.4),
        ("11h", 8000, 0.7),
        ("13h", 6000, 0.55),
        ("15h", 12000, 1.0),
        ("17h", 9000, 0.75),
        ("19h", 6000, 0.55)
    ]

    public var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Header
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Relatórios")
                            .font(.system(size: 24, weight: .bold))
                            .foregroundColor(BarberTheme.textWhite)
                        Text("Desempenho e faturamento financeiro")
                            .font(.system(size: 13))
                            .foregroundColor(BarberTheme.textGray)
                    }
                    Spacer()
                }
                .padding(.horizontal, 16)
                .padding(.top, 12)

                // Period Selector
                HStack(spacing: 8) {
                    ForEach(PeriodFilter.allCases) { period in
                        Button(action: { store.selectedPeriod = period }) {
                            Text(period.rawValue)
                                .font(.system(size: 13, weight: .semibold))
                                .foregroundColor(store.selectedPeriod == period ? BarberTheme.bgBlack : BarberTheme.textGray)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 8)
                                .background(store.selectedPeriod == period ? BarberTheme.accentGold : BarberTheme.surfaceElevated)
                                .cornerRadius(10)
                        }
                    }
                }
                .padding(.horizontal, 16)

                // Main Revenue Banner
                VStack(spacing: 8) {
                    Text("TOTAL ARRECADADO (\(store.selectedPeriod.rawValue.uppercased()))")
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundColor(BarberTheme.textMuted)

                    Text(BarberTheme.formatKz(store.metrics.revenueKz))
                        .font(.system(size: 34, weight: .heavy))
                        .foregroundColor(BarberTheme.accentGold)

                    HStack(spacing: 4) {
                        Image(systemName: "arrow.up.right")
                        Text("+\(store.metrics.revenueChangePct)% comparado ao período anterior")
                    }
                    .font(.system(size: 12, weight: .semibold))
                    .foregroundColor(BarberTheme.positiveGreen)
                }
                .frame(maxWidth: .infinity)
                .padding(20)
                .barberCard()
                .padding(.horizontal, 16)

                // Bar Chart Visualizer
                VStack(alignment: .leading, spacing: 14) {
                    Text("FLUXO DE VENDAS POR HORÁRIO")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(BarberTheme.textGray)

                    HStack(alignment: .bottom, spacing: 14) {
                        ForEach(chartData, id: \.hour) { item in
                            VStack(spacing: 6) {
                                Text("\(item.value / 1000)k")
                                    .font(.system(size: 10, weight: .medium))
                                    .foregroundColor(BarberTheme.accentGoldLight)

                                RoundedRectangle(cornerRadius: 6)
                                    .fill(
                                        LinearGradient(
                                            colors: [BarberTheme.accentGold, BarberTheme.accentGoldDark],
                                            startPoint: .top,
                                            endPoint: .bottom
                                        )
                                    )
                                    .frame(height: 120 * item.height)

                                Text(item.hour)
                                    .font(.system(size: 11, weight: .medium))
                                    .foregroundColor(BarberTheme.textGray)
                            }
                            .frame(maxWidth: .infinity)
                        }
                    }
                    .frame(height: 160, alignment: .bottom)
                }
                .padding(16)
                .barberCard()
                .padding(.horizontal, 16)

                // Summary Stats
                VStack(spacing: 12) {
                    summaryRow(title: "Clientes Atendidos", value: "\(store.metrics.clientsCount)", change: "+\(store.metrics.clientsChangePct)%")
                    Divider().background(BarberTheme.borderDark)
                    summaryRow(title: "Ticket Médio por Cliente", value: BarberTheme.formatKz(store.metrics.avgTicketKz), change: "+\(store.metrics.avgTicketChangePct)%")
                    Divider().background(BarberTheme.borderDark)
                    summaryRow(title: "Total de Serviços Realizados", value: "\(store.metrics.servicesCount)", change: "+\(store.metrics.servicesChangePct)%")
                }
                .padding(16)
                .barberCard()
                .padding(.horizontal, 16)

                Spacer().frame(height: 40)
            }
        }
        .background(BarberTheme.bgBlack.ignoresSafeArea())
    }

    private func summaryRow(title: String, value: String, change: String) -> some View {
        HStack {
            Text(title)
                .font(.system(size: 14))
                .foregroundColor(BarberTheme.textWhite)
            Spacer()
            VStack(alignment: .trailing, spacing: 2) {
                Text(value)
                    .font(.system(size: 15, weight: .bold))
                    .foregroundColor(BarberTheme.accentGold)
                Text(change)
                    .font(.system(size: 11))
                    .foregroundColor(BarberTheme.positiveGreen)
            }
        }
    }
}
