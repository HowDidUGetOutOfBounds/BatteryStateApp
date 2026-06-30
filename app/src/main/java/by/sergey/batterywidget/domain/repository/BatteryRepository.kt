package by.sergey.batterywidget.domain.repository

import by.sergey.batterywidget.domain.model.BatteryInfo
import kotlinx.coroutines.flow.Flow

interface BatteryRepository {
    fun observeBatteryStatus(): Flow<BatteryInfo>
}
