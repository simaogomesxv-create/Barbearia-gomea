//
//  BarberModels.swift
//  BarberPro iOS - Barbearia Gomes
//

import Foundation

public struct Client: Identifiable, Codable, Hashable {
    public let id: String
    public var name: String
    public var phone: String
    public var lastService: String
    public var lastServiceDate: String
    public var initials: String

    public init(id: String = UUID().uuidString, name: String, phone: String, lastService: String, lastServiceDate: String, initials: String = "") {
        self.id = id
        self.name = name
        self.phone = phone
        self.lastService = lastService
        self.lastServiceDate = lastServiceDate
        if initials.isEmpty {
            let parts = name.split(separator: " ")
            let first = parts.first?.prefix(1) ?? ""
            let second = parts.count > 1 ? parts.last?.prefix(1) ?? "" : ""
            self.initials = "\(first)\(second)".uppercased()
        } else {
            self.initials = initials
        }
    }
}

public struct ServiceItem: Identifiable, Codable, Hashable {
    public let id: String
    public var name: String
    public var count: Int
    public var totalKz: Int64
    public var pricePerService: Int64
    public var iconType: String // "hair", "beard", "hair_beard", "eyebrow"

    public init(id: String = UUID().uuidString, name: String, count: Int = 0, totalKz: Int64 = 0, pricePerService: Int64, iconType: String = "hair") {
        self.id = id
        self.name = name
        self.count = count
        self.totalKz = totalKz
        self.pricePerService = pricePerService
        self.iconType = iconType
    }
}

public enum PeriodFilter: String, CaseIterable, Identifiable {
    case hoje = "Hoje"
    case semana = "Semana"
    case mes = "Mês"
    case ano = "Ano"

    public var id: String { rawValue }
}

public enum PaymentMethod: String, CaseIterable, Identifiable, Codable {
    case dinheiro = "Dinheiro"
    case multicaixa = "Multicaixa / TPA"
    case transferencia = "Transferência"

    public var id: String { rawValue }
}

public struct SaleRecord: Identifiable, Codable {
    public let id: String
    public let clientName: String
    public let serviceName: String
    public let valueKz: Int64
    public let paymentMethod: PaymentMethod
    public let date: Date
    public let formattedTime: String

    public init(id: String = UUID().uuidString, clientName: String, serviceName: String, valueKz: Int64, paymentMethod: PaymentMethod, date: Date = Date(), formattedTime: String = "") {
        self.id = id
        self.clientName = clientName
        self.serviceName = serviceName
        self.valueKz = valueKz
        self.paymentMethod = paymentMethod
        self.date = date
        if formattedTime.isEmpty {
            let formatter = DateFormatter()
            formatter.dateFormat = "HH:mm"
            self.formattedTime = formatter.string(from: date)
        } else {
            self.formattedTime = formattedTime
        }
    }
}

public struct BusinessMetrics: Codable {
    public var clientsCount: Int
    public var clientsChangePct: Int
    public var yesterdayClients: Int
    public var revenueKz: Int64
    public var revenueChangePct: Int
    public var yesterdayRevenueKz: Int64
    public var avgTicketKz: Int64
    public var avgTicketChangePct: Int
    public var servicesCount: Int
    public var servicesChangePct: Int
    public var yesterdayServices: Int

    public init(
        clientsCount: Int = 18,
        clientsChangePct: Int = 12,
        yesterdayClients: Int = 16,
        revenueKz: Int64 = 45000,
        revenueChangePct: Int = 18,
        yesterdayRevenueKz: Int64 = 38000,
        avgTicketKz: Int64 = 2500,
        avgTicketChangePct: Int = 10,
        servicesCount: Int = 5,
        servicesChangePct: Int = 25,
        yesterdayServices: Int = 4
    ) {
        self.clientsCount = clientsCount
        self.clientsChangePct = clientsChangePct
        self.yesterdayClients = yesterdayClients
        self.revenueKz = revenueKz
        self.revenueChangePct = revenueChangePct
        self.yesterdayRevenueKz = yesterdayRevenueKz
        self.avgTicketKz = avgTicketKz
        self.avgTicketChangePct = avgTicketChangePct
        self.servicesCount = servicesCount
        self.servicesChangePct = servicesChangePct
        self.yesterdayServices = yesterdayServices
    }
}

public struct BarbershopProfile: Codable {
    public var name: String = "Barbearia Gomes"
    public var managerName: String = "Esmeraldo Tafinha"
    public var role: String = "Gerente Geral"
    public var phone: String = "+244 948 840 379"
    public var email: String = "esmeraldo@barbeariagomes.ao"
    public var address: String = "Luanda, Angola"
    public var currencySymbol: String = "Kz"

    public init() {}
}

public struct HourlySalesPoint: Identifiable {
    public var id: String { hourLabel }
    public let hourLabel: String
    public let valueKz: Int64
    public let relativeHeight: Double
}
