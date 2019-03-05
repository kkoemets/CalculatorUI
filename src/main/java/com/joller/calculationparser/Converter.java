package com.joller.calculationparser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.joller.calculationparser.Command.*;


public class Converter {

    private VariableContainer variableContainer;
    private CommandFinder commandFinder;
    private StringBuilder sb;

    private String[] protectedSymbols;
    private Map<String, String> hashedProtectedSymbols;

    public Converter() {
        this.variableContainer = new VariableContainer();
        this.commandFinder = new CommandFinder();
        this.sb = new StringBuilder();

        this.protectedSymbols = new String[]{
                "sqrt(",
                "pi()",
                "min(",
                "max(",
        };
        this.hashedProtectedSymbols = createMapOfProtectedSymbols();
    }

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
    private void hashProtectedSymbols(StringBuilder sb) {
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
    private void dehashProtectedSymbols(StringBuilder sb) {
        for (String symbol : hashedProtectedSymbols.keySet()) {
            int startIndex;
            if ((startIndex = sb.indexOf(hashedProtectedSymbols.get(symbol))) != -1) {
                sb.replace(startIndex, startIndex + hashedProtectedSymbols.get(symbol).length(), symbol);
            }
        }
    }

    private String getSavedVariablesAsString() {
        return variableContainer.getVariableBaseListed();
    }

    public Command getCommandType(String line) throws IllegalArgumentException {
        line = removeWhitespace(line);
        return commandFinder.getCommandType(line);
    }

    private String removeWhitespace(String line) {
        return line.replaceAll("\\s+","");
    }

    public String parse(String line, Command command) {

        if (command == COMMENT) {
            return parseComment(line);
        }

        line = removeWhitespace(line);

        if (command == SET) {
            return parseSet(line);
        } else if(command == CALCF)
            return parseCalcf(line);
        else {
            return line + "<<< Error: Unknown command!";
        }
    }

    private String parseCalcf(String line) {
        int precision = getPrecision(line);

        sb.setLength(0);
        sb.append(removeCommandSyntax(line));

        String unit = line.substring(line.lastIndexOf(',') + 1);
        sb.replace(sb.lastIndexOf(",") , sb.length(), ""); // remove unit

        String variableName = getVariableName(line, sb);
        sb.replace(0, variableName.length() + 1,""); // remove variable name

        String calculationWithVariableNames = sb.toString();
        hashProtectedSymbols(sb);

        String[] arr = getSavedVariableNames();

        // have to start searching for longer variable names in case there are almost identical
        // variable names, e.g. L_1s and L_1s must be converted first in this case.
        sortArrByStringLengthDesc(arr);
        replaceVariableNamesWithValues(arr, sb);
        dehashProtectedSymbols(sb);
        String calculationWithoutVariableNames = sb.toString();

        String result = precision + "\n" + variableName + "\n" + calculationWithVariableNames
                + "\n" + calculationWithoutVariableNames + "\n" + unit;
        return result;
    }

    private void replaceVariableNamesWithValues(String[] arr, StringBuilder sb) {
        for (String variableName : arr) {
            while (true) { // while-loop in-case multiple occurrences of the same variable
                int i;
                if ((i = sb.indexOf(variableName)) != -1) {
                    sb.replace(
                            i, i + variableName.length(),
                            variableContainer.getValue(variableName)
                    );
                } else {
                    break;
                }
            }
        }
    }

    private void sortArrByStringLengthDesc(String[] arr) {
        Arrays.sort(arr, (a, b)->Integer.compare(b.length(), a.length()));
    }

    private String[] getSavedVariableNames() {
        String[] arr = new String[variableContainer.getLength()];
        int j = 0;
        for (String variableName : variableContainer.getVariableBase().keySet()) {
            arr[j] = variableName;
            j++;
        }
        return arr;
    }

    private int getPrecision(String line) {
        String decimalPlace = line.substring(line.indexOf('(') + 1, line.indexOf(')'));


        if (decimalPlace.isEmpty()) throw new IllegalArgumentException(line + "<<< Error: " +
            "calcf() must contain decimal precision between parentheses");

        int precision;
        try {
            precision = Integer.parseInt(decimalPlace);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(line + "<<< Error: " +
                    "calcf() must contain decimal precision between parentheses");
        }

        return precision;
    }

    private String parseComment(String line) {
        sb.setLength(0);
        sb.append(removeCommandSyntax(line));
        return sb.toString();
    }

    private String parseSet(String line) {
        sb.setLength(0);
        sb.append(removeCommandSyntax(line));

        String variableName = getVariableName(line, sb);

        removeVariableFromStringBuilder(sb, variableName);

        String value = getValue(line, sb);

        sb.replace(0, value.length() + 1, "");

        String unit = sb.toString();

        variableContainer.add(variableName, value, unit);

        return variableName + "=" + value + " " + unit;
    }

    private String getValue(String line, StringBuilder sb) {
        int indexComa = sb.lastIndexOf(",");
        if (indexComa == -1) {
            throw new IllegalArgumentException(line + "<<< Error: does not contain ',' to separate " +
                    "unit from value");
        }
        return sb.substring(0, indexComa);
    }

    private void removeVariableFromStringBuilder(StringBuilder sb, String variableName) {
        sb.replace(0, variableName.length() + 1, "");
    }

    public String getVariableName(String line, StringBuilder sb) {
        int indexEquals = sb.indexOf("=");
        if (indexEquals  == -1) {
            throw new IllegalArgumentException(line + "<<< Error: does not contain '=' to set " +
                    "variable");
        }
        return sb.substring(0, indexEquals);
    }

    public String removeCommandSyntax(String line) {
       return line.substring(line.indexOf(':') + 1) ;

    }
}


