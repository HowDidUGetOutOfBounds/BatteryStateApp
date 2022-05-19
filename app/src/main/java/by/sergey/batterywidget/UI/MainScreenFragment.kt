package by.sergey.batterywidget.UI

import android.content.Intent
import android.os.BatteryManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import by.sergey.batterywidget.databinding.FragmentMainScreenBinding
import by.sergey.batterywidget.repository.MainActivityViewModel


class MainScreenFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivityViewModel.batteryIntentLiveData.observe(this) { data ->
            with(binding)
            {
                textViewBatteryLevel.text =
                    data.getIntExtra(BatteryManager.EXTRA_LEVEL, 0).toString() + "%"
                batteryLevel.progress = data.getIntExtra(BatteryManager.EXTRA_LEVEL, 0).toFloat()

                textViewVoltageData.text = data.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0).toString() + " mV"
                textViewHealthData.text = mainActivityViewModel.getHealthFormIntent(data)
                textViewBatteryTypeData.text = data.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
                textViewTemperatureData.text = mainActivityViewModel.getTemperatureFormIntent(data).toString() + "°C"
                textViewChargingStatusData.text = mainActivityViewModel.getChargingStatusFromIntent(data)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
