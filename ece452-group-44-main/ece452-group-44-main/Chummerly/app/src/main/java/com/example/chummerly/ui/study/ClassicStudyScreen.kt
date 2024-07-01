package com.example.chummerly.ui.study

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.chummerly.data.entity.CardEntity
import com.example.chummerly.ui.study.StudyViewModel


@Composable
fun ClassicStudyScreen(
    studyViewModel: StudyViewModel = hiltViewModel(),
    onClose: () -> Unit,
    onFinishStudy: () -> Unit,
    readText: (String) -> Unit
) {
    val uiState by studyViewModel.uiState.collectAsState();
    val (currentCardIndex, setCurrentCardIndex) = remember { mutableStateOf(0) }
    var currentCard = CardEntity(deckId = 0, front = "", id = 0, back = "", font = "", tags = "")
    if(uiState.studyList.size > 0){ currentCard = uiState.studyList[currentCardIndex] }

    val (isFlipped, setIsFlipped) = rememberSaveable { mutableStateOf(false) }

    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            horizontalArrangement=Arrangement.SpaceBetween,
            verticalAlignment=Alignment.CenterVertically
        ) {
            Text(uiState.deck.name, fontWeight=FontWeight.ExtraBold)
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
                    .fillMaxHeight(if (isFlipped) 0.9f else 1f)
            ) {
                Text(
                    if (!isFlipped) currentCard.front else currentCard.back,
                    Modifier.padding(16.dp)
                )
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick={ readText(if (!isFlipped) currentCard.front else currentCard.back )},
                    Modifier
                        .width(50.dp)
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription="Read Card")
                }
            }
            if (isFlipped) {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (currentCardIndex < uiState.studyList.size -1) {
                            setCurrentCardIndex(currentCardIndex + 1)
                            setIsFlipped(false);
                        } else {
                            onFinishStudy()
                        }
                    },
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    shape=RoundedCornerShape(8.dp)
                ) {
                    Text("Next Card")
                }
            }
        }
    }
}
