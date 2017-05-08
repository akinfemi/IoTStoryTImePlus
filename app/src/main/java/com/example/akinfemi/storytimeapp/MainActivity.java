package com.example.akinfemi.storytimeapp;

        import java.util.ArrayList;

        import java.util.Locale;
        import java.util.concurrent.ExecutionException;

        import android.app.AlertDialog;
        import android.content.ActivityNotFoundException;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.os.Bundle;
        import android.speech.RecognizerIntent;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.LinearLayout;
        import android.widget.Toast;

        import com.facebook.drawee.backends.pipeline.Fresco;
        import com.facebook.drawee.interfaces.DraweeController;
        import com.facebook.drawee.view.SimpleDraweeView;

        import org.json.JSONException;
        import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private final int SPEECH_RECOGNITION_CODE = 1;
    private ImageButton btnMicrophone;
    private SimpleDraweeView draweeView;
    private EditText tmpIn;
    JSONObject emotion = new JSONObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Starting", "App has started!");
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_main);
        draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                .build();
        draweeView.setController(controller);

        btnMicrophone = (ImageButton) findViewById(R.id.btn_mic);

        tmpIn = (EditText) findViewById(R.id.tempinput);

        btnMicrophone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });
    }

    /**
     * Start speech to text intent. This opens up Google Speech Recognition API dialog box to listen the speech input.
     * */
    private void startSpeechToText() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
         intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported on this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback for speech recognition activity
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    tmpIn.setText(text);
                    WatsonNLU watsonNLU = new WatsonNLU(text);
                    try {
                        ArrayList<String> response = watsonNLU.analyze();
                        emotion = watsonNLU.getEmo();
                        display(response);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
//                    new WatsonUnderstandTask().execute(text);
                }
                break;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if (id == R.id.add){
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Listen to a story instead?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent listenIntent = new Intent(getApplicationContext(), new_story.class);
                            startActivity(listenIntent);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void display(ArrayList<String> response) throws JSONException, ExecutionException, InterruptedException {

        Double anger;
        Double fear;
        Double joy;
        for (int i = 0; i < response.size(); i++){

            Download dwnld = new Download(draweeView, response.get(i));
            dwnld.drawGif();

            anger = Double.parseDouble(emotion.getString("anger").toString());
            fear = Double.parseDouble(emotion.getString("fear").toString());
            joy = Double.parseDouble(emotion.getString("joy").toString());

            final LinearLayout backgroundLL = (LinearLayout) findViewById(R.id.backgroundLL);

            backgroundLL.setBackgroundColor(android.graphics.Color.rgb((int)(anger * 255),(int) (joy * 255),(int)(fear*255)));

            Lighten lights = new Lighten( Double.toString(anger), Double.toString(joy), Double.toString(fear));
            lights.LightUp();
        }
    }
}