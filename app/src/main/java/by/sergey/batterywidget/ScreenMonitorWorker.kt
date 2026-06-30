package by.sergey.batterywidget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import by.sergey.batterywidget.utills.Constants.INFO_TAG

class ScreenMonitorWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        return try {
            Log.d(INFO_TAG, "Registering battery receiver from work manager")
            registerBatteryReceiver()
            Result.success()
        } catch (throwable: Throwable) {
            Log.e(INFO_TAG, "Error in worker")
            Result.failure()
        }
    }

    override fun onStopped() {
        super.onStopped()
        unregisterReceivers(applicationContext)
    }

    private fun registerBatteryReceiver() {
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                BatteryAppWidget.requestWidgetUpdate(context)
            }
        }
        applicationContext.registerReceiver(
            batteryReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
    }

    companion object {
        private lateinit var batteryReceiver: BroadcastReceiver

        fun unregisterReceivers(context: Context) {
            try {
                Log.d(INFO_TAG, "Unregistering battery receiver from work manager")
                context.unregisterReceiver(batteryReceiver)
            } catch (e: Exception) {
                Log.d(INFO_TAG, "unregisterReceivers: ${e.stackTrace}. This is different app context issue")
            }
        }
    }
}
