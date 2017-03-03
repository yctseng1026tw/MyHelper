package com.eastioquick.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;

import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class VoiceHelper {
    private static TextToSpeech tts;
    public static int VOICE_RECOGNITION_REQUEST_CODE=12345;
    Context context;
    Locale locale;
    public VoiceHelper(Context context){
        this.context=context;
        this.locale=Locale.CHINESE;
        tts=getTTS();
    }
    public VoiceHelper(Context context,Locale locale){
        this.context=context;
        this.locale=locale;
        tts=getTTS();
    }
    private  TextToSpeech getTTS(){
        if(tts!=null)return tts;
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    //tts.setLanguage(Locale.CHINESE);
                    tts.setLanguage(locale);
                }
            }
        });
        return tts;
    }

    public void stopSpeak(){
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
    public void speak(String text){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    public void listen(int VOICE_RECOGNITION_REQUEST_CODE){
        VoiceHelper.VOICE_RECOGNITION_REQUEST_CODE=VOICE_RECOGNITION_REQUEST_CODE;
        //使用RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //設定辨識語言(這邊設定的是繁體中文)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-TW");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening...");
        //因為必須取得便是結果，所以用startActivityForResult來啟動Intent
        ((Activity)context).startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }
    public List listenResult( int resultCode, Intent data) {
        List rtn = null;
        if (resultCode == RESULT_OK) {
            // 取得 STT 語音辨識的結果段落
            rtn = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        }
        return rtn;
    }
}
