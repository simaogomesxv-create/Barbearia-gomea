package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Chat
import com.example.data.automation.AutomationManager
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Formatters
import com.example.data.local.AppointmentEntity
import com.example.data.repository.BarberDataRepository
import com.example.model.BarbershopProfile
import com.example.model.BusinessMetrics
import com.example.model.HourlySalesPoint
import com.example.ui.components.BarberHourlyBarChart
import com.example.ui.components.MetricGridCard
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.ButtonPrimaryBg
import com.example.ui.theme.ButtonPrimaryText
import com.example.ui.theme.CardBorder
import com.example.ui.theme.InputBg
import com.example.ui.theme.InputBorder
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun DashboardScreen(
  metrics: BusinessMetrics,
  hourlySales: List<HourlySalesPoint>,
  dailyAppointments: List<AppointmentEntity>,
  selectedDateYmd: String,
  profile: BarbershopProfile = BarbershopProfile(),
  automationReportText: String? = null,
  onGenerateReport: () -> Unit = {},
  onCloseReport: () -> Unit = {},
  onDateSelected: (String) -> Unit,
  onEditAppointment: (AppointmentEntity) -> Unit,
  onMenuClicked: () -> Unit,
  onProfileClicked: () -> Unit,
  onNewSaleClicked: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val todayYmd = remember { BarberDataRepository.getTodayDateYmd() }
  val yesterdayYmd = remember { BarberDataRepository.getYesterdayDateYmd() }

  val dateLabel = when (selectedDateYmd) {
    todayYmd -> "Hoje (${BarberDataRepository.formatDateToDisplay(selectedDateYmd)})"
    yesterdayYmd -> "Ontem (${BarberDataRepository.formatDateToDisplay(selectedDateYmd)})"
    else -> BarberDataRepository.formatDateToDisplay(selectedDateYmd)
  }

  val totalFeitoNoDia = dailyAppointments.filter { it.status == "CONCLUIDO" }.sumOf { it.totalPrice }
  val atendimentosFeitos = dailyAppointments.count { it.status == "CONCLUIDO" }
  val ticketMedioDia = if (atendimentosFeitos > 0) totalFeitoNoDia / atendimentosFeitos else 500L
  val managerFirstName = profile.managerName.split(" ").firstOrNull() ?: "Esmeraldo"

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
        .padding(horizontal = 20.dp, vertical = 12.dp)
        .widthIn(max = 600.dp)
    ) {
      // Top Navigation / Header Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onMenuClicked,
          modifier = Modifier.testTag("dashboard_btn_menu")
        ) {
          Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = TextWhite
          )
        }

        // Circular Avatar "G" (Barbearia Gomes)
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(TextWhite)
            .clickable { onProfileClicked() }
            .testTag("dashboard_btn_avatar"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "G",
            color = BackgroundBlack,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Greeting with Golden Crown
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Olá, $managerFirstName!",
          color = TextWhite,
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
          imageVector = Icons.Default.EmojiEvents,
          contentDescription = "Coroa",
          tint = AccentGold,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = "Aqui está o resumo da ${profile.name}",
        color = TextGray,
        fontSize = 13.sp
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Date Picker Dropdown Pill
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(SurfaceDark)
          .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
          .clickable {
            // Toggle between Hoje and Ontem or show toast
            val nextDate = if (selectedDateYmd == todayYmd) yesterdayYmd else todayYmd
            onDateSelected(nextDate)
            Toast.makeText(context, "Data alternada: $nextDate", Toast.LENGTH_SHORT).show()
          }
          .padding(horizontal = 14.dp, vertical = 12.dp)
          .testTag("dashboard_date_filter")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CalendarToday,
              contentDescription = null,
              tint = TextGray,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = dateLabel,
              color = TextWhite,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
          }

          Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = TextGray,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 2x2 Grid of Metrics (from real database)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        MetricGridCard(
          title = "Clientes atendidos",
          value = "${metrics.clientsCount}",
          changePctText = "↑ ${metrics.clientsChangePct}%",
          footnote = "Ontem: ${metrics.yesterdayClients}",
          icon = Icons.Outlined.Person,
          modifier = Modifier
            .weight(1f)
            .testTag("metric_clientes_atendidos")
        )

        MetricGridCard(
          title = "Faturamento",
          value = Formatters.formatKz(metrics.revenueKz),
          changePctText = "↑ ${metrics.revenueChangePct}%",
          footnote = "Ontem: ${Formatters.formatKz(metrics.yesterdayRevenueKz)}",
          icon = Icons.Outlined.AttachMoney,
          modifier = Modifier
            .weight(1f)
            .testTag("metric_faturamento")
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        MetricGridCard(
          title = "Ticket médio",
          value = Formatters.formatKz(metrics.avgTicketKz),
          changePctText = "Base ~500 Kz",
          footnote = null,
          icon = Icons.Outlined.ReceiptLong,
          modifier = Modifier
            .weight(1f)
            .testTag("metric_ticket_medio")
        )

        MetricGridCard(
          title = "Serviços",
          value = "${metrics.servicesCount}",
          changePctText = "↑ ${metrics.servicesChangePct}%",
          footnote = "Ontem: ${metrics.yesterdayServices}",
          icon = Icons.Outlined.ContentCut,
          modifier = Modifier
            .weight(1f)
            .testTag("metric_servicos")
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Evolution Bar Chart (from real database)
      BarberHourlyBarChart(
        dataPoints = hourlySales,
        totalTodayText = Formatters.formatKz(metrics.revenueKz),
        modifier = Modifier.testTag("chart_evolucao_faturamento")
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Agenda & Cálculo do Dia
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Agenda & Atendimentos",
            color = TextWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Cálculo: ${Formatters.formatKz(totalFeitoNoDia)} ($atendimentosFeitos feitos • Médio: ${Formatters.formatKz(ticketMedioDia)})",
            color = PositiveGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Button(
            onClick = onGenerateReport,
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0x3325D366),
              contentColor = PositiveGreen
            ),
            shape = RoundedCornerShape(100.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(Icons.Outlined.Chat, contentDescription = null, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Fechamento", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          }

          Button(
            onClick = onNewSaleClicked,
            colors = ButtonDefaults.buttonColors(
              containerColor = SurfaceElevated,
              contentColor = TextWhite
            ),
            shape = RoundedCornerShape(100.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Atender", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      if (dailyAppointments.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(20.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "Nenhum atendimento registrado nesta data.",
              color = TextGray,
              fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Toque em 'Atender' ou no botão '+' abaixo para cadastrar clientes e cortes.",
              color = TextMuted,
              fontSize = 12.sp,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      } else {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          dailyAppointments.forEach { appt ->
            AppointmentRowItem(
              appointment = appt,
              onEditClick = { onEditAppointment(appt) }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
    }

    // Quick FAB
    FloatingActionButton(
      onClick = onNewSaleClicked,
      containerColor = TextWhite,
      contentColor = BackgroundBlack,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 20.dp, end = 20.dp)
        .testTag("fab_new_sale")
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Novo Atendimento / Venda"
      )
    }

    if (!automationReportText.isNullOrBlank()) {
      AutomatedReportModal(
        reportText = automationReportText,
        onDismiss = onCloseReport
      )
    }
  }
}

@Composable
fun AppointmentRowItem(
  appointment: AppointmentEntity,
  onEditClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(SurfaceDark)
      .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
      .clickable { onEditClick() }
      .padding(horizontal = 14.dp, vertical = 12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(SurfaceElevated),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = appointment.timeHm,
            color = AccentGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = appointment.clientName,
            color = TextWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = appointment.serviceName,
              color = TextGray,
              fontSize = 12.sp
            )
            if (appointment.extraValue > 0) {
              Text(
                text = " (+${Formatters.formatKz(appointment.extraValue)})",
                color = AccentGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = Formatters.formatKz(appointment.totalPrice),
            color = TextWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = appointment.paymentMethod,
            color = TextMuted,
            fontSize = 10.sp
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        IconButton(
          onClick = {
            val reminder = AutomationManager.generateClientReminder(
              clientName = appointment.clientName,
              serviceName = appointment.serviceName,
              totalPrice = appointment.totalPrice,
              timeHm = appointment.timeHm,
              barbershopName = "Barbearia Gomes"
            )
            val sendIntent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(Intent.EXTRA_TEXT, reminder)
              type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Enviar Lembrete WhatsApp"))
          },
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Chat,
            contentDescription = "Lembrete WhatsApp",
            tint = PositiveGreen,
            modifier = Modifier.size(16.dp)
          )
        }
        IconButton(
          onClick = onEditClick,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Editar / Adicionar valor",
            tint = TextGray,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

@Composable
fun AutomatedReportModal(
  reportText: String,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Chat, contentDescription = null, tint = PositiveGreen)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Fechamento Diário Automático",
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
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceElevated)
            .padding(12.dp)
        ) {
          Text(
            text = reportText,
            color = TextWhite,
            fontSize = 12.sp,
            lineHeight = 18.sp
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("Fechamento Barbearia Gomes", reportText)
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "Relatório copiado para a área de transferência!", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = SurfaceElevated,
              contentColor = TextWhite
            ),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Copiar", fontSize = 12.sp)
          }

          Button(
            onClick = {
              val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, reportText)
                type = "text/plain"
              }
              context.startActivity(Intent.createChooser(sendIntent, "Enviar Fechamento no WhatsApp"))
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF25D366),
              contentColor = BackgroundBlack
            ),
            modifier = Modifier.weight(1.3f)
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
