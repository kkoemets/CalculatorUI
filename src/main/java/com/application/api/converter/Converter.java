package com.application.api.converter;

public class Converter {

    public String convertString(String string, VariableBase varBase) {
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
}
