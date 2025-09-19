package com.example.funpayparser

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.funpayparser.ui.theme.Purple40
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LotsVMFactory(
    private val application: Application,
    private val settingsVM: SettingsVM
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LotsVM::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LotsVM(application, settingsVM) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class LotsUIState {
    object Loading: LotsUIState()
    data class Success(val lots: List<Lot>): LotsUIState()
    data class Error(val message: String): LotsUIState()
}

@SuppressLint("StaticFieldLeak")
class LotsVM(application: Application, private val settingsVM: SettingsVM) : AndroidViewModel(application) {
    private val context = application.applicationContext
    var refreshTimer = mutableStateOf(30)
        private set
    var isButtonEnabled = mutableStateOf(true)
        private set
    var uiState = mutableStateOf<LotsUIState>(LotsUIState.Loading)
        private set

    private var autoRefreshJob: Job? = null

    fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                loadLots()
                resetTimer()
                while (isActive && refreshTimer.value > 0) {
                    delay(1000L)
                    refreshTimer.value -= 1
                }
            }
        }
    }

    fun loadLots() {
        viewModelScope.launch {
            uiState.value = LotsUIState.Loading
            isButtonEnabled.value = false
            try {
                val parsed = withContext(Dispatchers.IO) {
                    lotsParser()
                }
                uiState.value = LotsUIState.Success(filterLots(parsed))
                isButtonEnabled.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                uiState.value = LotsUIState.Error(e.message ?: "Неизвестная ошибка")
                isButtonEnabled.value = true
            }
        }
    }

    fun filterLots(lots: List<Lot>) = lots.filter {
        it.price <= settingsVM.maxPrice.doubleValue &&
                it.quantity >= settingsVM.minQuantity.intValue &&
                it.reviewsCount >= settingsVM.minReviews.intValue
    }

    init {
        startAutoRefresh()
    }

    fun resetTimer() {
        viewModelScope.launch {
            getSettings(context).collect { data ->
                refreshTimer.value = data.refreshTime
            }
        }
    }
}

@Composable
fun LotsBox(vm: LotsVM = viewModel(), padding: PaddingValues) {
    Column(modifier = Modifier.fillMaxSize().padding(padding), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            Text("До обновления: ${vm.refreshTimer.value} сек")
            IconButton(
                onClick = { vm.startAutoRefresh() },
                colors = IconButtonColors(
                    Color.Transparent,
                    Color.Black,
                    Color.Transparent,
                    Color.Gray
                ),
                enabled = vm.isButtonEnabled.value
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Обновить")
            }
        }
        Spacer(Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().border(BorderStroke(2.dp, Color.Black)), contentAlignment = Alignment.Center) {
            when (val state = vm.uiState.value) {
                is LotsUIState.Loading -> {
                    Column {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(4.dp))
                        Text("Загрузка...")
                    }
                }
                is LotsUIState.Success -> {
                    LotsColumn(state.lots)
                }
                is LotsUIState.Error -> {
                    Text("Ошибка: ${state.message}")
                }
            }
        }
    }
}

@Composable
fun LotsColumn(lots: List<Lot>) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.height(400.dp).fillMaxWidth()
                .background(Color.LightGray)
        ) {
            items(lots) { lot ->
                LotCard(lot)
                HorizontalDivider(thickness = 1.dp)
            }
        }
    }
}

@Composable
fun LotCard(lot: Lot) {
    val uriHandler = LocalUriHandler.current
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
            Text("${lot.quantity} R💲 - ${lot.price}₽", fontWeight = FontWeight.Bold)
            Text("${lot.merchant} ⭐ ${lot.reviewsCount} отзывов", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
        FilledIconButton(onClick = { uriHandler.openUri(lot.link) }, modifier = Modifier.align(Alignment.CenterVertically), colors = IconButtonColors(Purple40, Color.White, Color.Black, Color.Gray), shape = RoundedCornerShape(12.dp)) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Купить",
            )
        }
    }
}
@Composable
fun MainScreen(application: Application, padding: PaddingValues) {
    val settingsVM: SettingsVM = viewModel()
    val lotsVM: LotsVM = viewModel(factory = LotsVMFactory(application, settingsVM))

    LotsBox(vm = lotsVM, padding = padding)
}