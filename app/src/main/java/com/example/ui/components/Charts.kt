package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HourlySalesPoint
import com.example.model.LineSalesPoint
import com.example.model.MonthSalesPoint
import com.example.ui.theme.CardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

/**
 * Hourly Evolution Bar Chart (Início / Dashboard)
 */
@Composable
fun BarberHourlyBarChart(
  dataPoints: List<HourlySalesPoint>,
  totalTodayText: String = "45.000 Kz",
  modifier: Modifier = Modifier
) {
  var selectedIndex by remember { mutableStateOf(dataPoints.lastIndex) }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(SurfaceDark)
      .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
      .padding(16.dp)
  ) {
    Column {
      // Header: "Evolução do faturamento" + right badge "Hoje \n 45.000 Kz"
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Text(
          text = "Evolução do faturamento",
          color = TextWhite,
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold
        )

        Column(
          horizontalAlignment = Alignment.End
        ) {
          Text(
            text = "Hoje",
            color = TextMuted,
            fontSize = 10.sp
          )
          Text(
            text = totalTodayText,
            color = TextWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Chart area with Y-axis on left and bars
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp),
        verticalAlignment = Alignment.Bottom
      ) {
        // Y-axis labels
        Column(
          modifier = Modifier
            .fillMaxHeight()
            .padding(end = 10.dp),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          Text("100", color = TextMuted, fontSize = 9.sp)
          Text("50", color = TextMuted, fontSize = 9.sp)
          Text("0", color = TextMuted, fontSize = 9.sp)
        }

        // Bars
        Row(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          dataPoints.forEachIndexed { index, point ->
            val isSelected = index == selectedIndex
            val barColor = if (isSelected) Color(0xFFE4E4E7) else Color(0xFF3F3F46)

            Column(
              modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { selectedIndex = index },
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Bottom
            ) {
              // Bar column
              Box(
                modifier = Modifier
                  .width(16.dp)
                  .fillMaxHeight(point.relativeHeight)
                  .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                  .background(barColor)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // X-axis hour labels
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        dataPoints.forEach { point ->
          Text(
            text = point.hourLabel,
            color = TextMuted,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  }
}

/**
 * Line Chart for Vendas Screen with Tooltip ("16h / 8.500 Kz")
 */
@Composable
fun BarberSalesLineChart(
  dataPoints: List<LineSalesPoint>,
  modifier: Modifier = Modifier
) {
  var activeIndex by remember { mutableStateOf(4) } // 16h point by default
  val currentPoint = dataPoints.getOrNull(activeIndex) ?: dataPoints.first()

  Column(
    modifier = modifier.fillMaxWidth()
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(130.dp)
    ) {
      // Background subtle grid lines
      Canvas(modifier = Modifier.fillMaxWidth().height(110.dp)) {
        val w = size.width
        val h = size.height

        // Horizontal guide lines
        drawLine(
          color = Color(0x1AFFFFFF),
          start = Offset(0f, 0f),
          end = Offset(w, 0f),
          strokeWidth = 1f
        )
        drawLine(
          color = Color(0x1AFFFFFF),
          start = Offset(0f, h / 2f),
          end = Offset(w, h / 2f),
          strokeWidth = 1f
        )
        drawLine(
          color = Color(0x1AFFFFFF),
          start = Offset(0f, h),
          end = Offset(w, h),
          strokeWidth = 1f
        )

        // Draw smooth path
        if (dataPoints.isNotEmpty()) {
          val path = Path()
          val pointOffsets = dataPoints.map { p ->
            Offset(
              x = p.xRatio * w,
              y = h - (p.yRatio * h)
            )
          }

          path.moveTo(pointOffsets.first().x, pointOffsets.first().y)
          for (i in 0 until pointOffsets.size - 1) {
            val p0 = pointOffsets[i]
            val p1 = pointOffsets[i + 1]
            val control1 = Offset((p0.x + p1.x) / 2f, p0.y)
            val control2 = Offset((p0.x + p1.x) / 2f, p1.y)
            path.cubicTo(control1.x, control1.y, control2.x, control2.y, p1.x, p1.y)
          }

          // Draw line
          drawPath(
            path = path,
            color = Color.White,
            style = Stroke(
              width = 2.5.dp.toPx(),
              cap = StrokeCap.Round,
              join = StrokeJoin.Round
            )
          )

          // Draw small dots
          pointOffsets.forEachIndexed { idx, offset ->
            if (idx == activeIndex) {
              drawCircle(
                color = Color.White,
                radius = 5.dp.toPx(),
                center = offset
              )
              drawCircle(
                color = Color.Black,
                radius = 2.5.dp.toPx(),
                center = offset
              )
            } else {
              drawCircle(
                color = Color(0x66FFFFFF),
                radius = 2.5.dp.toPx(),
                center = offset
              )
            }
          }
        }
      }

      // Tooltip Card hovering above 16h (centered around xRatio ~ 0.68)
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(end = 56.dp, top = 4.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF1B1B1F))
          .border(1.dp, Color(0xFF33333A), RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = currentPoint.hourLabel,
            color = TextMuted,
            fontSize = 10.sp
          )
          Text(
            text = "${currentPoint.valueKz / 1000}.${if (currentPoint.valueKz % 1000 == 0L) "000" else "500"} Kz",
            color = TextWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // X-axis labels
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      dataPoints.forEachIndexed { index, point ->
        Text(
          text = point.hourLabel,
          color = if (index == activeIndex) TextWhite else TextMuted,
          fontSize = 10.sp,
          modifier = Modifier.clickable { activeIndex = index }
        )
      }
    }
  }
}

/**
 * Monthly Bar Chart (Relatório Mensal - Comparativo Jan - Out)
 */
@Composable
fun BarberMonthlyBarChart(
  months: List<MonthSalesPoint>,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(SurfaceDark)
      .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
      .padding(16.dp)
  ) {
    Column {
      Text(
        text = "Comparativo",
        color = TextWhite,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp),
        verticalAlignment = Alignment.Bottom
      ) {
        // Y-axis
        Column(
          modifier = Modifier
            .fillMaxHeight()
            .padding(end = 10.dp),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          Text("1.5M", color = TextMuted, fontSize = 9.sp)
          Text("1M", color = TextMuted, fontSize = 9.sp)
          Text("500K", color = TextMuted, fontSize = 9.sp)
          Text("0", color = TextMuted, fontSize = 9.sp)
        }

        // Bars
        Row(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          months.forEach { point ->
            Column(
              modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Bottom
            ) {
              Box(
                modifier = Modifier
                  .width(14.dp)
                  .fillMaxHeight(point.relativeHeight)
                  .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                  .background(Color(0xFFE4E4E7))
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Month labels
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        months.forEach { point ->
          Text(
            text = point.monthLabel,
            color = TextMuted,
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  }
}

/**
 * Annual Bar Chart (Relatório Anual - Resumo por mês Jan - Dez)
 */
@Composable
fun BarberAnnualBarChart(
  months: List<MonthSalesPoint>,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(SurfaceDark)
      .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
      .padding(16.dp)
  ) {
    Column {
      Text(
        text = "Resumo por mês",
        color = TextWhite,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp),
        verticalAlignment = Alignment.Bottom
      ) {
        // Y-axis
        Column(
          modifier = Modifier
            .fillMaxHeight()
            .padding(end = 10.dp),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          Text("15M", color = TextMuted, fontSize = 9.sp)
          Text("10M", color = TextMuted, fontSize = 9.sp)
          Text("5M", color = TextMuted, fontSize = 9.sp)
          Text("0", color = TextMuted, fontSize = 9.sp)
        }

        // Bars
        Row(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          months.forEach { point ->
            Column(
              modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Bottom
            ) {
              Box(
                modifier = Modifier
                  .width(11.dp)
                  .fillMaxHeight(point.relativeHeight)
                  .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                  .background(Color(0xFFE4E4E7))
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Month labels
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        months.forEach { point ->
          Text(
            text = point.monthLabel,
            color = TextMuted,
            fontSize = 8.5.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  }
}
