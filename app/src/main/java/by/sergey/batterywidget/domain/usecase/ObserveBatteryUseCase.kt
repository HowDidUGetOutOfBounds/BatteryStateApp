package by.sergey.batterywidget.domain.usecase

import by.sergey.batterywidget.domain.model.BatteryInfo
import by.sergey.batterywidget.domain.repository.BatteryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBatteryUseCase @Inject constructor(
    private val repository: BatteryRepository
) {
    operator fun invoke(): Flow<BatteryInfo> = repository.observeBatteryStatus()
}
