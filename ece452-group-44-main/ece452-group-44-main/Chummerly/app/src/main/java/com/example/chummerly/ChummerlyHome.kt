package com.example.chummerly

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
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
@Composable
fun ChummerlyHome(
    onRandomReturnClick: () -> Unit = {},
    onDecksClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    // if there is an existing session present (either auto-save progress after quitting
    var savedSession by remember { mutableStateOf(false) }
    val onClicks = arrayOf(onDecksClick, onProfileClick, onSettingsClick)

    val icons = arrayOf(
        Icons.Filled.List,
        Icons.Filled.Person,
        Icons.Filled.Settings
    )
    val titles = arrayOf("Decks", "Profile", "Settings")
    val subtitles = arrayOf(
        "View a list of your decks.",
        "View your progress and information.",
        "Change your settings."
    )

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(color = MaterialTheme.colorScheme.primary)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Welcome To Chummerly!",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                fontSize = 36.sp,
                textAlign = TextAlign.Center
            )
        }
        Column(Modifier.padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 16.dp)) {
            Text(
                text = if (!savedSession) "Start a new random study session" else "Continue where you last left off",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                fontSize = 18.sp
            )
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    )
                    // TODO - handle clicks in navigation
                    .clickable { onRandomReturnClick() }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (!savedSession) Icons.Filled.Shuffle else Icons.Filled.KeyboardReturn,
                            contentDescription = if (!savedSession) "Shuffle Icon" else "Return Icon"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (!savedSession) "Random Study" else "Current Study Session",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Arrow Icon",
                            tint = Color.Black
                        )
                    }
                    Text(text = if (!savedSession) "Start studying a random deck." else "Back to your session.")
                }
            }
            for (i in 0..2) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        // TODO - handle clicks in navigation
                        .clickable {
                            val onClickCall = onClicks[i]
                            onClickCall()
                        }
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
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "Arrow Icon",
                                tint = Color.Black
                            )
                        }
                        Text(text = subtitles[i])
                    }
                }
            }
        }
    }
}
