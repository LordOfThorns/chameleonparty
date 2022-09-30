package com.example.finalproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
                resultGenerationMap = performTaxonomicGeneration(allCharacteristics, resultGenerationMap);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + generatorMode);
        }

        return resultGenerationMap;
    }

    private Map<String, String> performSimpleGeneration(ArrayList<Characteristic> allCharacteristics, Map<String, String> resultGenerationMap) {

        for (Characteristic characteristic:allCharacteristics){
            ArrayList<CharacteristicValue> charValues = characteristic.getValues();
            String characteristicName = characteristic.getCharacteristicName();
            String generatedValue = charValues.get(getRandomNumber(0,charValues.size()-1)).getValueTextContent();
            resultGenerationMap.put(characteristicName, generatedValue);
        }
        return resultGenerationMap;
    }

    private Map<String, String> performTaxonomicGeneration(ArrayList<Characteristic> allCharacteristics, Map<String, String> resultGenerationMap) {

        for (Characteristic characteristic:allCharacteristics){
            String generatedValue = "";
            Map <Pair, CharacteristicValue> pairCharValMap = new HashMap<>();
            int higherThreshold = 0;
            for (CharacteristicValue chV : characteristic.getValues()){
                Pair pair = new Pair(higherThreshold, higherThreshold+chV.getWeightOfValue() - 1);
                pairCharValMap.put(pair, chV);
                higherThreshold += chV.getWeightOfValue();
            }

            if (characteristic.isAllowMultipleValues()){
                LinkedHashSet<String> resultMultVal = new LinkedHashSet<>();
                int multipleValuesNumber = getRandomNumber(characteristic.getValuesNumberLowerLimit(),characteristic.getValuesNumberUpperLimit());
                while (resultMultVal.size()<multipleValuesNumber){ //make sure that values are non repeatable
                    resultMultVal.add(getCharValueUsingPairMap(pairCharValMap, higherThreshold));
                }
                for (String val : resultMultVal){
                    generatedValue += val + ", ";
                }
                generatedValue = generatedValue.substring(0, generatedValue.length() - 2);
            }

            else {
                generatedValue = getCharValueUsingPairMap(pairCharValMap, higherThreshold);
            }
            String characteristicName = characteristic.getCharacteristicName();
            resultGenerationMap.put(characteristicName, generatedValue);
        }
        return resultGenerationMap;
    }

    public int getRandomNumber(int min, int max) {
        return (int) Math.round(((Math.random() * (max - min)) + min));
    }

    public String getCharValueUsingPairMap(Map <Pair, CharacteristicValue> pairCharValMap, int higherThreshold){
        int res = getRandomNumber(0,higherThreshold-1);
        for (Map.Entry<Pair, CharacteristicValue> entry : pairCharValMap.entrySet()) {
            if (entry.getKey().belongsToPair(res)){
                return entry.getValue().getValueTextContent();
            }
        }
        return "none";
    }

    class Pair{

        int lowerBorderOfValue;
        int upperBorderOfValue;

        public Pair(int lowerBorderOfValue, int upperBorderOfValue) {
            this.lowerBorderOfValue = lowerBorderOfValue;
            this.upperBorderOfValue = upperBorderOfValue;
        }

        public boolean belongsToPair(int n){
            if (n>=lowerBorderOfValue && n<=upperBorderOfValue){
                return true;
            }
            else {
                return false;
            }
        }
    }


}
