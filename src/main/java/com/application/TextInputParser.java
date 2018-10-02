package com.application;

import com.application.api.Utils;
import com.application.api.calculator.Calculator;
import com.application.api.converter.Converter;
import com.application.api.converter.VariableBase;


import java.util.HashMap;
import java.util.Map;


/** Parses input string and outputs new string.
 */
public class TextInputParser extends Converter {

    private Map<String, Action> actionMap = createActionMap();
    private VariableBase variableBase;
    private Calculator calculator = new Calculator();

    public TextInputParser(VariableBase variableBase) {
        super(variableBase);
        this.variableBase = variableBase;
    }

    enum Action {
        MATH,
        COMMENT,
        VARIABLE
    }

    private Map<String, Action> createActionMap() {
        Map<String, Action> actionMap = new HashMap<>();
        actionMap.put("set", Action.VARIABLE);
        actionMap.put("c", Action.COMMENT);
        actionMap.put("calcf", Action.MATH);
        return actionMap;
    }


    /** Finds command in argument string and returns corresponding enum Action value.
     * @param string
     * @return
     *          - Action
     * @throws IllegalArgumentException if is not possible to determine Action in the string
     */
    private Action findAction(String string) throws IllegalArgumentException {
        for (String command : actionMap.keySet()) {
            int n;
            if ((n = command.length()) > string.length()) continue;
            if (string.substring(0, n).equals(command)) {
                return actionMap.get(command);
            }
        }
        throw new IllegalArgumentException(string + " no command found!");
    }


    /** Removes comments from argument string
     * @param string
     * @return
     *          - string without comments
     */
    private String removeComments(String string) {
        int commentIndex;
        if ((commentIndex = string.indexOf("//")) == -1) return string;
        return string.substring(0, commentIndex);
    }


    /** Main method of TextInputParser.
     * @param string
     *                - to be parsed
     * @return
     *                - new string
     * @throws IllegalArgumentException if unknown string for TextInputParser
     * @throws NullPointerException if null is passed as argument
     */
    public String parse(String string) throws IllegalArgumentException, NullPointerException {
        if (string == null) throw new NullPointerException(string);
        if (string.length() < 1) return "";
        Action action = findAction(string);
        switch (action)  {
            case MATH:
                string = Utils.clean(string);
                string = removeComments(string);
                return parseMath(string);
            case COMMENT:
                string = removeComments(string);
                return parseComment(string);
            case VARIABLE:
                string = Utils.clean(string);
                string = removeComments(string);
                return parseVariable(string);
        }
        return null;
    }


    private int indexOfColon(String string) {
        int colonIndex;
        if ((colonIndex = string.indexOf(":")) == -1) throw new IllegalArgumentException(string + " does not contain colon (':') sign.");
        return colonIndex;
    }


    private int indexOfComa(String string) {
        int comaIndex;
        if ((comaIndex = string.lastIndexOf(",")) == -1) throw new IllegalArgumentException(string + " does not have coma separator for identifying math equation answer unit.");
        return comaIndex;
    }


    private int indexOfEquals(String string) {
        int equalsIndex;
        if ((equalsIndex = string.lastIndexOf("=")) == -1) throw new IllegalArgumentException(string + " does not contain equals ('=') sign.");
        return equalsIndex;
    }


    /** Variable name is between ':' and '='
     * @param string
     * @param indexOfColon
     * @param indexOfEquals
     * @return
     *          - string representation of variables name
     */
    private String findVariableName(String string, int indexOfColon, int indexOfEquals) {
        String variableName;
        if ((variableName = string.substring(indexOfColon + 1, indexOfEquals)).isEmpty()) throw new IllegalArgumentException (string + " does not contain variable name");
        return variableName;
    }


    /** Arithmetic is between '=' and ','
     * @param string
     * @param indexOfEquals
     * @param indexOfComa
     * @return
     *          - string representation of arithmetic with possible variables
     */
    private String findArithmetic(String string, int indexOfEquals, int indexOfComa) {
        String arithmetic;
        if ((arithmetic = string.substring(indexOfEquals + 1, indexOfComa)).isEmpty()) throw new IllegalArgumentException (string + " does not contain mathematical expression");
        return arithmetic;
    }


    /** Units are after last ','
     * @param string
     * @param indexOfComa
     * @return
     *          - string representation of unit
     */
    private String findUnit(String string, int indexOfComa) {
        return string.substring(indexOfComa + 1);
    }


    /**
     * @param string
     * @return
     * @throws IllegalArgumentException
     */
    private String parseMath(String string) throws IllegalArgumentException {
        String[] arr = addToVariableBase(string);
        return arr[0] + "=" + arr[1] + "=" + arr[2] + "=" + arr[3] + ", " + arr[4];
    }


    /**
     * @param string
     * @return
     */
    private String parseVariable(String string) {
        String[] arr = addToVariableBase(string);
        return arr[0] + "=" + arr[1] + ", " + arr[4];
    }


    /** Parses string in case of ACTION.VARIABLE or ACTION.MATH and new variable to variable base
     * @param string
     * @return
     *          - String array length of 5
     *              arr[0] - string representation of variable name
     *              arr[1] - string representation of arithmetic
     *              arr[2] - string representation of calculations
     *              arr[3] - string representation of result
     *              arr[4] - string representation of unit
     * @throws IllegalArgumentException
     */
    private String[] addToVariableBase(String string) throws IllegalArgumentException {
        calculator.setDecimalPlace(2);
        int comaIndex = indexOfComa(string);
        int equalsIndex = indexOfEquals(string);
        int colonIndex = indexOfColon(string);
        String variableName = findVariableName(string, colonIndex, equalsIndex);
        String arithmetic = findArithmetic(string, equalsIndex, comaIndex);
        String unit = findUnit(string, comaIndex);
        String calculations = super.convertString(arithmetic);
        String result;
        if (findAction(string) == Action.MATH) {
            int indexOfParenthisis = colonIndex-1;
            String subString = string.substring(0, indexOfParenthisis);
            String decimalPlace = subString.substring(subString.indexOf('(') + 1);
            calculator.setDecimalPlace(Integer.parseInt(decimalPlace));
        }
        try {
            result = calculator.calculate(calculations);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
        variableBase.add(variableName, result, unit);
        return new String[]{variableName, arithmetic, calculations, result, unit};
    }


    /** Parses comment string
     * @param string
     * @return
     *          - string without comment tag 'c:'
     */
    private String parseComment(String string) {
        return string.substring(indexOfColon(string) + 1);
    }
}
