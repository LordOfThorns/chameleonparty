package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;

import com.example.finalproject.networkapi.AIImageGenerator;

public class MainActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button4);

        button.setOnClickListener(view -> {
            Intent intent = new Intent(this, ManualGenerator.class);
            startActivity(intent);
        });

    }


}