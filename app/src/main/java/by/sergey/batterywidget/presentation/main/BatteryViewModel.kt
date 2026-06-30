package by.sergey.batterywidget.presentation.main

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.sergey.batterywidget.domain.usecase.ObserveBatteryUseCase
import by.sergey.batterywidget.presentation.theme.BatteryAmber
import by.sergey.batterywidget.presentation.theme.BatteryDarkGreen
import by.sergey.batterywidget.presentation.theme.BatteryDeepOrange
import by.sergey.batterywidget.presentation.theme.BatteryFullGreen
import by.sergey.batterywidget.presentation.theme.BatteryGreen
import by.sergey.batterywidget.presentation.theme.BatteryLightGreen
import by.sergey.batterywidget.presentation.theme.BatteryOrange
import by.sergey.batterywidget.presentation.theme.BatteryRed
import by.sergey.batterywidget.presentation.theme.BatteryYellow
import by.sergey.batterywidget.presentation.theme.BatteryYellowGreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BatteryViewModel @Inject constructor(
    private val observeBatteryUseCase: ObserveBatteryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BatteryState())
    val state: StateFlow<BatteryState> = _state.asStateFlow()

    private val _intents = Channel<BatteryIntent>(Channel.BUFFERED)
    val intents: Channel<BatteryIntent> = _intents

    init {
        viewModelScope.launch {
            _intents.consumeAsFlow().collect { intent ->
                when (intent) {
                    BatteryIntent.Refresh -> Unit
                }
            }
        }

        viewModelScope.launch {
            observeBatteryUseCase().collect { info ->
                _state.update {
                    BatteryState(
                        level = info.level,
                        voltage = "${info.voltage} mV",
                        health = info.health,
                        technology = info.technology,
                        temperature = "${info.temperature}°C",
                        chargingStatus = info.chargingStatus,
                        isCharging = info.isCharging,
                        levelColor = batteryLevelColor(info.level)
                    )
                }
            }
        }
    }

    private fun batteryLevelColor(level: Int): Color = when (level) {
        in 0..10 -> BatteryRed
        in 11..20 -> BatteryDeepOrange
        in 21..30 -> BatteryOrange
        in 31..40 -> BatteryAmber
        in 41..50 -> BatteryYellow
        in 51..60 -> BatteryYellowGreen
        in 61..70 -> BatteryLightGreen
        in 71..80 -> BatteryGreen
        in 81..90 -> BatteryDarkGreen
        else -> BatteryFullGreen
    }
}
