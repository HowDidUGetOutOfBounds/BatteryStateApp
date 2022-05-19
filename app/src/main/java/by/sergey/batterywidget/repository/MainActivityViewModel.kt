package by.sergey.batterywidget.repository


import android.app.Application
import android.content.Context
import android.content.IntentFilter
import by.sergey.batterywidget.utills.ReceiverLiveData
import androidx.lifecycle.LiveData
import android.content.Intent
import android.os.BatteryManager
import androidx.lifecycle.AndroidViewModel


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val batteryIntentLiveData: LiveData<Intent> =
        ReceiverLiveData(
            application, IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        ) { context: Context, intent: Intent -> intent }

    fun getHealthFormIntent(data: Intent): String { //TODO: extract string constants into res
        val batteryStateValue = data.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)

        when (batteryStateValue) {
            BatteryManager.BATTERY_HEALTH_COLD -> {
                return "Cold"
            }
            BatteryManager.BATTERY_HEALTH_GOOD -> {
                return "Good"
            }
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> {
                return "Overheat"
            }
            BatteryManager.BATTERY_HEALTH_DEAD -> {
                return "Dead"
            }
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> {
                return "Overvoltage"
            }
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> {
                return "Unspecified failure"
            }
            else -> {
                return "Unknown"
            }
        }

    }

    fun getTemperatureFormIntent(data: Intent): Float {
        return (data.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)).toFloat() / 10
    }

    fun getChargingStatusFromIntent(data: Intent): String {
        val status = data.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        when(status){
            BatteryManager.BATTERY_STATUS_CHARGING -> {
                return "Charging"
            }
            BatteryManager.BATTERY_STATUS_DISCHARGING -> {
                return "Discharging"
            }
            BatteryManager.BATTERY_STATUS_FULL -> {
                return "Full"
            }
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> {
                return "Not Charging"
            }
            BatteryManager.BATTERY_STATUS_UNKNOWN -> {
                return "Unknown"
            }
            else -> {
                return ""
            }
        }
    }

}