package com.example.finalproject;

import static com.example.finalproject.ImportantUtilities.showErrorMessage;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class TaxonomySettings extends AppCompatActivity {

    private ImageButton addTaxonomy;
    private Button submitButton;
    private Spinner taxOptions;
    private LinearLayout taxonomiesLayout;

    private ArrayList<SetMultTaxonomy> allTaxT1 = new ArrayList<>(); //all set multiple taxonomies
    private ArrayList<SetLocalWeightsTaxonomy> allTaxT2 = new ArrayList<>(); //all set local weights taxonomies

    private ArrayList<Characteristic> allCharacteristicsDeserialized;
    private String whatToGenerate;
    private LinkedHashSet<String> availableMultTaxEntries = new LinkedHashSet<>();//needed to avoid repetitive input
    private LinkedHashSet<String> availableLocalWeightsEntries = new LinkedHashSet<>();

    private Map<String, Characteristic> nameCharMap = new HashMap<>();
    private transient LinearLayout.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxonomy_settings);

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = 120;
        params.width = 120;

        submitButton = findViewById(R.id.button11);
        addTaxonomy = findViewById(R.id.imageButtonTax);
        taxOptions = findViewById(R.id.spinner);
        taxonomiesLayout = findViewById(R.id.taxEntriesLayout);
        allCharacteristicsDeserialized = (ArrayList<Characteristic>) getIntent().getSerializableExtra("allCharacteristicsSerialized");
        whatToGenerate = (String) getIntent().getSerializableExtra("whatToGenerate");

        for (Characteristic ch: allCharacteristicsDeserialized){
            availableMultTaxEntries.add(ch.getCharacteristicName());
            availableLocalWeightsEntries.add(ch.getCharacteristicName());
            nameCharMap.put(ch.getCharacteristicName(), ch);
        }

        addTaxonomy.setOnClickListener(view-> {
            addNewTaxonomy(taxOptions.getSelectedItem().toString().trim());
        });

        submitButton.setOnClickListener(view-> {
            if (setAllCharacteristicsAndReturnAsList()){ //if no errors in input data found
                GeneratorMode generatorMode = GeneratorMode.taxonomic;
                Intent intent = new Intent(this, GeneratorResult.class);
                intent.putExtra("generatorMode", (Serializable) generatorMode);
                intent.putExtra("allCharacteristicsSerialized", (Serializable) allCharacteristicsDeserialized);
                intent.putExtra("whatToGenerate", whatToGenerate);
                startActivity(intent);
            }
        });
    }

    private boolean setAllCharacteristicsAndReturnAsList() {
        for (SetMultTaxonomy multTax: allTaxT1){
            if (!multTax.setValues()) return false;
        }
        for (SetLocalWeightsTaxonomy weightTax: allTaxT2){
            if (!weightTax.setValues()) return false;
        }
        return true;
    }

    private void addNewTaxonomy(String taxonomyType){

        LinearLayout newTaxonomyLayout = new LinearLayout(this);
        newTaxonomyLayout.setOrientation(LinearLayout.VERTICAL);
        newTaxonomyLayout.setX(30);

        System.out.println(taxonomyType);
        switch(taxonomyType){
            case "Set Multiple Values":
                if (createSetMultValTaxonomyLayout(newTaxonomyLayout) == null){
                    return; //new taxonomy not available
                }
                break;
            case "Set Weights Locally":
                if (createLocalWeightsTaxonomyLayout(newTaxonomyLayout) == null){
                    return; //new taxonomy not available
                }
                break;
            case "Set Factors":
                //not implemented yet, may appear in the next versions
                break;
        }

        //separator line to make taxonomy readable
        //https://stackoverflow.com/questions/5049852/android-drawing-separator-divider-line-in-layout
        View dividerView = new View(this);

        TypedArray array = this.getTheme().obtainStyledAttributes(new int[] {android.R.attr.listDivider});
        Drawable draw = array.getDrawable(0);
        array.recycle();
        dividerView.setBackgroundDrawable(draw);
        newTaxonomyLayout.addView(dividerView);

        taxonomiesLayout.addView(newTaxonomyLayout);

    }

    private String createLocalWeightsTaxonomyLayout(LinearLayout parentLayout) {
        if (availableLocalWeightsEntries.size()==0){ //no more options
            return null;
        }
        SetLocalWeightsTaxonomy newTax = new SetLocalWeightsTaxonomy(parentLayout, this);

        return "Ok";
    }

    private String createSetMultValTaxonomyLayout(LinearLayout parentLayout) {
        if (availableLocalWeightsEntries.size()==0){ //no more options
            return null;
        }
        SetMultTaxonomy newTax = new SetMultTaxonomy(parentLayout, this);

        return "Ok";
    }

    private  Characteristic getCharByItsName(String name){
        return nameCharMap.get(name);
    }

    class SetMultTaxonomy{
        Context context;
        LinearLayout parentLayout;
        Button removeEntry;
        Button approveEntry;
        Spinner charChooser;
        Characteristic chosenChar;
        EditText lowerNumberLimit;
        EditText upperNumberLimit;

        public SetMultTaxonomy(LinearLayout parentLayout, Context context) {
            this.context = context;
            this.parentLayout = parentLayout;

            LinearLayout newTaxonomyLayoutHor = new LinearLayout(context);
            newTaxonomyLayoutHor.setOrientation(LinearLayout.HORIZONTAL);
            parentLayout.addView(newTaxonomyLayoutHor);
            removeEntry = new Button(context);
            removeEntry.setText("x");
            removeEntry.setLayoutParams(params);
            newTaxonomyLayoutHor.addView(removeEntry);
            TextView textView = new TextView(context);
            textView.setText("Characteristic: ");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            newTaxonomyLayoutHor.addView(textView);

            charChooser = new Spinner(context);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, new ArrayList<String>(availableMultTaxEntries));
            charChooser.setAdapter(dataAdapter);
            charChooser.setSelection(0);
            newTaxonomyLayoutHor.addView(charChooser);

            approveEntry = new Button(context);
            approveEntry.setText("✓");
            approveEntry.setLayoutParams(params);
            newTaxonomyLayoutHor.addView(approveEntry);

            removeEntry.setOnClickListener(view-> {
                allTaxT1.remove(this);
                charChooser.setEnabled(true);
                availableMultTaxEntries.add(charChooser.getSelectedItem().toString().trim());
                if (parentLayout!=null){
                    ViewGroup parent = (ViewGroup) parentLayout.getParent();
                    if (parent != null) {
                        parent.removeView(parentLayout);
                    }
                }
            });

            approveEntry.setOnClickListener(view-> {
                allTaxT1.add(this);
                approveEntry.setEnabled(false);
                chosenChar = getCharByItsName(charChooser.getSelectedItem().toString().trim());
                charChooser.setEnabled(false);

                if (chosenChar == null){
                    showErrorMessage(context, "Cannot find a chosen characteristic.");
                    return;
                }

                availableMultTaxEntries.remove(charChooser.getSelectedItem().toString().trim());
                LinearLayout newTaxonomyLayoutFromTo = new LinearLayout(context);
                newTaxonomyLayoutFromTo.setOrientation(LinearLayout.HORIZONTAL);
                parentLayout.addView(newTaxonomyLayoutFromTo);

                lowerNumberLimit = new EditText(context);
                lowerNumberLimit.setInputType(InputType.TYPE_CLASS_NUMBER);
                upperNumberLimit = new EditText(context);
                upperNumberLimit.setInputType(InputType.TYPE_CLASS_NUMBER);

                TextView textViewt = new TextView(context);
                textViewt.setText("From: ");
                textViewt.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);

                TextView textView2 = new TextView(context);
                textView2.setText("to: ");
                textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);

                TextView textView3 = new TextView(context);
                textView3.setText("(2 ... " + chosenChar.getValues().size() + ")");
                textView3.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                textView3.setTypeface(null, Typeface.ITALIC);

                newTaxonomyLayoutFromTo.addView(textViewt);
                newTaxonomyLayoutFromTo.addView(lowerNumberLimit);
                newTaxonomyLayoutFromTo.addView(textView2);
                newTaxonomyLayoutFromTo.addView(upperNumberLimit);
                newTaxonomyLayoutFromTo.addView(textView3);
            });
        }

        public boolean setValues() {
            try{
                int lowLimVal = Integer.valueOf(lowerNumberLimit.getText().toString());
                int upLimVal = Integer.valueOf(upperNumberLimit.getText().toString());
                if (lowLimVal>upLimVal){
                    throw new Exception("The lower number must not be bigger than the upper number.");
                }
                if (lowLimVal<2){
                    throw new Exception("Too low lower number limit.");
                }
                else {
                    chosenChar.setValuesNumberLowerLimit(lowLimVal);
                }
                if (upLimVal>chosenChar.getValues().size()){
                    throw new Exception("Too big upper number limit.");
                }
                else {
                    chosenChar.setValuesNumberUpperLimit(upLimVal);
                }
                chosenChar.setAllowMultipleValues(true);
            } catch (Exception e) {
                showErrorMessage(context, "Incorrect input! " + e.getMessage());
                return false;
            }
            return true;
        }
    }

    class SetLocalWeightsTaxonomy{
        Context context;
        LinearLayout parentLayout;
        Button removeEntry;
        Button approveEntry;
        Spinner charChooser;
        Characteristic chosenChar;
        Map <CharacteristicValue, EditText> valWeightMap = new HashMap<>();

        public SetLocalWeightsTaxonomy(LinearLayout parentLayout, Context context) {
            this.context = context;
            this.parentLayout = parentLayout;

            allTaxT2.add(this);

            LinearLayout newTaxonomyLayoutHor = new LinearLayout(context);
            newTaxonomyLayoutHor.setOrientation(LinearLayout.HORIZONTAL);
            parentLayout.addView(newTaxonomyLayoutHor);

            removeEntry = new Button(context);
            removeEntry.setText("x");
            removeEntry.setLayoutParams(params);
            newTaxonomyLayoutHor.addView(removeEntry);

            TextView textView = new TextView(context);
            textView.setText("Characteristic: ");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            newTaxonomyLayoutHor.addView(textView);

            charChooser = new Spinner(context);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, new ArrayList<String>(availableLocalWeightsEntries));
            charChooser.setAdapter(dataAdapter);
            charChooser.setSelection(0);
            newTaxonomyLayoutHor.addView(charChooser);

            approveEntry = new Button(context);
            approveEntry.setText("✓");
            approveEntry.setLayoutParams(params);
            newTaxonomyLayoutHor.addView(approveEntry);

            removeEntry.setOnClickListener(view-> {
                allTaxT2.remove(this);
                charChooser.setEnabled(true);
                availableLocalWeightsEntries.add(charChooser.getSelectedItem().toString().trim());
                if (parentLayout!=null){
                    ViewGroup parent = (ViewGroup) parentLayout.getParent();
                    if (parent != null) {
                        parent.removeView(parentLayout);
                    }
                }
            });

            approveEntry.setOnClickListener(view-> {
                chosenChar = getCharByItsName(charChooser.getSelectedItem().toString().trim());
                availableLocalWeightsEntries.remove(charChooser.getSelectedItem().toString().trim());
                approveEntry.setEnabled(false);
                charChooser.setEnabled(false);

                if (chosenChar == null){
                    showErrorMessage(context, "Cannot find a chosen characteristic.");
                    return;
                }

                LinearLayout newTaxonomyLayoutLocalWeights = new LinearLayout(context);
                newTaxonomyLayoutLocalWeights.setOrientation(LinearLayout.VERTICAL);
                parentLayout.addView(newTaxonomyLayoutLocalWeights);

               for (CharacteristicValue chV: chosenChar.getValues()){
                   LinearLayout newTaxonomyLayoutVal = new LinearLayout(context);
                   newTaxonomyLayoutVal.setOrientation(LinearLayout.HORIZONTAL);
                   newTaxonomyLayoutLocalWeights.addView(newTaxonomyLayoutVal);

                   TextView textView2 = new TextView(context);
                   textView2.setText(chV.getValueTextContent() + ", weight: ");
                   textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);

                   EditText valueWeight = new EditText(context);
                   valueWeight.setInputType(InputType.TYPE_CLASS_NUMBER);

                   valWeightMap.put(chV, valueWeight);
                   newTaxonomyLayoutVal.addView(textView2);
                   newTaxonomyLayoutVal.addView(valueWeight);
                }
            });
        }

        public boolean setValues() {
            try{
                for (Map.Entry<CharacteristicValue, EditText> entry : valWeightMap.entrySet()) {
                    int weight = Integer.valueOf(entry.getValue().getText().toString());
                    if (weight < 0){
                        throw new Exception("Weight must be more than 0!");
                    }
                    else{
                        entry.getKey().setWeightOfValue(weight);
                    }
                }
            } catch (Exception e) {
                showErrorMessage(context, "Incorrect input! " + e.getClass().getSimpleName());
                return false;
            }
            return true;
        }
    }
}