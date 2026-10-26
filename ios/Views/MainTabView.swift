//
//  MainTabView.swift
//  BarberPro iOS - Barbearia Gomes
//

import SwiftUI

public struct MainTabView: View {
    @EnvironmentObject var store: BarberStore

    @State private var selectedTab: Int = 0
    @State private var showNewSaleSheet: Bool = false
    @State private var showAddClientSheet: Bool = false

    public var body: some View {
        ZStack(alignment: .bottom) {
            // Content Views
            TabView(selection: $selectedTab) {
                DashboardView(
                    showNewSaleSheet: $showNewSaleSheet,
                    showNewClientSheet: $showAddClientSheet
                )
                .tag(0)

                ClientsView(
                    showAddClientSheet: $showAddClientSheet
                )
                .tag(1)

                ReportsView()
                .tag(2)

                SettingsView()
                .tag(3)
            }
            .tabViewStyle(.page(indexDisplayMode: .never))

            // Custom Floating Dark & Gold Navigation Bar
            customTabBar
        }
        .sheet(isPresented: $showNewSaleSheet) {
            NewSaleView()
                .environmentObject(store)
        }
        .sheet(isPresented: $showAddClientSheet) {
            AddClientView()
                .environmentObject(store)
        }
    }

    private var customTabBar: some View {
        HStack(spacing: 0) {
            tabButton(title: "Início", icon: "house.fill", index: 0)
            tabButton(title: "Clientes", icon: "person.2.fill", index: 1)

            // Center Quick Action
            Button(action: { showNewSaleSheet = true }) {
                Image(systemName: "plus")
                    .font(.system(size: 20, weight: .bold))
                    .foregroundColor(BarberTheme.bgBlack)
                    .frame(width: 48, height: 48)
                    .background(BarberTheme.accentGold)
                    .clipShape(Circle())
                    .shadow(color: BarberTheme.accentGold.opacity(0.35), radius: 8, x: 0, y: 4)
            }
            .offset(y: -10)
            .padding(.horizontal, 8)

            tabButton(title: "Relatórios", icon: "chart.bar.fill", index: 2)
            tabButton(title: "Ajustes", icon: "gearshape.fill", index: 3)
        }
        .padding(.horizontal, 16)
        .padding(.top, 10)
        .padding(.bottom, 24)
        .background(
            BarberTheme.surfaceElevated
                .overlay(
                    Rectangle()
                        .frame(height: 1)
                        .foregroundColor(BarberTheme.borderDark),
                    alignment: .top
                )
                .ignoresSafeArea()
        )
    }

    private func tabButton(title: String, icon: String, index: Int) -> some View {
        Button(action: { selectedTab = index }) {
            VStack(spacing: 4) {
                Image(systemName: icon)
                    .font(.system(size: 18))
                Text(title)
                    .font(.system(size: 11, weight: .medium))
            }
            .foregroundColor(selectedTab == index ? BarberTheme.accentGold : BarberTheme.textMuted)
            .frame(maxWidth: .infinity)
        }
    }
}
