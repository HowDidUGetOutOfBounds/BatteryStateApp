package by.sergey.batterywidget

import android.app.KeyguardManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import by.sergey.batterywidget.domain.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScreenMonitorService : Service() {

    @Inject lateinit var logger: Logger

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        logger.log(TAG, "Service created")

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        registerReceiver(screenStateReceiver, filter)
        logger.log(TAG, "Receivers registered (screen on/off, user present, battery changed)")
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.log(TAG, "Service destroyed, unregistering receivers")
        try {
            unregisterReceiver(screenStateReceiver)
        } catch (e: Exception) {
            logger.log(TAG, "unregisterReceiver error: $e")
        }
    }

    private val screenStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    logger.log(TAG, "Screen OFF -> alarm OFF")
                    BatteryAppWidget.turnAlarmOff(context)
                }
                Intent.ACTION_SCREEN_ON -> {
                    val keyguardManager =
                        context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    if (!keyguardManager.isKeyguardLocked) {
                        logger.log(TAG, "Screen ON (unlocked) -> alarm ON")
                        BatteryAppWidget.turnAlarmOn(context)
                    } else {
                        logger.log(TAG, "Screen ON (locked) -> waiting for unlock")
                    }
                }
                Intent.ACTION_USER_PRESENT -> {
                    logger.log(TAG, "User present (unlocked) -> alarm ON")
                    BatteryAppWidget.turnAlarmOn(context)
                }
                Intent.ACTION_BATTERY_CHANGED -> {
                    BatteryAppWidget.requestWidgetUpdate(context)
                }
            }
        }
    }

    companion object {
        private const val TAG = "ScreenMonitorService"
    }
}
