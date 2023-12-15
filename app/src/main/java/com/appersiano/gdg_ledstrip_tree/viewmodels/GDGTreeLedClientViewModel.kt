package com.appersiano.gdg_ledstrip_tree.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.appersiano.gdg_ledstrip_tree.client.TreeLedBleClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.time.Instant

private const val TAG = "GDGTreeLedClientViewModel"

class GDGTreeLedClientViewModel(application: Application) : AndroidViewModel(application) {

    private val bleClient by lazy { TreeLedBleClient(application) }

    //region MutableStateFlow
    val bleDeviceStatus = bleClient.deviceConnectionStatus
    val rgbValue = MutableStateFlow(TimedValue(Color(0,0,0), Instant.now()))
    //endregion

    init {
        viewModelScope.launch {
            bleClient.ledColor.collect {
                val color = Color(it)
                Log.i(TAG, "Color r: " + color.toArgb().red)
                Log.i(TAG, "Color g: " + color.toArgb().green)
                Log.i(TAG, "Color b: " + color.toArgb().blue)
                rgbValue.value = TimedValue(Color(color.toArgb().red, color.toArgb().green, color.toArgb().blue))
            }
        }
        viewModelScope.launch {
            bleClient.ledStatus.collect {
                Log.d(TAG, "LED Effect: $it")
            }
        }

    }

    fun connect(macAddress: String) {
        bleClient.connect(macAddress)
    }

    fun disconnect() {
        bleClient.disconnect()
    }

    fun setLEDColor(red: Int, green: Int, blue: Int) {
        bleClient.setLEDColor(red, green, blue)
    }

    fun readLEDColor() {
        bleClient.readLEDColor()
    }

    fun setLEDEffect(it: Int) {
        bleClient.setLEDEffect(it)
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "onCleared: ")
    }

    data class TimedValue<T>(val value: T, val timestamp: Instant = Instant.now())
}
