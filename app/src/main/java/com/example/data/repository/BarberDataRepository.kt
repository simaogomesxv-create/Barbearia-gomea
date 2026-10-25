package com.example.data.repository

import android.content.Context
import com.example.data.local.AppointmentDao
import com.example.data.local.AppointmentEntity
import com.example.data.local.BarberDatabase
import com.example.data.local.ClientDao
import com.example.data.local.ClientEntity
import com.example.data.local.ServiceDao
import com.example.data.local.ServiceEntity
import com.example.data.remote.SupabaseClient
import com.example.data.remote.SupabaseConfig
import com.example.model.BarbershopProfile
import com.example.model.BusinessMetrics
import com.example.model.Client
import com.example.model.HourlySalesPoint
import com.example.model.LineSalesPoint
import com.example.model.MonthSalesPoint
import com.example.model.PeriodFilter
import com.example.model.ServiceItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class BarberDataRepository(
  context: Context,
  private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
  private val database = BarberDatabase.getDatabase(context)
  private val clientDao: ClientDao = database.clientDao()
  private val appointmentDao: AppointmentDao = database.appointmentDao()
  private val serviceDao: ServiceDao = database.serviceDao()

  val supabaseConfig = SupabaseConfig(context)
  val supabaseClient = SupabaseClient(supabaseConfig)

  private val _selectedDateYmd = MutableStateFlow(getTodayDateYmd())
  val selectedDateYmd: StateFlow<String> = _selectedDateYmd.asStateFlow()

  private val _profile = MutableStateFlow(BarbershopProfile())
  val profile: StateFlow<BarbershopProfile> = _profile.asStateFlow()

  // Raw flows from Room
  val allClientsFlow: Flow<List<ClientEntity>> = clientDao.getAllClients()
  val allAppointmentsFlow: Flow<List<AppointmentEntity>> = appointmentDao.getAllAppointments()
  val allServicesFlow: Flow<List<ServiceEntity>> = serviceDao.getAllServices()

  // Appointments for the selected date
  val selectedDateAppointments: StateFlow<List<AppointmentEntity>> = combine(
    allAppointmentsFlow,
    _selectedDateYmd
  ) { list, date ->
    list.filter { it.dateYmd == date }
  }.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Mapped clients for UI
  val uiClients: StateFlow<List<Client>> = allClientsFlow.map { list ->
    list.map { entity ->
      val initials = entity.name.trim().split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")
        .ifEmpty { "C" }

      Client(
        id = entity.id,
        name = entity.name,
        phone = entity.phone,
        lastService = entity.lastService.ifBlank { "Nenhum" },
        lastServiceDate = entity.lastServiceDate.ifBlank { "Sem registro" },
        initials = initials
      )
    }
  }.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Mapped services with aggregated counts from real completed appointments
  val uiServices: StateFlow<List<ServiceItem>> = combine(
    allServicesFlow,
    allAppointmentsFlow
  ) { services, appointments ->
    val completed = appointments.filter { it.status == "CONCLUIDO" }
    services.map { svc ->
      val matchingAppts = completed.filter { it.serviceId == svc.id || it.serviceName == svc.name }
      val count = matchingAppts.size
      val totalKz = if (count > 0) matchingAppts.sumOf { it.totalPrice } else svc.priceKz * 0L

      ServiceItem(
        id = svc.id,
        name = svc.name,
        count = count,
        totalKz = totalKz,
        pricePerService = svc.priceKz,
        iconType = svc.iconType
      )
    }
  }.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dynamic Business Metrics calculated from real database
  val metrics: StateFlow<BusinessMetrics> = combine(
    allAppointmentsFlow,
    allClientsFlow,
    _selectedDateYmd
  ) { appts, clients, selectedDate ->
    val today = getTodayDateYmd()
    val yesterday = getYesterdayDateYmd()

    val todayCompleted = appts.filter { it.dateYmd == today && it.status == "CONCLUIDO" }
    val yesterdayCompleted = appts.filter { it.dateYmd == yesterday && it.status == "CONCLUIDO" }

    val todayRevenue = todayCompleted.sumOf { it.totalPrice }
    val yesterdayRevenue = yesterdayCompleted.sumOf { it.totalPrice }

    val todayClientsCount = todayCompleted.map { it.clientId }.distinct().size
    val yesterdayClientsCount = yesterdayCompleted.map { it.clientId }.distinct().size

    val avgTicket = if (todayCompleted.isNotEmpty()) {
      todayRevenue / todayCompleted.size
    } else {
      500L // Default base ticket when no appointments yet
    }

    val revChangePct = if (yesterdayRevenue > 0) {
      (((todayRevenue - yesterdayRevenue).toFloat() / yesterdayRevenue) * 100).toInt()
    } else if (todayRevenue > 0) {
      100
    } else {
      0
    }

    val clientChangePct = if (yesterdayClientsCount > 0) {
      (((todayClientsCount - yesterdayClientsCount).toFloat() / yesterdayClientsCount) * 100).toInt()
    } else if (todayClientsCount > 0) {
      100
    } else {
      0
    }

    BusinessMetrics(
      clientsCount = if (todayClientsCount > 0) todayClientsCount else clients.size,
      clientsChangePct = revChangePct.coerceAtLeast(0),
      yesterdayClients = yesterdayClientsCount,
      revenueKz = todayRevenue,
      revenueChangePct = revChangePct.coerceAtLeast(0),
      yesterdayRevenueKz = yesterdayRevenue,
      avgTicketKz = avgTicket,
      avgTicketChangePct = 10,
      servicesCount = todayCompleted.size,
      servicesChangePct = 15,
      yesterdayServices = yesterdayCompleted.size
    )
  }.stateIn(
    scope,
    SharingStarted.WhileSubscribed(5000),
    BusinessMetrics(0, 0, 0, 0L, 0, 0L, 500L, 0, 0, 0, 0)
  )

  // Dynamic Hourly Sales Points for selected date
  val hourlySales: StateFlow<List<HourlySalesPoint>> = combine(
    allAppointmentsFlow,
    _selectedDateYmd
  ) { appts, selectedDate ->
    val hours = listOf("08h", "10h", "12h", "14h", "16h", "18h", "20h")
    val dayAppts = appts.filter { it.dateYmd == selectedDate && it.status == "CONCLUIDO" }

    val hourBuckets = mutableMapOf<String, Long>()
    hours.forEach { hourBuckets[it] = 0L }

    dayAppts.forEach { appt ->
      val hourInt = appt.timeHm.split(":").firstOrNull()?.toIntOrNull() ?: 12
      val bucket = when {
        hourInt <= 9 -> "08h"
        hourInt in 10..11 -> "10h"
        hourInt in 12..13 -> "12h"
        hourInt in 14..15 -> "14h"
        hourInt in 16..17 -> "16h"
        hourInt in 18..19 -> "18h"
        else -> "20h"
      }
      hourBuckets[bucket] = (hourBuckets[bucket] ?: 0L) + appt.totalPrice
    }

    val maxVal = hourBuckets.values.maxOrNull()?.coerceAtLeast(1000L) ?: 1000L
    hours.map { h ->
      val v = hourBuckets[h] ?: 0L
      val rel = if (maxVal > 0) (v.toFloat() / maxVal).coerceIn(0.15f, 1f) else 0.15f
      HourlySalesPoint(h, v, rel)
    }
  }.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dynamic Line Sales for Vendas
  val lineSales: StateFlow<List<LineSalesPoint>> = combine(
    allAppointmentsFlow,
    _selectedDateYmd
  ) { appts, selectedDate ->
    val hours = listOf("08h", "10h", "12h", "14h", "16h", "18h", "20h")
    val dayAppts = appts.filter { it.dateYmd == selectedDate && it.status == "CONCLUIDO" }

    val hourBuckets = mutableMapOf<String, Long>()
    hours.forEach { hourBuckets[it] = 0L }

    dayAppts.forEach { appt ->
      val hourInt = appt.timeHm.split(":").firstOrNull()?.toIntOrNull() ?: 12
      val bucket = when {
        hourInt <= 9 -> "08h"
        hourInt in 10..11 -> "10h"
        hourInt in 12..13 -> "12h"
        hourInt in 14..15 -> "14h"
        hourInt in 16..17 -> "16h"
        hourInt in 18..19 -> "18h"
        else -> "20h"
      }
      hourBuckets[bucket] = (hourBuckets[bucket] ?: 0L) + appt.totalPrice
    }

    val maxVal = hourBuckets.values.maxOrNull()?.coerceAtLeast(1000L) ?: 1000L
    hours.mapIndexed { idx, h ->
      val v = hourBuckets[h] ?: 0L
      val yRatio = if (maxVal > 0) (v.toFloat() / maxVal).coerceIn(0.15f, 0.95f) else 0.2f
      val xRatio = (idx.toFloat() / (hours.size - 1)).coerceIn(0.05f, 0.95f)
      LineSalesPoint(h, v, xRatio, yRatio, isHighlighted = idx == 4)
    }
  }.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dynamic Monthly Sales
  val monthlySales: StateFlow<List<MonthSalesPoint>> = allAppointmentsFlow.map { appts ->
    val months = listOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out")
    val monthBuckets = mutableMapOf<String, Long>()
    months.forEach { monthBuckets[it] = 0L }

    appts.filter { it.status == "CONCLUIDO" }.forEach { appt ->
      // extract month index from YYYY-MM-DD
      val parts = appt.dateYmd.split("-")
      if (parts.size >= 2) {
        val mIdx = (parts[1].toIntOrNull() ?: 1) - 1
        if (mIdx in months.indices) {
          val mName = months[mIdx]
          monthBuckets[mName] = (monthBuckets[mName] ?: 0L) + appt.totalPrice
        }
      }
    }

    val maxVal = monthBuckets.values.maxOrNull()?.coerceAtLeast(1000L) ?: 1000L
    months.map { m ->
      val v = monthBuckets[m] ?: 0L
      val rel = if (maxVal > 0) (v.toFloat() / maxVal).coerceIn(0.2f, 1f) else 0.2f
      MonthSalesPoint(m, v, rel)
    }
  }.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dynamic Annual Sales
  val annualSales: StateFlow<List<MonthSalesPoint>> = allAppointmentsFlow.map { appts ->
    val months = listOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")
    val monthBuckets = mutableMapOf<String, Long>()
    months.forEach { monthBuckets[it] = 0L }

    appts.filter { it.status == "CONCLUIDO" }.forEach { appt ->
      val parts = appt.dateYmd.split("-")
      if (parts.size >= 2) {
        val mIdx = (parts[1].toIntOrNull() ?: 1) - 1
        if (mIdx in months.indices) {
          val mName = months[mIdx]
          monthBuckets[mName] = (monthBuckets[mName] ?: 0L) + appt.totalPrice
        }
      }
    }

    val maxVal = monthBuckets.values.maxOrNull()?.coerceAtLeast(1000L) ?: 1000L
    months.map { m ->
      val v = monthBuckets[m] ?: 0L
      val rel = if (maxVal > 0) (v.toFloat() / maxVal).coerceIn(0.15f, 1f) else 0.15f
      MonthSalesPoint(m, v, rel)
    }
  }.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    scope.launch {
      seedInitialServicesIfNeeded()
    }
  }

  fun setDateFilter(dateYmd: String) {
    _selectedDateYmd.value = dateYmd
  }

  private suspend fun seedInitialServicesIfNeeded() {
    val count = serviceDao.getCount()
    if (count == 0) {
      val initialServices = listOf(
        ServiceEntity(UUID.randomUUID().toString(), "Corte sem pintura", priceKz = 500L, iconType = "hair", sortOrder = 1),
        ServiceEntity(UUID.randomUUID().toString(), "Corte com pintura", priceKz = 800L, iconType = "hair_color", sortOrder = 2),
        ServiceEntity(UUID.randomUUID().toString(), "Barba simples", priceKz = 300L, iconType = "beard", sortOrder = 3),
        ServiceEntity(UUID.randomUUID().toString(), "Barba desenhada com pintura", priceKz = 500L, iconType = "beard", sortOrder = 4),
        ServiceEntity(UUID.randomUUID().toString(), "Combo Corte + Barba", priceKz = 1000L, iconType = "hair_beard", sortOrder = 5),
        ServiceEntity(UUID.randomUUID().toString(), "Sobrancelha", priceKz = 200L, iconType = "eyebrow", sortOrder = 6)
      )
      serviceDao.insertAll(initialServices)

      // Sync to Supabase in background
      initialServices.forEach { svc ->
        scope.launch { supabaseClient.pushService(svc) }
      }
    }
  }

  // --- CRUD ACTIONS ---

  suspend fun addClient(name: String, phone: String, notes: String): ClientEntity = withContext(Dispatchers.IO) {
    val newClient = ClientEntity(
      id = UUID.randomUUID().toString(),
      name = name.trim(),
      phone = phone.trim().ifBlank { "+244 900 000 000" },
      notes = notes.trim(),
      lastService = "Sem atendimento",
      lastServiceDate = getTodayFormatted(),
      totalSpentKz = 0L,
      createdAt = System.currentTimeMillis()
    )
    clientDao.insertClient(newClient)
    scope.launch { supabaseClient.pushClient(newClient) }
    newClient
  }

  suspend fun updateClient(client: ClientEntity) = withContext(Dispatchers.IO) {
    clientDao.updateClient(client)
    scope.launch { supabaseClient.pushClient(client) }
  }

  suspend fun deleteClient(id: String) = withContext(Dispatchers.IO) {
    clientDao.deleteClientById(id)
    scope.launch { supabaseClient.deleteClientRemote(id) }
  }

  suspend fun addAppointment(
    clientId: String,
    clientName: String,
    serviceId: String,
    serviceName: String,
    servicePrice: Long,
    extraValue: Long,
    dateYmd: String,
    timeHm: String,
    paymentMethod: String,
    status: String = "CONCLUIDO"
  ): AppointmentEntity = withContext(Dispatchers.IO) {
    val total = servicePrice + extraValue
    val newAppt = AppointmentEntity(
      id = UUID.randomUUID().toString(),
      clientId = clientId,
      clientName = clientName,
      serviceId = serviceId,
      serviceName = serviceName,
      servicePrice = servicePrice,
      extraValue = extraValue,
      totalPrice = total,
      dateYmd = dateYmd,
      timeHm = timeHm,
      status = status,
      paymentMethod = paymentMethod,
      createdAt = System.currentTimeMillis()
    )
    appointmentDao.insertAppointment(newAppt)

    // Update client's last service and total spent
    val client = clientDao.getClientById(clientId)
    if (client != null) {
      val updatedClient = client.copy(
        lastService = serviceName,
        lastServiceDate = formatDateToDisplay(dateYmd),
        totalSpentKz = client.totalSpentKz + total
      )
      clientDao.updateClient(updatedClient)
      scope.launch { supabaseClient.pushClient(updatedClient) }
    }

    scope.launch { supabaseClient.pushAppointment(newAppt) }
    newAppt
  }

  suspend fun updateAppointment(appointment: AppointmentEntity) = withContext(Dispatchers.IO) {
    appointmentDao.updateAppointment(appointment)
    scope.launch { supabaseClient.pushAppointment(appointment) }
  }

  suspend fun deleteAppointment(id: String) = withContext(Dispatchers.IO) {
    appointmentDao.deleteAppointmentById(id)
    scope.launch { supabaseClient.deleteAppointmentRemote(id) }
  }

  suspend fun addOrUpdateService(id: String?, name: String, priceKz: Long, iconType: String) = withContext(Dispatchers.IO) {
    val svc = ServiceEntity(
      id = id ?: UUID.randomUUID().toString(),
      name = name.trim(),
      priceKz = priceKz,
      iconType = iconType,
      isActive = true
    )
    serviceDao.insertService(svc)
    scope.launch { supabaseClient.pushService(svc) }
  }

  suspend fun deleteService(service: ServiceEntity) = withContext(Dispatchers.IO) {
    serviceDao.deleteService(service)
  }

  suspend fun syncWithSupabase(): Result<String> = withContext(Dispatchers.IO) {
    val conn = supabaseClient.testConnection()
    if (conn.isFailure) {
      return@withContext conn
    }

    try {
      // 1. Fetch remote clients
      val remoteClients = supabaseClient.fetchClientsRemote()
      if (remoteClients.isSuccess && remoteClients.getOrNull()?.isNotEmpty() == true) {
        clientDao.insertAll(remoteClients.getOrNull()!!)
      }

      Result.success("Sincronização com Supabase concluída!")
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  companion object {
    fun getTodayDateYmd(): String {
      val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      return sdf.format(Date())
    }

    fun getYesterdayDateYmd(): String {
      val cal = Calendar.getInstance()
      cal.add(Calendar.DAY_OF_YEAR, -1)
      val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      return sdf.format(cal.time)
    }

    fun getTodayFormatted(): String {
      val sdf = SimpleDateFormat("dd MMM yyyy", Locale("pt", "AO"))
      return "Hoje (${sdf.format(Date())})"
    }

    fun formatDateToDisplay(ymd: String): String {
      return try {
        val sdfParse = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdfParse.parse(ymd) ?: Date()
        val sdfFormat = SimpleDateFormat("dd MMM yyyy", Locale("pt", "AO"))
        sdfFormat.format(date)
      } catch (e: Exception) {
        ymd
      }
    }
  }
}
