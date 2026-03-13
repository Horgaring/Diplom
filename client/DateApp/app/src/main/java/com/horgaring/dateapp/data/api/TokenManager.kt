package com.horgaring.dateapp.data.api

import android.content.Context
import com.horgaring.dateapp.DateApp

object TokenManager {

    private const val PREFS_NAME = "dateapp_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_EMAIL = "user_email"
    private const val KEY_FIRST_NAME = "user_first_name"

    private val prefs by lazy {
        DateApp.instance.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_TOKEN, value).apply()

    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    var email: String?
        get() = prefs.getString(KEY_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_EMAIL, value).apply()

    var firstName: String?
        get() = prefs.getString(KEY_FIRST_NAME, null)
        set(value) = prefs.edit().putString(KEY_FIRST_NAME, value).apply()

    val isLoggedIn: Boolean
        get() = token != null

    fun clear() {
        prefs.edit().clear().apply()
    }
}
