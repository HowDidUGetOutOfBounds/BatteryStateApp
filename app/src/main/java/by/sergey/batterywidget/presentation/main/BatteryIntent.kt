package by.sergey.batterywidget.presentation.main

sealed interface BatteryIntent {
    data object Refresh : BatteryIntent
}
