package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Formatters
import com.example.data.local.AppointmentEntity
import com.example.ui.AppScreen
import com.example.ui.BarberViewModel
import com.example.ui.MainTab
import com.example.ui.components.BarberProBottomNav
import com.example.ui.screens.AnnualReportScreen
import com.example.ui.screens.ClientsScreen
import com.example.ui.screens.ComprehensiveSaleModal
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MonthlyReportScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.SalesScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.BarberProTheme
import com.example.ui.theme.ButtonPrimaryBg
import com.example.ui.theme.ButtonPrimaryText
import com.example.ui.theme.CardBorder
import com.example.ui.theme.InputBg
import com.example.ui.theme.InputBorder
import com.example.ui.theme.NegativeRed
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      BarberProTheme {
        BarberProApp()
      }
    }
  }
}

@Composable
fun BarberProApp(
  viewModel: BarberViewModel = viewModel()
) {
  val context = LocalContext.current

  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
  val metrics by viewModel.metrics.collectAsStateWithLifecycle()
  val services by viewModel.services.collectAsStateWithLifecycle()
  val rawServices by viewModel.rawServices.collectAsStateWithLifecycle()
  val rawClients by viewModel.rawClients.collectAsStateWithLifecycle()
  val hourlySales by viewModel.hourlySales.collectAsStateWithLifecycle()
  val lineSales by viewModel.lineSales.collectAsStateWithLifecycle()
  val monthlySales by viewModel.monthlySales.collectAsStateWithLifecycle()
  val annualSales by viewModel.annualSales.collectAsStateWithLifecycle()
  val filteredClients by viewModel.filteredClients.collectAsStateWithLifecycle()
  val clientSearchQuery by viewModel.clientSearchQuery.collectAsStateWithLifecycle()
  val vendasPeriodFilter by viewModel.vendasPeriodFilter.collectAsStateWithLifecycle()
  val profile by viewModel.profile.collectAsStateWithLifecycle()
  val showAddSaleDialog by viewModel.showAddSaleDialog.collectAsStateWithLifecycle()
  val selectedDateYmd by viewModel.selectedDateYmd.collectAsStateWithLifecycle()
  val dailyAppointments by viewModel.dailyAppointments.collectAsStateWithLifecycle()
  val selectedApptForEdit by viewModel.selectedAppointmentForEdit.collectAsStateWithLifecycle()
  val syncStatusMessage by viewModel.syncStatusMessage.collectAsStateWithLifecycle()
  val automationReportText by viewModel.automationReportText.collectAsStateWithLifecycle()
  val commissionCalc by viewModel.commissionCalc.collectAsStateWithLifecycle()
  val commissionPercent by viewModel.commissionPercent.collectAsStateWithLifecycle()

  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(BackgroundBlack)
  ) {
    Crossfade(
      targetState = currentScreen,
      label = "screen_transition"
    ) { screen ->
      when (screen) {
        AppScreen.WELCOME -> {
          WelcomeScreen(
            onStartClicked = { viewModel.navigateTo(AppScreen.LOGIN) },
            onAlreadyHaveAccountClicked = { viewModel.navigateTo(AppScreen.LOGIN) }
          )
        }

        AppScreen.LOGIN -> {
          LoginScreen(
            onBack = { viewModel.navigateTo(AppScreen.WELCOME) },
            initialIdentifier = viewModel.authManager.lastRememberedIdentifier,
            onLoginAttempt = { id, pass, rem -> viewModel.login(id, pass, rem) },
            onRegisterAttempt = { name, id, pass, role, pin -> viewModel.register(name, id, pass, role, pin) },
            onVerifyPin = { pin -> viewModel.verifyQuickPin(pin) }
          )
        }

        AppScreen.MAIN -> {
          Scaffold(
            bottomBar = {
              BarberProBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { tab -> viewModel.selectTab(tab) }
              )
            },
            containerColor = BackgroundBlack
          ) { innerPadding ->
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
              when (selectedTab) {
                MainTab.INICIO -> {
                  DashboardScreen(
                    metrics = metrics,
                    hourlySales = hourlySales,
                    dailyAppointments = dailyAppointments,
                    selectedDateYmd = selectedDateYmd,
                    profile = profile,
                    automationReportText = null, // handled globally at root level
                    onGenerateReport = { viewModel.generateDailyReportWhatsApp() },
                    onCloseReport = { viewModel.closeAutomationReport() },
                    onDateSelected = { viewModel.setDateFilter(it) },
                    onEditAppointment = { appt -> viewModel.openEditAppointment(appt) },
                    onMenuClicked = { viewModel.selectTab(MainTab.MAIS) },
                    onProfileClicked = { viewModel.selectTab(MainTab.MAIS) },
                    onNewSaleClicked = { viewModel.openAddSaleDialog() }
                  )
                }

                MainTab.CLIENTES -> {
                  ClientsScreen(
                    clients = filteredClients,
                    rawClients = rawClients,
                    searchQuery = clientSearchQuery,
                    onSearchQueryChanged = { viewModel.setClientSearchQuery(it) },
                    onBack = { viewModel.selectTab(MainTab.INICIO) },
                    onAddClient = { name, phone, notes ->
                      viewModel.addClient(name, phone, notes)
                    },
                    onUpdateClient = { updated ->
                      viewModel.updateClient(updated)
                    },
                    onDeleteClient = { id ->
                      viewModel.deleteClient(id)
                    },
                    onScheduleForClient = { name, id ->
                      viewModel.openAddSaleDialog()
                    }
                  )
                }

                MainTab.VENDAS -> {
                  SalesScreen(
                    metrics = metrics,
                    services = services,
                    rawServices = rawServices,
                    clients = rawClients,
                    lineSales = lineSales,
                    selectedPeriod = vendasPeriodFilter,
                    onPeriodSelected = { viewModel.setVendasPeriodFilter(it) },
                    onBack = { viewModel.selectTab(MainTab.INICIO) },
                    onRegisterSale = { cId, cName, sId, sName, sPrice, extra, date, time, payMethod ->
                      viewModel.registerAppointment(cId, cName, sId, sName, sPrice, extra, date, time, payMethod)
                    },
                    onEditServicePrice = { svc ->
                      viewModel.addOrUpdateService(svc.id, svc.name, svc.priceKz, svc.iconType)
                    }
                  )
                }

                MainTab.MAIS -> {
                  MoreScreen(
                    profile = profile,
                    currentUser = currentUser,
                    rawServices = rawServices,
                    supabaseConfig = viewModel.supabaseConfig,
                    syncStatusMessage = syncStatusMessage,
                    commissionCalc = commissionCalc,
                    commissionPercent = commissionPercent,
                    onSetCommissionPercent = { viewModel.setCommissionPercent(it) },
                    onGenerateDailyReportWhatsApp = { viewModel.generateDailyReportWhatsApp() },
                    onSaveSupabaseConfig = { url, key ->
                      viewModel.saveSupabaseCredentials(url, key)
                      Toast.makeText(context, "Credenciais Supabase salvas!", Toast.LENGTH_SHORT).show()
                    },
                    onSyncSupabase = {
                      viewModel.testAndSyncSupabase()
                    },
                    onAddOrUpdateService = { id, name, price, iconType ->
                      viewModel.addOrUpdateService(id, name, price, iconType)
                      Toast.makeText(context, "Serviço salvo!", Toast.LENGTH_SHORT).show()
                    },
                    onDeleteService = { svc ->
                      viewModel.deleteService(svc)
                      Toast.makeText(context, "Serviço removido.", Toast.LENGTH_SHORT).show()
                    },
                    onChangePassword = { oldPass, newPass ->
                      viewModel.changePassword(oldPass, newPass)
                    },
                    onChangePin = { newPin ->
                      viewModel.changeQuickPin(newPin)
                    },
                    onUpdatePersonalProfile = { name, phone, email ->
                      viewModel.updatePersonalProfile(name, phone, email)
                    },
                    onNavigateToMonthlyReport = {
                      viewModel.navigateTo(AppScreen.RELATORIO_MENSAL)
                    },
                    onNavigateToAnnualReport = {
                      viewModel.navigateTo(AppScreen.RELATORIO_ANUAL)
                    },
                    onLogout = { viewModel.logout() },
                    onBack = { viewModel.selectTab(MainTab.INICIO) }
                  )
                }
              }
            }
          }
        }

        AppScreen.RELATORIO_MENSAL -> {
          MonthlyReportScreen(
            monthlySales = monthlySales,
            onBack = { viewModel.navigateTo(AppScreen.MAIN) }
          )
        }

        AppScreen.RELATORIO_ANUAL -> {
          AnnualReportScreen(
            annualSales = annualSales,
            onBack = { viewModel.navigateTo(AppScreen.MAIN) }
          )
        }
      }
    }

    // Global Add Sale Dialog triggered from Dashboard FAB
    if (showAddSaleDialog) {
      ComprehensiveSaleModal(
        services = rawServices,
        clients = rawClients,
        onDismiss = { viewModel.closeAddSaleDialog() },
        onConfirm = { cId, cName, sId, sName, sPrice, extra, date, time, payMethod ->
          viewModel.registerAppointment(cId, cName, sId, sName, sPrice, extra, date, time, payMethod)
          Toast.makeText(context, "Atendimento registrado no Supabase!", Toast.LENGTH_SHORT).show()
        }
      )
    }

    // Edit Appointment / Acréscimo de Valor Modal
    selectedApptForEdit?.let { appt ->
      EditAppointmentModal(
        appointment = appt,
        onDismiss = { viewModel.openEditAppointment(null) },
        onSave = { updated ->
          viewModel.updateAppointment(updated)
          Toast.makeText(context, "Atendimento atualizado no Supabase!", Toast.LENGTH_SHORT).show()
        },
        onDelete = { id ->
          viewModel.deleteAppointment(id)
          Toast.makeText(context, "Atendimento excluído.", Toast.LENGTH_SHORT).show()
        }
      )
    }

    // Global Automated WhatsApp Report Modal
    if (!automationReportText.isNullOrBlank()) {
      com.example.ui.screens.AutomatedReportModal(
        reportText = automationReportText!!,
        onDismiss = { viewModel.closeAutomationReport() }
      )
    }
  }
}

@Composable
fun EditAppointmentModal(
  appointment: AppointmentEntity,
  onDismiss: () -> Unit,
  onSave: (AppointmentEntity) -> Unit,
  onDelete: (String) -> Unit
) {
  var clientName by remember { mutableStateOf(appointment.clientName) }
  var servicePriceText by remember { mutableStateOf(appointment.servicePrice.toString()) }
  var extraValueText by remember { mutableStateOf(appointment.extraValue.toString()) }
  var status by remember { mutableStateOf(appointment.status) }
  var paymentMethod by remember { mutableStateOf(appointment.paymentMethod) }

  val basePrice = servicePriceText.toLongOrNull() ?: appointment.servicePrice
  val extraVal = extraValueText.toLongOrNull() ?: 0L
  val calculatedTotal = basePrice + extraVal

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = {
      Text(
        text = "Editar Atendimento & Acréscimo",
        color = TextWhite,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = clientName,
          onValueChange = { clientName = it },
          label = { Text("Nome do cliente", color = TextGray) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBg,
            unfocusedContainerColor = InputBg,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = AccentGold,
            unfocusedBorderColor = InputBorder
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Text("Serviço: ${appointment.serviceName}", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

        OutlinedTextField(
          value = servicePriceText,
          onValueChange = { servicePriceText = it },
          label = { Text("Preço do Serviço (Kz)", color = TextGray) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBg,
            unfocusedContainerColor = InputBg,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = AccentGold,
            unfocusedBorderColor = InputBorder
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = extraValueText,
          onValueChange = { extraValueText = it },
          label = { Text("Acréscimo de Valor ao Cliente (Kz)", color = AccentGold) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBg,
            unfocusedContainerColor = InputBg,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = AccentGold,
            unfocusedBorderColor = InputBorder
          ),
          modifier = Modifier.fillMaxWidth()
        )

        // Live Total
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceElevated)
            .padding(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Total Atualizado:", color = TextWhite, fontSize = 13.sp)
            Text(Formatters.formatKz(calculatedTotal), color = PositiveGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
          }
        }

        // Status Toggle
        Text("Status do Atendimento:", color = TextGray, fontSize = 12.sp)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("CONCLUIDO", "AGENDADO", "CANCELADO").forEach { st ->
            val isSel = status == st
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) SurfaceElevated else InputBg)
                .border(1.dp, if (isSel) AccentGold else InputBorder, RoundedCornerShape(8.dp))
                .clickable { status = st }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = when (st) {
                  "CONCLUIDO" -> "Concluído"
                  "AGENDADO" -> "Agendado"
                  else -> "Cancelado"
                },
                color = if (isSel) TextWhite else TextGray,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
          onClick = { onDelete(appointment.id) },
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0x33EF4444),
            contentColor = NegativeRed
          ),
          shape = RoundedCornerShape(100.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Excluir Atendimento")
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSave(
            appointment.copy(
              clientName = clientName.trim(),
              servicePrice = basePrice,
              extraValue = extraVal,
              totalPrice = calculatedTotal,
              status = status,
              paymentMethod = paymentMethod
            )
          )
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = ButtonPrimaryBg,
          contentColor = ButtonPrimaryText
        ),
        shape = RoundedCornerShape(100.dp)
      ) {
        Text("Salvar", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancelar", color = TextGray)
      }
    }
  )
}
