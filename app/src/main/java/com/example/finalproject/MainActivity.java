package com.example.finalproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;

import com.example.finalproject.networkapi.AIImageGenerator;
import com.example.finalproject.networkapi.ImportantUtilities;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private Button userGuideButton;
    private Button generateByExcelInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button4);
        userGuideButton = findViewById(R.id.button5);
        generateByExcelInput = findViewById(R.id.button3);

        //TODO: too long method, may be better to rewrite
        ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Add same code that you want to add in onActivityResult method
                    if (result.getResultCode() == Activity.RESULT_OK){

                        ArrayList<Characteristic> allCharacteristics = new ArrayList<>();
                        String whatToGenerate = "";
                        Characteristic currentCharacteristic = null;

                        String previousCell = "";
                        Uri uri = result.getData().getData();
                        InputStream stream = null;
                        try {
                            stream = getContentResolver().openInputStream(uri);
                            HSSFWorkbook workbook = new HSSFWorkbook(stream);
                            HSSFSheet sheet = workbook.getSheetAt(0);
                            Iterator<Row> rowIterator = sheet.iterator();
                            while(rowIterator.hasNext()) {
                                Row row = rowIterator.next();
                                Iterator<Cell> cellIterator = row.cellIterator();
                                while(cellIterator.hasNext()) {

                                    Cell cell = cellIterator.next();
                                    String currentCellContent = cell.getStringCellValue();

                                    if (previousCell.trim().toLowerCase().equals("target")){
                                        whatToGenerate = currentCellContent;
                                    }
                                    else if (previousCell.trim().toLowerCase().contains("char")){
                                        currentCharacteristic = new Characteristic(allCharacteristics);
                                        currentCharacteristic.setCharacteristicName(currentCellContent);
                                        currentCharacteristic.setValues(new ArrayList<CharacteristicValue>());
                                        allCharacteristics.add(currentCharacteristic);
                                    }
                                    else if (previousCell.trim().toLowerCase().contains("val")){
                                        CharacteristicValue chV = new CharacteristicValue(currentCharacteristic.getValues());
                                        chV.setValueTextContent(currentCellContent);
                                        currentCharacteristic.getValues().add(chV);
                                    }
                                    previousCell = cell.getStringCellValue();
                                }
                            }

                            if (ManualGenerator.checkAndSendErrorMessageIfNeeded(allCharacteristics, whatToGenerate) != null){
                                throw new Exception("Incorrect input data");
                            }

                            GeneratorMode generatorMode = GeneratorMode.simple;
                            Intent intent = new Intent(this, GeneratorResult.class);
                            intent.putExtra("allCharacteristicsSerialized", (Serializable) allCharacteristics);
                            intent.putExtra("generatorMode", (Serializable) generatorMode);
                            intent.putExtra("whatToGenerate", whatToGenerate);
                            startActivity(intent);

                        } catch (IllegalArgumentException e) {
                            ImportantUtilities.showErrorMessage(this, "Prohibited format. " +
                                    "Please save the file as .xls. and try again.");
                        } catch (Exception e) {
                            ImportantUtilities.showErrorMessage(this, e.getMessage());
                        }
                        try {
                            stream.close();
                        } catch (IOException e) {}
                    }
                });

        button.setOnClickListener(view -> {
            Intent intent = new Intent(this, ManualGenerator.class);
            startActivity(intent);
        });

        userGuideButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, UserGuideActivity.class);
            startActivity(intent);
        });

        generateByExcelInput.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityIntent.launch(intent);
        });
    }

}