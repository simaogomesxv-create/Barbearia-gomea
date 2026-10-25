package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Formatters
import com.example.data.auth.AuthSessionManager
import com.example.data.auth.AuthUser
import com.example.data.auth.PasswordStrength
import com.example.data.automation.CommissionCalculation
import com.example.data.local.ServiceEntity
import com.example.data.remote.SupabaseConfig
import com.example.model.BarbershopProfile
import kotlinx.coroutines.launch
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BackgroundBlack
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

@Composable
fun MoreScreen(
  profile: BarbershopProfile,
  currentUser: AuthUser,
  rawServices: List<ServiceEntity>,
  supabaseConfig: SupabaseConfig,
  syncStatusMessage: String?,
  commissionCalc: CommissionCalculation = CommissionCalculation(0L, 50, 0L, 0L, 0),
  commissionPercent: Int = 50,
  onSetCommissionPercent: (Int) -> Unit = {},
  onGenerateDailyReportWhatsApp: () -> Unit = {},
  onSaveSupabaseConfig: (url: String, key: String) -> Unit,
  onSyncSupabase: () -> Unit,
  onAddOrUpdateService: (id: String?, name: String, priceKz: Long, iconType: String) -> Unit,
  onDeleteService: (ServiceEntity) -> Unit,
  onChangePassword: suspend (oldPass: String, newPass: String) -> Result<Unit> = { _, _ -> Result.success(Unit) },
  onChangePin: suspend (newPin: String) -> Result<Unit> = { _ -> Result.success(Unit) },
  onUpdatePersonalProfile: suspend (name: String, phone: String, email: String) -> Result<AuthUser> = { _, _, _ -> Result.success(currentUser) },
  onNavigateToMonthlyReport: () -> Unit,
  onNavigateToAnnualReport: () -> Unit,
  onLogout: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  var activeModalId by remember { mutableStateOf<String?>(null) }

  val menuItems = listOf(
    MenuItemData("security", "Credenciais & Segurança Pessoal", Icons.Outlined.Shield, "menu_seguranca"),
    MenuItemData("automations", "Automações & Comissões", Icons.Outlined.AutoAwesome, "menu_automacoes"),
    MenuItemData("profile", "Perfil da barbearia", Icons.Outlined.Storefront, "menu_perfil"),
    MenuItemData("services", "Serviços e preços", Icons.Outlined.ReceiptLong, "menu_servicos"),
    MenuItemData("payments", "Formas de pagamento", Icons.Outlined.CreditCard, "menu_pagamentos"),
    MenuItemData("staff", "Funcionários", Icons.Outlined.Badge, "menu_funcionarios"),
    MenuItemData("reports", "Relatórios", Icons.Outlined.BarChart, "menu_relatorios"),
    MenuItemData("notifications", "Notificações", Icons.Outlined.Notifications, "menu_notificacoes"),
    MenuItemData("supabase", "Banco de Dados Supabase", Icons.Outlined.CloudUpload, "menu_supabase"),
    MenuItemData("settings", "Configurações", Icons.Outlined.Settings, "menu_configuracoes")
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(BackgroundBlack)
      .windowInsetsPadding(WindowInsets.statusBars)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 10.dp)
        .widthIn(max = 600.dp)
    ) {
      // Top Title: "Mais"
      Text(
        text = "Mais",
        color = TextWhite,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 10.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // User Profile Header Card (Displays personal authenticated user info)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(SurfaceDark)
          .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
          .clickable { activeModalId = "security" }
          .padding(horizontal = 16.dp, vertical = 14.dp)
          .testTag("more_profile_card")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            // White circular avatar with user initial
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(AccentGold),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = currentUser.name.take(1).uppercase(),
                color = BackgroundBlack,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = currentUser.name,
                  color = TextWhite,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  imageVector = Icons.Default.Shield,
                  contentDescription = "Protegido",
                  tint = PositiveGreen,
                  modifier = Modifier.size(14.dp)
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${currentUser.role} • Barbearia Gomes",
                color = PositiveGreen,
                fontSize = 12.sp
              )
            }
          }

          Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextGray,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Menu List Options
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        menuItems.forEach { item ->
          MenuItemRow(
            item = item,
            onClick = {
              when (item.id) {
                "reports" -> activeModalId = "reports_selector"
                else -> activeModalId = item.id
              }
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Bottom Button "Sair"
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .clip(RoundedCornerShape(100.dp))
          .background(SurfaceDark)
          .border(1.dp, CardBorder, RoundedCornerShape(100.dp))
          .clickable { onLogout() }
          .testTag("btn_logout"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Sair",
          color = TextWhite,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Modals for menu actions
  when (activeModalId) {
    "security" -> {
      SecurityCredentialsModal(
        currentUser = currentUser,
        onChangePassword = onChangePassword,
        onChangePin = onChangePin,
        onUpdatePersonalProfile = onUpdatePersonalProfile,
        onLogout = onLogout,
        onDismiss = { activeModalId = null }
      )
    }

    "automations" -> {
      AutomationsManagementModal(
        commissionCalc = commissionCalc,
        commissionPercent = commissionPercent,
        onSetCommissionPercent = onSetCommissionPercent,
        onGenerateReport = {
          activeModalId = null
          onGenerateDailyReportWhatsApp()
        },
        onDismiss = { activeModalId = null }
      )
    }

    "supabase" -> {
      SupabaseManagementModal(
        config = supabaseConfig,
        syncStatusMessage = syncStatusMessage,
        onDismiss = { activeModalId = null },
        onSaveConfig = onSaveSupabaseConfig,
        onSync = onSyncSupabase
      )
    }

    "services" -> {
      ServicesCrudModal(
        services = rawServices,
        onDismiss = { activeModalId = null },
        onAddOrUpdate = onAddOrUpdateService,
        onDelete = onDeleteService
      )
    }

    "reports_selector" -> {
      ReportsSelectorModal(
        onDismiss = { activeModalId = null },
        onOpenMonthly = {
          activeModalId = null
          onNavigateToMonthlyReport()
        },
        onOpenAnnual = {
          activeModalId = null
          onNavigateToAnnualReport()
        }
      )
    }

    "profile" -> {
      ProfileDetailModal(profile = profile, onDismiss = { activeModalId = null })
    }

    "payments" -> {
      PaymentMethodsModal(onDismiss = { activeModalId = null })
    }

    "staff" -> {
      StaffListModal(onDismiss = { activeModalId = null })
    }

    "notifications" -> {
      GenericInfoModal(
        title = "Notificações",
        content = "Notificações ativadas para novos agendamentos e fechamento diário do caixa.",
        onDismiss = { activeModalId = null }
      )
    }

    "settings" -> {
      GenericInfoModal(
        title = "Configurações Gerais",
        content = "Moeda oficial: Kwanza (Kz)\nTicket médio padrão: 500 Kz\nCortes: 500 Kz (sem pintura) / 800 Kz (com pintura)\nFuso horário: WAT (UTC+1)\nBanco de dados: Supabase + Cache Local Room",
        onDismiss = { activeModalId = null }
      )
    }
  }
}

@Composable
fun SupabaseManagementModal(
  config: SupabaseConfig,
  syncStatusMessage: String?,
  onDismiss: () -> Unit,
  onSaveConfig: (url: String, key: String) -> Unit,
  onSync: () -> Unit
) {
  var urlInput by remember { mutableStateOf(config.url) }
  var keyInput by remember { mutableStateOf(config.anonKey) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.CloudUpload, contentDescription = null, tint = AccentGold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Banco de Dados Supabase",
          color = TextWhite,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "Configure a URL e chave Anon do seu projeto Supabase para sincronização em nuvem:",
          color = TextGray,
          fontSize = 12.sp
        )

        OutlinedTextField(
          value = urlInput,
          onValueChange = { urlInput = it },
          label = { Text("Supabase URL", color = TextGray) },
          placeholder = { Text("https://xyz.supabase.co", color = TextMuted) },
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
          value = keyInput,
          onValueChange = { keyInput = it },
          label = { Text("Supabase Anon Key", color = TextGray) },
          placeholder = { Text("eyJhbGciOiJIUzI1NiIsIn...", color = TextMuted) },
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

        // Status Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceElevated)
            .padding(10.dp)
        ) {
          Column {
            Text(
              text = if (config.isConfigured) "• Configuração Supabase ativa" else "• Armazenamento local Room ativo (Offline-first)",
              color = if (config.isConfigured) PositiveGreen else AccentGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
            if (!syncStatusMessage.isNullOrBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(text = syncStatusMessage, color = TextWhite, fontSize = 11.sp)
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = {
              onSaveConfig(urlInput.trim(), keyInput.trim())
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = SurfaceElevated,
              contentColor = TextWhite
            ),
            modifier = Modifier.weight(1f)
          ) {
            Text("Salvar Chaves", fontSize = 12.sp)
          }

          Button(
            onClick = onSync,
            colors = ButtonDefaults.buttonColors(
              containerColor = AccentGold,
              contentColor = BackgroundBlack
            ),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Testar & Sincronizar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(
          containerColor = ButtonPrimaryBg,
          contentColor = ButtonPrimaryText
        ),
        shape = RoundedCornerShape(100.dp)
      ) {
        Text("Fechar")
      }
    }
  )
}

@Composable
fun ServicesCrudModal(
  services: List<ServiceEntity>,
  onDismiss: () -> Unit,
  onAddOrUpdate: (id: String?, name: String, priceKz: Long, iconType: String) -> Unit,
  onDelete: (ServiceEntity) -> Unit
) {
  var editingService by remember { mutableStateOf<ServiceEntity?>(null) }
  var isAddingNew by remember { mutableStateOf(false) }
  var newName by remember { mutableStateOf("") }
  var newPriceText by remember { mutableStateOf("500") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Tabela de Serviços & Preços",
          color = TextWhite,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold
        )
        IconButton(onClick = { isAddingNew = !isAddingNew }) {
          Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = AccentGold)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        if (isAddingNew) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(SurfaceElevated)
              .padding(10.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text("Novo Serviço:", color = AccentGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
              OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Nome do serviço (ex: Corte sem pintura)") },
                modifier = Modifier.fillMaxWidth()
              )
              OutlinedTextField(
                value = newPriceText,
                onValueChange = { newPriceText = it },
                label = { Text("Preço em Kz (ex: 500)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
              )
              Button(
                onClick = {
                  val price = newPriceText.toLongOrNull() ?: 500L
                  if (newName.isNotBlank()) {
                    onAddOrUpdate(null, newName, price, "hair")
                    newName = ""
                    newPriceText = "500"
                    isAddingNew = false
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimaryBg, contentColor = ButtonPrimaryText),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("Salvar Novo Serviço")
              }
            }
          }
        }

        // List of current services
        services.forEach { svc ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(SurfaceElevated)
              .padding(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(svc.name, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(Formatters.formatKz(svc.priceKz), color = AccentGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
              Row {
                IconButton(onClick = { editingService = svc }) {
                  Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TextGray, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = { onDelete(svc) }) {
                  Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = NegativeRed, modifier = Modifier.size(16.dp))
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimaryBg, contentColor = ButtonPrimaryText),
        shape = RoundedCornerShape(100.dp)
      ) {
        Text("Fechar")
      }
    }
  )

  editingService?.let { svc ->
    EditServicePriceModal(
      service = svc,
      onDismiss = { editingService = null },
      onSave = { updated ->
        onAddOrUpdate(updated.id, updated.name, updated.priceKz, updated.iconType)
        editingService = null
      }
    )
  }
}

data class MenuItemData(
  val id: String,
  val title: String,
  val icon: ImageVector,
  val testTag: String
)

@Composable
fun MenuItemRow(
  item: MenuItemData,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(SurfaceDark)
      .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 13.dp)
      .testTag(item.testTag)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceElevated),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = TextWhite,
            modifier = Modifier.size(17.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
          text = item.title,
          color = TextWhite,
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium
        )
      }

      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = TextGray,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

@Composable
fun ReportsSelectorModal(
  onDismiss: () -> Unit,
  onOpenMonthly: () -> Unit,
  onOpenAnnual: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = {
      Text(
        text = "Selecione o Relatório",
        color = TextWhite,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .clickable { onOpenMonthly() }
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Relatório Mensal", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
              Text("Outubro 2026 - Comparativo e métricas", color = TextGray, fontSize = 12.sp)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = AccentGold)
          }
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .clickable { onOpenAnnual() }
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Relatório Anual", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
              Text("Exercício 2026 - Resumo anual por mês", color = TextGray, fontSize = 12.sp)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = AccentGold)
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("Fechar", color = TextGray)
      }
    }
  )
}

@Composable
fun ProfileDetailModal(profile: BarbershopProfile, onDismiss: () -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = { Text(profile.name, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Gestor: ${profile.managerName}", color = TextWhite, fontSize = 13.sp)
        Text("Telefone: ${profile.phone}", color = TextGray, fontSize = 13.sp)
        Text("E-mail: ${profile.email}", color = TextGray, fontSize = 13.sp)
        Text("Localização: ${profile.address}", color = TextGray, fontSize = 13.sp)
      }
    },
    confirmButton = {
      Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimaryBg, contentColor = ButtonPrimaryText)) {
        Text("Fechar")
      }
    }
  )
}

@Composable
fun PaymentMethodsModal(onDismiss: () -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = { Text("Formas de Pagamento", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("✓ Multicaixa Express (Recomendado)", color = TextWhite, fontSize = 13.sp)
        Text("✓ TPA Multicaixa", color = TextWhite, fontSize = 13.sp)
        Text("✓ Dinheiro / Numerário (Kz)", color = TextWhite, fontSize = 13.sp)
        Text("✓ Transferência Bancária (IBAN)", color = TextWhite, fontSize = 13.sp)
      }
    },
    confirmButton = {
      Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimaryBg, contentColor = ButtonPrimaryText)) {
        Text("Fechar")
      }
    }
  )
}

@Composable
fun StaffListModal(onDismiss: () -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = { Text("Equipe de Barbeiros", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("• Esmeraldo Tafinha — Gerente Geral & Master Barber", color = TextWhite, fontSize = 13.sp)
        Text("• Carlos Alberto — Especialista em Barba", color = TextWhite, fontSize = 13.sp)
        Text("• Elton Lopes — Cortes Clássicos & Fade", color = TextWhite, fontSize = 13.sp)
        Text("• Rafael Dias — Barbeiro Jr.", color = TextWhite, fontSize = 13.sp)
      }
    },
    confirmButton = {
      Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimaryBg, contentColor = ButtonPrimaryText)) {
        Text("Fechar")
      }
    }
  )
}

@Composable
fun GenericInfoModal(title: String, content: String, onDismiss: () -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = { Text(title, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
    text = { Text(content, color = TextWhite, fontSize = 13.sp) },
    confirmButton = {
      Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimaryBg, contentColor = ButtonPrimaryText)) {
        Text("OK")
      }
    }
  )
}

@Composable
fun AutomationsManagementModal(
  commissionCalc: CommissionCalculation,
  commissionPercent: Int,
  onSetCommissionPercent: (Int) -> Unit,
  onGenerateReport: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = AccentGold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Central de Automações",
          color = TextWhite,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Barbearia Gomes • Gerente Esmeraldo Tafinha",
          color = AccentGold,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )

        // Card 1: Comissões
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Divisão Automática de Comissões", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
              "Faturamento apurado: ${Formatters.formatKz(commissionCalc.totalRevenue)} (${commissionCalc.completedServicesCount} cortes)",
              color = TextGray,
              fontSize = 12.sp
            )

            // Percentage Selector
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf(40, 50, 60).forEach { rate ->
                val isSel = commissionPercent == rate
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSel) AccentGold else InputBg)
                    .clickable { onSetCommissionPercent(rate) }
                    .padding(vertical = 6.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "$rate% Equipe",
                    color = if (isSel) BackgroundBlack else TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("Repasse Barbeiros:", color = TextGray, fontSize = 11.sp)
                Text(Formatters.formatKz(commissionCalc.barberShareKz), color = PositiveGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
              Column(horizontalAlignment = Alignment.End) {
                Text("Barbearia Gomes:", color = TextGray, fontSize = 11.sp)
                Text(Formatters.formatKz(commissionCalc.barbershopShareKz), color = AccentGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        // Card 2: Fechamento WhatsApp
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Fechamento Diário Automático", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
              "Gera um relatório profissional pronto para WhatsApp com faturamento, formas de pagamento e divisão de comissões.",
              color = TextGray,
              fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
              onClick = onGenerateReport,
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25D366),
                contentColor = BackgroundBlack
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Gerar Fechamento Agora (WhatsApp)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }

        // Card 3: Automação de Lembretes & Sincronização
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Lembretes & Sincronização em Nuvem", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
              "• Lembretes de agendamentos com 1 toque no ícone do WhatsApp na agenda.\n" +
                "• Sincronização instantânea entre o cache local Room e o banco Supabase em nuvem.",
              color = TextGray,
              fontSize = 12.sp,
              lineHeight = 16.sp
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimaryBg, contentColor = ButtonPrimaryText),
        shape = RoundedCornerShape(100.dp)
      ) {
        Text("Fechar")
      }
    }
  )
}

@Composable
fun SecurityCredentialsModal(
  currentUser: AuthUser,
  onChangePassword: suspend (oldPass: String, newPass: String) -> Result<Unit>,
  onChangePin: suspend (newPin: String) -> Result<Unit>,
  onUpdatePersonalProfile: suspend (name: String, phone: String, email: String) -> Result<AuthUser>,
  onLogout: () -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  var currentPassword by remember { mutableStateOf("") }
  var newPassword by remember { mutableStateOf("") }
  var confirmNewPassword by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }

  var newPin by remember { mutableStateOf("") }
  var isSavingPassword by remember { mutableStateOf(false) }
  var isSavingPin by remember { mutableStateOf(false) }

  var feedbackMessage by remember { mutableStateOf<String?>(null) }
  var isFeedbackError by remember { mutableStateOf(false) }

  val newPasswordStrength = remember(newPassword) { AuthSessionManager.calculatePasswordStrength(newPassword) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Shield, contentDescription = null, tint = PositiveGreen)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Credenciais & Segurança",
          color = TextWhite,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Feedback message banner
        feedbackMessage?.let { msg ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(if (isFeedbackError) NegativeRed.copy(alpha = 0.2f) else PositiveGreen.copy(alpha = 0.2f))
              .border(1.dp, if (isFeedbackError) NegativeRed else PositiveGreen, RoundedCornerShape(8.dp))
              .padding(10.dp)
          ) {
            Text(
              text = msg,
              color = TextWhite,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Active Personal Credential Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AccentGold),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = currentUser.name.take(1).uppercase(),
                    color = BackgroundBlack,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(currentUser.name, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                  Text(currentUser.role, color = PositiveGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(100.dp))
                  .background(PositiveGreen.copy(alpha = 0.15f))
                  .border(1.dp, PositiveGreen.copy(alpha = 0.5f), RoundedCornerShape(100.dp))
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text("Autenticado", color = PositiveGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }

            HorizontalDivider(color = CardBorder, modifier = Modifier.padding(vertical = 4.dp))

            Text("• E-mail: ${currentUser.email}", color = TextGray, fontSize = 12.sp)
            Text("• Telefone: ${currentUser.phone}", color = TextGray, fontSize = 12.sp)
            Text("• Criptografia: SHA-256 + Salt Seguro", color = AccentGold, fontSize = 12.sp)
            if (currentUser.lastLoginFormatted.isNotBlank()) {
              Text("• Último Acesso: ${currentUser.lastLoginFormatted}", color = TextMuted, fontSize = 11.sp)
            }
          }
        }

        // Section 1: Change Personal Password
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Outlined.Lock, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Alterar Senha Pessoal", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(
              value = currentPassword,
              onValueChange = { currentPassword = it },
              placeholder = { Text("Senha atual", color = TextMuted, fontSize = 12.sp) },
              visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                  Icon(
                    imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(16.dp)
                  )
                }
              },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                focusedBorderColor = AccentGold,
                unfocusedBorderColor = InputBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = newPassword,
              onValueChange = { newPassword = it },
              placeholder = { Text("Nova senha pessoal (mín. 6 caracteres)", color = TextMuted, fontSize = 12.sp) },
              visualTransformation = PasswordVisualTransformation(),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                focusedBorderColor = AccentGold,
                unfocusedBorderColor = InputBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            )

            if (newPassword.isNotBlank()) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                LinearProgressIndicator(
                  progress = {
                    when (newPasswordStrength) {
                      PasswordStrength.FORTE -> 1f
                      PasswordStrength.MEDIA -> 0.6f
                      PasswordStrength.FRACA -> 0.3f
                    }
                  },
                  color = when (newPasswordStrength) {
                    PasswordStrength.FORTE -> PositiveGreen
                    PasswordStrength.MEDIA -> AccentGold
                    PasswordStrength.FRACA -> NegativeRed
                  },
                  trackColor = SurfaceDark,
                  modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Força: ${newPasswordStrength.label}",
                  color = AccentGold,
                  fontSize = 10.sp
                )
              }
            }

            OutlinedTextField(
              value = confirmNewPassword,
              onValueChange = { confirmNewPassword = it },
              placeholder = { Text("Confirmar nova senha", color = TextMuted, fontSize = 12.sp) },
              visualTransformation = PasswordVisualTransformation(),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                focusedBorderColor = AccentGold,
                unfocusedBorderColor = InputBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            )

            Button(
              onClick = {
                if (currentPassword.isBlank()) {
                  feedbackMessage = "Informe a senha atual para prosseguir."
                  isFeedbackError = true
                  return@Button
                }
                if (newPassword.length < 6) {
                  feedbackMessage = "A nova senha deve ter no mínimo 6 caracteres."
                  isFeedbackError = true
                  return@Button
                }
                if (newPassword != confirmNewPassword) {
                  feedbackMessage = "A confirmação de senha não confere."
                  isFeedbackError = true
                  return@Button
                }
                isSavingPassword = true
                coroutineScope.launch {
                  val res = onChangePassword(currentPassword, newPassword)
                  isSavingPassword = false
                  if (res.isSuccess) {
                    currentPassword = ""
                    newPassword = ""
                    confirmNewPassword = ""
                    feedbackMessage = "Senha pessoal atualizada com sucesso!"
                    isFeedbackError = false
                  } else {
                    feedbackMessage = res.exceptionOrNull()?.message ?: "Erro ao atualizar senha."
                    isFeedbackError = true
                  }
                }
              },
              enabled = !isSavingPassword && currentPassword.isNotBlank() && newPassword.length >= 6,
              colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimaryBg, contentColor = ButtonPrimaryText),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(if (isSavingPassword) "Salvando..." else "Atualizar Senha Segura", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Section 2: Personal Quick Unlock PIN (4 digits)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("PIN Pessoal de Acesso Rápido", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Text(
              "Configure seu PIN de 4 dígitos para login rápido e desbloqueio em dispositivos de uso pessoal.",
              color = TextGray,
              fontSize = 11.sp
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedTextField(
                value = newPin,
                onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) newPin = it },
                placeholder = { Text("Novo PIN (4 dígitos)", color = TextMuted, fontSize = 12.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = InputBg,
                  unfocusedContainerColor = InputBg,
                  focusedBorderColor = AccentGold,
                  unfocusedBorderColor = InputBorder,
                  focusedTextColor = TextWhite,
                  unfocusedTextColor = TextWhite
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              )

              Button(
                onClick = {
                  if (newPin.length != 4) {
                    feedbackMessage = "O PIN deve ter exatamente 4 dígitos."
                    isFeedbackError = true
                    return@Button
                  }
                  isSavingPin = true
                  coroutineScope.launch {
                    val res = onChangePin(newPin)
                    isSavingPin = false
                    if (res.isSuccess) {
                      newPin = ""
                      feedbackMessage = "PIN pessoal atualizado com sucesso!"
                      isFeedbackError = false
                    } else {
                      feedbackMessage = res.exceptionOrNull()?.message ?: "Erro ao atualizar PIN."
                      isFeedbackError = true
                    }
                  }
                },
                enabled = !isSavingPin && newPin.length == 4,
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimaryBg, contentColor = ButtonPrimaryText),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(if (isSavingPin) "..." else "Salvar PIN", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        // Section 3: Safe session termination
        Button(
          onClick = {
            onDismiss()
            onLogout()
          },
          colors = ButtonDefaults.buttonColors(containerColor = NegativeRed.copy(alpha = 0.2f), contentColor = NegativeRed),
          shape = RoundedCornerShape(100.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Desconectar Sessão Deste Dispositivo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimaryBg, contentColor = ButtonPrimaryText),
        shape = RoundedCornerShape(100.dp)
      ) {
        Text("Fechar")
      }
    }
  )
}


