package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.ImageView;

import com.example.finalproject.networkapi.AIImageGenerator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageGenerationAI extends AppCompatActivity {

    ImageView generatedPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_generation_ai);

        generatedPicture = findViewById(R.id.imageView2);
        Button generate = findViewById(R.id.button9);
        Button findInGoogle = findViewById(R.id.button10);
        String description = (String) getIntent().getSerializableExtra("description");

        System.out.println(description);


        findInGoogle.setOnClickListener(view-> {

            Uri uri = Uri.parse("https://www.google.com/search?tbm=isch&q=" + description.replaceAll(" ", "+"));
            System.out.println("https://www.google.com/search?tbm=isch&q=" + description.replaceAll(" ", "+"));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        generate.setOnClickListener(view-> {

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
            executor.execute(() -> {
                //Background work here
                AIImageGenerator aiGen = new AIImageGenerator();
                Bitmap bmp = aiGen.getGeneratedImage(description);
                handler.post(() -> {
                    //UI Thread work here
                    if (bmp != null){
                        generatedPicture.setImageBitmap(bmp);
                        System.out.println("Succesfully loaded image");
                    }
                    else{
                        System.out.println("Problems occur");
                    }
                    generate.setEnabled(true);
                    generate.setText("Generate again using AI");
                });
            });
        });


    }
}