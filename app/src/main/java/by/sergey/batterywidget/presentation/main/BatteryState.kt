package by.sergey.batterywidget.presentation.main

import androidx.compose.ui.graphics.Color
import by.sergey.batterywidget.presentation.theme.BatteryFullGreen

data class BatteryState(
    val level: Int = 0,
    val voltage: String = "",
    val health: String = "",
    val technology: String = "",
    val temperature: String = "",
    val chargingStatus: String = "",
    val isCharging: Boolean = false,
    val levelColor: Color = BatteryFullGreen
)
