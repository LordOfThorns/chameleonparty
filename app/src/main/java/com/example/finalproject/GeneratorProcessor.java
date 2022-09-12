package com.example.finalproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The class performs all generation-related operations. As a result it normally returns a map of
 * values, i.e. characteristic name --> a single value/multiple values separated with a comma pairs
 */
public class GeneratorProcessor {

    public Map<String, String> generate(GeneratorMode generatorMode, ArrayList<Characteristic> allCharacteristics){

        Map<String, String> resultGenerationMap = new HashMap<>();

        switch(generatorMode) {
            case simple:
                resultGenerationMap = performSimpleGeneration(allCharacteristics, resultGenerationMap);
                break;

            case taxonomic:
                resultGenerationMap = performTaxonomicGeneration(allCharacteristics);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + generatorMode);
        }

        return resultGenerationMap;
    }

    private Map<String, String> performSimpleGeneration(ArrayList<Characteristic> allCharacteristics, Map<String, String> resultGenerationMap) {

        for (Characteristic characteristic:allCharacteristics){
           // characteristic.setActualValues();
            ArrayList<CharacteristicValue> charValues = characteristic.getValues();
            if (characteristic.isAllowMultipleValues()){
                int multipleValuesNumber;
                if (characteristic.getMultipleValuesNumber() != null) {
                    multipleValuesNumber = characteristic.getMultipleValuesNumber();
                }
                else{
                    multipleValuesNumber = getRandomNumber(2,charValues.size());
                }
                //TODO:complete this part - multiple values processing
            }
            else {
                String characteristicName = characteristic.getCharacteristicName();
                String generatedValue = charValues.get(getRandomNumber(0,charValues.size()-1)).getValueTextContent();
                resultGenerationMap.put(characteristicName, generatedValue);
            }
        }
        return resultGenerationMap;
    }

    private Map<String, String> performTaxonomicGeneration(ArrayList<Characteristic> allCharacteristics) {//TODO:complete method
        return null;
    }

    public int getRandomNumber(int min, int max) {
        int res = (int) Math.round(((Math.random() * (max - min)) + min));
        return res;
    }


}
