package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun MetricGridCard(
  title: String,
  value: String,
  changePctText: String,
  icon: ImageVector,
  footnote: String? = null,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(SurfaceDark)
      .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
      .padding(14.dp)
  ) {
    Column(
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Header: icon + label
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceElevated),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextWhite,
            modifier = Modifier.size(16.dp)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = title,
          color = TextGray,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 1
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Value
      Text(
        text = value,
        color = TextWhite,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Change percentage badge
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = changePctText,
          color = PositiveGreen,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      // Footnote
      if (!footnote.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = footnote,
          color = TextMuted,
          fontSize = 11.sp,
          fontWeight = FontWeight.Normal
        )
      }
    }
  }
}

@Composable
fun HeroSummaryCard(
  title: String,
  value: String,
  changePctText: String,
  subtitle: String,
  icon: ImageVector,
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
            imageVector = icon,
            contentDescription = null,
            tint = TextWhite,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = title,
          color = TextGray,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = value,
          color = TextWhite,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = changePctText,
          color = PositiveGreen,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = subtitle,
        color = TextMuted,
        fontSize = 12.sp
      )
    }
  }
}
