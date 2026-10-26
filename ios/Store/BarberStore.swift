//
//  BarberStore.swift
//  BarberPro iOS - Barbearia Gomes
//

import Foundation
import Combine
import SwiftUI

@MainActor
public class BarberStore: ObservableObject {
    // Authentication & Security
    public static let primaryIdentifier = "+244 948 840 379"
    public static let primaryPassword = "Gomesbarber"
    public static let primaryPin = "2526"

    @Published public var isAuthenticated: BooleanState = .locked
    @Published public var authErrorMessage: String? = nil
    @Published public var enteredPin: String = ""
    @Published public var rememberMe: Bool = true

    public enum BooleanState {
        case locked
        case pinRequired
        case authenticated
    }

    // Business State
    @Published public var profile: BarbershopProfile = BarbershopProfile()
    @Published public var metrics: BusinessMetrics = BusinessMetrics()
    @Published public var clients: [Client] = []
    @Published public var services: [ServiceItem] = []
    @Published public var recentSales: [SaleRecord] = []
    @Published public var selectedPeriod: PeriodFilter = .hoje

    public init() {
        loadInitialData()
    }

    // MARK: - Authentication
    public func loginWithCredentials(identifier: String, password: String) -> Bool {
        authErrorMessage = nil
        let cleanIdentifier = identifier.trimmingCharacters(in: .whitespacesAndNewlines)
            .replacingOccurrences(of: " ", with: "")
        let expectedClean = Self.primaryIdentifier.replacingOccurrences(of: " ", with: "")

        let idMatches = cleanIdentifier == expectedClean || cleanIdentifier == "948840379"
        let passMatches = password == Self.primaryPassword

        if idMatches && passMatches {
            isAuthenticated = .authenticated
            return true
        } else {
            authErrorMessage = "Credenciais inválidas. Utilize o identificador oficial e senha da Barbearia Gomes."
            return false
        }
    }

    public func unlockWithPin(_ pin: String) -> Bool {
        if pin == Self.primaryPin {
            isAuthenticated = .authenticated
            enteredPin = ""
            authErrorMessage = nil
            return true
        } else {
            authErrorMessage = "PIN incorreto. Tente novamente."
            enteredPin = ""
            return false
        }
    }

    public func lockSession() {
        isAuthenticated = .pinRequired
        enteredPin = ""
        authErrorMessage = nil
    }

    public func logout() {
        isAuthenticated = .locked
        enteredPin = ""
        authErrorMessage = nil
    }

    // MARK: - Business Operations
    public func registerSale(clientName: String, service: ServiceItem, paymentMethod: PaymentMethod) {
        let record = SaleRecord(
            clientName: clientName.isEmpty ? "Cliente Balcão" : clientName,
            serviceName: service.name,
            valueKz: service.pricePerService,
            paymentMethod: paymentMethod
        )

        recentSales.insert(record, at: 0)

        // Update service count
        if let idx = services.firstIndex(where: { $0.id == service.id }) {
            services[idx].count += 1
            services[idx].totalKz += service.pricePerService
        }

        // Update metrics
        metrics.revenueKz += service.pricePerService
        metrics.clientsCount += 1
        metrics.servicesCount += 1
        if metrics.clientsCount > 0 {
            metrics.avgTicketKz = metrics.revenueKz / Int64(metrics.clientsCount)
        }

        // Update client last service if matched
        if let clientIdx = clients.firstIndex(where: { $0.name.lowercased() == clientName.lowercased() }) {
            clients[clientIdx].lastService = service.name
            let df = DateFormatter()
            df.dateFormat = "dd MMM yyyy"
            clients[clientIdx].lastServiceDate = df.string(from: Date())
        }
    }

    public func addClient(name: String, phone: String, initialService: String = "Corte") {
        let df = DateFormatter()
        df.dateFormat = "dd MMM yyyy"
        let newClient = Client(
            name: name,
            phone: phone,
            lastService: initialService,
            lastServiceDate: df.string(from: Date())
        )
        clients.insert(newClient, at: 0)
    }

    // MARK: - Initial Mock / Cache
    private func loadInitialData() {
        clients = [
            Client(id: "1", name: "João Manuel", phone: "+244 923 456 789", lastService: "Corte", lastServiceDate: "20 Out 2026", initials: "JM"),
            Client(id: "2", name: "Carlos Alberto", phone: "+244 937 123 456", lastService: "Barba", lastServiceDate: "19 Out 2026", initials: "CA"),
            Client(id: "3", name: "Elton Lopes", phone: "+244 912 987 654", lastService: "Corte + Barba", lastServiceDate: "18 Out 2026", initials: "EL"),
            Client(id: "4", name: "Rafael Dias", phone: "+244 926 654 321", lastService: "Corte", lastServiceDate: "17 Out 2026", initials: "RD"),
            Client(id: "5", name: "Vitor Silva", phone: "+244 930 111 222", lastService: "Barba", lastServiceDate: "16 Out 2026", initials: "VI")
        ]

        services = [
            ServiceItem(id: "s1", name: "Corte de cabelo", count: 12, totalKz: 24000, pricePerService: 2000, iconType: "hair"),
            ServiceItem(id: "s2", name: "Barba", count: 4, totalKz: 8000, pricePerService: 2000, iconType: "beard"),
            ServiceItem(id: "s3", name: "Corte + Barba", count: 2, totalKz: 6000, pricePerService: 3000, iconType: "hair_beard"),
            ServiceItem(id: "s4", name: "Sobrancelha", count: 1, totalKz: 2000, pricePerService: 2000, iconType: "eyebrow")
        ]

        recentSales = [
            SaleRecord(id: "r1", clientName: "João Manuel", serviceName: "Corte de cabelo", valueKz: 2000, paymentMethod: .multicaixa, formattedTime: "14:30"),
            SaleRecord(id: "r2", clientName: "Carlos Alberto", serviceName: "Barba", valueKz: 2000, paymentMethod: .dinheiro, formattedTime: "13:15"),
            SaleRecord(id: "r3", clientName: "Elton Lopes", serviceName: "Corte + Barba", valueKz: 3000, paymentMethod: .transferencia, formattedTime: "11:45")
        ]
    }
}
