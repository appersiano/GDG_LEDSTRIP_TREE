package com.appersiano.gdg_ledstrip_tree.viewmodels

import android.app.Application
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.appersiano.gdg_ledstrip_tree.scanner.GDGTreeLedLEDBleScanner
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ScannerViewModel is in charge to manage the scanning process and logic
 */
private const val TAG = "ScannerViewModel"

class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    private val bleDeviceScanner by lazy { GDGTreeLedLEDBleScanner(application) }

    val scanStatus = bleDeviceScanner.scanStatus
    val listDevices = mutableStateListOf<ScanResult>()

    init {
        viewModelScope.launch {
            bleDeviceScanner.scanResultFlow.receiveAsFlow().collect {
                listDevices.add(it)
            }
        }
    }

    fun startScan() {
        bleDeviceScanner.startScan()
        listDevices.clear()
    }

    fun stopScan() {
        bleDeviceScanner.stopScan()
        bleDeviceScanner.clearCaches()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "onCleared: ")
    }
}
