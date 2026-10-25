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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Formatters
import com.example.data.local.AppointmentEntity
import com.example.data.local.ClientEntity
import com.example.data.local.ServiceEntity
import com.example.model.BusinessMetrics
import com.example.model.LineSalesPoint
import com.example.model.PeriodFilter
import com.example.model.ServiceItem
import com.example.ui.components.BarberSalesLineChart
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SalesScreen(
  metrics: BusinessMetrics,
  services: List<ServiceItem>,
  rawServices: List<ServiceEntity>,
  clients: List<ClientEntity>,
  lineSales: List<LineSalesPoint>,
  selectedPeriod: PeriodFilter,
  onPeriodSelected: (PeriodFilter) -> Unit,
  onBack: () -> Unit,
  onRegisterSale: (clientId: String, clientName: String, serviceId: String, serviceName: String, servicePrice: Long, extraValue: Long, dateYmd: String, timeHm: String, paymentMethod: String) -> Unit,
  onEditServicePrice: (ServiceEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  var showNewSaleModal by remember { mutableStateOf(false) }
  var serviceToEdit by remember { mutableStateOf<ServiceEntity?>(null) }

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
      // Top Bar: Back, "Vendas", Add
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("sales_back_btn")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Voltar",
            tint = TextWhite
          )
        }

        Text(
          text = "Vendas",
          color = TextWhite,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )

        IconButton(
          onClick = { showNewSaleModal = true },
          modifier = Modifier.testTag("sales_add_btn")
        ) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(SurfaceElevated)
              .border(1.dp, CardBorder, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Nova Venda",
              tint = TextWhite,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Period Filter Segmented Pills (Hoje, Semana, Mês, Ano)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(100.dp))
          .background(SurfaceDark)
          .border(1.dp, CardBorder, RoundedCornerShape(100.dp))
          .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        PeriodFilter.values().forEach { period ->
          val isSelected = selectedPeriod == period
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(100.dp))
              .background(if (isSelected) TextWhite else Color.Transparent)
              .clickable { onPeriodSelected(period) }
              .padding(vertical = 8.dp)
              .testTag("sales_filter_${period.name.lowercase()}"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = period.label,
              color = if (isSelected) BackgroundBlack else TextGray,
              fontSize = 13.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Faturamento Card with Line Chart
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(SurfaceDark)
          .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
          .padding(16.dp)
          .testTag("card_vendas_faturamento")
      ) {
        Column {
          // Top Row: Tag Icon + "Faturamento"
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceElevated),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Outlined.LocalOffer,
                contentDescription = null,
                tint = TextWhite,
                modifier = Modifier.size(16.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Faturamento Real",
              color = TextGray,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Revenue Value + Green Tag
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = Formatters.formatKz(metrics.revenueKz),
              color = TextWhite,
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "↑ ${metrics.revenueChangePct}%",
              color = PositiveGreen,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Ontem: ${Formatters.formatKz(metrics.yesterdayRevenueKz)} • Ticket Médio: ${Formatters.formatKz(metrics.avgTicketKz)}",
            color = TextMuted,
            fontSize = 12.sp
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Smooth Line Chart
          BarberSalesLineChart(dataPoints = lineSales)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Section: "Serviços mais realizados"
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Tabela de Serviços & Vendas",
          color = TextWhite,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Toque para atender",
          color = TextMuted,
          fontSize = 11.sp
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Services List
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        services.forEach { service ->
          val rawSvc = rawServices.firstOrNull { it.id == service.id }
          RealServiceRowItem(
            service = service,
            onClick = {
              showNewSaleModal = true
            },
            onEditPrice = {
              if (rawSvc != null) {
                serviceToEdit = rawSvc
              }
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }

  // New Sale Modal with Extra Value and Client selection
  if (showNewSaleModal) {
    ComprehensiveSaleModal(
      services = rawServices,
      clients = clients,
      onDismiss = { showNewSaleModal = false },
      onConfirm = { cId, cName, sId, sName, sPrice, extra, date, time, payMethod ->
        onRegisterSale(cId, cName, sId, sName, sPrice, extra, date, time, payMethod)
        showNewSaleModal = false
        Toast.makeText(context, "Atendimento registrado no Supabase!", Toast.LENGTH_SHORT).show()
      }
    )
  }

  // Edit Service Price Dialog
  serviceToEdit?.let { svc ->
    EditServicePriceModal(
      service = svc,
      onDismiss = { serviceToEdit = null },
      onSave = { updated ->
        onEditServicePrice(updated)
        serviceToEdit = null
        Toast.makeText(context, "Preço atualizado!", Toast.LENGTH_SHORT).show()
      }
    )
  }
}

@Composable
fun RealServiceRowItem(
  service: ServiceItem,
  onClick: () -> Unit,
  onEditPrice: () -> Unit,
  modifier: Modifier = Modifier
) {
  val icon: ImageVector = when (service.iconType) {
    "beard" -> Icons.Outlined.Face
    "eyebrow" -> Icons.Outlined.Spa
    "hair_color" -> Icons.Outlined.ContentCut
    "hair_beard" -> Icons.Outlined.ContentCut
    else -> Icons.Outlined.ContentCut
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(SurfaceDark)
      .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 12.dp)
      .testTag("service_item_${service.id}")
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
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceElevated),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextWhite,
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = service.name,
            color = TextWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "Preço: ${Formatters.formatKz(service.pricePerService)}",
            color = AccentGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "${service.count} atendimentos",
            color = TextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = Formatters.formatKz(service.totalKz),
            color = TextWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
          onClick = onEditPrice,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Editar Preço",
            tint = TextGray,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

@Composable
fun EditServicePriceModal(
  service: ServiceEntity,
  onDismiss: () -> Unit,
  onSave: (ServiceEntity) -> Unit
) {
  var priceText by remember { mutableStateOf(service.priceKz.toString()) }
  var nameText by remember { mutableStateOf(service.name) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = {
      Text(
        text = "Editar Preço do Serviço",
        color = TextWhite,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = nameText,
          onValueChange = { nameText = it },
          label = { Text("Nome do serviço", color = TextGray) },
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
          value = priceText,
          onValueChange = { priceText = it },
          label = { Text("Preço em Kwanzas (Kz)", color = TextGray) },
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
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val newPrice = priceText.toLongOrNull() ?: service.priceKz
          onSave(service.copy(name = nameText.trim(), priceKz = newPrice))
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = ButtonPrimaryBg,
          contentColor = ButtonPrimaryText
        ),
        shape = RoundedCornerShape(100.dp)
      ) {
        Text("Salvar Preço", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancelar", color = TextGray)
      }
    }
  )
}

@Composable
fun ComprehensiveSaleModal(
  services: List<ServiceEntity>,
  clients: List<ClientEntity>,
  onDismiss: () -> Unit,
  onConfirm: (
    clientId: String,
    clientName: String,
    serviceId: String,
    serviceName: String,
    servicePrice: Long,
    extraValue: Long,
    dateYmd: String,
    timeHm: String,
    paymentMethod: String
  ) -> Unit
) {
  var selectedService by remember { mutableStateOf(services.firstOrNull()) }
  var clientNameInput by remember { mutableStateOf(clients.firstOrNull()?.name ?: "Novo Cliente") }
  var selectedClientId by remember { mutableStateOf(clients.firstOrNull()?.id ?: "") }
  var extraValueText by remember { mutableStateOf("0") }
  var paymentMethod by remember { mutableStateOf("Multicaixa Express") }

  val basePrice = selectedService?.priceKz ?: 500L
  val extraValue = extraValueText.toLongOrNull() ?: 0L
  val calculatedTotal = basePrice + extraValue

  val now = remember { Date() }
  val dateYmd = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now) }
  val timeHm = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(now) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = {
      Text(
        text = "Registrar Atendimento",
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
        // Client selection or type
        Text("Cliente:", color = TextGray, fontSize = 12.sp)
        OutlinedTextField(
          value = clientNameInput,
          onValueChange = {
            clientNameInput = it
            selectedClientId = clients.firstOrNull { c -> c.name.equals(it, ignoreCase = true) }?.id ?: ""
          },
          placeholder = { Text("Nome do cliente", color = TextMuted) },
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

        // Services picker
        Text("Selecione o Serviço:", color = TextGray, fontSize = 12.sp)
        services.forEach { svc ->
          val isSel = selectedService?.id == svc.id
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSel) SurfaceElevated else InputBg)
              .border(1.dp, if (isSel) AccentGold else InputBorder, RoundedCornerShape(10.dp))
              .clickable { selectedService = svc }
              .padding(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(svc.name, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
              Text(Formatters.formatKz(svc.priceKz), color = AccentGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Extra value input (Acréscimo de valor a cada cliente)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Acréscimo de Valor / Extra (Kz):",
          color = AccentGold,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
          value = extraValueText,
          onValueChange = { extraValueText = it },
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

        // Total preview
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceElevated)
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Valor Total a Cobrar:", color = TextWhite, fontSize = 13.sp)
            Text(Formatters.formatKz(calculatedTotal), color = PositiveGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
          }
        }

        // Payment method
        Text("Forma de Pagamento:", color = TextGray, fontSize = 12.sp)
        listOf("Multicaixa Express", "TPA Multicaixa", "Dinheiro (Kz)", "Transferência").forEach { method ->
          val isSel = paymentMethod == method
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSel) SurfaceElevated else InputBg)
              .border(1.dp, if (isSel) AccentGold else InputBorder, RoundedCornerShape(8.dp))
              .clickable { paymentMethod = method }
              .padding(8.dp)
          ) {
            Text(method, color = if (isSel) TextWhite else TextGray, fontSize = 12.sp)
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val svc = selectedService ?: services.firstOrNull() ?: return@Button
          val cName = clientNameInput.ifBlank { "Cliente Avulso" }
          val cId = selectedClientId.ifBlank { "c_${System.currentTimeMillis()}" }
          onConfirm(
            cId,
            cName,
            svc.id,
            svc.name,
            svc.priceKz,
            extraValue,
            dateYmd,
            timeHm,
            paymentMethod
          )
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = ButtonPrimaryBg,
          contentColor = ButtonPrimaryText
        ),
        shape = RoundedCornerShape(100.dp)
      ) {
        Text("Concluir Atendimento", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancelar", color = TextGray)
      }
    }
  )
}
