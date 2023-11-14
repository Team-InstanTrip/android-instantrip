package com.instantrip.util

import android.content.Context
import android.content.SharedPreferences
import timber.log.Timber

object PreferenceUtil {
    private const val PREF_KEY = "instantrip"
    private var prefs : SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            Timber.d("prefs 초기화")
            prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
        }
    }

    fun setString(key: String, value: String) {
        prefs?.edit()?.putString(key, value)?.apply()
    }

    fun getString(key: String, defaultValue: String): String? {
        return prefs?.getString(key, defaultValue)
    }

    fun setBoolean(key: String, value: Boolean) {
        prefs?.edit()?.putBoolean(key, value)?.apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean? {
        return prefs?.getBoolean(key, defaultValue)
    }
}