package com.appersiano.gdg_ledstrip_tree

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appersiano.gdg_ledstrip_tree.client.TreeLedBleClient
import com.appersiano.gdg_ledstrip_tree.ui.theme.GDG_LEDSTRIP_TREETheme
import com.appersiano.gdg_ledstrip_tree.viewmodels.GDGTreeLedClientViewModel
import com.appersiano.gdg_ledstrip_tree.viewmodels.ScannerViewModel
import com.appersiano.gdg_ledstrip_tree.views.DetailScreen
import com.appersiano.gdg_ledstrip_tree.views.MainScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GDG_LEDSTRIP_TREETheme {
                val scannerViewModel: ScannerViewModel = viewModel()

                val scanStatus = scannerViewModel.scanStatus.collectAsState()
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "main",
                ) {
                    composable(route = "main") {

                        val bluetoothPermissionState = rememberMultiplePermissionsState(
                            listOf(
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_CONNECT
                            )
                        )

                        if (bluetoothPermissionState.allPermissionsGranted) {
                            MainScreen(
                                navController,
                                onStartScan = {
                                    scannerViewModel.startScan()
                                },
                                onStopScan = {
                                    scannerViewModel.stopScan()
                                },
                                scanStatus,
                                scannerViewModel.listDevices,
                            )
                        } else {
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                Box {
                                    Button(modifier = Modifier.align(Alignment.Center), onClick = {
                                        bluetoothPermissionState.launchMultiplePermissionRequest()
                                    }) {
                                        Text("Grant BLE Permission")
                                    }
                                }
                            }
                        }
                    }

                    composable(route = "detail/{macAddress}") { backStackEntry ->
                        val macAddress = backStackEntry.arguments?.getString("macAddress")
                        macAddress?.let {
                            val viewModel: GDGTreeLedClientViewModel = viewModel()
                            val deviceState by viewModel.bleDeviceStatus.collectAsState()
                            val currentRGB by viewModel.rgbValue.collectAsState()

                            val localContext = LocalContext.current

                            LaunchedEffect(currentRGB) {
                                if (deviceState == TreeLedBleClient.SDeviceStatus.READY)
                                    Toast.makeText(
                                        localContext,
                                        "currentRGB: R ${currentRGB.value.toArgb().red}, G ${currentRGB.value.toArgb().green}, B ${currentRGB.value.toArgb().blue}",
                                        Toast.LENGTH_LONG
                                    ).show()
                            }

                            DetailScreen(
                                macAddress = macAddress,
                                onConnect = {
                                    viewModel.connect(macAddress)
                                    scannerViewModel.stopScan()
                                },
                                onDisconnect = { viewModel.disconnect() },
                                status = deviceState,
                                onEffectSelected = { viewModel.setLEDEffect(it) },
                                onLedColorSelect = {
                                    viewModel.setLEDColor(
                                        it.toArgb().red,
                                        it.toArgb().green,
                                        it.toArgb().blue
                                    )
                                },
                                onReadLedColor = { viewModel.readLEDColor() },
                                rgbValue = currentRGB.value
                            )
                        }
                    }
                }
            }
        }
    }
}