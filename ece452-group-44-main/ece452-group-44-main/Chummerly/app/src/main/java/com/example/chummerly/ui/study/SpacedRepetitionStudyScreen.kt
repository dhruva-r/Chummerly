package com.example.chummerly.ui.study



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
// Implemented with the assistance of chatGpt
@Composable
fun SpacedRepetitionStudyScreen(
    spacedStudyViewModel: SpacedStudyViewModel = hiltViewModel(),
    onClose: () -> Unit,
    onFinishSpacedStudy: () -> Unit,
    readText: (String) -> Unit
) {
    val uiState by spacedStudyViewModel.uiState.collectAsState()
    var (isFlipped, setIsFlipped) = rememberSaveable { mutableStateOf(false) }
    val currentCard = uiState.studyList.getOrNull(uiState.currentCardIndex)
    // Calculate the time remaining for the next review
    val deck = uiState.deck
    val currentTimeMillis = System.currentTimeMillis()
    val timeRemainingMillis = (uiState.nextReviewTimeMillis - currentTimeMillis).coerceAtLeast(0)

    Column (
        modifier=Modifier.fillMaxSize()
    ){
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            horizontalArrangement=Arrangement.SpaceBetween,
            verticalAlignment=Alignment.CenterVertically
        ) {
            Text(deck.name, fontWeight=FontWeight.ExtraBold)
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription="Close Deck")
            }
        }
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .clickable { setIsFlipped(!isFlipped) },
            contentAlignment=Alignment.TopStart
        ) {
            Card(
                modifier= Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(if (isFlipped) 0.86f else 1f)
            ) {
                if (currentCard != null) {
                    Text(
                        if (!isFlipped) currentCard.front else currentCard.back,
                                Modifier.padding(16.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick={
                        if (currentCard != null) {
                            readText(if (!isFlipped) currentCard.front else currentCard.back)
                        }
                    },
                    Modifier.width(50.dp).height(50.dp)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription="Read Card")
                }
            }
            if (isFlipped) {
                Row(
                    Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                    horizontalArrangement=Arrangement.SpaceBetween,
                    verticalAlignment=Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = {
                            // need help here setting the next card and updating the algorithm
                            if(uiState.currentCardIndex +1 < uiState.studyList.size){
                                spacedStudyViewModel.getNextCard(1)
                                setIsFlipped(false)
                            } else{
                                onFinishSpacedStudy()
                            }
                        },
                        Modifier.height(80.dp).fillMaxWidth().weight(1f),
                        shape=RoundedCornerShape(8.dp),
                    ) {
                        Column(
                            horizontalAlignment=Alignment.CenterHorizontally
                        ) {
                            Text("Again", fontWeight = FontWeight.Bold)
                            Text("1 min")
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = {
                            val currentCardIndexDebug = uiState.currentCardIndex
                            println("Current Card Index: $currentCardIndexDebug")
                            // need help here setting the next card and updating the algorithm
                            if(uiState.currentCardIndex +1 < uiState.studyList.size){
                                spacedStudyViewModel.getNextCard(2)
                                setIsFlipped(false)
                            } else{
                                onFinishSpacedStudy()
                            }

                        },
                        Modifier.height(80.dp).fillMaxWidth().weight(1f),
                        shape=RoundedCornerShape(8.dp),
                    ) {
                        Column(
                            horizontalAlignment=Alignment.CenterHorizontally
                        ) {
                            Text("Good", fontWeight = FontWeight.Bold)
                            Text("10 min")
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = {
                            // need help here setting the next card and updating the algorithm
                            if(uiState.currentCardIndex +1 < uiState.studyList.size){
                                spacedStudyViewModel.getNextCard(3)
                                setIsFlipped(false)
                            } else{
                                onFinishSpacedStudy()
                            }

                            print("card::" + uiState.currentCardIndex)


                        },
                        Modifier.height(80.dp).fillMaxWidth().weight(1f),
                        shape=RoundedCornerShape(8.dp),
                    ) {
                        Column(
                            horizontalAlignment=Alignment.CenterHorizontally
                        ) {
                            Text("Easy", fontWeight = FontWeight.Bold)
                            Text("1 day")
                        }
                    }
                }
            }
        }
        // Show the time remaining until the next review
        if (timeRemainingMillis > 0) {
            Text(
                text = "Next Review in ${timeRemainingMillis / 1000} seconds",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
            )
        }
    }
}

