package by.sergey.batterywidget.domain.model

data class BatteryInfo(
    val level: Int,
    val voltage: Int,
    val health: String,
    val technology: String,
    val temperature: Float,
    val chargingStatus: String,
    val isCharging: Boolean
)
