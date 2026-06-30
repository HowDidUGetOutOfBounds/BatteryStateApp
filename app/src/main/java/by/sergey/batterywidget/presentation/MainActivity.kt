package by.sergey.batterywidget.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import by.sergey.batterywidget.presentation.navigation.BatteryNavHost
import by.sergey.batterywidget.presentation.theme.BatteryWidgetTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BatteryWidgetTheme {
                BatteryNavHost()
            }
        }
    }
}
