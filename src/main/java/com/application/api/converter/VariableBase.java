package com.application.api.converter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Meant for storing variables with values in a HashMap
 * Key - string representation of variable name
 * Value - string representation of the variables value
 */
public class VariableBase {

    private Map<String, String> variableBase;
    private Map<String, String> units;
    private int length;

    public VariableBase() {
        variableBase = new LinkedHashMap<>();
        units = new HashMap<>();
        length = 0;
    }


    public void add(String variable, String value, String unit) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }
        variableBase.put(variable, value);
        units.put(variable, unit);
        length++;
    }


    public void deleteAll() {
        variableBase.clear();
        units.clear();
        length = 0;
    }


    public String getValue(String variableName) {
        if (variableBase.containsKey(variableName)) {
            return variableBase.get(variableName);
        }
        return this + "Does not contain variable " + variableName;
    }


    public boolean isEmpty() {
        return variableBase.isEmpty();
    }


    public String getVariableBaseListed() {
        StringBuilder sb = new StringBuilder();
        if (!isEmpty()) {
            for (String key : variableBase.keySet()) {
                sb.append("    " + key + "=" +variableBase.get(key) + ", " + units.get(key) +"\n");
            }
            sb.replace(sb.lastIndexOf("\n"),sb.length(),"");
            return sb.toString();
        }
        return "No variables have been set!";
    }


    public boolean contains(String string) {
        return variableBase.containsKey(string);
    }


    public Map<String, String> getVariableBase() {
        return variableBase;
    }


    public String getUnit(String variableName) {
        return units.get(variableName);
    }

    public int getLength() {
        return length;
    }

}
