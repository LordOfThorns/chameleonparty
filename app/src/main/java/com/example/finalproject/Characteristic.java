package com.example.finalproject;

import android.content.Context;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Contains all information of a single characteristic
 */
public class Characteristic implements Serializable {

    //transient modifier excludes the field from serialization that helps to avoid problems while transfer between intents
    private String characteristicName;

    public ArrayList<CharacteristicValue> getValues() {
        return values;
    }

    public void setCharacteristicName(String characteristicName) {
        this.characteristicName = characteristicName;
    }

    public void setValues(ArrayList<CharacteristicValue> values) {
        this.values = values;
    }

    private ArrayList<CharacteristicValue> values = new ArrayList<>();
    private transient LinearLayout newEntryLayout;
    private transient LinearLayout allValuesLayout;
    private transient LinearLayout newCharacteristicTitleLayout;
    private transient LinearLayout addValueLayout;
    private transient Button removeCharacteristic;
    private transient EditText editTextCharName;

    public boolean isAllowMultipleValues() {
        return allowMultipleValues;
    }

    private boolean allowMultipleValues = false;

    public int getValuesNumberLowerLimit() {
        return valuesNumberLowerLimit;
    }

    public int getValuesNumberUpperLimit() {
        return valuesNumberUpperLimit;
    }

    private int valuesNumberLowerLimit;

    public void setAllowMultipleValues(boolean allowMultipleValues) {
        this.allowMultipleValues = allowMultipleValues;
    }

    public void setValuesNumberLowerLimit(int valuesNumberLowerLimit) {
        this.valuesNumberLowerLimit = valuesNumberLowerLimit;
    }

    public void setValuesNumberUpperLimit(int valuesNumberUpperLimit) {
        this.valuesNumberUpperLimit = valuesNumberUpperLimit;
    }

    private int valuesNumberUpperLimit;

    public Integer getMultipleValuesNumber() {
        return multipleValuesNumber;
    }

    private Integer multipleValuesNumber = null;

    private transient ArrayList<Characteristic> parentList;

    private transient LinearLayout.LayoutParams params;

    public Characteristic(ArrayList<Characteristic> parentList) {
        this.parentList = parentList;
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = 120;
        params.width = 120;
    }

    public void addCharacteristic(Context context, LinearLayout characteristicsLayout){
        //Layout for one entry and its components
        newEntryLayout = new LinearLayout(context);
        newEntryLayout.setOrientation(LinearLayout.VERTICAL);

        allValuesLayout = new LinearLayout(context);
        allValuesLayout.setOrientation(LinearLayout.VERTICAL);

        addValueLayout = new LinearLayout(context);
        addValueLayout.setOrientation(LinearLayout.HORIZONTAL);
        addValueLayout.setX(30);

        addLayoutForCharInfo(newEntryLayout, context);

        newEntryLayout.addView(allValuesLayout);

        newEntryLayout.addView(addValueLayout);

        characteristicsLayout.addView(newEntryLayout);
    }

    private void addLayoutForCharInfo(LinearLayout parentLayout, Context context){
        newCharacteristicTitleLayout = new LinearLayout(context);
        newCharacteristicTitleLayout.setOrientation(LinearLayout.HORIZONTAL);

        removeCharacteristic = new Button(context);
        removeCharacteristic.setText("x");
        removeCharacteristic.setLayoutParams(params);

        removeCharacteristic.setOnClickListener(view-> {
            parentList.remove(this);
            if (newEntryLayout!=null){
                ViewGroup parent = (ViewGroup) newEntryLayout.getParent();
                if (parent != null) {
                    parent.removeView(newEntryLayout);
                }
            }
        });

        TextView textView = new TextView(context);
        textView.setText("Characteristic: ");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);

        editTextCharName = new EditText(context);

        newCharacteristicTitleLayout.addView(removeCharacteristic);
        newCharacteristicTitleLayout.addView(textView);
        newCharacteristicTitleLayout.addView(editTextCharName);
        parentLayout.addView(newCharacteristicTitleLayout);

        Button addNewValue = new Button(context);
        addNewValue.setText("+");
        addNewValue.setLayoutParams(params);


        addNewValue.setOnClickListener(view-> { //lamda
            addNewValue(context);
        });
        TextView textView2 = new TextView(context);
        textView2.setText("Add new value: ");
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        addValueLayout.addView(addNewValue);
        addValueLayout.addView(textView2);
    }

    public void addNewValue(Context context){
        CharacteristicValue newCharValue = new CharacteristicValue(values);
        values.add(newCharValue);
        newCharValue.addLayoutForCharValueInfo(allValuesLayout, context, values.size());

        LinearLayout addNewValueLayout = new LinearLayout(context);
        addNewValueLayout.setOrientation(LinearLayout.HORIZONTAL);

    }

    public String getCharacteristicName() {
        return characteristicName;
    }

    public void setActualValues() {
        this.characteristicName = String.valueOf(editTextCharName.getText());
        for (CharacteristicValue chV: values){
            chV.setActualValue();
        }
    }

}
