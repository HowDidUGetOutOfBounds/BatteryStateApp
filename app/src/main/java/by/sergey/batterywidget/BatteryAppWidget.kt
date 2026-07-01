package by.sergey.batterywidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.SystemClock
import android.widget.RemoteViews
import by.sergey.batterywidget.domain.Logger
import by.sergey.batterywidget.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BatteryAppWidget : AppWidgetProvider() {

    @Inject lateinit var logger: Logger

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        logger.log(TAG, "onUpdate called")
        val currentLevel = calculateBatteryLevel(context)
        if (batteryLevel != currentLevel) {
            batteryLevel = currentLevel
            logger.log(TAG, "level changed to $batteryLevel")
        }
        updateViews(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        logger.log(TAG, "onEnabled - widget added to homescreen")
        turnAlarmOn(context)
        val serviceIntent = Intent(context, ScreenMonitorService::class.java)
        context.startService(serviceIntent)
        logger.log(TAG, "ScreenMonitorService started")
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        logger.log(TAG, "onDisabled - all widgets removed")
        turnAlarmOff(context)
        context.stopService(Intent(context, ScreenMonitorService::class.java))
        logger.log(TAG, "ScreenMonitorService stopped")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == ACTION_BATTERY_UPDATE) {
            logger.log(TAG, "onReceive - alarm triggered")
            val currentLevel = calculateBatteryLevel(context!!)
            if (batteryLevel != currentLevel) {
                batteryLevel = currentLevel
                logger.log(TAG, "level changed to $batteryLevel via alarm")
                updateViews(context)
            } else {
                logger.log(TAG, "level unchanged ($batteryLevel), skipping update")
            }
        }
    }

    companion object {
        private const val TAG = "BatteryAppWidget"
        private const val ACTION_BATTERY_UPDATE = "by.sergey.batterywidget.action.UPDATE"
        private var batteryLevel = 0
        private const val ALARM_INTERVAL_MS = 3000L

        fun requestWidgetUpdate(context: Context) {
            val currentLevel = calculateBatteryLevel(context)
            if (batteryLevel != currentLevel) {
                batteryLevel = currentLevel
                updateViews(context)
            }
        }

        fun turnAlarmOn(context: Context) {
            val intent = Intent(context, BatteryAppWidget::class.java).apply {
                action = ACTION_BATTERY_UPDATE
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 1000,
                ALARM_INTERVAL_MS,
                pendingIntent
            )
        }

        fun turnAlarmOff(context: Context) {
            val intent = Intent(context, BatteryAppWidget::class.java).apply {
                action = ACTION_BATTERY_UPDATE
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }

        private fun calculateBatteryLevel(context: Context): Int {
            val batteryIntent = context.applicationContext.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0
            val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
            return level * 100 / scale
        }

        private fun updateViews(context: Context) {
            val views = RemoteViews(context.packageName, R.layout.battery_app_widget)
            views.setTextViewText(R.id.appwidget_text_percentage, "$batteryLevel%")

            val drawableRes = when (batteryLevel) {
                in 0..10 -> R.drawable.battery_state_0_10
                in 11..20 -> R.drawable.battery_state_10_20
                in 21..30 -> R.drawable.battery_state_20_30
                in 31..40 -> R.drawable.battery_state_30_40
                in 41..50 -> R.drawable.battery_state_40_50
                in 51..60 -> R.drawable.battery_state_50_60
                in 61..70 -> R.drawable.battery_state_60_70
                in 71..80 -> R.drawable.battery_state_70_80
                in 81..90 -> R.drawable.battery_state_80_90
                else -> R.drawable.battery_state_90_100
            }
            views.setImageViewResource(R.id.batteryBacks, drawableRes)

            val openAppIntent = Intent(context.applicationContext, MainActivity::class.java)
            views.setOnClickPendingIntent(
                R.id.batteryMask,
                PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE)
            )

            val componentName = ComponentName(context, BatteryAppWidget::class.java)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.updateAppWidget(componentName, views)
        }
    }
}
