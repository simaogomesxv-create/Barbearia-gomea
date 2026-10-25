package com.example.data.automation

import com.example.data.Formatters
import com.example.data.local.AppointmentEntity
import com.example.data.repository.BarberDataRepository
import com.example.model.BusinessMetrics

data class CommissionCalculation(
  val totalRevenue: Long,
  val commissionRatePercent: Int = 50,
  val barberShareKz: Long,
  val barbershopShareKz: Long,
  val completedServicesCount: Int
)

object AutomationManager {

  /**
   * Generates a professional formatted daily closing report ready to share via WhatsApp / SMS.
   */
  fun generateDailyClosingReport(
    appointments: List<AppointmentEntity>,
    metrics: BusinessMetrics,
    dateYmd: String,
    barbershopName: String = "Barbearia Gomes",
    managerName: String = "Esmeraldo Tafinha",
    commissionPercent: Int = 50
  ): String {
    val completed = appointments.filter { it.status == "CONCLUIDO" }
    val totalRevenue = if (completed.isNotEmpty()) completed.sumOf { it.totalPrice } else metrics.revenueKz
    val totalCount = if (completed.isNotEmpty()) completed.size else metrics.servicesCount

    val multicaixaTotal = completed.filter { it.paymentMethod.contains("Multicaixa", ignoreCase = true) }.sumOf { it.totalPrice }
    val dinheiroTotal = completed.filter { it.paymentMethod.contains("Dinheiro", ignoreCase = true) }.sumOf { it.totalPrice }
    val outrosTotal = totalRevenue - multicaixaTotal - dinheiroTotal

    val barberCommission = (totalRevenue * commissionPercent) / 100
    val barbershopNet = totalRevenue - barberCommission

    val topService = completed.groupBy { it.serviceName }
      .maxByOrNull { it.value.size }

    val formattedDate = BarberDataRepository.formatDateToDisplay(dateYmd)

    return buildString {
      appendLine("💈 *FECHAMENTO DIÁRIO - $barbershopName*")
      appendLine("📅 Data: $formattedDate")
      appendLine("👤 Gerente: $managerName")
      appendLine("━━━━━━━━━━━━━━━━━━━")
      appendLine("💰 *Faturamento Total:* ${Formatters.formatKz(totalRevenue)}")
      appendLine("✂️ *Atendimentos Feitos:* $totalCount clientes")
      val avg = if (totalCount > 0) totalRevenue / totalCount else 500L
      appendLine("🎯 *Ticket Médio:* ${Formatters.formatKz(avg)}")
      appendLine("━━━━━━━━━━━━━━━━━━━")
      appendLine("💳 *Formas de Pagamento:*")
      appendLine("• Multicaixa Express / TPA: ${Formatters.formatKz(multicaixaTotal)}")
      appendLine("• Dinheiro / Numerário: ${Formatters.formatKz(dinheiroTotal)}")
      if (outrosTotal > 0) {
        appendLine("• Outros / Transferência: ${Formatters.formatKz(outrosTotal)}")
      }
      appendLine("━━━━━━━━━━━━━━━━━━━")
      appendLine("🤝 *Divisão Automática ($commissionPercent% / ${100 - commissionPercent}%):*")
      appendLine("• Comissões da Equipe: ${Formatters.formatKz(barberCommission)}")
      appendLine("• Saldo da Barbearia: ${Formatters.formatKz(barbershopNet)}")
      if (topService != null) {
        appendLine("━━━━━━━━━━━━━━━━━━━")
        appendLine("⭐ *Serviço Mais Realizado:* ${topService.key} (${topService.value.size}x)")
      }
      appendLine("━━━━━━━━━━━━━━━━━━━")
      appendLine("✅ _Relatório gerado automaticamente pelo BarberPro_")
    }
  }

  /**
   * Generates a personalized automated WhatsApp reminder for a scheduled appointment.
   */
  fun generateClientReminder(
    clientName: String,
    serviceName: String,
    totalPrice: Long,
    timeHm: String,
    barbershopName: String = "Barbearia Gomes"
  ): String {
    return "Olá $clientName! 👋\n" +
      "Lembramos que o seu atendimento na *$barbershopName* está confirmado para hoje às *$timeHm* ($serviceName - ${Formatters.formatKz(totalPrice)}).\n\n" +
      "Esperamos por você! Qualquer imprevisto, favor avisar com antecedência. 💈"
  }

  /**
   * Calculates commissions automatically
   */
  fun calculateCommissions(
    totalRevenue: Long,
    completedCount: Int,
    ratePercent: Int = 50
  ): CommissionCalculation {
    val barber = (totalRevenue * ratePercent) / 100
    val shop = totalRevenue - barber
    return CommissionCalculation(
      totalRevenue = totalRevenue,
      commissionRatePercent = ratePercent,
      barberShareKz = barber,
      barbershopShareKz = shop,
      completedServicesCount = completedCount
    )
  }
}
