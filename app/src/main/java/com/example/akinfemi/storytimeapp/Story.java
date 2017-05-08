package com.example.akinfemi.storytimeapp;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class Story extends AppCompatActivity {

    private SimpleDraweeView draweeView;
    private String story_name;
    private ImageView play;
    private ArrayList<String> story = new ArrayList<String>();
    private ArrayList<String> keywords = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        play = (ImageView) findViewById(R.id.startbtn);
        final TextView txtview = (TextView) findViewById(R.id.text_display);
        setSupportActionBar(toolbar);
        Fresco.initialize(this);
        draweeView = (SimpleDraweeView) findViewById(R.id.gif_display);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                .build();
        draweeView.setController(controller);

        Intent i = getIntent();
        story_name = i.getStringExtra("StoryName");
        getSupportActionBar().setTitle(story_name);

        /*
            Load story file into memory
        */

        try {
            story = new LoadStory().execute(story_name).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
/*
Play Button Clicked
 */
        play.setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @Override
            public void onClick(View v) {

                play.animate().alpha(0f).setDuration(500).setInterpolator(new DecelerateInterpolator()).withEndAction(
                        new Runnable() {
                            @Override
                            public void run() {
                                play.animate().alpha(1f).setDuration(2000).setInterpolator(new AccelerateInterpolator()).start();
                            }
                        }
                );
                Log.i("Play:", "play button clicked!");
//                for (i = 0; i < story.size(); i++) {
                    try {
                        txtview.setText(story.get(i));
                        keywords = new WatsonNLU(story.get(i)).analyze();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                WatsonTTS textToSpeech = new WatsonTTS(story.get(i));
                textToSpeech.Speak();
                Download dwn = new Download(draweeView, keywords.get(0));
                dwn.drawGif();
                i++;
//                    Runnable runnable = new Runnable() {
//                        @Override
//                        public void run() {
////                            if (keywords.get(0) != null)
//                            Download dwn = new Download(draweeView, keywords.get(0));
////                            System.out.println("Run");
//                            dwn.drawGif();
//                        }
//                    };

//                    synchronized (runnable) {
//                        try {
//                            System.out.println(keywords.get(0));
//                            Thread.sleep(2000);
//                        }
//                        catch(InterruptedException e){
//                            e.printStackTrace();
//                        }
//                    }
//                }
            }
        });
    }



    private class LoadStory extends AsyncTask<String, Void, ArrayList> {
        InputStream is;
        @Override
        protected ArrayList<String> doInBackground(String... params) {

            if (params[0].equalsIgnoreCase("Pegasus (by Javier Vasquez)")){
                //load pegasus
                is = getResources().openRawResource(R.raw.pegasus);
            }
            else if (params[0].equalsIgnoreCase("Humpty Dumpty")){
                //load humpty dumpty
                is = getResources().openRawResource(R.raw.pegasus);
            }
            else if (params[0].equalsIgnoreCase("Three little mice")){
                //load Three little mice
                is = getResources().openRawResource(R.raw.pegasus);
            }
            try {
                if (is != null) {
                    story = readStory(is);
                }else{
                    System.out.println("NUll-- input stream");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return story;
        }

//        @Override
//        protected void onPostExecute(ArrayList<String> result){
//            Log.i("Load Story:", "Success");
//        }
    }

    private ArrayList<String> readStory(InputStream inputStream) throws IOException {

        String temp = "";
        String str = "";
        Log.i("Path?", inputStream.toString());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        while ((temp = bufferedReader.readLine()) != null){
            str += temp;
        }
        String[] tmp = str.split("\\.");
        story =  new ArrayList<String>(Arrays.asList(tmp));

        return story;
    }
}