package com.appersiano.gdg_ledstrip_tree.views

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.appersiano.gdg_ledstrip_tree.scanner.SCScan

@SuppressLint("MissingPermission")
@Composable
fun MainScreen(
    navController: NavController,
    onStartScan: () -> Unit = {},
    onStopScan: () -> Unit = {},
    scanStatus: State<SCScan?>,
    scanResult: SnapshotStateList<ScanResult>,
) {
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp),
        ) {
            Text(text = "GDG Tree Led", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.size(8.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = onStartScan) {
                Text(text = "Start Scan")
            }
            Button(modifier = Modifier.fillMaxWidth(), onClick = onStopScan) {
                Text(text = "Stop Scan!")
            }

            var counter = 1.0f
            var color = Color.Red

            when (scanStatus.value) {
                is SCScan.ERROR -> {
                    counter = 1f
                    color = Color.Red
                }

                SCScan.START -> {
                    counter = 1f
                    color = Color.Green
                }

                SCScan.STOP -> {
                    counter = 1f
                    color = Color.Red
                }

                SCScan.UNKNOWN -> {
                    counter = 1f
                    color = Color.Gray
                }

                null -> {
                    counter = 0f
                }
            }

            ProgressScan(counter, color)

            ScanStatusText(scanStatus.value)

            Divider()
            LazyColumn {
                items(scanResult) {
                    Spacer(Modifier.size(8.dp))
                    DevicePickerItem(
                        navController,
                        it.device.address,
                        it.rssi,
                        it.device.name ?: "-"
                    )
                }
            }
        }
    }
}

@Composable
fun DevicePickerItem(navController: NavController, macAddress: String, rssi: Int, name: String) {
    Card(Modifier.clickable {
        navController.navigate("detail/$macAddress") {
            launchSingleTop = true
        }
    }) {
        Column(Modifier.padding(10.dp)) {
            Text(
                text = "$name ($macAddress)",
                style = TextStyle(
                    fontSize = 20.sp,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Text(text = "RSSI: $rssi", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
fun ScanStatusText(scanStatus: SCScan?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Scan Status: ${scanStatus.toString()}",
            modifier = Modifier
                .padding(16.dp),
            color = Color.White
        )
        if (scanStatus == SCScan.START) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun ProgressScan(value: Float, colorState: Color) {
    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth(),
        color = colorState,
        progress = value
    )
}