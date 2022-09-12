package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.Button;
import android.widget.TextView;

import com.example.finalproject.networkapi.AIImageGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class GeneratorResult extends AppCompatActivity {

    private Button generateAgain;
    private Button generatePicture;
    private Button backToMenu;

    private TextView titleTV;
    private TextView resultTV;

    private ArrayList<Characteristic> allCharacteristicsDeserialized;
    private GeneratorMode generatorMode;
    private String whatToGenerate;
    private Map <String, String> result = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_result);
        titleTV = findViewById(R.id.textView4);
        resultTV = findViewById(R.id.textView5);
        generateAgain = findViewById(R.id.button6);
        generatePicture = findViewById(R.id.button7);
        backToMenu = findViewById(R.id.button8);

        allCharacteristicsDeserialized = (ArrayList<Characteristic>) getIntent().getSerializableExtra("allCharacteristicsSerialized");
        generatorMode = (GeneratorMode) getIntent().getSerializableExtra("generatorMode");
        whatToGenerate = (String) getIntent().getSerializableExtra("whatToGenerate");
        result = generateAndShow();

        generateAgain.setOnClickListener(view-> {
            result = generateAndShow();
        });

        generatePicture.setOnClickListener(view-> {
            switchToImageGeneration();

        });
    }

    private Map <String, String> generateAndShow(){
        GeneratorProcessor generatorProcessor = new GeneratorProcessor();
        Map <String, String> result = generatorProcessor.generate(generatorMode, allCharacteristicsDeserialized);
        result.put("WhatToGenerate", whatToGenerate);
        String resultHTMLString = "";

        for (Map.Entry<String, String> entry : result.entrySet()) {
            if (!entry.getKey().equals("WhatToGenerate")){
                resultHTMLString += "<font color=\"#0000FF\"><b>" +  entry.getKey() + ":</b></font>&ensp;" + entry.getValue() + "<br><br>";
            }
        }
        resultHTMLString += "</tbody></table>";

        Spanned sp = Html.fromHtml(resultHTMLString);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resultTV.setText(Html.fromHtml(resultHTMLString, Html.FROM_HTML_MODE_COMPACT));
        } else {
            resultTV.setText(Html.fromHtml(resultHTMLString));
        }
        titleTV.setText("Result: " + result.get("WhatToGenerate"));

        return result;
    }

    private String createImageDescription (Map <String, String> resultMap){
        String imageDescription = "";
        imageDescription += resultMap.get("WhatToGenerate") + " ";
        for (Map.Entry<String, String> entry : result.entrySet()) {
            if (!entry.getKey().equals("WhatToGenerate")){
                imageDescription += entry.getValue() + " ";
            }
        }
        return imageDescription;
    }

    private void switchToImageGeneration(){
        String description = "";
        if (result!=null){
            description = createImageDescription(result);
        }

        System.out.println(description);

        Intent intent = new Intent(this, ImageGenerationAI.class);
        intent.putExtra("description", description);
        startActivity(intent);
    }

}