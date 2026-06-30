package by.sergey.batterywidget.data

import android.app.Application
import android.content.Intent
import android.os.BatteryManager
import by.sergey.batterywidget.R
import by.sergey.batterywidget.domain.model.BatteryInfo
import by.sergey.batterywidget.domain.repository.BatteryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BatteryRepositoryImpl @Inject constructor(
    private val dataSource: BatteryStatusDataSource,
    private val app: Application
) : BatteryRepository {

    override fun observeBatteryStatus(): Flow<BatteryInfo> =
        dataSource.observe().map { intent -> mapToDomain(intent) }

    private fun mapToDomain(intent: Intent): BatteryInfo {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
        return BatteryInfo(
            level = level * 100 / scale,
            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0),
            health = formatHealth(intent),
            technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "",
            temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10f,
            chargingStatus = formatStatus(intent),
            isCharging = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) == BatteryManager.BATTERY_STATUS_CHARGING
        )
    }

    private fun formatHealth(data: Intent): String {
        return when (data.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)) {
            BatteryManager.BATTERY_HEALTH_COLD -> app.getString(R.string.battery_health_cold)
            BatteryManager.BATTERY_HEALTH_GOOD -> app.getString(R.string.battery_health_good)
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> app.getString(R.string.battery_health_overheat)
            BatteryManager.BATTERY_HEALTH_DEAD -> app.getString(R.string.battery_health_dead)
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> app.getString(R.string.battery_health_overvoltage)
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> app.getString(R.string.battery_health_unspecified_failure)
            else -> app.getString(R.string.battery_health_unknown)
        }
    }

    private fun formatStatus(data: Intent): String {
        return when (data.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
            BatteryManager.BATTERY_STATUS_CHARGING -> app.getString(R.string.battery_status_charging)
            BatteryManager.BATTERY_STATUS_DISCHARGING -> app.getString(R.string.battery_status_discharging)
            BatteryManager.BATTERY_STATUS_FULL -> app.getString(R.string.battery_status_full)
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> app.getString(R.string.battery_status_not_charging)
            BatteryManager.BATTERY_STATUS_UNKNOWN -> app.getString(R.string.battery_status_unknown)
            else -> ""
        }
    }
}
