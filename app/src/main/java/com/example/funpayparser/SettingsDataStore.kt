package com.example.funpayparser

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore by preferencesDataStore("settings")

data class SettingsData(
    val refreshTime: Int,
    val minQuantity: Int,
    val maxPrice: Double,
    val minReviews: Int
)

object SettingsKeys {
    val refreshTime = intPreferencesKey("refreshTime")
    val minQuantity = intPreferencesKey("minQuantity")
    val maxPrice = doublePreferencesKey("maxPrice")
    val minReviews = intPreferencesKey("minReviews")
}

fun getSettings(context: Context): Flow<SettingsData> {
    return context.settingsDataStore.data.map { prefs ->
        SettingsData(
            refreshTime = prefs[SettingsKeys.refreshTime] ?: 30,
            minQuantity = prefs[SettingsKeys.minQuantity] ?: 0,
            maxPrice = prefs[SettingsKeys.maxPrice] ?: 1.0,
            minReviews = prefs[SettingsKeys.minReviews] ?: 0
        )
    }
}

suspend fun saveSettings(context: Context, settings: SettingsData) {
    context.settingsDataStore.edit { prefs ->
        prefs[SettingsKeys.refreshTime] = settings.refreshTime
        prefs[SettingsKeys.minQuantity] = settings.minQuantity
        prefs[SettingsKeys.maxPrice] = settings.maxPrice
        prefs[SettingsKeys.minReviews] = settings.minReviews
    }
}