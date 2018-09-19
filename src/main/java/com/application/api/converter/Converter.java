package com.application.api.converter;

import java.util.Arrays;

class TestRun {
}

public class Converter {

    public String convertString(String string, VariableBase varBase) {
        StringBuilder stringBuilder = new StringBuilder(string);
        String[] arr = new String[varBase.getLength()];
        int j = 0;
        for (String variableName : varBase.getVariableBase().keySet()) {
            arr[j] = variableName;
            j++;
        }
        // have to start searching for longer variable names incase there are almost identical variable names, e.g. L_1s and L_1
        // L_1s must be converted first in this case.
        Arrays.sort(arr, (a, b)->Integer.compare(b.length(), a.length()));
        for (String variableName : arr) {
            while (true) { // while-loop in-case multiple occurrences of the same variable
                int i;
                if ((i = stringBuilder.indexOf(variableName)) != -1) {
                    stringBuilder.replace(i, i + variableName.length(), varBase.getValue(variableName));
                } else {
                    break;
                }
            }
        }
        return stringBuilder.toString();
    }
}
