package com.example

import com.example.data.auth.AuthSessionManager
import com.example.data.auth.CredentialType
import com.example.data.auth.PasswordStrength
import com.example.data.automation.AutomationManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testAngolanPhoneFormatting() {
    val rawPhone = "923456789"
    val formatted = AuthSessionManager.formatAngolanPhone(rawPhone)
    assertEquals("+244 923 456 789", formatted)
  }

  @Test
  fun testCredentialTypeDetection() {
    val emailType = AuthSessionManager.checkCredentialType("esmeraldo@barbeariagomes.ao")
    assertEquals(CredentialType.EMAIL, emailType)

    val phoneType = AuthSessionManager.checkCredentialType("+244 923 456 789")
    assertEquals(CredentialType.ANGOLA_PHONE, phoneType)
  }

  @Test
  fun testPasswordStrength() {
    val weak = AuthSessionManager.calculatePasswordStrength("123")
    assertEquals(PasswordStrength.FRACA, weak)

    val medium = AuthSessionManager.calculatePasswordStrength("admin123")
    assertEquals(PasswordStrength.MEDIA, medium)

    val strong = AuthSessionManager.calculatePasswordStrength("Gomes@2026!Sec")
    assertEquals(PasswordStrength.FORTE, strong)
  }

  @Test
  fun testSaltedSha256Hashing() {
    val salt = AuthSessionManager.generateSalt()
    assertEquals(32, salt.length) // 16 bytes = 32 hex chars

    val hash1 = AuthSessionManager.hashWithSalt("admin123", salt)
    val hash2 = AuthSessionManager.hashWithSalt("admin123", salt)
    // Deterministic for same salt
    assertEquals(hash1, hash2)
    assertEquals(64, hash1.length) // SHA-256 = 64 hex characters

    // Different salts produce different hashes
    val anotherSalt = AuthSessionManager.generateSalt()
    val hashAnother = AuthSessionManager.hashWithSalt("admin123", anotherSalt)
    assertNotEquals(hash1, hashAnother)

    // Wrong password produces different hash
    val wrongPassHash = AuthSessionManager.hashWithSalt("wrongPass", salt)
    assertNotEquals(hash1, wrongPassHash)
  }

  @Test
  fun testQuickPinHashing() {
    val salt = "test_salt_123456"
    val pinHash = AuthSessionManager.hashWithSalt("1234", salt)
    val verifyGood = AuthSessionManager.hashWithSalt("1234", salt)
    val verifyBad = AuthSessionManager.hashWithSalt("9999", salt)

    assertEquals(pinHash, verifyGood)
    assertNotEquals(pinHash, verifyBad)
  }

  @Test
  fun testAutomationCommissions() {
    // 50% split of 100,000 Kz
    val calc = AutomationManager.calculateCommissions(
      totalRevenue = 100_000L,
      completedCount = 20,
      ratePercent = 50
    )
    assertEquals(50_000L, calc.barberShareKz)
    assertEquals(50_000L, calc.barbershopShareKz)
    assertEquals(20, calc.completedServicesCount)
  }

  @Test
  fun testWhatsAppReminderGeneration() {
    val reminder = AutomationManager.generateClientReminder(
      clientName = "Esmeraldo Tafinha",
      serviceName = "Corte sem pintura",
      totalPrice = 500L,
      timeHm = "14:30",
      barbershopName = "Barbearia Gomes"
    )
    assertTrue(reminder.contains("Esmeraldo Tafinha"))
    assertTrue(reminder.contains("Barbearia Gomes"))
    assertTrue(reminder.contains("14:30"))
    assertTrue(reminder.contains("500"))
  }

  @Test
  fun testPrimaryManagerCredentialsVerification() {
    val phone = "+244 948 840 379"
    val formatted = AuthSessionManager.formatAngolanPhone("948840379")
    assertEquals(phone, formatted)
    assertEquals(CredentialType.ANGOLA_PHONE, AuthSessionManager.checkCredentialType(phone))

    val salt = AuthSessionManager.generateSalt()
    val passHash = AuthSessionManager.hashWithSalt("Gomesbarber", salt)
    val pinHash = AuthSessionManager.hashWithSalt("2526", salt)

    assertEquals(passHash, AuthSessionManager.hashWithSalt("Gomesbarber", salt))
    assertEquals(pinHash, AuthSessionManager.hashWithSalt("2526", salt))
  }
}
