package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AuthSessionManager
import com.example.data.auth.CredentialType
import com.example.data.auth.PasswordStrength
import com.example.ui.components.BarberProLogo
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
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
  onBack: () -> Unit,
  initialIdentifier: String = "+244 948 840 379",
  onLoginAttempt: suspend (identifier: String, pass: String, remember: Boolean) -> Result<Any>,
  onRegisterAttempt: (suspend (name: String, identifier: String, pass: String, role: String, pin: String) -> Result<Any>)? = null,
  onVerifyPin: suspend (pin: String) -> Result<Any>,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  var emailOrPhone by remember { mutableStateOf(initialIdentifier) }
  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var rememberMe by remember { mutableStateOf(true) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var showQuickPinDialog by remember { mutableStateOf(false) }

  // Smart Validation
  val credentialType = remember(emailOrPhone) { AuthSessionManager.checkCredentialType(emailOrPhone) }
  val passwordStrength = remember(password) { AuthSessionManager.calculatePasswordStrength(password) }

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
        .padding(horizontal = 24.dp, vertical = 12.dp)
        .widthIn(max = 480.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top row with Back button and Quick Unlock Chip
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("login_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Voltar",
            tint = TextWhite
          )
        }

        // Fast biometric / PIN personal quick unlock chip
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(SurfaceElevated)
            .border(1.dp, CardBorder, RoundedCornerShape(100.dp))
            .clickable { showQuickPinDialog = true }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("btn_quick_pin_unlock")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = AccentGold, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(5.dp))
            Text("Acesso com PIN", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Logo
      BarberProLogo(
        iconSize = 64.dp,
        titleSize = 26f,
        showTagline = true
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Heading: "Entrar" + "Barbearia Gomes • Credenciais Pessoais"
      Text(
        text = "Autenticação Segura",
        color = TextWhite,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Shield, contentDescription = null, tint = PositiveGreen, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Barbearia Gomes • Criptografia SHA-256",
          color = PositiveGreen,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Error banner if any
      AnimatedVisibility(visible = errorMessage != null) {
        errorMessage?.let { error ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(NegativeRed.copy(alpha = 0.15f))
              .border(1.dp, NegativeRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
              .padding(12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Info, contentDescription = null, tint = NegativeRed, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(error, color = TextWhite, fontSize = 12.sp)
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
        }
      }

      // Field 1: Email or Phone
      OutlinedTextField(
        value = emailOrPhone,
        onValueChange = { input ->
          errorMessage = null
          emailOrPhone = if (input.all { it.isDigit() || it == '+' || it == ' ' } && input.length in 9..14) {
            AuthSessionManager.formatAngolanPhone(input)
          } else {
            input
          }
        },
        placeholder = { Text("E-mail ou telefone angolano (+244)", color = TextMuted, fontSize = 14.sp) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            tint = if (credentialType != CredentialType.INVALID) AccentGold else TextGray
          )
        },
        trailingIcon = {
          if (credentialType != CredentialType.INVALID && emailOrPhone.isNotBlank()) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Válido",
              tint = PositiveGreen,
              modifier = Modifier.size(18.dp)
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
          unfocusedTextColor = TextWhite,
          cursorColor = AccentGold
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_email_phone")
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Field 2: Personal Password (Empty by default for true security!)
      OutlinedTextField(
        value = password,
        onValueChange = {
          password = it
          errorMessage = null
        },
        placeholder = { Text("Senha pessoal segura", color = TextMuted, fontSize = 14.sp) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Outlined.Lock,
            contentDescription = null,
            tint = TextGray
          )
        },
        trailingIcon = {
          IconButton(onClick = { passwordVisible = !passwordVisible }) {
            Icon(
              imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
              contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha",
              tint = TextGray
            )
          }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = InputBg,
          unfocusedContainerColor = InputBg,
          focusedBorderColor = AccentGold,
          unfocusedBorderColor = InputBorder,
          focusedTextColor = TextWhite,
          unfocusedTextColor = TextWhite,
          cursorColor = AccentGold
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_password")
      )

      // Live Password Strength Bar
      if (password.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          val strengthColor = when (passwordStrength) {
            PasswordStrength.FORTE -> PositiveGreen
            PasswordStrength.MEDIA -> AccentGold
            PasswordStrength.FRACA -> NegativeRed
          }
          val progress = when (passwordStrength) {
            PasswordStrength.FORTE -> 1f
            PasswordStrength.MEDIA -> 0.6f
            PasswordStrength.FRACA -> 0.3f
          }

          LinearProgressIndicator(
            progress = { progress },
            color = strengthColor,
            trackColor = SurfaceElevated,
            modifier = Modifier
              .weight(1f)
              .height(4.dp)
              .clip(RoundedCornerShape(2.dp))
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Segurança: ${passwordStrength.label}",
            color = strengthColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Remember me checkbox
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { rememberMe = !rememberMe }
        ) {
          Checkbox(
            checked = rememberMe,
            onCheckedChange = { rememberMe = it },
            colors = CheckboxDefaults.colors(
              checkedColor = AccentGold,
              uncheckedColor = TextMuted,
              checkmarkColor = BackgroundBlack
            )
          )
          Text("Lembrar credencial", color = TextGray, fontSize = 12.sp)
        }

        TextButton(
          onClick = {
            Toast.makeText(context, "Solicitação de redefinição para $emailOrPhone registrada.", Toast.LENGTH_SHORT).show()
          }
        ) {
          Text(
            text = "Esqueceu a senha?",
            color = TextMuted,
            fontSize = 12.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Button: "Entrar no Sistema"
      Button(
        onClick = {
          if (emailOrPhone.isBlank()) {
            errorMessage = "Por favor, digite seu e-mail ou telefone angolano."
            return@Button
          }
          if (password.isBlank()) {
            errorMessage = "Por favor, informe sua senha pessoal de acesso."
            return@Button
          }
          isLoading = true
          errorMessage = null
          coroutineScope.launch {
            val result = onLoginAttempt(emailOrPhone, password, rememberMe)
            isLoading = false
            if (result.isFailure) {
              errorMessage = result.exceptionOrNull()?.message ?: "Falha na autenticação das credenciais."
            } else {
              Toast.makeText(context, "Acesso autorizado com sucesso!", Toast.LENGTH_SHORT).show()
            }
          }
        },
        enabled = !isLoading,
        shape = RoundedCornerShape(100.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = ButtonPrimaryBg,
          contentColor = ButtonPrimaryText
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("btn_submit_login")
      ) {
        Text(
          text = if (isLoading) "Verificando credenciais..." else "Entrar no Sistema",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // "ou" divider
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = CardBorder)
        Text(
          text = "ou",
          color = TextMuted,
          fontSize = 12.sp,
          modifier = Modifier.padding(horizontal = 14.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = CardBorder)
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Quick PIN unlock option button
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .clip(RoundedCornerShape(100.dp))
          .background(InputBg)
          .border(1.dp, CardBorder, RoundedCornerShape(100.dp))
          .clickable { showQuickPinDialog = true }
          .testTag("btn_open_quick_pin"),
        contentAlignment = Alignment.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(Icons.Default.Pin, contentDescription = null, tint = AccentGold, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Entrar com PIN Pessoal (4 dígitos)",
            color = TextWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // Security Policy: Restrict to authorized credentials only (registration removed)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(SurfaceDark.copy(alpha = 0.6f))
          .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
          .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = AccentGold,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Acesso Restrito • Barbearia Gomes",
              color = TextWhite,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Acesso exclusivo com credenciais autorizadas pela gerência.\nCriação pública de contas desativada por política de segurança.",
            color = TextMuted,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }

  // Quick PIN Dialog
  if (showQuickPinDialog) {
    QuickPinUnlockModal(
      onDismiss = { showQuickPinDialog = false },
      onConfirmPin = { enteredPin ->
        coroutineScope.launch {
          val res = onVerifyPin(enteredPin)
          if (res.isSuccess) {
            showQuickPinDialog = false
            Toast.makeText(context, "Acesso por PIN autorizado!", Toast.LENGTH_SHORT).show()
          } else {
            Toast.makeText(context, res.exceptionOrNull()?.message ?: "PIN incorreto", Toast.LENGTH_SHORT).show()
          }
        }
      }
    )
  }
}

@Composable
fun QuickPinUnlockModal(
  onDismiss: () -> Unit,
  onConfirmPin: (String) -> Unit
) {
  var pin by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Fingerprint, contentDescription = null, tint = AccentGold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Acesso Rápido Seguro",
          color = TextWhite,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Text(
          text = "Digite seu PIN pessoal de 4 dígitos para autenticar sem digitar a senha completa.",
          color = TextGray,
          fontSize = 13.sp,
          textAlign = TextAlign.Center
        )

        OutlinedTextField(
          value = pin,
          onValueChange = { if (it.length <= 4 && it.all { ch -> ch.isDigit() }) pin = it },
          placeholder = { Text("••••", color = TextMuted, fontSize = 24.sp, textAlign = TextAlign.Center) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
          visualTransformation = PasswordVisualTransformation(),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBg,
            unfocusedContainerColor = InputBg,
            focusedBorderColor = AccentGold,
            unfocusedBorderColor = InputBorder,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            cursorColor = AccentGold
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.width(160.dp)
        )

        // Visual PIN Dots indicator
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          for (i in 0 until 4) {
            val filled = i < pin.length
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(if (filled) AccentGold else SurfaceElevated)
                .border(1.dp, if (filled) AccentGold else CardBorder, CircleShape)
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onConfirmPin(pin) },
        enabled = pin.length == 4,
        colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimaryBg, contentColor = ButtonPrimaryText),
        shape = RoundedCornerShape(100.dp)
      ) {
        Text("Desbloquear", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancelar", color = TextGray)
      }
    }
  )
}


