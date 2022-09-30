package com.example.finalproject;

import static com.example.finalproject.ImportantUtilities.showErrorMessage;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.externallibfiles.ShakeDetector;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class ManualGenerator extends AppCompatActivity implements ShakeDetector.Listener {

    private LinearLayout characteristicsLayout;
    private ImageButton addCharacteristic;
    private Button setUpTaxonomy;
    private  Button generateNow;
    private ArrayList<Characteristic> allCharacteristics = new ArrayList<>();
    private EditText whatToGenerate;
    private Switch shakingSensorOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_generator);
        addCharacteristic = findViewById(R.id.imageButton);
        setUpTaxonomy = findViewById(R.id.button);
        generateNow = findViewById(R.id.button2);
        whatToGenerate = findViewById(R.id.editTextTextPersonName);
        characteristicsLayout = findViewById(R.id.linearLayout);
        shakingSensorOn = findViewById(R.id.switch1);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);

        addCharacteristic.setOnClickListener(view-> {
            Characteristic newCharacteristic = new Characteristic(allCharacteristics);
            allCharacteristics.add(newCharacteristic);
            newCharacteristic.addCharacteristic(this, characteristicsLayout);
            newCharacteristic.addNewValue(this);
        });

        setUpTaxonomy.setOnClickListener(view-> {
            for (Characteristic characteristic:allCharacteristics){
                characteristic.setActualValues();
            }
            String errorMessage = checkAndSendErrorMessageIfNeeded(allCharacteristics, String.valueOf(whatToGenerate.getText()));
            if (errorMessage==null){
                Intent intent = new Intent(this, TaxonomySettings.class);
                intent.putExtra("allCharacteristicsSerialized", (Serializable) allCharacteristics);
                intent.putExtra("whatToGenerate", String.valueOf(whatToGenerate.getText()));
                startActivity(intent);
            }
            else {
                showErrorMessage(this, errorMessage);
            }
        });

        generateNow.setOnClickListener(view-> {
            GeneratorMode generatorMode = GeneratorMode.simple;
            for (Characteristic characteristic:allCharacteristics){
                characteristic.setActualValues();
            }

            String errorMessage = checkAndSendErrorMessageIfNeeded(allCharacteristics, String.valueOf(whatToGenerate.getText()));
            if (errorMessage==null){
                Intent intent = new Intent(this, GeneratorResult.class);
                intent.putExtra("allCharacteristicsSerialized", (Serializable) allCharacteristics);
                intent.putExtra("generatorMode", (Serializable) generatorMode);
                intent.putExtra("whatToGenerate", String.valueOf(whatToGenerate.getText()));
                startActivity(intent);
            }
            else {
                showErrorMessage(this, errorMessage);
            }
        });
    }

    public static String checkAndSendErrorMessageIfNeeded(ArrayList<Characteristic> allCharacteristics, String whatToGenerate){
        if (allCharacteristics.size()<1){
            return "Please add at least one characteristic.";
        }
        else if (String.valueOf(whatToGenerate).trim().length()==0){
            return "Please specify what you want to generate.";
        }
        else {
            for (Characteristic ch: allCharacteristics){
                if (ch.getCharacteristicName().trim().length()==0){
                    return "Empty characteristic names are not allowed.";
                }
                if (ch.getValues().size()<1){
                    return "Please add at least one value for the characteristic " + ch.getCharacteristicName() + ".";
                }
                else{
                    for (CharacteristicValue chV: ch.getValues()){
                        if (chV.getValueTextContent().trim().length()==0){
                            return "Empty value names are not allowed.";
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override public void hearShake() {
        if (shakingSensorOn.isChecked()){
            this.recreate();
        }
    }
}