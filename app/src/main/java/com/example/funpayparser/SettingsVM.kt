package com.example.funpayparser

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsVM(application: Application): AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext
    var refreshTime = mutableIntStateOf(30)
        private set
    var minQuantity = mutableIntStateOf(0)
        private set
    var maxPrice = mutableDoubleStateOf(1.0)
        private set
    var minReviews = mutableIntStateOf(0)
        private set

    fun refreshSettings() {
        viewModelScope.launch {
            getSettings(context).collect { data ->
                refreshTime.intValue = data.refreshTime
                minQuantity.intValue = data.minQuantity
                maxPrice.doubleValue = data.maxPrice
                minReviews.intValue = data.minReviews
            }
        }
    }

    fun updateSettings(
                       refreshTime: Int? = null,
                       minQuantity: Int? = null,
                       maxPrice: Double? = null,
                       minReviews: Int? = null
    ) {
        viewModelScope.launch {
            context.settingsDataStore.edit { prefs ->
                refreshTime?.let { prefs[SettingsKeys.refreshTime] = it }
                minQuantity?.let { prefs[SettingsKeys.minQuantity] = it }
                maxPrice?.let { prefs[SettingsKeys.maxPrice] = it }
                minReviews?.let { prefs[SettingsKeys.minReviews] = it }
            }
        }
        refreshSettings()
    }

    init {
        refreshSettings()
    }
}