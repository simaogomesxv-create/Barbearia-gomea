package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.BarberProLogo
import com.example.ui.theme.ButtonPrimaryBg
import com.example.ui.theme.ButtonPrimaryText
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite

@Composable
fun WelcomeScreen(
  onStartClicked: () -> Unit,
  onAlreadyHaveAccountClicked: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier.fillMaxSize()
  ) {
    // Barbershop background image
    Image(
      painter = painterResource(id = R.drawable.barber_welcome_bg),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )

    // Dark moody gradient overlay (from subtle at top to deep solid black at bottom)
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color(0x99000000),
              Color(0xB309090B),
              Color(0xF009090B),
              Color(0xFF09090B)
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
          )
        )
    )

    // Content
    Column(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.statusBars)
        .windowInsetsPadding(WindowInsets.navigationBars)
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top spacing
      Spacer(modifier = Modifier.height(20.dp))

      // Center Brand Logo & Tagline
      BarberProLogo(
        iconSize = 80.dp,
        titleSize = 30f,
        showTagline = true
      )

      // Bottom Call-To-Action Area
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 480.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Organize seu negócio,\nacompanhe seus resultados\ne faça sua barbearia crescer.",
          color = TextGray,
          fontSize = 14.sp,
          lineHeight = 20.sp,
          textAlign = TextAlign.Center,
          fontWeight = FontWeight.Normal,
          modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // White Pill Button "Entrar no Sistema"
        Button(
          onClick = onStartClicked,
          shape = RoundedCornerShape(100.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = ButtonPrimaryBg,
            contentColor = ButtonPrimaryText
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("welcome_btn_comecar")
        ) {
          Text(
            text = "Entrar no Sistema",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Text Button "Acessar com Credenciais ou PIN"
        TextButton(
          onClick = onAlreadyHaveAccountClicked,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("welcome_btn_login")
        ) {
          Text(
            text = "Acessar com Credenciais ou PIN",
            color = TextWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}
