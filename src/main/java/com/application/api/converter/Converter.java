package com.application.api.converter;

import com.application.api.common.OperatorHandler;
import com.application.api.Utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Converter {
    private VariableBase varBase;
    private OperatorHandler operatorHandler;
    private Set numbers;

    public Converter() {
        varBase = new VariableBase();
        operatorHandler = OperatorHandler.getInstance();
        numbers = createNumbersSet();
    }

    private HashSet createNumbersSet() {
        Character[] numbers = new Character[]{'0','1','2','3','4','5','6','7','8','9'};
        HashSet<Character> numbersSet = new HashSet<>();
        Collections.addAll(numbersSet, numbers);
        return numbersSet;
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
            int i;
            if ((i = stringBuilder.indexOf(value)) != -1) {
                stringBuilder.replace(i, i + value.length(), varBase.getValue(value));
            }
        }
        return stringBuilder.toString();
    }


    public String setVar(String string) {
        string = Utils.clean(string);
        int index;
        if ((index = string.indexOf('=')) == -1) {
            return "Could not set variable! " + string + " does not contain an assignment(=) sign!";
//            throw new IllegalArgumentException("Could not set variable! " + string + " does not contain an assignment(=) sign!");
        }
        addVariable(string.substring(0, index), string.substring(index + 1, string.length()));
        return string.substring(0, index) + " = " + string.substring(index + 1, string.length());
    }


    public String getVars() {
        return varBase.getVariableBaseListed();
    }
}
