package com.eastioquick.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TTSService extends Service implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
    private TextToSpeech mTts;
    private String spokenText;
    private Locale locale;
    public final  static String SPOKEN_TEXT="SPOKEN_TEXT";
    public final  static String LANGUAGE="LANGUAGE";
    @Override
    public void onCreate() {
        mTts = new TextToSpeech(this, this);

        // This is a good place to set spokenText
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(locale);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                mTts.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        spokenText=intent.getStringExtra(SPOKEN_TEXT);
        locale=(Locale) intent.getSerializableExtra(LANGUAGE);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onUtteranceCompleted(String uttId) {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
