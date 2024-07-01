package com.example.chummerly.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chummerly.ui.study.FinishedStudyViewModel
import com.example.chummerly.ui.study.StudyViewModel
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit

@Composable
fun FinishedStudyScreen(
    finishedStudyViewModel: FinishedStudyViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    val uiState by finishedStudyViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var (confettiVisible,setConfettiVisible) = remember { mutableStateOf(true) }

    Box(
        modifier= Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (confettiVisible) {
            ConfettiAnimation()
            LaunchedEffect(Unit) {
                delay(6000)
                setConfettiVisible(false)
            }
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement=Arrangement.Center,
            horizontalAlignment=Alignment.CenterHorizontally
        ) {
            Text(
                text="Congratulations!",
                style=typography.headlineLarge,
                textAlign=TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(text="You completed ${uiState.deck.name}.")
        }
        Button(
            onClick=onFinish,
            modifier= Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text="Finish")
        }
    }
}

@Composable
fun ConfettiAnimation() {
    val party = Party(
        speed = 30f,
        maxSpeed = 50f,
        damping = 0.9f,
        angle = Angle.TOP,
        spread = 45,
        size = listOf(Size.SMALL, Size.LARGE),
        timeToLive = 3000L,
        rotation = Rotation(),
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
        emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(30),
        position = Position.Relative(0.5, 1.0),
        delay = 1000
    )

    KonfettiView(
        modifier = Modifier.fillMaxSize(),
        parties = listOf(
            party,
            party.copy(
                speed = 55f,
                maxSpeed = 65f,
                spread = 10,
                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(10),
            ),
            party.copy(
                speed = 50f,
                maxSpeed = 60f,
                spread = 120,
                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(40),
            ),
            party.copy(
                speed = 65f,
                maxSpeed = 80f,
                spread = 10,
                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(10),
            )
        )
    )
}

