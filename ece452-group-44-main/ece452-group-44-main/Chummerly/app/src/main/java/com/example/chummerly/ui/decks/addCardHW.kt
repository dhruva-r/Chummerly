package com.example.chummerly.ui.decks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.mutableStateListOf


//This is the properties of the drawings
data class BigBlackDrawing(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.Black,
    val drawWidth: Dp = 5.dp
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDHandWritingScreen(
    onAddCard: () -> Unit,
    onBackClick: () -> Unit
) {

    //This will store the list of drawings
    val drawings = remember { mutableStateListOf<BigBlackDrawing>() }
  
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState(), reverseScrolling = true).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.Start)
                .background(
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

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Add Handwriting", modifier = Modifier.align(Alignment.Start).fillMaxWidth(), fontSize = 24.sp)

        Spacer(modifier = Modifier.height(24.dp))

        //This is the canvas that we will be drawing on
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(Color.LightGray)
                .pointerInput(true) {

                    //This detects if a finger or stylus is in contact with the screen
                    detectDragGestures { change, dragAmount -> change.consume()

                        //Making a drawing based on the start and end position of the drag gesture
                        val drawing = BigBlackDrawing(
                            start = change.position - dragAmount,
                            end = change.position,
                            color = Color.Black,
                            drawWidth = 5.dp
                        )

                        //Saving the drawing in list of drawings
                        drawings.add(drawing)

                    }
                }
        ) {
            //Drawing the saved drawings
            drawings.forEach { drawing -> drawLine (
                    color = Color.Black,
                    start = drawing.start,
                    end = drawing.end,
                    strokeWidth = drawing.drawWidth.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }


        Spacer(modifier = Modifier.height(24.dp))


        Button(
            onClick = onAddCard,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Done")
        }
    }
}