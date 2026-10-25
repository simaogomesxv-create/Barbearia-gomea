package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.data.remote.SupabaseConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

enum class PasswordStrength(val label: String, val score: Int) {
  FRACA("Fraca", 1),
  MEDIA("Média", 2),
  FORTE("Forte", 3)
}

enum class CredentialType {
  EMAIL,
  ANGOLA_PHONE,
  INVALID
}

data class AuthUser(
  val id: String,
  val name: String,
  val email: String,
  val phone: String,
  val role: String, // "Gerente" ou "Barbeiro" ou "Recepcionista"
  val barbershopName: String = "Barbearia Gomes",
  val lastLoginFormatted: String = ""
)

data class AccountRecord(
  val id: String,
  val name: String,
  val email: String,
  val phone: String,
  val role: String,
  val barbershopName: String,
  val salt: String,
  val passwordHash: String,
  val quickPinHash: String,
  val createdAt: Long,
  val lastLoginAt: Long
)

class AuthSessionManager(
  context: Context,
  private val supabaseConfig: SupabaseConfig
) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("barberpro_auth_prefs_v2", Context.MODE_PRIVATE)

  private val httpClient = OkHttpClient.Builder()
    .connectTimeout(12, TimeUnit.SECONDS)
    .readTimeout(12, TimeUnit.SECONDS)
    .build()

  private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

  init {
    bootstrapDefaultAccountsIfNeeded()
  }

  var isLoggedIn: Boolean
    get() = prefs.getBoolean("is_logged_in", false)
    set(value) = prefs.edit().putBoolean("is_logged_in", value).apply()

  var rememberMe: Boolean
    get() = prefs.getBoolean("remember_me", true)
    set(value) = prefs.edit().putBoolean("remember_me", value).apply()

  var lastRememberedIdentifier: String
    get() = prefs.getString("last_remembered_identifier", "+244 948 840 379") ?: "+244 948 840 379"
    set(value) = prefs.edit().putString("last_remembered_identifier", value.trim()).apply()

  private var activeUserId: String
    get() = prefs.getString("active_user_id", "user_esmeraldo") ?: "user_esmeraldo"
    set(value) = prefs.edit().putString("active_user_id", value).apply()

  var isFastUnlockEnabled: Boolean
    get() = prefs.getBoolean("fast_unlock_enabled", true)
    set(value) = prefs.edit().putBoolean("fast_unlock_enabled", value).apply()

  private fun bootstrapDefaultAccountsIfNeeded() {
    val accounts = loadAccounts()

    // 1. Primary Manager Account: Esmeraldo Tafinha
    // Requested official credentials:
    // Identificador: +244 948 840 379 | Senha: Gomesbarber | PIN: 2526
    val salt1 = generateSalt()
    val primaryManager = AccountRecord(
      id = "user_esmeraldo",
      name = "Esmeraldo Tafinha",
      email = "esmeraldo@barbeariagomes.ao",
      phone = "+244 948 840 379",
      role = "Gerente Geral",
      barbershopName = "Barbearia Gomes",
      salt = salt1,
      passwordHash = hashWithSalt("Gomesbarber", salt1),
      quickPinHash = hashWithSalt("2526", salt1),
      createdAt = System.currentTimeMillis() - 86400000L * 30,
      lastLoginAt = System.currentTimeMillis()
    )

    // Remove any previous instance of user_esmeraldo or old number to ensure requested credentials take effect immediately
    accounts.removeAll { it.id == "user_esmeraldo" || it.email.equals("esmeraldo@barbeariagomes.ao", ignoreCase = true) || it.phone.filter { c -> c.isDigit() }.endsWith("948840379") }
    accounts.add(0, primaryManager)

    // 2. Master Barber Staff: Carlos Alberto
    val carlosExists = accounts.any { it.id == "user_carlos" || it.email.equals("carlos@barbeariagomes.ao", ignoreCase = true) }
    if (!carlosExists) {
      val salt2 = generateSalt()
      accounts.add(
        AccountRecord(
          id = "user_carlos",
          name = "Carlos Alberto",
          email = "carlos@barbeariagomes.ao",
          phone = "+244 924 111 222",
          role = "Barbeiro",
          barbershopName = "Barbearia Gomes",
          salt = salt2,
          passwordHash = hashWithSalt("barbeiro123", salt2),
          quickPinHash = hashWithSalt("4321", salt2),
          createdAt = System.currentTimeMillis() - 86400000L * 15,
          lastLoginAt = System.currentTimeMillis() - 3600000L * 4
        )
      )
    }

    saveAccounts(accounts)
    activeUserId = "user_esmeraldo"

    // If lastRememberedIdentifier was previously default or old, update to new primary phone
    val currentRemembered = prefs.getString("last_remembered_identifier", null)
    if (currentRemembered.isNullOrBlank() || currentRemembered.contains("esmeraldo@barbeariagomes.ao") || currentRemembered.contains("923")) {
      lastRememberedIdentifier = "+244 948 840 379"
    }
  }

  private fun loadAccounts(): MutableList<AccountRecord> {
    val jsonString = prefs.getString("registered_accounts_json", null) ?: return mutableListOf()
    val list = mutableListOf<AccountRecord>()
    try {
      val array = JSONArray(jsonString)
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          AccountRecord(
            id = obj.optString("id"),
            name = obj.optString("name"),
            email = obj.optString("email"),
            phone = obj.optString("phone"),
            role = obj.optString("role"),
            barbershopName = obj.optString("barbershopName", "Barbearia Gomes"),
            salt = obj.optString("salt"),
            passwordHash = obj.optString("passwordHash"),
            quickPinHash = obj.optString("quickPinHash"),
            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
            lastLoginAt = obj.optLong("lastLoginAt", System.currentTimeMillis())
          )
        )
      }
    } catch (_: Exception) {}
    return list
  }

  private fun saveAccounts(accounts: List<AccountRecord>) {
    val array = JSONArray()
    for (acc in accounts) {
      val obj = JSONObject().apply {
        put("id", acc.id)
        put("name", acc.name)
        put("email", acc.email)
        put("phone", acc.phone)
        put("role", acc.role)
        put("barbershopName", acc.barbershopName)
        put("salt", acc.salt)
        put("passwordHash", acc.passwordHash)
        put("quickPinHash", acc.quickPinHash)
        put("createdAt", acc.createdAt)
        put("lastLoginAt", acc.lastLoginAt)
      }
      array.put(obj)
    }
    prefs.edit().putString("registered_accounts_json", array.toString()).apply()
  }

  fun getCurrentUser(): AuthUser {
    val accounts = loadAccounts()
    val record = accounts.find { it.id == activeUserId } ?: accounts.firstOrNull()
    return if (record != null) {
      val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-AO")).format(Date(record.lastLoginAt))
      AuthUser(
        id = record.id,
        name = record.name,
        email = record.email,
        phone = record.phone,
        role = record.role,
        barbershopName = record.barbershopName,
        lastLoginFormatted = dateStr
      )
    } else {
      AuthUser(
        id = "user_esmeraldo",
        name = "Esmeraldo Tafinha",
        email = "esmeraldo@barbeariagomes.ao",
        phone = "+244 948 840 379",
        role = "Gerente Geral",
        barbershopName = "Barbearia Gomes",
        lastLoginFormatted = "Hoje"
      )
    }
  }

  private fun findAccountByIdentifier(identifier: String, accounts: List<AccountRecord>): AccountRecord? {
    val clean = identifier.trim()
    val cleanDigits = clean.filter { it.isDigit() }

    return accounts.find { acc ->
      if (acc.email.equals(clean, ignoreCase = true)) return@find true
      val accDigits = acc.phone.filter { it.isDigit() }
      if (cleanDigits.isNotEmpty() && accDigits.isNotEmpty()) {
        if (cleanDigits == accDigits) return@find true
        if (cleanDigits.endsWith(accDigits) || accDigits.endsWith(cleanDigits)) return@find true
      }
      false
    }
  }

  suspend fun authenticate(
    identifier: String,
    password: String,
    remember: Boolean
  ): Result<AuthUser> = withContext(Dispatchers.IO) {
    val cleanId = identifier.trim()
    if (cleanId.isBlank()) {
      return@withContext Result.failure(Exception("Por favor, informe seu e-mail ou telefone angolano."))
    }
    if (password.isBlank()) {
      return@withContext Result.failure(Exception("Por favor, digite sua senha de acesso."))
    }

    val accounts = loadAccounts()
    val account = findAccountByIdentifier(cleanId, accounts)

    if (account != null) {
      val computedHash = hashWithSalt(password, account.salt)
      if (computedHash == account.passwordHash) {
        // Successful login
        val updatedAccounts = accounts.map {
          if (it.id == account.id) it.copy(lastLoginAt = System.currentTimeMillis()) else it
        }
        saveAccounts(updatedAccounts)
        activeUserId = account.id
        isLoggedIn = true
        rememberMe = remember
        if (remember) {
          lastRememberedIdentifier = cleanId
        }

        val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-AO")).format(Date(System.currentTimeMillis()))
        return@withContext Result.success(
          AuthUser(
            id = account.id,
            name = account.name,
            email = account.email,
            phone = account.phone,
            role = account.role,
            barbershopName = account.barbershopName,
            lastLoginFormatted = dateStr
          )
        )
      } else {
        return@withContext Result.failure(Exception("Senha incorreta para esta credencial pessoal."))
      }
    }

    // Fallback: If Supabase Auth is configured, check online
    if (supabaseConfig.isConfigured && cleanId.contains("@")) {
      try {
        val url = "${supabaseConfig.url.trimEnd('/')}/auth/v1/token?grant_type=password"
        val payload = JSONObject().apply {
          put("email", cleanId)
          put("password", password)
        }
        val request = Request.Builder()
          .url(url)
          .addHeader("apikey", supabaseConfig.anonKey)
          .addHeader("Content-Type", "application/json")
          .post(payload.toString().toRequestBody(jsonMediaType))
          .build()

        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
          isLoggedIn = true
          rememberMe = remember
          if (remember) lastRememberedIdentifier = cleanId
          return@withContext Result.success(getCurrentUser())
        }
      } catch (_: Exception) {}
    }

    Result.failure(Exception("Credenciais pessoais não encontradas. Verifique seus dados ou registre-se."))
  }

  suspend fun verifyQuickPin(pin: String): Result<AuthUser> = withContext(Dispatchers.IO) {
    val cleanPin = pin.trim()
    if (cleanPin.length != 4 || !cleanPin.all { it.isDigit() }) {
      return@withContext Result.failure(Exception("O PIN de segurança pessoal deve conter exatamente 4 dígitos numéricos."))
    }

    val accounts = loadAccounts()
    // Find candidate account (active or last remembered), or match any authorized account by PIN
    var matchedAccount: AccountRecord? = null
    var candidate = accounts.find { it.id == activeUserId }
    if (candidate == null && lastRememberedIdentifier.isNotBlank()) {
      candidate = findAccountByIdentifier(lastRememberedIdentifier, accounts)
    }
    if (candidate != null && hashWithSalt(cleanPin, candidate.salt) == candidate.quickPinHash) {
      matchedAccount = candidate
    } else {
      matchedAccount = accounts.find { hashWithSalt(cleanPin, it.salt) == it.quickPinHash }
    }

    if (matchedAccount != null) {
      val updatedAccounts = accounts.map {
        if (it.id == matchedAccount.id) it.copy(lastLoginAt = System.currentTimeMillis()) else it
      }
      saveAccounts(updatedAccounts)
      activeUserId = matchedAccount.id
      isLoggedIn = true
      lastRememberedIdentifier = matchedAccount.email
      val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-AO")).format(Date(System.currentTimeMillis()))
      return@withContext Result.success(
        AuthUser(
          id = matchedAccount.id,
          name = matchedAccount.name,
          email = matchedAccount.email,
          phone = matchedAccount.phone,
          role = matchedAccount.role,
          barbershopName = matchedAccount.barbershopName,
          lastLoginFormatted = dateStr
        )
      )
    }

    Result.failure(Exception("PIN de 4 dígitos incorreto ou não cadastrado."))
  }

  suspend fun registerNewAccount(
    fullName: String,
    identifier: String,
    password: String,
    role: String = "Barbeiro",
    pin: String = "1234"
  ): Result<AuthUser> = withContext(Dispatchers.IO) {
    val cleanName = fullName.trim()
    val cleanId = identifier.trim()
    val cleanPass = password.trim()
    val cleanPin = if (pin.trim().length == 4 && pin.trim().all { it.isDigit() }) pin.trim() else "1234"

    if (cleanName.isBlank()) {
      return@withContext Result.failure(Exception("Nome completo é obrigatório."))
    }
    if (cleanPass.length < 6) {
      return@withContext Result.failure(Exception("A senha pessoal deve conter pelo menos 6 caracteres."))
    }

    val accounts = loadAccounts()
    if (findAccountByIdentifier(cleanId, accounts) != null) {
      return@withContext Result.failure(Exception("Já existe uma conta registrada com este e-mail ou telefone."))
    }

    val email = if (cleanId.contains("@")) cleanId else "${cleanName.lowercase().replace(" ", "")}@barbeariagomes.ao"
    val phone = if (cleanId.all { it.isDigit() || it == '+' || it == ' ' }) formatAngolanPhone(cleanId) else "+244 923 000 000"

    val newSalt = generateSalt()
    val newId = "user_${System.currentTimeMillis()}"
    val newRecord = AccountRecord(
      id = newId,
      name = cleanName,
      email = email,
      phone = phone,
      role = role,
      barbershopName = "Barbearia Gomes",
      salt = newSalt,
      passwordHash = hashWithSalt(cleanPass, newSalt),
      quickPinHash = hashWithSalt(cleanPin, newSalt),
      createdAt = System.currentTimeMillis(),
      lastLoginAt = System.currentTimeMillis()
    )

    accounts.add(newRecord)
    saveAccounts(accounts)

    activeUserId = newId
    isLoggedIn = true
    rememberMe = true
    lastRememberedIdentifier = cleanId

    // Push to Supabase Auth if configured
    if (supabaseConfig.isConfigured && cleanId.contains("@")) {
      try {
        val url = "${supabaseConfig.url.trimEnd('/')}/auth/v1/signup"
        val payload = JSONObject().apply {
          put("email", email)
          put("password", cleanPass)
          put("data", JSONObject().apply {
            put("full_name", cleanName)
            put("role", role)
          })
        }
        val request = Request.Builder()
          .url(url)
          .addHeader("apikey", supabaseConfig.anonKey)
          .addHeader("Content-Type", "application/json")
          .post(payload.toString().toRequestBody(jsonMediaType))
          .build()

        httpClient.newCall(request).execute()
      } catch (_: Exception) {}
    }

    val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-AO")).format(Date(System.currentTimeMillis()))
    Result.success(
      AuthUser(
        id = newRecord.id,
        name = newRecord.name,
        email = newRecord.email,
        phone = newRecord.phone,
        role = newRecord.role,
        barbershopName = newRecord.barbershopName,
        lastLoginFormatted = dateStr
      )
    )
  }

  suspend fun changePassword(currentPass: String, newPass: String): Result<Unit> = withContext(Dispatchers.IO) {
    if (newPass.trim().length < 6) {
      return@withContext Result.failure(Exception("A nova senha deve ter pelo menos 6 caracteres."))
    }
    val accounts = loadAccounts()
    val userIndex = accounts.indexOfFirst { it.id == activeUserId }
    if (userIndex == -1) {
      return@withContext Result.failure(Exception("Usuário ativo não encontrado."))
    }

    val user = accounts[userIndex]
    val oldHash = hashWithSalt(currentPass.trim(), user.salt)
    if (oldHash != user.passwordHash) {
      return@withContext Result.failure(Exception("A senha atual digitada está incorreta."))
    }

    val freshSalt = generateSalt()
    val updatedUser = user.copy(
      salt = freshSalt,
      passwordHash = hashWithSalt(newPass.trim(), freshSalt),
      quickPinHash = hashWithSalt(extractPinFromOld(user), freshSalt)
    )

    accounts[userIndex] = updatedUser
    saveAccounts(accounts)
    Result.success(Unit)
  }

  private fun extractPinFromOld(user: AccountRecord): String {
    // If PIN hash cannot be reversed, keep default or update cleanly
    return "1234"
  }

  suspend fun changeQuickPin(newPin: String): Result<Unit> = withContext(Dispatchers.IO) {
    val clean = newPin.trim()
    if (clean.length != 4 || !clean.all { it.isDigit() }) {
      return@withContext Result.failure(Exception("O novo PIN deve conter exatamente 4 dígitos numéricos."))
    }

    val accounts = loadAccounts()
    val userIndex = accounts.indexOfFirst { it.id == activeUserId }
    if (userIndex == -1) {
      return@withContext Result.failure(Exception("Usuário ativo não encontrado."))
    }

    val user = accounts[userIndex]
    val updatedUser = user.copy(
      quickPinHash = hashWithSalt(clean, user.salt)
    )
    accounts[userIndex] = updatedUser
    saveAccounts(accounts)
    Result.success(Unit)
  }

  suspend fun updatePersonalProfile(name: String, phone: String, email: String): Result<AuthUser> = withContext(Dispatchers.IO) {
    val accounts = loadAccounts()
    val userIndex = accounts.indexOfFirst { it.id == activeUserId }
    if (userIndex == -1) {
      return@withContext Result.failure(Exception("Usuário ativo não encontrado."))
    }

    val user = accounts[userIndex]
    val updatedUser = user.copy(
      name = name.trim().ifBlank { user.name },
      phone = phone.trim().ifBlank { user.phone },
      email = email.trim().ifBlank { user.email }
    )
    accounts[userIndex] = updatedUser
    saveAccounts(accounts)

    Result.success(
      AuthUser(
        id = updatedUser.id,
        name = updatedUser.name,
        email = updatedUser.email,
        phone = updatedUser.phone,
        role = updatedUser.role,
        barbershopName = updatedUser.barbershopName,
        lastLoginFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-AO")).format(Date(updatedUser.lastLoginAt))
      )
    )
  }

  fun logout() {
    isLoggedIn = false
    if (!rememberMe) {
      lastRememberedIdentifier = ""
    }
  }

  companion object {
    fun hashWithSalt(input: String, salt: String): String {
      val digest = MessageDigest.getInstance("SHA-256")
      val combined = "$salt:$input"
      val hashBytes = digest.digest(combined.toByteArray(Charsets.UTF_8))
      return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun generateSalt(): String {
      val random = SecureRandom()
      val bytes = ByteArray(16)
      random.nextBytes(bytes)
      return bytes.joinToString("") { "%02x".format(it) }
    }

    fun checkCredentialType(input: String): CredentialType {
      val trimmed = input.trim()
      val emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)\$")
      if (emailPattern.matcher(trimmed).matches()) {
        return CredentialType.EMAIL
      }

      val digitsOnly = trimmed.filter { it.isDigit() }
      // Angolan numbers start with 9 and have 9 digits, or +244 with 9 digits (total 12 digits)
      if (digitsOnly.length == 9 && digitsOnly.startsWith("9")) {
        return CredentialType.ANGOLA_PHONE
      }
      if (digitsOnly.length == 12 && digitsOnly.startsWith("2449")) {
        return CredentialType.ANGOLA_PHONE
      }

      return if (trimmed.length >= 3) CredentialType.EMAIL else CredentialType.INVALID
    }

    fun calculatePasswordStrength(pass: String): PasswordStrength {
      if (pass.length < 6) return PasswordStrength.FRACA
      var score = 0
      if (pass.any { it.isDigit() }) score++
      if (pass.any { it.isUpperCase() }) score++
      if (pass.any { !it.isLetterOrDigit() }) score++
      if (pass.length >= 8) score++

      return when {
        score >= 3 -> PasswordStrength.FORTE
        score >= 1 -> PasswordStrength.MEDIA
        else -> PasswordStrength.FRACA
      }
    }

    fun formatAngolanPhone(raw: String): String {
      val digits = raw.filter { it.isDigit() }
      return when {
        digits.startsWith("244") && digits.length >= 12 -> {
          "+244 ${digits.substring(3, 6)} ${digits.substring(6, 9)} ${digits.substring(9)}"
        }
        digits.length == 9 -> {
          "+244 ${digits.substring(0, 3)} ${digits.substring(3, 6)} ${digits.substring(6)}"
        }
        else -> raw
      }
    }
  }
}
