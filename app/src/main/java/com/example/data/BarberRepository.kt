package com.example.data

import com.example.model.BarbershopProfile
import com.example.model.BusinessMetrics
import com.example.model.Client
import com.example.model.HourlySalesPoint
import com.example.model.LineSalesPoint
import com.example.model.MonthSalesPoint
import com.example.model.PeriodFilter
import com.example.model.ServiceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.Locale

object Formatters {
  fun formatKz(amount: Long): String {
    val formatter = NumberFormat.getIntegerInstance(Locale("pt", "AO"))
    return "${formatter.format(amount)} Kz"
  }
}

class BarberRepository {

  private val _profile = MutableStateFlow(BarbershopProfile())
  val profile: StateFlow<BarbershopProfile> = _profile.asStateFlow()

  private val _metrics = MutableStateFlow(
    BusinessMetrics(
      clientsCount = 18,
      clientsChangePct = 12,
      yesterdayClients = 16,
      revenueKz = 45000L,
      revenueChangePct = 18,
      yesterdayRevenueKz = 38000L,
      avgTicketKz = 2500L,
      avgTicketChangePct = 10,
      servicesCount = 5,
      servicesChangePct = 25,
      yesterdayServices = 4
    )
  )
  val metrics: StateFlow<BusinessMetrics> = _metrics.asStateFlow()

  private val _clients = MutableStateFlow(
    listOf(
      Client(
        id = "1",
        name = "João Manuel",
        phone = "+244 923 456 789",
        lastService = "Corte",
        lastServiceDate = "20 Out 2026",
        initials = "JM"
      ),
      Client(
        id = "2",
        name = "Carlos Alberto",
        phone = "+244 937 123 456",
        lastService = "Barba",
        lastServiceDate = "19 Out 2026",
        initials = "CA"
      ),
      Client(
        id = "3",
        name = "Elton Lopes",
        phone = "+244 912 987 654",
        lastService = "Corte + Barba",
        lastServiceDate = "18 Out 2026",
        initials = "EL"
      ),
      Client(
        id = "4",
        name = "Rafael Dias",
        phone = "+244 926 654 321",
        lastService = "Corte",
        lastServiceDate = "17 Out 2026",
        initials = "RD"
      ),
      Client(
        id = "5",
        name = "Vitor Silva",
        phone = "+244 930 111 222",
        lastService = "Barba",
        lastServiceDate = "16 Out 2026",
        initials = "VI"
      )
    )
  )
  val clients: StateFlow<List<Client>> = _clients.asStateFlow()

  private val _services = MutableStateFlow(
    listOf(
      ServiceItem("s1", "Corte de cabelo", count = 12, totalKz = 24000L, pricePerService = 2000L, iconType = "hair"),
      ServiceItem("s2", "Barba", count = 4, totalKz = 8000L, pricePerService = 2000L, iconType = "beard"),
      ServiceItem("s3", "Corte + Barba", count = 2, totalKz = 6000L, pricePerService = 3000L, iconType = "hair_beard"),
      ServiceItem("s4", "Sobrancelha", count = 1, totalKz = 2000L, pricePerService = 2000L, iconType = "eyebrow")
    )
  )
  val services: StateFlow<List<ServiceItem>> = _services.asStateFlow()

  // Hourly data for Dashboard Evolution bar chart
  private val _hourlySales = MutableStateFlow(
    listOf(
      HourlySalesPoint("08h", 2500L, 0.22f),
      HourlySalesPoint("10h", 4500L, 0.38f),
      HourlySalesPoint("12h", 6000L, 0.48f),
      HourlySalesPoint("14h", 8000L, 0.65f),
      HourlySalesPoint("16h", 8500L, 0.70f),
      HourlySalesPoint("18h", 10000L, 0.85f),
      HourlySalesPoint("20h", 12000L, 1.0f)
    )
  )
  val hourlySales: StateFlow<List<HourlySalesPoint>> = _hourlySales.asStateFlow()

  // Line sales points for Vendas screen
  private val _lineSales = MutableStateFlow(
    listOf(
      LineSalesPoint("08h", 2500L, 0.05f, 0.20f),
      LineSalesPoint("10h", 4500L, 0.20f, 0.35f),
      LineSalesPoint("12h", 6000L, 0.36f, 0.50f),
      LineSalesPoint("14h", 7500L, 0.52f, 0.40f),
      LineSalesPoint("16h", 8500L, 0.68f, 0.75f, isHighlighted = true),
      LineSalesPoint("18h", 7800L, 0.84f, 0.60f),
      LineSalesPoint("20h", 9500L, 0.95f, 0.65f)
    )
  )
  val lineSales: StateFlow<List<LineSalesPoint>> = _lineSales.asStateFlow()

  // Monthly data for Screen 6 (Jan - Out)
  private val _monthlySales = MutableStateFlow(
    listOf(
      MonthSalesPoint("Jan", 420000L, 0.30f),
      MonthSalesPoint("Fev", 480000L, 0.35f),
      MonthSalesPoint("Mar", 550000L, 0.42f),
      MonthSalesPoint("Abr", 610000L, 0.48f),
      MonthSalesPoint("Mai", 690000L, 0.52f),
      MonthSalesPoint("Jun", 750000L, 0.58f),
      MonthSalesPoint("Jul", 820000L, 0.62f),
      MonthSalesPoint("Ago", 930000L, 0.70f),
      MonthSalesPoint("Set", 1020000L, 0.82f),
      MonthSalesPoint("Out", 1250000L, 1.0f)
    )
  )
  val monthlySales: StateFlow<List<MonthSalesPoint>> = _monthlySales.asStateFlow()

  // Annual data for Screen 7 (Jan - Dez)
  private val _annualSales = MutableStateFlow(
    listOf(
      MonthSalesPoint("Jan", 680000L, 0.32f),
      MonthSalesPoint("Fev", 750000L, 0.40f),
      MonthSalesPoint("Mar", 820000L, 0.46f),
      MonthSalesPoint("Abr", 890000L, 0.52f),
      MonthSalesPoint("Mai", 940000L, 0.56f),
      MonthSalesPoint("Jun", 990000L, 0.62f),
      MonthSalesPoint("Jul", 1080000L, 0.68f),
      MonthSalesPoint("Ago", 1150000L, 0.74f),
      MonthSalesPoint("Set", 1230000L, 0.80f),
      MonthSalesPoint("Out", 1250000L, 0.84f),
      MonthSalesPoint("Nov", 1350000L, 0.90f),
      MonthSalesPoint("Dez", 1550000L, 1.0f)
    )
  )
  val annualSales: StateFlow<List<MonthSalesPoint>> = _annualSales.asStateFlow()

  fun addClient(name: String, phone: String, service: String) {
    val cleanName = name.trim()
    val initials = cleanName.split(" ")
      .filter { it.isNotBlank() }
      .take(2)
      .map { it.first().uppercaseChar() }
      .joinToString("")
      .ifEmpty { "C" }

    val newClient = Client(
      id = System.currentTimeMillis().toString(),
      name = cleanName,
      phone = phone.ifBlank { "+244 900 000 000" },
      lastService = service.ifBlank { "Corte" },
      lastServiceDate = "Hoje (20 Out 2026)",
      initials = initials
    )
    _clients.value = listOf(newClient) + _clients.value
  }

  fun registerSale(serviceId: String, clientName: String? = null) {
    val currentServices = _services.value.toMutableList()
    val idx = currentServices.indexOfFirst { it.id == serviceId }
    if (idx != -1) {
      val item = currentServices[idx]
      val updated = item.copy(
        count = item.count + 1,
        totalKz = item.totalKz + item.pricePerService
      )
      currentServices[idx] = updated
      _services.value = currentServices

      // Update metrics
      val curMetrics = _metrics.value
      val newRev = curMetrics.revenueKz + item.pricePerService
      val newClients = curMetrics.clientsCount + 1
      _metrics.value = curMetrics.copy(
        revenueKz = newRev,
        clientsCount = newClients,
        avgTicketKz = if (newClients > 0) newRev / newClients else curMetrics.avgTicketKz,
        servicesCount = curMetrics.servicesCount + 1
      )

      if (!clientName.isNullOrBlank()) {
        addClient(clientName, "+244 9xx xxx xxx", item.name)
      }
    }
  }
}
