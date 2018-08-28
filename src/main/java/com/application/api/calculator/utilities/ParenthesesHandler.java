package com.application.api.calculator.utilities;

/**
 * ParenthesesHandler handles parentheses.
 * E.g. find the most nested formula in parentheses.
 */
public class ParenthesesHandler {

    private static ParenthesesHandler uniqueInstance;

    private ParenthesesHandler() {
    }


    public static ParenthesesHandler getInstance() {
        if (uniqueInstance == null) {
            synchronized(ParenthesesHandler.class) {
                if (uniqueInstance == null)
                    uniqueInstance = new ParenthesesHandler();
            }
        }
        return uniqueInstance;
    }


    /**
     * Returns the most nested parentheses because those have to be calculated first.
     * In case there are no nests it works like normal.
     * @param string
     * @return everything inside parentheses (inclusive)
     */
    public String findParentheses(String string) {
        return string.substring(getFormulaStartIndex(string), getFormulaEndIndex(string) + 1);
    }


    /**
     * Returns the index of the closing parenthesis.
     * In case of nested parentheses, returns index of the most nested right parenthesis.
     * @param string inspected string
     * @return index of right parenthesis
     */
    public int getFormulaEndIndex(String string) {
        int end;
        // in a nested parentheses the most nested right parenthesis is always first
        if ((end = string.indexOf(")")) == - 1) throw new IllegalArgumentException("Equation doesn't contain parentheses. String: " + string);
        return end;
    }


    /**
     * Returns the index of the opening parenthesis.
     * In case of nested parentheses, returns index of the most nested left parenthesis.
     * @param string
     * @return index of left parenthesis
     */
    public int getFormulaStartIndex(String string) {
        int end = getFormulaEndIndex(string);
        for (int i = end; 0 <= i; i--) {
            if (string.charAt(i) == '(') {
                return i;
            }
        }
        throw new IllegalArgumentException("Something is wrong! \"'('\" Not found! String:" + string);
    }


    /**
     * Removes parentheses from first and last index of the string.
     * Throws IllegalArgumentException if there's nothing to remove.
     * @param string
     * @return cleaned string without parentheses
     */
    public String removeParentheses(String string) {
        if (!(string.charAt(0) == '(') && !(string.charAt(string.length()) == ')')) throw new IllegalArgumentException("Incorrect string to remove braces. String: " + string);
        return string.substring(1, string.length() - 1);
    }
}
