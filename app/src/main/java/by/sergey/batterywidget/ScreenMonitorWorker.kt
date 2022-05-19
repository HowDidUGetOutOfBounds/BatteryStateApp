package by.sergey.batterywidget

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import by.sergey.batterywidget.utills.Constants.INFO_TAG
import java.lang.Exception


class ScreenMonitorWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {

        return try {
            Log.d(INFO_TAG, "Registering receivers from work manager")
            registerScreenOffReceiver()
            registerScreenOnReceiver()
            registerUserPresentReceiver()

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

    private fun registerUserPresentReceiver() {
        userPresentReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                BatteryAppWidget.turnAlarmOnOff(context, true)
            }
        }
        applicationContext.registerReceiver(
            userPresentReceiver,
            IntentFilter(Intent.ACTION_USER_PRESENT)
        )
    }

    private fun registerScreenOffReceiver() {
        screenOffReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                BatteryAppWidget.turnAlarmOnOff(context, false)
            }
        }
        applicationContext.registerReceiver(
            screenOffReceiver,
            IntentFilter(Intent.ACTION_SCREEN_OFF)
        )
    }

    private fun registerScreenOnReceiver() {
        screenOnReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                val keyguardManager =
                    context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                if (!keyguardManager.isKeyguardLocked) {
                    BatteryAppWidget.turnAlarmOnOff(context, true)
                }
            }
        }
        applicationContext.registerReceiver(screenOnReceiver, IntentFilter(Intent.ACTION_SCREEN_ON))
    }

    companion object {
        private lateinit var screenOnReceiver: BroadcastReceiver
        private lateinit var screenOffReceiver: BroadcastReceiver
        private lateinit var userPresentReceiver: BroadcastReceiver

        fun unregisterReceivers(context: Context) {

            try {
                with(context) {
                    Log.d(INFO_TAG, "Unregistering receivers from work manager")
                    unregisterReceiver(screenOffReceiver)
                    unregisterReceiver(screenOnReceiver)
                    unregisterReceiver(userPresentReceiver)
                }
            }
            catch (e: Exception)
            {
                Log.d(INFO_TAG, "unregisterReceivers: ${e.stackTrace}. This is different app context issue")
            }

        }
    }
}