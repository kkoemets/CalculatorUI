package com.application.api.converter;

import com.application.api.common.OperatorHandler;
import com.application.api.common.Utils;
import com.application.api.converter.variablebase.VariableBase;

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
        varBase.add(varName, value);
    }

    public String convertString(String string) {
        StringBuilder sb = new StringBuilder(string);
        StringBuilder collector = new StringBuilder();
        while (sb.length() > 0) {
            if (operatorHandler.operators.contains(sb.charAt(0))) {
                collector.append(sb.charAt(0));
                sb.replace(0, 1, "");
            }
            String var = findVarRecursively(sb.toString());
            if (!var.isEmpty()) {
                if(numbers.contains(var.charAt(0))) {
                    collector.append(var);
                    sb.replace(0, var.length(), "");
                } else if (!varBase.contains(var)) {
//                    return "Could not convert! " + var + " has not been set!";
                    throw new IllegalArgumentException("Could not convert! Variable " + var + " has not been set!");
                } else {
                    collector.append(varBase.getValue(var));
                    sb.replace(0, var.length(), "");
                }
            }
        }
        return collector.toString();
    }

    private String findVarRecursively(String string) {
        if (string.isEmpty()) return "";
        if (operatorHandler.operators.contains(string.charAt(0))) {
            return "";
        }
        return string.charAt(0) + findVarRecursively(string.substring(1,string.length()));
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
