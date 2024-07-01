package com.example.chummerly

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChummerlyApp(readTextFunction = ::readText)
        }

        // Initialize text to speech
        tts = TextToSpeech(this, this);
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val lang = tts!!.setLanguage(Locale.US);
            if (lang != TextToSpeech.LANG_AVAILABLE) {
                println("TTS Initialization Failed.");
            }
        }
    }

    private fun readText(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "readText");
    }

    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop();
            tts!!.shutdown();
        }
        super.onDestroy()
    }
}
