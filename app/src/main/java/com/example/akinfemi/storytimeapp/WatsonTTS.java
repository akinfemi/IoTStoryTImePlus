package com.example.akinfemi.storytimeapp;
import android.os.AsyncTask;

import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

/**
 * Created by akinfemi on 5/8/17.
 */

public class WatsonTTS {
    private String text;
    private String username = "cf568731-ac6c-4358-9a0e-7293ec0c68b3";
    private String password = "CPV1lka8XwBT";

    StreamPlayer streamPlayer;
    WatsonTTS (String text){
        this.text = text;
    }

    void Speak(){
        new WatsonTTSTask().execute(text);
    }

    private TextToSpeech textToSpeechService(){
        TextToSpeech service = new TextToSpeech();
        service.setUsernameAndPassword(username,password);
        return service;
    }

    private class WatsonTTSTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(final String... params) {

            TextToSpeech tts = textToSpeechService();
            streamPlayer = new StreamPlayer();
            streamPlayer.playStream(tts.synthesize(String.valueOf(params[0]), Voice.EN_ALLISON).execute());
//            synchronized (runnable) {
//
//            }
            return null;
        }
    }
}
