package com.example.firebase.game;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

    public class Texttospeech {
        private TextToSpeech t;
        private boolean isReady = false;

        public Texttospeech(Context context) {
            t = new TextToSpeech(context, status -> {
                if (status != TextToSpeech.ERROR) {
                    t.setLanguage(Locale.US);
                    isReady = true;
                }
            });
        }

        public void speak(String message) {
            if (isReady && t != null) {
                t.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }

        public void shutdown() {
            if (t != null) {
                t.stop();
                t.shutdown();
            }
        }
    }
