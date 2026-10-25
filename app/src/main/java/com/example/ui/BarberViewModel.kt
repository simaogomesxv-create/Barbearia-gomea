package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthSessionManager
import com.example.data.auth.AuthUser
import com.example.data.automation.AutomationManager
import com.example.data.automation.CommissionCalculation
import com.example.data.local.AppointmentEntity
import com.example.data.local.ClientEntity
import com.example.data.local.ServiceEntity
import com.example.data.repository.BarberDataRepository
import com.example.model.Client
import com.example.model.PeriodFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BarberViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = BarberDataRepository(application, viewModelScope)
  val authManager = AuthSessionManager(application, repository.supabaseConfig)

  private val _currentUser = MutableStateFlow(authManager.getCurrentUser())
  val currentUser: StateFlow<AuthUser> = _currentUser.asStateFlow()

  private val _isLoggedIn = MutableStateFlow(authManager.isLoggedIn)
  val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  private val _currentScreen = MutableStateFlow(
    if (authManager.isLoggedIn) AppScreen.MAIN else AppScreen.WELCOME
  )
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _selectedTab = MutableStateFlow(MainTab.INICIO)
  val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

  private val _vendasPeriodFilter = MutableStateFlow(PeriodFilter.HOJE)
  val vendasPeriodFilter: StateFlow<PeriodFilter> = _vendasPeriodFilter.asStateFlow()

  private val _clientSearchQuery = MutableStateFlow("")
  val clientSearchQuery: StateFlow<String> = _clientSearchQuery.asStateFlow()

  // Modals & Dialogs
  private val _showAddClientDialog = MutableStateFlow(false)
  val showAddClientDialog: StateFlow<Boolean> = _showAddClientDialog.asStateFlow()

  private val _showAddSaleDialog = MutableStateFlow(false)
  val showAddSaleDialog: StateFlow<Boolean> = _showAddSaleDialog.asStateFlow()

  private val _selectedClientForEdit = MutableStateFlow<ClientEntity?>(null)
  val selectedClientForEdit: StateFlow<ClientEntity?> = _selectedClientForEdit.asStateFlow()

  private val _selectedAppointmentForEdit = MutableStateFlow<AppointmentEntity?>(null)
  val selectedAppointmentForEdit: StateFlow<AppointmentEntity?> = _selectedAppointmentForEdit.asStateFlow()

  private val _syncStatusMessage = MutableStateFlow<String?>(null)
  val syncStatusMessage: StateFlow<String?> = _syncStatusMessage.asStateFlow()

  // Automations state
  private val _automationReportText = MutableStateFlow<String?>(null)
  val automationReportText: StateFlow<String?> = _automationReportText.asStateFlow()

  private val _commissionPercent = MutableStateFlow(50)
  val commissionPercent: StateFlow<Int> = _commissionPercent.asStateFlow()

  // Repository data streams
  val metrics = repository.metrics
  val services = repository.uiServices
  val rawServices = repository.allServicesFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val rawClients = repository.allClientsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val hourlySales = repository.hourlySales
  val lineSales = repository.lineSales
  val monthlySales = repository.monthlySales
  val annualSales = repository.annualSales
  val profile = repository.profile
  val selectedDateYmd = repository.selectedDateYmd
  val dailyAppointments = repository.selectedDateAppointments
  val supabaseConfig = repository.supabaseConfig

  // Commission calculation derived state
  val commissionCalc: StateFlow<CommissionCalculation> = combine(
    metrics,
    _commissionPercent
  ) { met, rate ->
    AutomationManager.calculateCommissions(
      totalRevenue = met.revenueKz,
      completedCount = met.servicesCount,
      ratePercent = rate
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CommissionCalculation(0L, 50, 0L, 0L, 0))

  // Filtered clients list
  val filteredClients: StateFlow<List<Client>> = combine(
    repository.uiClients,
    _clientSearchQuery
  ) { list, query ->
    if (query.isBlank()) {
      list
    } else {
      list.filter {
        it.name.contains(query, ignoreCase = true) ||
          it.phone.contains(query, ignoreCase = true) ||
          it.lastService.contains(query, ignoreCase = true)
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun navigateTo(screen: AppScreen) {
    _currentScreen.value = screen
  }

  fun selectTab(tab: MainTab) {
    _selectedTab.value = tab
    if (_currentScreen.value != AppScreen.MAIN) {
      _currentScreen.value = AppScreen.MAIN
    }
  }

  fun setVendasPeriodFilter(filter: PeriodFilter) {
    _vendasPeriodFilter.value = filter
  }

  fun setClientSearchQuery(query: String) {
    _clientSearchQuery.value = query
  }

  fun setDateFilter(dateYmd: String) {
    repository.setDateFilter(dateYmd)
  }

  fun setCommissionPercent(percent: Int) {
    _commissionPercent.value = percent.coerceIn(10, 90)
  }

  fun openAddClientDialog() {
    _showAddClientDialog.value = true
  }

  fun closeAddClientDialog() {
    _showAddClientDialog.value = false
  }

  fun openAddSaleDialog() {
    _showAddSaleDialog.value = true
  }

  fun closeAddSaleDialog() {
    _showAddSaleDialog.value = false
  }

  fun openEditClient(client: ClientEntity?) {
    _selectedClientForEdit.value = client
  }

  fun openEditAppointment(appointment: AppointmentEntity?) {
    _selectedAppointmentForEdit.value = appointment
  }

  fun addClient(name: String, phone: String, notes: String = "") {
    viewModelScope.launch {
      repository.addClient(name, phone, notes)
      closeAddClientDialog()
    }
  }

  fun updateClient(client: ClientEntity) {
    viewModelScope.launch {
      repository.updateClient(client)
      _selectedClientForEdit.value = null
    }
  }

  fun deleteClient(id: String) {
    viewModelScope.launch {
      repository.deleteClient(id)
      _selectedClientForEdit.value = null
    }
  }

  fun registerAppointment(
    clientId: String,
    clientName: String,
    serviceId: String,
    serviceName: String,
    servicePrice: Long,
    extraValue: Long,
    dateYmd: String,
    timeHm: String,
    paymentMethod: String
  ) {
    viewModelScope.launch {
      repository.addAppointment(
        clientId = clientId,
        clientName = clientName,
        serviceId = serviceId,
        serviceName = serviceName,
        servicePrice = servicePrice,
        extraValue = extraValue,
        dateYmd = dateYmd,
        timeHm = timeHm,
        paymentMethod = paymentMethod
      )
      closeAddSaleDialog()
    }
  }

  fun updateAppointment(appointment: AppointmentEntity) {
    viewModelScope.launch {
      repository.updateAppointment(appointment)
      _selectedAppointmentForEdit.value = null
    }
  }

  fun deleteAppointment(id: String) {
    viewModelScope.launch {
      repository.deleteAppointment(id)
      _selectedAppointmentForEdit.value = null
    }
  }

  fun addOrUpdateService(id: String?, name: String, priceKz: Long, iconType: String = "hair") {
    viewModelScope.launch {
      repository.addOrUpdateService(id, name, priceKz, iconType)
    }
  }

  fun deleteService(service: ServiceEntity) {
    viewModelScope.launch {
      repository.deleteService(service)
    }
  }

  fun saveSupabaseCredentials(url: String, anonKey: String) {
    supabaseConfig.url = url
    supabaseConfig.anonKey = anonKey
    testAndSyncSupabase()
  }

  fun testAndSyncSupabase() {
    viewModelScope.launch {
      _syncStatusMessage.value = "Sincronizando com Supabase..."
      val result = repository.syncWithSupabase()
      if (result.isSuccess) {
        _syncStatusMessage.value = result.getOrNull() ?: "Conectado ao Supabase com sucesso!"
      } else {
        _syncStatusMessage.value = "Aviso: operando em modo local seguro (${result.exceptionOrNull()?.message})"
      }
    }
  }

  // --- SMART & SECURE AUTHENTICATION ---

  suspend fun login(identifier: String, pass: String, remember: Boolean): Result<AuthUser> {
    val res = authManager.authenticate(identifier, pass, remember)
    if (res.isSuccess) {
      _currentUser.value = res.getOrNull() ?: authManager.getCurrentUser()
      _isLoggedIn.value = true
      _currentScreen.value = AppScreen.MAIN
    }
    return res
  }

  suspend fun register(name: String, identifier: String, pass: String, role: String = "Barbeiro", pin: String = "1234"): Result<AuthUser> {
    val res = authManager.registerNewAccount(name, identifier, pass, role, pin)
    if (res.isSuccess) {
      _currentUser.value = res.getOrNull() ?: authManager.getCurrentUser()
      _isLoggedIn.value = true
      _currentScreen.value = AppScreen.MAIN
    }
    return res
  }

  suspend fun verifyQuickPin(pin: String): Result<AuthUser> {
    val res = authManager.verifyQuickPin(pin)
    if (res.isSuccess) {
      _currentUser.value = res.getOrNull() ?: authManager.getCurrentUser()
      _isLoggedIn.value = true
      _currentScreen.value = AppScreen.MAIN
    }
    return res
  }

  suspend fun changePassword(oldPass: String, newPass: String): Result<Unit> {
    return authManager.changePassword(oldPass, newPass)
  }

  suspend fun changeQuickPin(newPin: String): Result<Unit> {
    return authManager.changeQuickPin(newPin)
  }

  suspend fun updatePersonalProfile(name: String, phone: String, email: String): Result<AuthUser> {
    val res = authManager.updatePersonalProfile(name, phone, email)
    if (res.isSuccess) {
      _currentUser.value = res.getOrNull() ?: authManager.getCurrentUser()
    }
    return res
  }

  fun logout() {
    authManager.logout()
    _isLoggedIn.value = false
    _currentScreen.value = AppScreen.WELCOME
    _selectedTab.value = MainTab.INICIO
  }

  // --- AUTOMATIONS ---

  fun generateDailyReportWhatsApp() {
    val appts = dailyAppointments.value
    val mets = metrics.value
    val date = selectedDateYmd.value
    val report = AutomationManager.generateDailyClosingReport(
      appointments = appts,
      metrics = mets,
      dateYmd = date,
      barbershopName = "Barbearia Gomes",
      managerName = "Esmeraldo Tafinha",
      commissionPercent = _commissionPercent.value
    )
    _automationReportText.value = report
  }

  fun closeAutomationReport() {
    _automationReportText.value = null
  }
}
