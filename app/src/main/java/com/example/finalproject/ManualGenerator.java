package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.view.View;

import com.example.finalproject.networkapi.ImportantUtilities;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class ManualGenerator extends AppCompatActivity implements SensorListener {

    private LinearLayout characteristicsLayout;
    private ImageButton addCharacteristic;
    private Button setUpTaxonomy;
    private  Button generateNow;
    private ArrayList<Characteristic> allCharacteristics = new ArrayList<>();
    private ArrayList<LinearLayout> allLayouts = new ArrayList<>();//each layout contains necessary info about one characteristic
    private EditText whatToGenerate;
    private Switch shakingSensorOn;

    //sensor infos
    private static final int FORCE_THRESHOLD = 350;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 3;
    private SensorManager sensorMgr;
    private float mLastX=0, mLastY=0, mLastZ=0;
    private long mLastTime = System.currentTimeMillis();;
    private OnShakeListener mShakeListener;
    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;


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
            Intent intent = new Intent(this, TaxonomySettings.class);
            intent.putExtra("allCharacteristicsSerialized", (Serializable) allCharacteristics);
            intent.putExtra("whatToGenerate", String.valueOf(whatToGenerate.getText()));
            //for testing shaker - these lines of code induce shaking event
            sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onSensorChanged(SensorManager.SENSOR_ACCELEROMETER, new float[] {400, 400, 400});
            //startActivity(intent);
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
                ImportantUtilities.showErrorMessage(this, errorMessage);
/*                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage(errorMessage);
                alertDialogBuilder.setTitle("Incorrect input =(");
                alertDialogBuilder.setNegativeButton("ok", (dialogInterface, i) -> {
                    //nothing special
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();*/
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

    public interface OnShakeListener
    {
        public void onShake();
    }

    @Override
    public void onSensorChanged(int sensor, float[] values)
    {
        if (sensor != SensorManager.SENSOR_ACCELEROMETER || !shakingSensorOn.isChecked()) return;
        long now = System.currentTimeMillis();

        if ((now - mLastForce) > SHAKE_TIMEOUT) {
            mShakeCount = 0;
        }

        if ((now - mLastTime) > TIME_THRESHOLD) {
            long diff = now - mLastTime;
            float speed = Math.abs(values[SensorManager.DATA_X] + values[SensorManager.DATA_Y] + values[SensorManager.DATA_Z] - mLastX - mLastY - mLastZ) / diff * 10000;
            if (speed > FORCE_THRESHOLD) {
                if ((now - mLastShake > SHAKE_DURATION)) {
                    mLastShake = now;
                    mShakeCount = 0;
                    //for debugging
                    System.out.println("Shaking event!");
                    this.recreate();
                }
                mLastForce = now;
            }
            mLastTime = now;
            mLastX = values[SensorManager.DATA_X];
            mLastY = values[SensorManager.DATA_Y];
            mLastZ = values[SensorManager.DATA_Z];
        }
    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }
}