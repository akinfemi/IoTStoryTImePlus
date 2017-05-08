package com.example.akinfemi.storytimeapp;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.MalformedURLException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.Math.pow;

/**
 * Created by akinfemi on 5/8/17.
 */

public class Lighten {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    private String red;
    private String green;
    private String blue;
    Lighten(String red, String green, String blue){
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    void LightUp(){
        new LightenTask().execute(red, green, blue);
    }

    private class LightenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Double r = Double.parseDouble(params[0])*255;
            Double g = Double.parseDouble(params[1])*255;
            Double b = Double.parseDouble(params[2])*255;

            r = (r > 0.04045d) ? pow((r + 0.055d) / (1.0d + 0.055d), 2.4d) : (r / 12.92d);
            g = (g > 0.04045d) ? pow((g + 0.055d) / (1.0d + 0.055d), 2.4d) : (g / 12.92d);
            b = (b > 0.04045d) ? pow((b + 0.055d) / (1.0d + 0.055d), 2.4d) : (b / 12.92d);

            double X = r * 0.664511f + r * 0.154324f + r * 0.162028f;
            double Y = g * 0.283881f + g * 0.668433f + g * 0.047685f;
            double Z = b * 0.000088f + b * 0.072310f + b * 0.986039f;

            double x = X / (X + Y + Z);
            double y = Y / (X + Y + Z);

            String xs = Double.toString(x);
            String ys = Double.toString(y);

            System.out.println(x);
            System.out.println(y);

            RequestBody requestBody = RequestBody.create(JSON, "{\"xy\" :["+xs+","+ys+"]}");
            Request request = null;
            try {
                request = new Request.Builder()
                        .url("http://192.168.43.165/api/wI1ctifgI71yETIBvGZfa7ercS5BIetYxqxfZuQL/lights/4/state")
                        .put(requestBody)
                        .build();
                Response resp = client.newCall(request).execute();
                System.out.println(resp.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
