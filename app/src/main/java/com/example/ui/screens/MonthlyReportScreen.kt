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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MonthSalesPoint
import com.example.ui.components.BarberMonthlyBarChart
import com.example.ui.components.HeroSummaryCard
import com.example.ui.components.MetricGridCard
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.ButtonPrimaryBg
import com.example.ui.theme.ButtonPrimaryText
import com.example.ui.theme.CardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite

@Composable
fun MonthlyReportScreen(
  monthlySales: List<MonthSalesPoint>,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  var showDetailsDialog by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(BackgroundBlack)
      .windowInsetsPadding(WindowInsets.statusBars)
      .windowInsetsPadding(WindowInsets.navigationBars)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 10.dp)
        .widthIn(max = 600.dp)
    ) {
      // Top Bar: Back, "Relatório Mensal"
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("monthly_back_btn")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Voltar",
            tint = TextWhite
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Relatório Mensal",
          color = TextWhite,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Month Selector Pill
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(SurfaceDark)
          .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
          .clickable {
            Toast.makeText(context, "Mês selecionado: Outubro 2026", Toast.LENGTH_SHORT).show()
          }
          .padding(horizontal = 14.dp, vertical = 12.dp)
          .testTag("monthly_selector_pill")
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
              text = "Outubro 2026",
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

      // Faturamento Total Card
      HeroSummaryCard(
        title = "Faturamento total",
        value = "1.250.000 Kz",
        changePctText = "↑ 22%",
        subtitle = "Setembro: 1.020.000 Kz",
        icon = Icons.Outlined.Savings,
        modifier = Modifier.testTag("monthly_hero_card")
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 2 Metric cards: Total de clientes & Ticket médio
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        MetricGridCard(
          title = "Total de clientes",
          value = "312",
          changePctText = "↑ 15%",
          footnote = null,
          icon = Icons.Outlined.Person,
          modifier = Modifier
            .weight(1f)
            .testTag("monthly_metric_clientes")
        )

        MetricGridCard(
          title = "Ticket médio",
          value = "4.006 Kz",
          changePctText = "↑ 10%",
          footnote = null,
          icon = Icons.Outlined.ReceiptLong,
          modifier = Modifier
            .weight(1f)
            .testTag("monthly_metric_ticket")
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Comparativo Bar Chart
      BarberMonthlyBarChart(
        months = monthlySales,
        modifier = Modifier.testTag("monthly_bar_chart")
      )

      Spacer(modifier = Modifier.height(20.dp))

      // White Pill Button "Ver detalhes"
      Button(
        onClick = { showDetailsDialog = true },
        shape = RoundedCornerShape(100.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = ButtonPrimaryBg,
          contentColor = ButtonPrimaryText
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("monthly_btn_ver_detalhes")
      ) {
        Text(
          text = "Ver detalhes",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }

  if (showDetailsDialog) {
    MonthlyDetailsModal(onDismiss = { showDetailsDialog = false })
  }
}

@Composable
fun MonthlyDetailsModal(onDismiss: () -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = {
      Text(
        text = "Detalhamento - Outubro 2026",
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
        Text("• Melhor dia: Sábado (240.000 Kz)", color = TextWhite, fontSize = 13.sp)
        Text("• Atendimentos totais: 312 cortes", color = TextWhite, fontSize = 13.sp)
        Text("• Barbeiro destaque: Esmeraldo Tafinha (142 cortes)", color = TextWhite, fontSize = 13.sp)
        Text("• Forma de pagamento líder: Multicaixa Express (68%)", color = TextWhite, fontSize = 13.sp)
        Text("• Crescimento sobre mês anterior: +22.5%", color = TextWhite, fontSize = 13.sp)
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
        Text("OK")
      }
    }
  )
}
