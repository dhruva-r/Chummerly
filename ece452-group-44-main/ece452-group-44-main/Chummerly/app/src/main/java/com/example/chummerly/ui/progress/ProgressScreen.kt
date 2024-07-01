package com.example.chummerly.ui.progress

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

/* references:
    https://hackernoon.com/creating-a-grouped-bar-graph-using-jetpack-compose
    https://betterprogramming.pub/custon-charts-in-android-using-jetpack-compose-87b395c1d515
    https://medium.com/@developerchunk/create-bargraph-in-jetpack-compose-android-studio-kotlin-ec526be25479
 */

private val defaultMaxHeight = 200.dp
val barChartInputsPercent = (0..6).map { (1..100).random().toFloat() }

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProgressScreen(
    onBackClick: () -> Unit,
) {

    var testGoal by remember { mutableStateOf(TextFieldValue("50")) }
    var isDialogUp by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onBackClick()}, modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Progress Status",
            modifier = Modifier.align(Alignment.Start),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your current deck", /* TODO - add deck titles */
            modifier = Modifier.align(Alignment.Start),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            BarChart(modifier = Modifier.padding(20.dp), values = barChartInputsPercent)

        }
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            val days = listOf("S", "M", "T", "W", "Th", "F", "S")
            var count = 0

            days.forEach {
                if (count == LocalDate.now().dayOfWeek.value) {
                    Text(
                        text = it,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )


                } else {
                    Text(
                        text = it,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                count++
            }

        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {},
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "View Progress")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { isDialogUp = !isDialogUp },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Edit Goal")
        }

        val pattern = remember { Regex("^\\d+\$") }

        if (isDialogUp) {
            AlertDialog(
                onDismissRequest = { isDialogUp = !isDialogUp },
                title = { Text(text = ("Enter a goal for deck studying (0% - 100%)")) },
                text = {
                    OutlinedTextField(
                        value = testGoal,
                        onValueChange = {
                            if (it.text.isEmpty() || it.text.matches(pattern)) {
                                testGoal = it
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )},
            confirmButton = {
                Button(onClick = {
                    isDialogUp = false
                    if(testGoal.text.toInt() > 100){
                        testGoal = TextFieldValue("100")
                    }
                }){
                    Text(text = "Submit")
                }
            })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun BarChart(
    modifier: Modifier = Modifier, values: List<Float>, maxHeight: Dp = defaultMaxHeight
) {

    assert(values.isNotEmpty()) { "Input values are empty" }

    val borderColor = MaterialTheme.colorScheme.secondary
    val density = LocalDensity.current
    val strokeWidth = with(density) { 1.dp.toPx() }

    Row(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .height(maxHeight)
                .drawBehind {
//                    // draw X-Axis
//                    drawLine(
//                        color = borderColor,
//                        start = Offset(0f, size.height),
//                        end = Offset(size.width, size.height),
//                        strokeWidth = strokeWidth
//                    )
//                    // draw Y-Axis
//                    drawLine(
//                        color = borderColor,
//                        start = Offset(0f, 0f),
//                        end = Offset(0f, size.height),
//                        strokeWidth = strokeWidth,
//                    )
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, size.height / 2), /* TODO - adjust to goal */
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = strokeWidth,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(50f, 25f), 0f)
                    )
                }
                .alpha(0.8f)
        ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        var count = 0
        values.forEach { item ->
            if (count == LocalDate.now().dayOfWeek.value) {
                Bar(
                    value = item, color = MaterialTheme.colorScheme.primary, maxHeight = maxHeight
                )
            } else {
                Bar(
                    value = item, color = MaterialTheme.colorScheme.tertiary, maxHeight = maxHeight
                )
            }
            count++
        }
    }

}

@Composable
private fun RowScope.Bar(
    value: Float, color: Color, maxHeight: Dp
) {

    val itemHeight = remember(value) { value * maxHeight.value / 100 }

    Spacer(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .height(itemHeight.dp)
            .weight(1f)
            .background(color)
    )

}


