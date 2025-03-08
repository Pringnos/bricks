package com.example.firebase.game;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import com.example.firebase.R;

import java.util.HashMap;

public class Audio {

    private static SoundPool soundPool;
    private static HashMap<String, Integer> soundMap = new HashMap<>();
    private static boolean isLoaded = false;

    // Initialize SoundPool
    public static void init(Context context) {
        if (soundPool != null) return;

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)  // Max simultaneous sounds
                .setAudioAttributes(audioAttributes)
                .build();

        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            if (status == 0) { // Success
                isLoaded = true;
            }
        });
    }

    public static void playSound(String soundName, float volume) {
        if (!isLoaded) return;

        Integer soundId = soundMap.get(soundName);
        if (soundId != null) {
            float finalVolume = Math.min(Math.max(volume, 0.0f), 1.0f); // Clamp volume
            soundPool.play(soundId, finalVolume, finalVolume, 1, 0, 1);
        }
    }
    public static void loadSound(Context context, String soundName, int resId) {
        if (soundMap.containsKey(soundName)) return;

        int soundId = soundPool.load(context, resId, 1);
        soundMap.put(soundName, soundId);
    }

    // Release resources when done
    public static void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            soundMap.clear();
            isLoaded = false;
        }
    }
}
