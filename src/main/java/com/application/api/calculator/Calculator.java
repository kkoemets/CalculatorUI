package com.application.api.calculator;

import com.application.api.common.OperatorHandler;
import com.application.api.calculator.utilities.ParenthesesHandler;

import java.math.BigDecimal;

import java.math.MathContext;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Calculator implementation.
 * Every calculation logic is done in this class
 */
public class Calculator {

    private String arithmeticEquation; //input equation
    private ParenthesesHandler parenthesesHandler; //helps with handling parentheses
    private OperatorHandler operatorHandler; //helps with handling operators(find left and right number from operators location mostly)
    private int decimalPlace;

    public Calculator() {
        parenthesesHandler = ParenthesesHandler.getInstance();
        operatorHandler = OperatorHandler.getInstance();
        decimalPlace = 2; // default
    }


    public String parseDoubleToString(double number) {
        return "";
    }


    /**
     * Sets the arithmetic equation which will be calculated
     * @param string mathematical arithmetic equation represented as a String
     */
    public void setArithmeticEquation(String string) {
        arithmeticEquation = string;
    }


    public void setDecimalPlace(int decimalPlace) {
        this.decimalPlace = decimalPlace;
    }


    public String calculate(String string) {
        if (arithmeticEquation.isEmpty()) throw new IllegalStateException("No arithmetic equation was set. Please use method setArithmeticEquation(String string)");
        String answer = calculateEquation(string);
        return answer.substring(0, answer.indexOf('.')+ decimalPlace + 1);
    }


    /**
     * This is the main method, where all methods are connected
     * @param string input equation
     * @return final calculation
     */
    public String calculateEquation(String string) {
        StringBuilder sb = new StringBuilder(string);
        while (sb.indexOf("(") != -1) {
            // if calculating powers is possible => base and exponent are not calculated braces
            if (sb.indexOf("^") != -1) {
                calculatePowerIfPossible(sb);
            }
            // now finding most nested parentheses and calculating it
            String mostNestedStr = parenthesesHandler.findParentheses(sb.toString()); // returned with parentheses
            int mostNestedStrStart = parenthesesHandler.getFormulaStartIndex(sb.toString());
            int mostNestedStrEnd = parenthesesHandler.getFormulaEndIndex(sb.toString());
            mostNestedStr = parenthesesHandler.removeParentheses(mostNestedStr);
            mostNestedStr = calculateEquation(mostNestedStr);
            sb.replace(mostNestedStrStart, mostNestedStrEnd +1, mostNestedStr);
        }
        calculatePowerIfPossible(sb);
        String sbStr = calculateMultiplyDivision(sb.toString());
        return calculatePlusMinus(sbStr);
    }


    /**
     * Calculates power if some conditions are met:
     * to calculate power, both base and exponent must be without parentheses
     * @param sb takes StringBuilder in as a parameter, replaces base and exponent with corresponding answer
     */
    public void calculatePowerIfPossible(StringBuilder sb) {
        for (int i = 1; i < sb.length() - 1; i++) {
            if (sb.charAt(i) == '^') {
                if (!sb.substring(i-1, i + 1).equals(")^") && !sb.substring(i, i + 2).equals("^(")) { // if true then base and exponent are numbers
                    // finding index of bases first char
                    String base = operatorHandler.findLeftNumber(sb.toString(), i);
                    // findinging index of exponents last char
                    String exponent = operatorHandler.findRightNumber(sb.toString(), i);
                    // calculating
                    String answer = "" + new BigDecimal(1.0).multiply(new BigDecimal(Math.pow(new BigDecimal(base).doubleValue(),new BigDecimal(exponent).doubleValue())), MathContext.DECIMAL128).toPlainString();
                    // replacing power in StringBuilder
                    sb.replace(i - base.length(), i + exponent.length() + 1, answer);
                    i = 0; // since StringBuilder was edited, it is safer to start all over
                }
            }
        }
    }


    /**
     * Multiplies and divides numbers
     * @param string equation
     * @return string with multiplication and division signs replaced with
     */
    public String calculateMultiplyDivision(String string) {
        while (true) {
            int indexOfOperator = string.indexOf('*'); // returns -1 if not found
            int divide = string.indexOf('/'); // returns -1 if not found
            if (divide != -1) {
                // must consider that operator with lower index must be calculated first
                if (indexOfOperator == -1 || indexOfOperator > divide) indexOfOperator = divide;
            }
            if (indexOfOperator == -1) break;
            string = calculateMultiplyDivisionOperator(string, string.charAt(indexOfOperator));
        }
        return string;
    }


    /**
     * Given string representation of equation and multiplication or division char, finds the first instance of it
     * then finds left and right numbers and replaces original strings part with the answer
     * @param string equation
     * @param operator '/' or '*'
     * @return
     */
    public String calculateMultiplyDivisionOperator(String string, char operator) throws NumberFormatException {
        if (operator != '/') {
            if (operator != '*') {
                throw new IllegalArgumentException(operator + " is not a multiplication or division operator!");
            }
        }
        int indexOperator = operatorHandler.findIndexOfOperator(string, operator);
        String leftNumber = operatorHandler.findLeftNumber(string, indexOperator);
        int leftNumberBeginningIndex = indexOperator - leftNumber.length();
        String rightNumber = operatorHandler.findRightNumber(string, indexOperator);
        int rightNumberEndingIndex = indexOperator + rightNumber.length();
            if (operator == '*') {
                    String answer = "" + new BigDecimal(leftNumber).multiply(new BigDecimal(rightNumber), MathContext.DECIMAL128).toPlainString();
                    return new StringBuilder(string).replace(leftNumberBeginningIndex, rightNumberEndingIndex + 1, answer).toString();
            }
            String answer = "" + new BigDecimal(leftNumber).divide(new BigDecimal(rightNumber), MathContext.DECIMAL128).toPlainString();
            return new StringBuilder(string).replace(leftNumberBeginningIndex, rightNumberEndingIndex + 1, answer).toString();
    }


    /**
     * Collects all numbers from string to queue and sums them up
     * String must not contain any other operators than '+' or '-'
     * @param string equation with numbers, sum and subtraction chars
     * @return sum of negative and positive numbers
     */
    public String calculatePlusMinus(String string) {
        Queue<BigDecimal> queue = new LinkedList<>();
        StringBuilder sb = new StringBuilder(string);
        BigDecimal sum = new BigDecimal(0.0);
        while (sb.length() > 0) {
            String number = findNumberForSummingRecursively(sb.toString());
            int n = number.length();
            if (number.charAt(0) == '+') number = number.substring(1, number.length());
            queue.add(new BigDecimal(number));
            sb.replace( sb.length() - n, sb.length(), "");
        }

        for (BigDecimal num : queue) {
            sum = sum.add(num, MathContext.DECIMAL128);
        }
        return "" + sum.toPlainString();
    }


    /**
     * Recursively goes through numbers until sum or subtraction symbols are met
     * Must not contain any other operators than '+' or '-'
     * @param string
     * @return
     */
    public String findNumberForSummingRecursively(String string) {
        if (string.isEmpty()) return "";
        if (string.charAt(string.length() - 1) == '-') return "-";
        if (string.charAt(string.length() - 1) == '+') return "+";
        return  findNumberForSummingRecursively(string.substring(0,string.length() - 1)) + string.charAt(string.length() - 1);
    }


    /**
     * @return
     */
    public String getArithmeticEquation() {
        return arithmeticEquation;
    }
}