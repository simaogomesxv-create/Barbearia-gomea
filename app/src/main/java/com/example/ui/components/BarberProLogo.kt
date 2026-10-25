package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AccentGold
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite

@Composable
fun BarberProLogo(
  modifier: Modifier = Modifier,
  iconSize: Dp = 68.dp,
  showTagline: Boolean = true,
  titleSize: Float = 26f
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Emblem with crown and crossed scissors
    Box(
      modifier = Modifier
        .size(iconSize)
        .clip(CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(id = R.drawable.app_logo),
        contentDescription = "Logo Barbearia Gomes",
        modifier = Modifier.size(iconSize)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // BarberPro Title
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Barber",
        color = TextWhite,
        fontSize = titleSize.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
      )
      Text(
        text = "Pro",
        color = AccentGold,
        fontSize = titleSize.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
      )
    }

    if (showTagline) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Mais que cortes, é gestão.",
        color = TextGray,
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal
      )
    }
  }
}
