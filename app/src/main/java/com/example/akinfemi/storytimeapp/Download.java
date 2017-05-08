package com.example.akinfemi.storytimeapp;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.InputStream;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * Created by akinfemi on 5/7/17.
 */

public class Download {
    private String api_path = "http://api.giphy.com/v1/gifs/search?&api_key=dc6zaTOxFJmzC&limit=1&rating=y";
    private String query = "&q=";
    private SimpleDraweeView draweeView;
    String param;

    Download(SimpleDraweeView draweeV, String param){
        this.draweeView = draweeV;
        this.param = api_path+ query + param;
    }
    void drawGif(){
        new DownloadTask().execute(param);
    }
    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params){
            String gif_url ="";
            InputStream is;
            URL url;
            try {
                url = new URL(params[0]);
                is = url.openStream();
                JsonReader rdr = Json.createReader(is);
                JsonObject obj = rdr.readObject();
                JsonArray results = obj.getJsonArray("data");
                JsonObject result = results.getValuesAs(JsonObject.class).get(0);
                gif_url = result.getJsonObject("images").getJsonObject("original").getString("url");

            }catch (Exception e){
                e.printStackTrace();
            }
            return gif_url;
        }

        @Override
        protected void onPostExecute(String gif_url){
            super.onPostExecute(gif_url);
            try {
                Log.d("url", gif_url);
                if(gif_url.length()!=0){
                    Uri uri = Uri.parse(gif_url);
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setUri(uri)
                            .setAutoPlayAnimations(true)
                            .build();
                    draweeView.setController(controller);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
