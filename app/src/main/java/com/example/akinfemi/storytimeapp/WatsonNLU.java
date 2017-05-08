package com.example.akinfemi.storytimeapp;

import android.os.AsyncTask;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by akinfemi on 5/8/17.
 */

public class WatsonNLU {
    private String toAnalyze;
    private ArrayList<String> result= new ArrayList<String>();
    private String response;
    private JSONObject emotion = new JSONObject();


    WatsonNLU(String analyze){
        this.toAnalyze = analyze;
    }

    ArrayList<String> analyze() throws ExecutionException, InterruptedException {

        response = new WatsonUnderstandTask().execute(toAnalyze).get();

        JSONArray arr = null;
        try {
            arr = new JSONArray(response);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject object = arr.getJSONObject(i);
                result.add(object.getString("text"));
            }
            emotion = arr.getJSONObject(0).getJSONObject("emotion");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    JSONObject getEmo(){
        return emotion;
    }
    private class WatsonUnderstandTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
                    NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
                    "9af3dd26-8450-48fb-8878-c1b697a7330e",
                    "esyU4rPHDcMN"
            );
            String toAnalyze = params[0];
            KeywordsOptions keywords= new KeywordsOptions.Builder()
                    .sentiment(true)
                    .emotion(true)
                    .limit(3)
                    .build();

            Features features = new Features.Builder()
                    .keywords(keywords)
                    .build();

            AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                    .text(toAnalyze)
                    .features(features)
                    .build();

            AnalysisResults response = service
                    .analyze(parameters)
                    .execute();

            return response.getKeywords().toString();
        }

    }
}
