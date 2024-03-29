package by.sergey.batterywidget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.os.SystemClock
import android.app.AlarmManager
import android.app.PendingIntent
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.annotation.RequiresApi


import android.app.Activity
import android.content.*
import android.os.IBinder
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.content.Intent
import by.sergey.batterywidget.utills.Constants.INFO_TAG
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import by.sergey.batterywidget.UI.MainActivity
import java.util.*


/**
 * Implementation of App Widget functionality.
 * I don't think that UI element, which is inherited from Broadcast receiver is UI layer,
 * so MVVM is not applied here. Widgets were reworked on API 31, but by now, most devices doesn't support that API
 * Here is just nice sample to see how to organize background tasks without running app to do some work
 */
class BatteryAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        // Sometimes when the phone is booting, onUpdate method gets called before onEnabled()
        val currentLevel: Int = calculateBatteryLevel(context)
        if (batteryLevel != currentLevel) {
            batteryLevel = currentLevel
        }
        updateViews(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        turnAlarmOnOff(context, true)

        // due to service limitations after Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(INFO_TAG, "Launching ScreenMonitorService for 8 Android and above")
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
        turnAlarmOnOff(context, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ScreenMonitorWorker.unregisterReceivers(context.applicationContext)
        } else {
            context.stopService(Intent(context, ScreenMonitorService::class.java))
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent!!.action.equals(ACTION_BATTERY_UPDATE)) {
            val currentLevel = calculateBatteryLevel(context!!)
            if (batteryLevel != currentLevel) {
                batteryLevel = currentLevel
                updateViews(context)
            }
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


    private fun updateViews(context: Context) {         //Note that you can get view from widget only from higher API levels, and just set width to batteryBacks
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
        views.setOnClickPendingIntent(R.id.batteryMask,PendingIntent.getActivity(context, 0, openAppIntent, 0))

        val componentName = ComponentName(context, BatteryAppWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.updateAppWidget(componentName, views)
    }

    companion object {
        private const val ACTION_BATTERY_UPDATE = "by.sergey.batterywidget.action.UPDATE"
        private var batteryLevel = 0

        fun turnAlarmOnOff(context: Context, turnOn: Boolean) {
            Log.d(INFO_TAG, "turnAlarmOnOff was called with $turnOn flag")

            val intent = Intent(context, BatteryAppWidget::class.java)
            intent.setAction(ACTION_BATTERY_UPDATE)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (turnOn) { // Add extra 1 sec because sometimes ACTION_BATTERY_CHANGED is called after the first alarm
                alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    3000L,
                    pendingIntent
                )
            } else {
                alarmManager.cancel(pendingIntent)
            }

        }
    }
}

