package com.example.data.remote

import android.util.Log
import com.example.data.local.AppointmentEntity
import com.example.data.local.ClientEntity
import com.example.data.local.ServiceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SupabaseClient(private val config: SupabaseConfig) {

  private val client = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .writeTimeout(15, TimeUnit.SECONDS)
    .build()

  private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

  private fun getCleanBaseUrl(): String {
    var raw = config.url.trimEnd('/')
    if (!raw.startsWith("http://") && !raw.startsWith("https://")) {
      raw = "https://$raw"
    }
    return raw
  }

  suspend fun testConnection(): Result<String> = withContext(Dispatchers.IO) {
    if (!config.isConfigured) {
      return@withContext Result.failure(Exception("Supabase URL ou Anon Key não configurados."))
    }
    try {
      val url = "${getCleanBaseUrl()}/rest/v1/"
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", config.anonKey)
        .addHeader("Authorization", "Bearer ${config.anonKey}")
        .get()
        .build()

      val response = client.newCall(request).execute()
      if (response.isSuccessful || response.code in 200..399) {
        Result.success("Conexão com Supabase estabelecida com sucesso!")
      } else {
        Result.failure(Exception("Supabase retornou código ${response.code}: ${response.message}"))
      }
    } catch (e: Exception) {
      Log.e("SupabaseClient", "Erro de conexão", e)
      Result.failure(Exception("Falha ao conectar: ${e.localizedMessage ?: e.message}"))
    }
  }

  // --- CLIENTS ---
  suspend fun pushClient(clientEntity: ClientEntity): Result<Unit> = withContext(Dispatchers.IO) {
    if (!config.isConfigured) return@withContext Result.success(Unit)
    try {
      val json = JSONObject().apply {
        put("id", clientEntity.id)
        put("name", clientEntity.name)
        put("phone", clientEntity.phone)
        put("notes", clientEntity.notes)
        put("last_service", clientEntity.lastService)
        put("last_service_date", clientEntity.lastServiceDate)
        put("total_spent_kz", clientEntity.totalSpentKz)
        put("created_at", clientEntity.createdAt)
      }

      val url = "${getCleanBaseUrl()}/rest/v1/clients"
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", config.anonKey)
        .addHeader("Authorization", "Bearer ${config.anonKey}")
        .addHeader("Prefer", "resolution=merge-duplicates")
        .post(json.toString().toRequestBody(jsonMediaType))
        .build()

      val resp = client.newCall(request).execute()
      if (resp.isSuccessful) Result.success(Unit)
      else Result.failure(Exception("HTTP ${resp.code}: ${resp.message}"))
    } catch (e: Exception) {
      Log.w("SupabaseClient", "pushClient error: ${e.message}")
      Result.failure(e)
    }
  }

  suspend fun deleteClientRemote(id: String): Result<Unit> = withContext(Dispatchers.IO) {
    if (!config.isConfigured) return@withContext Result.success(Unit)
    try {
      val url = "${getCleanBaseUrl()}/rest/v1/clients?id=eq.$id"
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", config.anonKey)
        .addHeader("Authorization", "Bearer ${config.anonKey}")
        .delete()
        .build()

      val resp = client.newCall(request).execute()
      if (resp.isSuccessful) Result.success(Unit)
      else Result.failure(Exception("HTTP ${resp.code}"))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun fetchClientsRemote(): Result<List<ClientEntity>> = withContext(Dispatchers.IO) {
    if (!config.isConfigured) return@withContext Result.success(emptyList())
    try {
      val url = "${getCleanBaseUrl()}/rest/v1/clients?select=*"
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", config.anonKey)
        .addHeader("Authorization", "Bearer ${config.anonKey}")
        .get()
        .build()

      val resp = client.newCall(request).execute()
      if (!resp.isSuccessful) return@withContext Result.failure(Exception("HTTP ${resp.code}"))

      val body = resp.body?.string() ?: "[]"
      val array = JSONArray(body)
      val list = mutableListOf<ClientEntity>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          ClientEntity(
            id = obj.optString("id", System.currentTimeMillis().toString()),
            name = obj.optString("name", "Cliente"),
            phone = obj.optString("phone", ""),
            notes = obj.optString("notes", ""),
            lastService = obj.optString("last_service", ""),
            lastServiceDate = obj.optString("last_service_date", ""),
            totalSpentKz = obj.optLong("total_spent_kz", 0L),
            createdAt = obj.optLong("created_at", System.currentTimeMillis())
          )
        )
      }
      Result.success(list)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  // --- APPOINTMENTS (AGENDA) ---
  suspend fun pushAppointment(appointment: AppointmentEntity): Result<Unit> = withContext(Dispatchers.IO) {
    if (!config.isConfigured) return@withContext Result.success(Unit)
    try {
      val json = JSONObject().apply {
        put("id", appointment.id)
        put("client_id", appointment.clientId)
        put("client_name", appointment.clientName)
        put("service_id", appointment.serviceId)
        put("service_name", appointment.serviceName)
        put("service_price", appointment.servicePrice)
        put("extra_value", appointment.extraValue)
        put("total_price", appointment.totalPrice)
        put("date_ymd", appointment.dateYmd)
        put("time_hm", appointment.timeHm)
        put("status", appointment.status)
        put("payment_method", appointment.paymentMethod)
        put("created_at", appointment.createdAt)
      }

      val url = "${getCleanBaseUrl()}/rest/v1/appointments"
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", config.anonKey)
        .addHeader("Authorization", "Bearer ${config.anonKey}")
        .addHeader("Prefer", "resolution=merge-duplicates")
        .post(json.toString().toRequestBody(jsonMediaType))
        .build()

      val resp = client.newCall(request).execute()
      if (resp.isSuccessful) Result.success(Unit)
      else Result.failure(Exception("HTTP ${resp.code}"))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun deleteAppointmentRemote(id: String): Result<Unit> = withContext(Dispatchers.IO) {
    if (!config.isConfigured) return@withContext Result.success(Unit)
    try {
      val url = "${getCleanBaseUrl()}/rest/v1/appointments?id=eq.$id"
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", config.anonKey)
        .addHeader("Authorization", "Bearer ${config.anonKey}")
        .delete()
        .build()

      val resp = client.newCall(request).execute()
      if (resp.isSuccessful) Result.success(Unit)
      else Result.failure(Exception("HTTP ${resp.code}"))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  // --- SERVICES ---
  suspend fun pushService(service: ServiceEntity): Result<Unit> = withContext(Dispatchers.IO) {
    if (!config.isConfigured) return@withContext Result.success(Unit)
    try {
      val json = JSONObject().apply {
        put("id", service.id)
        put("name", service.name)
        put("price_kz", service.priceKz)
        put("icon_type", service.iconType)
        put("is_active", service.isActive)
        put("sort_order", service.sortOrder)
      }

      val url = "${getCleanBaseUrl()}/rest/v1/services"
      val request = Request.Builder()
        .url(url)
        .addHeader("apikey", config.anonKey)
        .addHeader("Authorization", "Bearer ${config.anonKey}")
        .addHeader("Prefer", "resolution=merge-duplicates")
        .post(json.toString().toRequestBody(jsonMediaType))
        .build()

      val resp = client.newCall(request).execute()
      if (resp.isSuccessful) Result.success(Unit)
      else Result.failure(Exception("HTTP ${resp.code}"))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
