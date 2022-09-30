package com.example.finalproject;

import static com.example.finalproject.ImportantUtilities.showErrorMessage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.networkapi.AIImageGenerator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageGenerationAI extends AppCompatActivity {

    ImageView generatedPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_generation_ai);
        EditText tokenET = findViewById(R.id.editTextToken);
        generatedPicture = findViewById(R.id.imageView2);
        Button generate = findViewById(R.id.button9);
        Button findInGoogle = findViewById(R.id.button10);
        String description = (String) getIntent().getSerializableExtra("description");

        findInGoogle.setOnClickListener(view-> {

            Uri uri = Uri.parse("https://www.google.com/search?tbm=isch&q=" + description.replaceAll(" ", "+"));
            System.out.println("https://www.google.com/search?tbm=isch&q=" + description.replaceAll(" ", "+"));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        generate.setOnClickListener(view-> {

            String sToken = null;
            if (tokenET.getText().toString().trim().length() == 0 || tokenET.getText().toString().contains(" ")){
                showErrorMessage(this, "A token is required for a picture generation.");
                return;
            }
            else{
                sToken = tokenET.getText().toString().trim();
            }

            //prevents multiple clicks which may overload the AI API for ages
            generate.setEnabled(false);
            generate.setText("...generation is processing... please wait...");
            String uri = "@drawable/patient_yoda";
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);
            generatedPicture.setImageDrawable(res);

            //a modern replacement for AsyncTask which is deprecated:
            //https://stackoverflow.com/questions/58767733/the-asynctask-api-is-deprecated-in-android-11-what-are-the-alternatives
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            String finalSToken = sToken;
            executor.execute(() -> {
                //Background work here
                String errorOccurred = null;
                AIImageGenerator aiGen = new AIImageGenerator();
                Bitmap bmp = null;
                try {
                    bmp = aiGen.getGeneratedImage(description, finalSToken);
                } catch (Exception e) {
                    errorOccurred = e.getMessage();
                }
                Bitmap finalBmp = bmp;
                String finalErrorOccurred = errorOccurred;
                handler.post(() -> {
                    //UI Thread work here
                    if (finalBmp != null){
                        generatedPicture.setImageBitmap(finalBmp);
                        System.out.println("Succesfully loaded image");
                    }
                    else{
                        System.out.println("Problems occur");
                    }
                    generate.setEnabled(true);
                    generate.setText("Generate again using AI");
                    if (finalErrorOccurred != null){
                        showErrorMessage(this, String.valueOf(finalErrorOccurred));
                    }
                });
            });
        });


    }
}