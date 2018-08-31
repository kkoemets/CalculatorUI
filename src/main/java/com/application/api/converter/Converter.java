package com.application.api.converter;

import com.application.api.Utils;

import java.util.HashMap;
import java.util.Map;


public class Converter {

    private VariableBase varBase;

    public Converter() {
        varBase = new VariableBase();
    }


    public void addVariable(String varName, String value) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }
        varBase.add(varName, value);
    }


    public String convertString(String string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        for (String value : varBase.getVariableBase().keySet()) {
            while (true) {
                int i;
                if ((i = stringBuilder.indexOf(value)) != -1) {
                    stringBuilder.replace(i, i + value.length(), varBase.getValue(value));
                } else {
                    break;
                }
            }
        }
        return stringBuilder.toString();
    }


    public void setVar(String string) {
        string = Utils.clean(string);
        int index;
        if ((index = string.indexOf('=')) == -1) {
//            return "Could not set variable! " + string + " does not contain an assignment(=) sign!";
            throw new IllegalArgumentException("Could not set variable! " + string + " does not contain an assignment(=) sign!");
        }
        addVariable(string.substring(0, index), string.substring(index + 1, string.length()));
    }


    public String getVars() {
        return varBase.getVariableBaseListed();
    }

    public Map<String, String> getVarBase() {
        return varBase.getVariableBase();
    }
}
