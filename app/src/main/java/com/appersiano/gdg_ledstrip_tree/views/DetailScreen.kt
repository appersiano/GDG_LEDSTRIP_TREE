package com.appersiano.gdg_ledstrip_tree.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.appersiano.gdg_ledstrip_tree.client.TreeLedBleClient

@Composable
fun DetailScreen(
    macAddress: String,
    onConnect: () -> Unit = {},
    onDisconnect: () -> Unit = {},
    onEffectSelected: (Int) -> Unit,
    onLedColorSelect: (Color) -> Unit,
    onReadLedColor: () -> Unit,
    rgbValue: Color,
    status: TreeLedBleClient.SDeviceStatus,
) {
    Surface(
        Modifier.fillMaxSize()
    ) {
        val scrollState = rememberScrollState()
        Column(
            Modifier
                .padding(24.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                text = "Connect to $macAddress"
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
            ) {
                Button(onClick = onConnect) {
                    Text("Connect")
                }
                Button(onClick = onDisconnect, modifier = Modifier.padding(start = 10.dp)) {
                    Text("Disconnect")
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp)
                            .background(getStatusColor(status), shape = CircleShape)
                            .requiredSize(15.dp)
                    )
                }
            }
            Divider(Modifier.padding(top = 10.dp, bottom = 10.dp), thickness = 1.dp)
            SetEffectComposable(onEffectSelected)
            Divider(Modifier.padding(top = 10.dp, bottom = 10.dp), thickness = 1.dp)
            SetLEDColor(rgbValue, onLedColorSelect, onReadLedColor)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SetEffectComposable(onEffectSelect: (Int) -> Unit) {
    val listEffect = listOf<Pair<String, Int>>(
        Pair("Led Off", 0),
        Pair("Steady", 1),
        Pair("Blink", 2),
        Pair("Alternate Blink", 3),
        Pair("GDG", 4),
    )

    Text(
        modifier = Modifier
            .fillMaxHeight()
            .wrapContentHeight(),
        textAlign = TextAlign.Center,
        text = "LED Effect"
    )

    FlowRow(modifier = Modifier.padding(8.dp)) {
        listEffect.forEach {
            Button(onClick = {
                onEffectSelect.invoke(it.second)
            }) {
                Text(text = it.first)
            }
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}

@Composable
private fun SetLEDColor(
    currentColor : Color,
    onSetLedColor: (Color) -> Unit,
    onReadLedColor: () -> Unit
) {

    var mRGBCopy by remember() { mutableStateOf(Color(0,0,0)) }

    var textValueRed = mRGBCopy.toArgb().red.toString()
    val sliderPositionRed = mRGBCopy.toArgb().red

    var textValueGreen = mRGBCopy.toArgb().green.toString()
    val sliderPositionGreen = mRGBCopy.toArgb().green

    var textValueBlue = mRGBCopy.toArgb().blue.toString()
    val sliderPositionBlue = mRGBCopy.toArgb().blue

    Column {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center,
                    text = "LED RGB Color"
                )
                Box(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .background(mRGBCopy, shape = CircleShape)
                        .requiredSize(25.dp)
                )
            }
            Button(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { onReadLedColor.invoke() }) {
                Text(text = "R")
            }
        }

        Row {
            Text(text = "RED")
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = textValueRed)
        }
        Slider(
            value = sliderPositionRed.toFloat(),
            onValueChange = {
                Log.i("RGB", "float: $it")
                mRGBCopy = (Color(it.toInt(), mRGBCopy.toArgb().green, mRGBCopy.toArgb().blue))
                textValueRed = sliderPositionRed.toString()
            },
            valueRange = 0f..255f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
        Row {
            Text(text = "GREEN")
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = textValueGreen)
        }
        Slider(
            value = sliderPositionGreen.toFloat(),
            onValueChange = {
                mRGBCopy = Color(mRGBCopy.toArgb().red, it.toInt(), mRGBCopy.toArgb().blue)
                textValueGreen = sliderPositionGreen.toString()
            },
            valueRange = 0f..255f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )

        Row {
            Text(text = "BLUE")
            Spacer(modifier = Modifier.size(5.dp))
            Text(text = textValueBlue)
        }
        Slider(
            value = sliderPositionBlue.toFloat(),
            onValueChange = {
                mRGBCopy = Color(mRGBCopy.toArgb().red, mRGBCopy.toArgb().green, it.toInt())
                textValueBlue = sliderPositionBlue.toString()
            },
            valueRange = 0f..255f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
    Button(
        onClick = {
            onSetLedColor(mRGBCopy)
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Set RGB")
    }
}

fun getStatusColor(status: TreeLedBleClient.SDeviceStatus): Color {
    return when (status) {
        TreeLedBleClient.SDeviceStatus.CONNECTED -> Color.Yellow
        TreeLedBleClient.SDeviceStatus.READY -> Color.Green
        TreeLedBleClient.SDeviceStatus.UNKNOWN -> Color.Gray
        is TreeLedBleClient.SDeviceStatus.DISCONNECTED -> Color.Red
    }
}
