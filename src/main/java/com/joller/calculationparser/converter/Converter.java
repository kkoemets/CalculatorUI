package com.joller.calculationparser.converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


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
                "sum("
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

    public Command getCommandType(String line) {
        line = removeWhitespace(line);
        Command command = commandFinder.getCommandType(line);
        if (command == Command.UNKNOWN) {
            throw new ConverterException(line, "no command found!");
        }
        return command;
    }

    private String removeWhitespace(String line) {
        return line.replaceAll("\\s+","");
    }

    public String convertComment(String line) {
        sb.setLength(0);
        sb.append(removeCommandSyntax(line));
        return sb.toString();
    }

    public String convertSet(String line) {
        String variableName = getVariableName(line);
        String value = getEquation(line); // a real number is expected
        String unit = getUnit(line);
        try {
            variableContainer.add(variableName, value, unit);
        } catch (Exception e) {
            throw new ConverterException(line, "must not contain variables, use calcf instead");
        }
        return variableName + "=" + value + " " + unit;
    }

    public ResultContainer convertCalcf(String line) {
        String equation = getEquation(line);
        return new ResultContainer(
                getPrecision(line) + "",
                getUnit(line),
                getVariableName(line),
                equation,
                swapVariablesWithValues(equation)
        );
    }

    private String swapVariablesWithValues(String unCalculatedWithVariables) {
        StringBuilder sb = new StringBuilder(unCalculatedWithVariables);

        hashProtectedSymbols(sb);

        String[] arr = getSavedVariableNames();

        // have to start searching for longer variable names in case there are almost identical
        // variable names, e.g. L_1s and L_1s must be converted first in this case.
        sortArrByStringLengthDesc(arr);
        replaceVariableNamesWithValues(arr, sb);
        dehashProtectedSymbols(sb);
        return sb.toString();
    }

    private String getEquation(String line) {
        int indexEquals = line.indexOf('=');
        int indexComa = line.lastIndexOf(',');
        if (indexEquals  == -1) {
            throw new ConverterException(line, "does not contain '=' to find " +
                    "an equation");
        }
        if (indexComa == - 1) {
            throw new ConverterException(line, "does not contain coma to separate equation " +
                    "from unit");
        }
        if (indexEquals > indexComa) {
            throw new ConverterException(line, "'=' is before ','");
        }
        return line.substring(indexEquals + 1, indexComa);
    }

    private String getVariableName(String line) {
        int indexEquals = line.indexOf('=');
        int indexColon = line.indexOf(':');
        if (indexEquals  == -1) {
            throw new ConverterException(line, "does not contain '=' to set " +
                    "variable");
        }
        if (indexColon  == -1) {
            throw new ConverterException(line, "does not contain ':' to set " +
                    "variable");
        }
        if (indexColon > indexEquals) {
            throw new ConverterException(line, "':' is before '='");
        }
        String variableName = line.substring(indexColon + 1, indexEquals);
        if ( variableName.length() < 1) {
            throw new ConverterException(line, "missing variable name");
        }
        return variableName;
    }

    private String getUnit(String line) {
        String unit = line.substring(line.lastIndexOf(',') + 1);
        if (unit.isEmpty()) unit = " ";
        return unit;
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

        if (decimalPlace.isEmpty()) throw new ConverterException(line,
                "calcf() must contain decimal precision between parentheses");

        int precision;
        try {
            precision = Integer.parseInt(decimalPlace);
        } catch (NumberFormatException e) {
            throw new ConverterException(line,
                    "calcf() must contain decimal precision between parentheses");
        }

        return precision;
    }




    private String removeCommandSyntax(String line) {
       return line.substring(line.indexOf(':') + 1) ;
    }

    public void deleteVariables() {
        variableContainer.deleteAll();
    }
}


