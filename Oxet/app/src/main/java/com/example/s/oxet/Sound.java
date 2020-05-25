package com.example.s.oxet;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class Sound {
    private SoundPool soundPool;
    public final int CORRECT, WRONG, DONE;

    public Sound(MainActivity mainActivity){
        soundPool = new SoundPool(5,AudioManager.STREAM_MUSIC, 0);
        CORRECT = soundPool.load(mainActivity, R.raw.correct, 1);
        WRONG = soundPool.load(mainActivity, R.raw.incorrect, 1);
        DONE = soundPool.load(mainActivity, R.raw.incorrect, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d("sound",sampleId+" loaded");
            }
        });
    }

    public void play(int id){
        soundPool.play(id, 1, 1, 0, 0, 1);
    }
}
