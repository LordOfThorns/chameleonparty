package com.example.finalproject;

import java.util.ArrayList;

public class Taxonomy {

    private ArrayList<TaxonomicFactorConfig> listOfTaxonomies = new ArrayList();
    //"If firstValue, probability of getting secondValue equals to factor. Overrides weights
    class TaxonomicFactorConfig{
        CharacteristicValue firstValue;
        Characteristic firstValueParentCharacteristic;
        CharacteristicValue secondValue;
        Characteristic secondValueParentCharacteristic;
        int factor;

        public TaxonomicFactorConfig(CharacteristicValue firstValue, Characteristic firstValueParentCharacteristic, CharacteristicValue secondValue, Characteristic secondValueParentCharacteristic, int factor) {
            this.firstValue = firstValue;
            this.firstValueParentCharacteristic = firstValueParentCharacteristic;
            this.secondValue = secondValue;
            this.secondValueParentCharacteristic = secondValueParentCharacteristic;
            this.factor = factor;
        }
    }

    public void addTaxonomicFactorConfigToTheList(CharacteristicValue firstValue, Characteristic firstValueParentCharacteristic, CharacteristicValue secondValue, Characteristic secondValueParentCharacteristic, int factor){
        TaxonomicFactorConfig taxonomicFactorConfig = new TaxonomicFactorConfig(firstValue, firstValueParentCharacteristic, secondValue, secondValueParentCharacteristic, factor);
        listOfTaxonomies.add(taxonomicFactorConfig);
    }
}
