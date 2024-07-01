package com.example.chummerly.ui.decks

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MyDecksScreen(
    onDeckClick: (deckId: Long) -> Unit,
    myDecksViewModel: MyDecksViewModel = hiltViewModel()
) {
    val uiState by myDecksViewModel.uiState.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Decks",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.Sort,
                        contentDescription = "Deck Settings"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Sort Alphabetical") },
                        onClick = { myDecksViewModel.sortAlphabetical() }
                    )
                    DropdownMenuItem(
                        text = { Text("Sort By Newest") },
                        onClick = { myDecksViewModel.sortByNewest() }
                    )
                    DropdownMenuItem(
                        text = { Text("Sort By Oldest") },
                        onClick = { myDecksViewModel.sortByOldest() }
                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            OutlinedTextField(
                value = uiState.keyword,
                onValueChange = { myDecksViewModel.updateKeyword(it) },
                label = { Text("Search") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
                    .padding(horizontal = 30.dp)
            )
        }
        if (uiState.decks.isNotEmpty()) {
            LazyColumn() {
                items(uiState.decks) { deck ->
                    Button(
                        onClick = {
                            onDeckClick(deck.id)
                        },
                        colors = ButtonDefaults.buttonColors(Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 15.dp)
                            .padding(horizontal = 30.dp)
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp)),
                        contentPadding = PaddingValues(start = 0.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 12.dp)) {
                            Column() {
                                Text(
                                    text = deck.name,
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                if (!deck.categoryId.isNullOrBlank()) {
                                    Text(
                                        text = deck.categoryId,
                                        color = Color.Black,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Light
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                text = if (uiState.decksExist) "No matching results found." else "Create a deck with the + button on the navigation bar.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .padding(horizontal = 30.dp)
            )
        }
    }
}
