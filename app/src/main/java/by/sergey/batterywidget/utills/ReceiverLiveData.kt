package by.sergey.batterywidget.utills

import androidx.lifecycle.LiveData
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import java.util.function.BiFunction


class ReceiverLiveData<T>(
    private val context: Context,
    private val filter: IntentFilter,
    private val mapFunc: BiFunction<Context, Intent, T>
) : LiveData<T>() {


    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(mBroadcastReceiver)
    }

    override fun onActive() {
        super.onActive()
        setValue(mapFunc.apply(context, Intent()))
        context.registerReceiver(mBroadcastReceiver, filter)
    }

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            setValue(mapFunc.apply(context, intent))
        }
    }
}