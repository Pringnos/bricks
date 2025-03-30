package com.example.firebase.game;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.example.firebase.R;

public class MyService extends Service {
    private MediaPlayer mediaPlayer;
    private boolean isPaused = false;
    private int resumePosition = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;  // We don't need to bind
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "PAUSE".equals(intent.getAction())) {
            pauseMusic();
            return START_STICKY;
        }
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.backround);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            Log.d("MusicService", "Music Started at Max Volume");
        }else if (isPaused) {  // RESUME LOGIC
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
            isPaused = false;
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d("MusicService", "Music Stopped");
        }
    }
    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            resumePosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            isPaused = true;
        }
}   }
