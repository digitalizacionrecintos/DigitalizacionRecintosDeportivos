package org.example.project.domain.manager

import android.content.Context
import android.content.SharedPreferences

private lateinit var sharedPreferences: SharedPreferences

fun initPreferences(context: Context) {
    sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
}

actual object PreferencesStorage {
    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return try {
            sharedPreferences.getBoolean(key, defaultValue)
        } catch (e: Exception) {
            defaultValue
        }
    }

    actual fun setBoolean(key: String, value: Boolean) {
        try {
            sharedPreferences.edit().putBoolean(key, value).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
