package com.application.api.converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/** Converts variables in mathematical expression to numbers
 */
public class Converter {

    private VariableBase varBase;
    public Converter(VariableBase variableBase) {
        varBase = variableBase;
    }

    private String[] protectedSymbols = new String[]{
            "sqrt(",
            "pi()",
            "min(",
            "max(",
    };

    private Map<String, String> hashedProtectedSymbols = createMapOfProtectedSymbols();


    private HashMap<String, String> createMapOfProtectedSymbols() {
        HashMap<String, String> hashedProtectedSymbols = new HashMap<>();
        for (String symbol : protectedSymbols) {
            hashedProtectedSymbols.put(symbol, "" + symbol.hashCode());
        }
        return hashedProtectedSymbols;
    }


    /** Converts MS Excel API to a hashcode.
     * e.g. we have an variable 'i' in variable base, Converter will try to convert pi() to p<value of 'i'>()
     *
     * @param sb
     */
    private void HashProtectedSymbols(StringBuilder sb) {
        for (String symbol : hashedProtectedSymbols.keySet()) {
            int startIndex;
            if ((startIndex = sb.indexOf(symbol)) != -1) {
                sb.replace(startIndex, startIndex + symbol.length(), hashedProtectedSymbols.get(symbol));
            }
        }
    }


    /** Converts hashcode to MS Excel API.
     * @param sb
     */
    private void DehashProtectedSymbols(StringBuilder sb) {
        for (String symbol : hashedProtectedSymbols.keySet()) {
            int startIndex;
            if ((startIndex = sb.indexOf(hashedProtectedSymbols.get(symbol))) != -1) {
                sb.replace(startIndex, startIndex + hashedProtectedSymbols.get(symbol).length(), symbol);
            }
        }
    }


    public String convertString(String string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        HashProtectedSymbols(stringBuilder);
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
        DehashProtectedSymbols(stringBuilder);
        return stringBuilder.toString();
    }
}
