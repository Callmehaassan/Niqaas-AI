package com.nikaas.app.ui.common

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class UrduVoiceAlertManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val urduLocale = Locale("ur", "PK")
            val result = tts?.setLanguage(urduLocale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to English US voice if Urdu is not supported on user's system engine
                tts?.setLanguage(Locale.US)
            }
            isInitialized = true

            pendingText?.let {
                speak(it)
                pendingText = null
            }
        }
    }

    fun speak(text: String) {
        if (!isInitialized) {
            pendingText = text
            return
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UrduAlertTTS")
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
