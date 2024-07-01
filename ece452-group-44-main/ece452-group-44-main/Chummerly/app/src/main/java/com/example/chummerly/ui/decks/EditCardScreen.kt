package com.example.chummerly.ui.decks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EditCardScreen(
    editCardViewModel: EditCardViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onFinish: () -> Unit,
) {
    val uiState by editCardViewModel.uiState.collectAsState()
    var isFrontSide by remember { mutableStateOf(true) }
    var openDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(horizontal = 30.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
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
            TextButton(
                modifier = Modifier,
                onClick = {
                    editCardViewModel.updateCard()
                    onFinish()
                }
            ) {
                Text("Save", fontSize = 16.sp)
            }
        }

        Text(text = "Edit Card", modifier = Modifier.align(Alignment.Start).padding(vertical = 16.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = if (isFrontSide) uiState.card.front else uiState.card.back,
            onValueChange = {
                if (isFrontSide) {
                    editCardViewModel.updateFront(it)
                } else {
                    editCardViewModel.updateBack(it)
                }
            },
            label = { Text(if (isFrontSide) "Front Side" else "Back Side") },
            placeholder = {
                Text(
                    if (isFrontSide) "The front side of your card - typically used for questions, prompts, or pieces of information you would like to recall." else "The back side of your card - typically used to display the answer or additional information related to the front side.",
                    color = Color.LightGray
                )
            },
            singleLine = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                isFrontSide = !isFrontSide
            },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Flip Card")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.card.tags,
            placeholder = { Text(text = "Optional", color = Color.LightGray) },
            onValueChange = { editCardViewModel.updateTags(it) },
            label = { Text(text = "Tags") },
            modifier = Modifier.fillMaxWidth(),
        )

//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = colour,
//            placeholder = { Text(text = "Default", color = Color.LightGray) },
//            onValueChange = { colour = it },
//            label = { Text(text = "Card Colour") },
//            modifier = Modifier.fillMaxWidth()
//        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.card.font,
            placeholder = { Text(text = "Default", color = Color.LightGray) },
            onValueChange = { editCardViewModel.updateFont(it) },
            label = { Text(text = "Card Font") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                openDeleteDialog = true
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Delete Card")
        }
    }

    if (openDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openDeleteDialog = false
            },
            title = {
                Text(text = "Delete Card")
            },
            text = {
                Text(text = "Are you sure you want to delete this card?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDeleteDialog = false
                        editCardViewModel.deleteCard()
                        onFinish()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDeleteDialog = false
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }
}
