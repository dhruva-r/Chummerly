package com.example.chummerly.ui.decks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeckScreen(
    createDeckViewModel: CreateDeckViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onFinish: () -> Unit,
    onCreateCategory: () -> Unit
) {
    val uiState by createDeckViewModel.uiState.collectAsState()

    var isExpanded by remember {
        mutableStateOf(false)
    }

    //This holds the state of the dropdown menu categories
    var category by remember {
        mutableStateOf("")
    }

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
                    createDeckViewModel.saveDeck()
                    onFinish()
                }
            ) {
                Text("Save", fontSize = 16.sp)
            }
        }

        Text(text = "Create Deck", modifier = Modifier.align(Alignment.Start).padding(vertical = 16.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Text(text = "Name", modifier = Modifier.align(Alignment.Start), fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.deck.name,
            onValueChange = {
                createDeckViewModel.updateName(it)
            },
            placeholder = { Text(text = "Deck name", color = Color.LightGray) },
            singleLine = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Category", modifier = Modifier.align(Alignment.Start), fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = it }) {
            TextField(
                value = uiState.deck.categoryId ?: "",
                onValueChange = {
                    if (it != "") {
                        createDeckViewModel.updateCategory(it)
                    }
                },
                readOnly = true,
                placeholder = { Text(text = "Select", color = Color.Black) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            //Creating the exposed drop down menu
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {

                uiState.categories.forEach() {category ->
                    DropdownMenuItem(
                        text = { Text(text = category.name) },
                        onClick = {
                            createDeckViewModel.updateCategory(category.name)
                            isExpanded = false
                        }
                    )
                }

                DropdownMenuItem(
                    text = { Text(text = "+ Create new category") },
                    onClick = {
                        onCreateCategory()
                        isExpanded = false
                    }
                )
            }

        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Description", modifier = Modifier.align(Alignment.Start), fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.deck.description,
            onValueChange = {
                createDeckViewModel.updateDescription(it)
            },
            placeholder = { Text(text = "Deck description", color = Color.LightGray) },
            singleLine = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = {
//                if (uiState.deck.name != "") {
//                    createDeckViewModel.saveDeck()
//                }
//            },
//            modifier = Modifier
//                .align(Alignment.CenterHorizontally)
//                .fillMaxWidth(),
//            shape = RoundedCornerShape(8.dp)
//        ) {
//            Text(text = "Done")
//        }
    }
}