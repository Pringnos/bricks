package com.example.firebase.game;

import android.content.Context;

import java.util.Locale;

    public class TextToSpeech {
        private android.speech.tts.TextToSpeech t;
        private boolean isReady = false;

        public TextToSpeech(Context context) {
            t = new android.speech.tts.TextToSpeech(context, status -> {
                if (status != android.speech.tts.TextToSpeech.ERROR) {
                    t.setLanguage(Locale.US);
                    isReady = true;
                }
            });
        }

        public void speak(String message) {
            if (isReady && t != null) {
                t.speak(message, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }

        public void shutdown() {
            if (t != null) {
                t.stop();
                t.shutdown();
            }
        }
    }
