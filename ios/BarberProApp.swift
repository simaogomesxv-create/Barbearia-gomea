//
//  BarberProApp.swift
//  BarberPro iOS - Barbearia Gomes
//

import SwiftUI

@main
public struct BarberProApp: App {
    @StateObject private var store = BarberStore()

    public init() {}

    public var body: some Scene {
        WindowGroup {
            Group {
                switch store.isAuthenticated {
                case .locked, .pinRequired:
                    LoginView()
                        .environmentObject(store)
                case .authenticated:
                    MainTabView()
                        .environmentObject(store)
                }
            }
            .preferredColorScheme(.dark)
        }
    }
}
