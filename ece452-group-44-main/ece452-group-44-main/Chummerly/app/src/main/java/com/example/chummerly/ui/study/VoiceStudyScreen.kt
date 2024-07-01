package com.example.chummerly.ui.study

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chummerly.data.entity.CardEntity
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit

fun answerVoice(context: Context, onResult: (String) -> Unit) {
    val sr = SpeechRecognizer.createSpeechRecognizer(context);
    val rl = object : RecognitionListener{
        override fun onResults(results: Bundle) {
            val s = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (s != null && s.size > 0) {
                // Complete transcription with first matched speech
                onResult(s[0]);
            } else {
                onResult("");
            }
        }
        override fun onBeginningOfSpeech()  { /* ... */}
        override fun onReadyForSpeech(params: Bundle?)  { /* ... */}
        override fun onRmsChanged(rmsdB: Float) { /* ... */}
        override fun onBufferReceived(buffer: ByteArray?) { /* ... */}
        override fun onEndOfSpeech() { /* ... */}
        override fun onError(error: Int) { println(error); /* ... */}
        override fun onPartialResults(partialResults: Bundle?) { /* ... */}
        override fun onEvent(eventType: Int, params: Bundle?) { /* ... */}
    }
    sr.setRecognitionListener(rl);

    // Start listening
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    sr.startListening(intent);
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceStudyScreen(
    voiceStudyViewModel: VoiceStudyViewModel = hiltViewModel(),
                     onClose: () -> Unit,
                     onFinishVoiceStudy: () -> Unit,
                     readText: (String) -> Unit
){

    val localContext = LocalContext.current

    // Used to request microphone access
    val hasMicAccess = remember { mutableStateOf( ContextCompat.checkSelfPermission( localContext, Manifest.permission.RECORD_AUDIO ) == PackageManager.PERMISSION_GRANTED ) }
    val launcher = rememberLauncherForActivityResult( contract = ActivityResultContracts.RequestPermission() ) { isGranted -> hasMicAccess.value = isGranted }

    // Check for microphone access
    if (!hasMicAccess.value) {
        LaunchedEffect(Unit) {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    val uiState by voiceStudyViewModel.uiState.collectAsState();
    val (currentCardIndex, setCurrentCardIndex) = remember { mutableStateOf(0) }
    var currentCard = CardEntity(deckId = 0, front = "", id = 0, back = "", font = "", tags = "")
    if(uiState.studyList.size > 0){ currentCard = uiState.studyList[currentCardIndex] }
    val (isFlipped, setIsFlipped) = remember { mutableStateOf(false) }

    val (currAnswer, setCurrAnswer) = remember { mutableStateOf("") }
    val (isAnswering, setIsAnswering) = remember { mutableStateOf(false) }
    val (isShowingModal, showModal) = remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState( skipPartiallyExpanded = false )

    var (confettiVisible,setConfettiVisible) = remember { mutableStateOf(false) }

    Box(
        modifier= Modifier
            .fillMaxSize()
    ) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(uiState.deck.name, fontWeight = FontWeight.ExtraBold)
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close Deck")
                }
            }
            Spacer(Modifier.height(4.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)
                ) {
                    if (!isFlipped) {
                        Text(currentCard.front, Modifier.padding(16.dp))
                    } else {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Your Answer",
                                fontWeight = FontWeight.Bold,
                                style = TextStyle(fontSize = 13.sp)
                            )
                            Spacer(Modifier.height(5.dp))
                            Text(
                                currAnswer,
                                fontWeight = FontWeight.Normal,
                                style = TextStyle(fontSize = 18.sp)
                            )
                            Spacer(Modifier.height(25.dp))
                            Text(
                                "Back Side",
                                fontWeight = FontWeight.Bold,
                                style = TextStyle(fontSize = 13.sp)
                            )
                            Spacer(Modifier.height(5.dp))
                            Text(
                                currentCard.back,
                                fontWeight = FontWeight.Normal,
                                style = TextStyle(fontSize = 18.sp)
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = { readText(if (!isFlipped) currentCard.front else currentCard.back) },
                        Modifier
                            .width(50.dp)
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Read Card")
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (isFlipped) {
                    Button(
                        onClick = {
                            setConfettiVisible(false);
                            if (currentCardIndex < uiState.studyList.size - 1) {
                                setCurrentCardIndex(currentCardIndex + 1)
                                setIsFlipped(false);
                            } else {
                                onFinishVoiceStudy()
                            }
                        },
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Next Card")
                    }
                } else {
                    Button(
                        enabled = (!isAnswering),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAnswering) MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.3f
                            ) else MaterialTheme.colorScheme.primary
                        ),
                        onClick = {
                            answerVoice(localContext) { transcription ->
                                setIsAnswering(false);
                                setCurrAnswer(transcription);
                                showModal(true);
                            };
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Mic Icon")
                        Text("Answer")
                    }
                }
            }
        }
        if (isShowingModal) {
            ModalBottomSheet(
                onDismissRequest = { showModal(false) },
                sheetState = bottomSheetState,
                windowInsets = WindowInsets(0)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 35.dp, start = 20.dp, end = 20.dp)
                ) {
                    Text(
                        text = "Your Answer",
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(fontSize = 13.sp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = currAnswer,
                        fontWeight = FontWeight.Normal,
                        style = TextStyle(fontSize = 18.sp)
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                    Button(
                        onClick = {
                            showModal(false);

                            if (currentCard.back.lowercase() == currAnswer.lowercase()) {
                                // Correct
                                Toast.makeText(localContext, "Correct!", Toast.LENGTH_LONG).show()
                                setConfettiVisible(true)
                            } else {
                                // Incorrect
                                Toast.makeText(localContext, "Sorry, that wasn't correct.", Toast.LENGTH_LONG).show()
                            }

                            setIsFlipped(true);
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        Text(text = "Continue", color = Color.White)
                    }
                    OutlinedButton(
                        onClick = {
                            setCurrAnswer("");
                            showModal(false);
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Try Again")
                    }
                }
            }
        }
        if (confettiVisible) {
            ConfettiAnimation()
            LaunchedEffect(Unit) {
                delay(6000)
                setConfettiVisible(false)
            }
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
        delay = 0
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
