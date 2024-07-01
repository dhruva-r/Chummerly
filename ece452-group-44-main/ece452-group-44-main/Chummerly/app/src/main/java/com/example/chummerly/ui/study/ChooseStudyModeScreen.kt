package com.example.chummerly.ui.study

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Created using chatGpt and kotlin lib examples
@Composable
fun ChooseStudyModeScreen(
    onBackClick: () -> Unit,
    onClassicStudyClick : () -> Unit,
    onShuffledStudyClick : () -> Unit,
    onSpacedRepetitionClick : () -> Unit,
    onVoiceStudyClick : () -> Unit
) {
    var selectedBox = remember { mutableStateOf(-1) }

    val icons = arrayOf(Icons.Filled.Star,Icons.Filled.FavoriteBorder,Icons.Filled.List,Icons.Default.Face);
    val titles = arrayOf("Normal", "Shuffled", "Spaced Repetition", "Verbal");
    val subtitles = arrayOf("Study cards in order.", "Study cards in random order.", "Cards will repeat at optimal times.", "Answer using your voice.");

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
        }
        Text(text = "Start Studying", modifier = Modifier.align(Alignment.Start).padding(vertical = 16.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Column (Modifier.padding(vertical = 16.dp)) {
            for (i in 0..3) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = if (selectedBox.value == i) Color.Black else Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedBox.value = i }
                        .alpha(if (selectedBox.value == -1 || selectedBox.value == i) 1f else 0.5f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icons[i],
                                contentDescription = "Study Icon",
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = titles[i],
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(text = subtitles[i])
                    }
                }
            }
        }
        Button(
            onClick = (
                if (selectedBox.value == 0) {
                    onClassicStudyClick
                } else if(selectedBox.value == 1){
                    onShuffledStudyClick
                } else if(selectedBox.value == 2){
                    onSpacedRepetitionClick
                } else{
                    onVoiceStudyClick
                }
            ),
            enabled = selectedBox.value != -1,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Study")
        }
    }
}
