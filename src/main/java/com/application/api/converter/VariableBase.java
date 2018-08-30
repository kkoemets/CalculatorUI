package com.application.api.converter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Meant for storing variables with values in a HashMap
 * Key - string representation of variable name
 * Value - string representation of the variables value
 */
public class VariableBase {

    private Map<String, String> variableBase;

    public VariableBase() {
        variableBase = new LinkedHashMap<>();
    }


    public void add(String variable, String value) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }
        variableBase.put(variable, value);
    }


    public void deleteAll() {
        variableBase.clear();
    }


    public String getValue(String variable) {
        if (variableBase.containsKey(variable)) {
            return variableBase.get(variable);
        }
        return this + "Does not contain variable " + variable;
    }


    public boolean isEmpty() {
        return variableBase.isEmpty();
    }


    public String getVariableBaseListed() {
        StringBuilder sb = new StringBuilder();
        if (!isEmpty()) {
            for (String key : variableBase.keySet()) {
                sb.append("    " + key + "=" +variableBase.get(key)+"\n");
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
}
