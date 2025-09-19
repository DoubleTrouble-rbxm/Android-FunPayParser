package com.example.funpayparser

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(padding: PaddingValues) {
    val vm: SettingsVM = viewModel()
    Column(
        modifier = Modifier.padding(padding).fillMaxSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            var text by remember { mutableStateOf("") }
            var hasError by remember { mutableStateOf(true) }
            var isFocused by remember { mutableStateOf(false) }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
                value = text,
                label = { Text("Время обновления (сек)") },
                onValueChange = { newText: String ->
                    text = newText
                    hasError = text.toIntOrNull() == null || text.toIntOrNull()!! <= 0
                                },
                placeholder = { Text("${vm.refreshTime.intValue}") },
                singleLine = true,
                trailingIcon = { AnimatedIconButton({ vm.updateSettings(refreshTime = text.toInt()) }, !hasError) },
                supportingText = {
                    if (hasError && isFocused) {
                        Text("Нужно положительное число.")
                    }
                }
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            var text by remember { mutableStateOf("") }
            var hasError by remember { mutableStateOf(true) }
            var isFocused by remember { mutableStateOf(false) }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
                value = text,
                label = { Text("Мининмальное количество (шт.)") },
                onValueChange = { newText: String ->
                    text = newText
                    hasError = text.toIntOrNull() == null || text.toIntOrNull()!! < 0
                },
                placeholder = { Text("${vm.minQuantity.intValue}") },
                singleLine = true,
                trailingIcon = { AnimatedIconButton({ vm.updateSettings(minQuantity = text.toInt()) }, !hasError) },
                supportingText = {
                    if (hasError && isFocused) {
                        Text("Нужно неотрицательное число.")
                    }
                }
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            var text by remember { mutableStateOf("") }
            var hasError by remember { mutableStateOf(true) }
            var isFocused by remember { mutableStateOf(false) }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
                value = text,
                label = { Text("Максимальная цена (₽)") },
                onValueChange = { newText: String ->
                    text = newText
                    hasError = text.toDoubleOrNull() == null || text.toDoubleOrNull()!! <= 0
                },
                placeholder = { Text("${vm.maxPrice.doubleValue}") },
                singleLine = true,
                trailingIcon = { AnimatedIconButton({ vm.updateSettings(maxPrice = text.toDouble()) }, !hasError) },
                supportingText = {
                    if (hasError && isFocused) {
                        Text("Нужно положительное число.")
                    }
                }
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            var text by remember { mutableStateOf("") }
            var hasError by remember { mutableStateOf(true) }
            var isFocused by remember { mutableStateOf(false) }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
                value = text,
                label = { Text("Мининмальное кол-во отзывов (шт.)") },
                onValueChange = { newText: String ->
                    text = newText
                    hasError = text.toIntOrNull() == null || text.toIntOrNull()!! < 0
                },
                placeholder = { Text("${vm.minReviews.intValue}") },
                singleLine = true,
                trailingIcon = { AnimatedIconButton({ vm.updateSettings(minReviews = text.toInt()) }, !hasError) },
                supportingText = {
                    if (hasError && isFocused) {
                        Text("Нужно неотрицательное число.")
                    }
                }
            )
        }
    }
}

@Composable
fun AnimatedIconButton(onClick: () -> Unit, isActive: Boolean) {
    var showCheck by remember { mutableStateOf(false) }
    IconButton(onClick = { onClick(); showCheck = true }, enabled = isActive, colors = IconButtonColors(Color.Transparent, Color.Blue, Color.Transparent, Color.Gray)) {
        Crossfade(targetState = showCheck, label = "") { state ->
            if (state) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Сохранено",
                    tint = Color.Green
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Сохранить"
                )
            }
        }
    }

    if (showCheck) {
        LaunchedEffect(showCheck) {
            delay(2000L)
            showCheck = false
        }
    }
}
