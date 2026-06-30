package by.sergey.batterywidget.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import by.sergey.batterywidget.presentation.main.BatteryViewModel
import by.sergey.batterywidget.presentation.main.MainScreen
import by.sergey.batterywidget.presentation.settings.SettingsScreen

private data object MainKey
private data object SettingsKey

@Composable
fun BatteryNavHost(viewModel: BatteryViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val backStack = remember { mutableStateListOf<Any>(MainKey) }

    NavDisplay(
        backStack = backStack,
        entryProvider = { key ->
            when (key) {
                MainKey -> NavEntry(key) {
                    MainScreen(
                        state = state,
                        onSettingsClick = { backStack.add(SettingsKey) }
                    )
                }
                SettingsKey -> NavEntry(key) {
                    SettingsScreen(
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
                else -> throw IllegalArgumentException("Unknown navigation key: $key")
            }
        }
    )
}
