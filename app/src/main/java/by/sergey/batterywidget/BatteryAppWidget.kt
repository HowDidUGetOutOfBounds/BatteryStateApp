package by.sergey.batterywidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.os.SystemClock
import android.app.AlarmManager
import android.app.PendingIntent
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import android.content.*
import by.sergey.batterywidget.utills.Constants.INFO_TAG
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import by.sergey.batterywidget.presentation.MainActivity

class BatteryAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val currentLevel: Int = calculateBatteryLevel(context)
        if (batteryLevel != currentLevel) {
            batteryLevel = currentLevel
        }
        updateViews(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        startFallbackAlarm(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(INFO_TAG, "Launching ScreenMonitorWorker for 8 Android and above")
            val myWorkRequest = OneTimeWorkRequest.Builder(ScreenMonitorWorker::class.java).build()
            WorkManager.getInstance(context).enqueue(myWorkRequest)
        } else {
            Log.d(INFO_TAG, "Launching ScreenMonitorService for pre 8 Android")
            val serviceIntent = Intent(context, ScreenMonitorService::class.java)
            context.startService(serviceIntent)
        }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        stopFallbackAlarm(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ScreenMonitorWorker.unregisterReceivers(context.applicationContext)
        } else {
            context.stopService(Intent(context, ScreenMonitorService::class.java))
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent!!.action.equals(ACTION_BATTERY_UPDATE)) {
            requestWidgetUpdate(context!!)
        }
    }

    companion object {
        private const val ACTION_BATTERY_UPDATE = "by.sergey.batterywidget.action.UPDATE"
        private var batteryLevel = 0

        fun requestWidgetUpdate(context: Context) {
            val currentLevel = calculateBatteryLevel(context)
            if (batteryLevel != currentLevel) {
                batteryLevel = currentLevel
                updateViews(context)
            }
        }

        private fun calculateBatteryLevel(context: Context): Int {
            val batteryIntent = context.applicationContext.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            val level = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
            return level * 100 / scale
        }

        private fun updateViews(context: Context) {
            val views = RemoteViews(context.packageName, R.layout.battery_app_widget)
            views.setTextViewText(R.id.appwidget_text_percentage, "$batteryLevel%")
            when (batteryLevel) {
                in 0..10 ->
                    views.setImageViewResource(R.id.batteryBacks, R.drawable.battery_state_0_10)
                in 11..20 ->
                    views.setImageViewResource(R.id.batteryBacks, R.drawable.battery_state_10_20)
                in 21..30 ->
                    views.setImageViewResource(R.id.batteryBacks, R.drawable.battery_state_20_30)
                in 31..40 ->
                    views.setImageViewResource(R.id.batteryBacks, R.drawable.battery_state_30_40)
                in 41..50 ->
                    views.setImageViewResource(R.id.batteryBacks, R.drawable.battery_state_40_50)
                in 51..60 ->
                    views.setImageViewResource(R.id.batteryBacks, R.drawable.battery_state_50_60)
                in 61..70 ->
                    views.setImageViewResource(R.id.batteryBacks, R.drawable.battery_state_60_70)
                in 71..80 ->
                    views.setImageViewResource(R.id.batteryBacks, R.drawable.battery_state_70_80)
                in 81..90 ->
                    views.setImageViewResource(R.id.batteryBacks, R.drawable.battery_state_80_90)
                in 91..100 ->
                    views.setImageViewResource(R.id.batteryBacks, R.drawable.battery_state_90_100)
            }

            val openAppIntent = Intent(context.applicationContext, MainActivity::class.java)
            val flags = PendingIntent.FLAG_IMMUTABLE
            views.setOnClickPendingIntent(R.id.batteryMask, PendingIntent.getActivity(context, 0, openAppIntent, flags))

            val componentName = ComponentName(context, BatteryAppWidget::class.java)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.updateAppWidget(componentName, views)
        }

        private fun startFallbackAlarm(context: Context) {
            Log.d(INFO_TAG, "Starting fallback alarm")

            val intent = Intent(context, BatteryAppWidget::class.java)
            intent.setAction(ACTION_BATTERY_UPDATE)
            val flags = PendingIntent.FLAG_IMMUTABLE
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, flags)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 5000,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                pendingIntent
            )
        }

        private fun stopFallbackAlarm(context: Context) {
            Log.d(INFO_TAG, "Stopping fallback alarm")

            val intent = Intent(context, BatteryAppWidget::class.java)
            intent.setAction(ACTION_BATTERY_UPDATE)
            val flags =
                PendingIntent.FLAG_IMMUTABLE
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, flags)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }
    }
}
