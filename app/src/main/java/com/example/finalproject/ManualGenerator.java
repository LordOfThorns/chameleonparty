package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManualGenerator extends AppCompatActivity {

    private LinearLayout characteristicsLayout;
    private ImageButton addCharacteristic;
    private Button setUpTaxonomy;
    private  Button generateNow;
    private ArrayList<Characteristic> allCharacteristics = new ArrayList<>();
    private ArrayList<LinearLayout> allLayouts = new ArrayList<>();//each layout contains necessary info about one characteristic
    private EditText whatToGenerate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_generator);
        addCharacteristic = findViewById(R.id.imageButton);
        setUpTaxonomy = findViewById(R.id.button);
        generateNow = findViewById(R.id.button2);
        whatToGenerate = findViewById(R.id.editTextTextPersonName);
        characteristicsLayout = findViewById(R.id.linearLayout);

        addCharacteristic.setOnClickListener(view-> { //lamda
            Characteristic newCharacteristic = new Characteristic(allCharacteristics);
            allCharacteristics.add(newCharacteristic);
            newCharacteristic.addCharacteristic(this, characteristicsLayout);
        });

        setUpTaxonomy.setOnClickListener(view-> {
            for (Characteristic characteristic:allCharacteristics){
                characteristic.setActualValues();
            }
            Intent intent = new Intent(this, TaxonomySettings.class);
            intent.putExtra("allCharacteristicsSerialized", (Serializable) allCharacteristics);
            intent.putExtra("whatToGenerate", String.valueOf(whatToGenerate.getText()));
            startActivity(intent);
        });

        generateNow.setOnClickListener(view-> {
            GeneratorMode generatorMode = GeneratorMode.simple;
            for (Characteristic characteristic:allCharacteristics){
                characteristic.setActualValues();
            }
            Intent intent = new Intent(this, GeneratorResult.class);
            intent.putExtra("allCharacteristicsSerialized", (Serializable) allCharacteristics);
            intent.putExtra("generatorMode", (Serializable) generatorMode);
            intent.putExtra("whatToGenerate", String.valueOf(whatToGenerate.getText()));
            startActivity(intent);
        });

    }
}