package com.example.chummerly.ui.decks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chummerly.data.entity.CardEntity
import com.example.chummerly.data.entity.DeckEntity
import com.example.chummerly.model.Card
import com.example.chummerly.model.Deck
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


//  Jaccard similarity - appropriate because the order of words is not important,
//  and the presence/absence of words is more important than their relative frequencies
fun jaccardSimilarity(a: String, b: String): Double {
    val setA = a.split(" ").toSet()
    val setB = b.split(" ").toSet()

    val intersection = setA.intersect(setB).size
    val union = setA.union(setB).size

    return (intersection.toDouble() / union.toDouble())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergeDecksScreen(
    onBackClick: () -> Unit,
    onFinish: () -> Unit,
    mergeDecksViewModel: MergeDecksViewModel = hiltViewModel()
) {
    val uiState by mergeDecksViewModel.uiState.collectAsState()
    mergeDecksViewModel.loadCategories();

    // Merge 2 decks
    suspend fun mergeDecks(id_A: Long, id_B: Long) : Pair<Deck, List<Array<CardEntity>>> {
        var newDeck = Deck("New Deck", "", "");
        var similar_cards = mutableListOf<Array<CardEntity>>()

        // Use deck ID's to get cards
        mergeDecksViewModel.getCardsForDeckId(id_A, 0);
        mergeDecksViewModel.getCardsForDeckId(id_B, 1);
        delay(1000);
        val a_cards = uiState.cardsA;
        val b_cards = uiState.cardsB;

        // Merge decks, returning new deck with non-conflicting cards & pairs of conflicting cards
        var similar_cards_b = mutableListOf<Int>()
        for (a in a_cards) {
            var isSimilar = false
            for (b in b_cards) {
                // Compare cards
                val similarity = jaccardSimilarity(a.front, b.front)
                if (similarity >= 0.25) {
                    isSimilar = true
                    similar_cards_b.add(b.id.toInt())
                    similar_cards.add(arrayOf(a, b))
                    break
                }
            }
            if (!isSimilar) newDeck.addCard(a.id.toInt(), a.front, a.back, a.tags, a.font)
        }

        for (b in b_cards) {
            if (!similar_cards_b.contains(b.id.toInt())) newDeck.addCard(b.id.toInt(), b.front, b.back, b.tags, b.font)
        }

        return Pair(newDeck, similar_cards)
    }

    var decks = listOf<DeckEntity>()
    if (uiState.decks.isNotEmpty()) decks = uiState.decks

    var currentScreen by remember { mutableStateOf("start") }
    var selectedDeckA by remember { mutableStateOf<DeckEntity?>(null) }
    var selectedDeckB by remember { mutableStateOf<DeckEntity?>(null) }

    val (newDeck, setNewDeck) = remember { mutableStateOf<Deck?>(null) }
    val (currentIndex, setCurrentIndex) = remember { mutableStateOf(0) }
    val (similarCards, setSimilarCards) = remember { mutableStateOf<List<Array<CardEntity>>>(emptyList()) }
    val (mergeA, setMergeA) = remember { mutableStateOf(false) }
    val (mergeB, setMergeB) = remember { mutableStateOf(false) }

    val (newName, updateNewName) = remember { mutableStateOf("") }
    val (newCategory, updateNewCategory) = remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    val (newDescription, updateNewDescription) = remember { mutableStateOf("") }

    when (currentScreen) {
        "start" -> {
            Column(
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 25.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                }
                // Title
                Text(
                    text = "Merge Decks",
                    style = TextStyle(fontSize = 20.sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 25.dp)
                )
                Spacer(modifier = Modifier.height(25.dp))
                // Subtitle
                Text(
                    text = "Please choose two decks to merge.",
                    style = TextStyle(fontSize = 15.sp),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 15.dp, bottom = 25.dp)
                )
                // Selected Decks
                Button(
                    onClick = { currentScreen = "selectDeck_1" },
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = if (selectedDeckA != null) selectedDeckA!!.name else "Click to Select",
                            color = if (selectedDeckA == null) Color.Black.copy(alpha = 0.3f) else Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    onClick = { currentScreen = "selectDeck_2" },
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = if (selectedDeckB != null) selectedDeckB!!.name else "Click to Select",
                            color = if (selectedDeckB == null) Color.Black.copy(alpha = 0.3f) else Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                // Export button
                Button(
                    onClick = {
                        if (selectedDeckA != null || selectedDeckB != null) {
                            // Merge the decks
                            CoroutineScope(Dispatchers.Main).launch {
                                val (mergedDeck, conflicts) = mergeDecks(selectedDeckA!!.id, selectedDeckB!!.id)
                                setNewDeck(mergedDeck)
                                setSimilarCards(conflicts)
                                currentScreen = if (conflicts.isEmpty()) "finish" else "mergeConflicts"
                            }
                        }
                    },
                    enabled = ((selectedDeckA != null) && (selectedDeckB != null)),
                    colors = ButtonDefaults.buttonColors(containerColor = if ((selectedDeckA != null) && (selectedDeckB != null)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(text = "Merge Decks")
                }
            }
        }
        "selectDeck_1" -> {
            Column(
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 25.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { currentScreen = "start" },
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
                }
                // Title
                Text(
                    text = "Choose Deck",
                    style = TextStyle(fontSize = 20.sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 25.dp)
                )
                Spacer(modifier = Modifier.height(25.dp))
                // List of decks
                LazyColumn {
                    itemsIndexed(decks) { _, deck ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .background(Color.Transparent)
                                .padding(15.dp)
                                .clickable {
                                    selectedDeckA = deck
                                    currentScreen = "start"
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 0.dp)
                            ) {
                                Text(
                                    text = deck.name,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = if (deck.categoryId == null) "No Category" else deck.categoryId!!,
                                    textAlign = TextAlign.Start,
                                    color = Color.Black.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = deck.description,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

            }
        }
        "selectDeck_2" -> {
            Column(
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 25.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { currentScreen = "start" },
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
                }
                // Title
                Text(
                    text = "Choose Deck",
                    style = TextStyle(fontSize = 20.sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 25.dp)
                )
                Spacer(modifier = Modifier.height(25.dp))
                // List of decks
                LazyColumn {
                    itemsIndexed(decks) { _, deck ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .background(Color.Transparent)
                                .padding(15.dp)
                                .clickable {
                                    selectedDeckB = deck
                                    currentScreen = "start"
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 0.dp)
                            ) {
                                Text(
                                    text = deck.name,
                                    textAlign = TextAlign.Start,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = if (deck.categoryId == null) "No Category" else deck.categoryId!!,
                                    textAlign = TextAlign.Start,
                                    color = Color.Black.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = deck.description,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
        "mergeConflicts" -> {
            if (currentIndex < similarCards.size) {
                val currPair = similarCards[currentIndex]
                Column(
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 25.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { currentScreen = "start" },
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
                    }
                    // Title
                    Text(
                        text = "Similar Cards",
                        style = TextStyle(fontSize = 20.sp),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 25.dp)
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                    // Subtitle
                    Text(
                        text = "We’ve detected these cards have a high degree of similarity. Please select one or more cards to include in your merged deck.",
                        style = TextStyle(fontSize = 15.sp),
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(top = 15.dp)
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                    // Conflicting card from first deck
                    Text(
                        text = """From "${selectedDeckA!!.name}"""",
                        style = TextStyle(fontSize = 13.sp),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Button(
                        onClick = { setMergeA(!mergeA) },
                        border = BorderStroke(
                            width = 2.dp,
                            color = if (mergeA) Color.Green else MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.3f
                            )
                        ),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 10.dp)
                        ) {
                            Text(
                                text = "Front Side",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = currPair[0].front,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                            Text(
                                text = "Back Side",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = currPair[0].back,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    // Conflicting card from second deck
                    Text(
                        text = """From "${selectedDeckB!!.name}"""",
                        style = TextStyle(fontSize = 13.sp),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Button(
                        onClick = { setMergeB(!mergeB) },
                        border = BorderStroke(
                            width = 2.dp,
                            color = if (mergeB) Color.Green else MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.3f
                            )
                        ),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 10.dp)
                        ) {
                            Text(
                                text = "Front Side",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = currPair[1].front,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                            Text(
                                text = "Back Side",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = currPair[1].back,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        enabled = (mergeA || mergeB),
                        onClick = {
                            if (mergeA) newDeck?.addCard( currPair[0].id.toInt(), currPair[0].front, currPair[0].back, currPair[0].tags, currPair[0].font);
                            if (mergeB) newDeck?.addCard( currPair[1].id.toInt(), currPair[1].front, currPair[1].back, currPair[1].tags, currPair[1].font);
                            setMergeA(false);
                            setMergeB(false);
                            if (currentIndex + 1 < similarCards.size) setCurrentIndex(currentIndex + 1);
                            else currentScreen = "finish"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .align(Alignment.CenterHorizontally),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Continue")
                    }
                }
            }
        }
        "finish" -> {
            // Finish
            Column(
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 25.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { currentScreen = "start" },
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
                        enabled = ( newName.isNotEmpty() ),
                        onClick = {
                            mergeDecksViewModel.saveDeck(newName, newCategory.ifEmpty { null }, newDescription.ifEmpty { "" }, newDeck!!);
                            onFinish();
                        }
                    ) {
                        Text("Finish", fontSize = 16.sp)
                    }
                }
                // Title
                Text(
                    text = "Complete Merge",
                    style = TextStyle(fontSize = 20.sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 25.dp)
                )
                Spacer(modifier = Modifier.height(25.dp))
                Text(text = "Name", modifier = Modifier.align(Alignment.Start), fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = newName,
                    onValueChange = { updateNewName(it) },
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
                        value = newCategory ?: "",
                        onValueChange = {
                            if (it != "") {
                                updateNewCategory(it)
                            }
                        },
                        readOnly = true,
                        placeholder = { Text(text = "Select", color = Color.Black) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }
                    ) {
                        uiState.categories.forEach() {category ->
                            DropdownMenuItem(
                                text = { Text(text = category.name) },
                                onClick = {
                                    updateNewCategory(category.name)
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Description", modifier = Modifier.align(Alignment.Start), fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = newDescription,
                    onValueChange = {
                        updateNewDescription(it)
                    },
                    placeholder = { Text(text = "Deck description", color = Color.LightGray) },
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}
