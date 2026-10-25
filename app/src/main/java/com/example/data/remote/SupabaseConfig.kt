package com.example.data.remote

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig

class SupabaseConfig(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("supabase_prefs", Context.MODE_PRIVATE)

  var url: String
    get() {
      val saved = prefs.getString("supabase_url", null)
      return if (!saved.isNullOrBlank()) {
        saved
      } else {
        try {
          BuildConfig.SUPABASE_URL
        } catch (e: Exception) {
          "https://xyzcompany.supabase.co"
        }
      }
    }
    set(value) {
      prefs.edit().putString("supabase_url", value.trim()).apply()
    }

  var anonKey: String
    get() {
      val saved = prefs.getString("supabase_anon_key", null)
      return if (!saved.isNullOrBlank()) {
        saved
      } else {
        try {
          BuildConfig.SUPABASE_ANON_KEY
        } catch (e: Exception) {
          ""
        }
      }
    }
    set(value) {
      prefs.edit().putString("supabase_anon_key", value.trim()).apply()
    }

  val isConfigured: Boolean
    get() = url.isNotBlank() &&
      !url.contains("xyzcompany.supabase.co") &&
      !url.contains("your-project-ref") &&
      anonKey.isNotBlank() &&
      anonKey.length > 20
}
