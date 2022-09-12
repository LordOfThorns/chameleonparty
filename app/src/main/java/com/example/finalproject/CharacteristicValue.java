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

public class CharacteristicValue implements Serializable {

    //transient modifier excludes the field from serialization that helps to avoid problems while transfer between intents
    private transient LinearLayout newCharacteristicValueLayout;
    private transient Button removeEntry;
    private int valueNumber;
    private transient ArrayList<CharacteristicValue> parentList;

    public String getValueTextContent() {
        return valueTextContent;
    }

    private String valueTextContent;

    private int weightOfValue = 1; //1 by default

    private transient EditText editTextValue;

    public CharacteristicValue(ArrayList<CharacteristicValue> parentList) {
        this.parentList = parentList;
    }

    /**
     * Adds one value line which contains input field
     * @param parentLayout LinerLayout allValuesLayout of parent Characteristic
     * @param context
     * @param valueNumber
     */
    public void addLayoutForCharValueInfo(LinearLayout parentLayout, Context context, int valueNumber){
        newCharacteristicValueLayout = new LinearLayout(context);
        newCharacteristicValueLayout.setOrientation(LinearLayout.HORIZONTAL);

        removeEntry = new Button(context);

        removeEntry.setOnClickListener(view-> {
            parentList.remove(this);
            if (newCharacteristicValueLayout!=null){
                ViewGroup parent = (ViewGroup) newCharacteristicValueLayout.getParent();
                if (parent != null) {
                    parent.removeView(newCharacteristicValueLayout);
                }
            }
        });

        TextView tv = new TextView(context);
        this.valueNumber = valueNumber;
        tv.setText("Value " + valueNumber + ": ");
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        editTextValue = new EditText(context);

        newCharacteristicValueLayout.addView(removeEntry);
        newCharacteristicValueLayout.addView(tv);
        newCharacteristicValueLayout.addView(editTextValue);
        newCharacteristicValueLayout.setX(30);

        parentLayout.addView(newCharacteristicValueLayout);
    }

    public void setActualValue() {
        valueTextContent = String.valueOf(editTextValue.getText());
    }

    @Override
    public String toString() {
        return "CharacteristicValue{" +
                "newCharacteristicValueLayout=" + newCharacteristicValueLayout +
                ", removeEntry=" + removeEntry +
                ", valueNumber=" + valueNumber +
                ", valueTextContent='" + valueTextContent + '\'' +
                ", editTextValue=" + editTextValue +
                '}';
    }
}
