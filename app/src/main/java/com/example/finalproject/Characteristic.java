package com.example.finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains all information of a single characteristic
 */
public class Characteristic implements Serializable {

    //transient modifier excludes the field from serialization that helps to avoid problems while transfer between intents
    private String characteristicName;

    public ArrayList<CharacteristicValue> getValues() {
        return values;
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

    public Integer getMultipleValuesNumber() {
        return multipleValuesNumber;
    }

    private Integer multipleValuesNumber = null;

    private transient ArrayList<Characteristic> parentList;


    public Characteristic(ArrayList<Characteristic> parentList) {
        this.parentList = parentList;
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

        //addLayoutForCharValueInfo(allValuesLayout, context);
        //addNewValue(allValuesLayout);

        newEntryLayout.addView(allValuesLayout);

        newEntryLayout.addView(addValueLayout);

        characteristicsLayout.addView(newEntryLayout);
    }

    private void addLayoutForCharInfo(LinearLayout parentLayout, Context context){
        newCharacteristicTitleLayout = new LinearLayout(context);
        newCharacteristicTitleLayout.setOrientation(LinearLayout.HORIZONTAL);

        removeCharacteristic = new Button(context);

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
        addNewValue.setWidth(addNewValue.getHeight());
        addNewValue.setText("+");
        addNewValue.setOnClickListener(view-> { //lamda
            addNewValue(context);
        });
        TextView textView2 = new TextView(context);
        textView2.setText("Add new value: ");
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        addValueLayout.addView(addNewValue);
        addValueLayout.addView(textView2);
    }

    private void addNewValue(Context context){
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

    @Override
    public String toString() {
        return "Characteristic{" +
                "characteristicName='" + characteristicName + '\'' +
                ", values=" + values +
                ", newEntryLayout=" + newEntryLayout +
                ", allValuesLayout=" + allValuesLayout +
                ", newCharacteristicTitleLayout=" + newCharacteristicTitleLayout +
                ", removeCharacteristic=" + removeCharacteristic +
                ", editTextCharName=" + editTextCharName +
                ", addValueLayout=" + addValueLayout +
                '}';
    }

}
