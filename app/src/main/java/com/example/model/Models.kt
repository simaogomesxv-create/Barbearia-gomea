package com.example.model

data class Client(
  val id: String,
  val name: String,
  val phone: String,
  val lastService: String,
  val lastServiceDate: String,
  val initials: String
)

data class ServiceItem(
  val id: String,
  val name: String,
  val count: Int,
  val totalKz: Long,
  val pricePerService: Long,
  val iconType: String // "hair", "beard", "hair_beard", "eyebrow"
)

enum class PeriodFilter(val label: String) {
  HOJE("Hoje"),
  SEMANA("Semana"),
  MES("Mês"),
  ANO("Ano")
}

data class HourlySalesPoint(
  val hourLabel: String,
  val valueKz: Long,
  val relativeHeight: Float // 0f to 1f for bar height
)

data class LineSalesPoint(
  val hourLabel: String,
  val valueKz: Long,
  val xRatio: Float,
  val yRatio: Float,
  val isHighlighted: Boolean = false
)

data class MonthSalesPoint(
  val monthLabel: String,
  val valueKz: Long,
  val relativeHeight: Float
)

data class BusinessMetrics(
  val clientsCount: Int,
  val clientsChangePct: Int,
  val yesterdayClients: Int,
  val revenueKz: Long,
  val revenueChangePct: Int,
  val yesterdayRevenueKz: Long,
  val avgTicketKz: Long,
  val avgTicketChangePct: Int,
  val servicesCount: Int,
  val servicesChangePct: Int,
  val yesterdayServices: Int
)

data class BarbershopProfile(
  val name: String = "Barbearia Gomes",
  val managerName: String = "Esmeraldo Tafinha",
  val role: String = "Gerente Geral",
  val phone: String = "+244 948 840 379",
  val email: String = "esmeraldo@barbeariagomes.ao",
  val address: String = "Luanda, Angola",
  val currencySymbol: String = "Kz"
)
