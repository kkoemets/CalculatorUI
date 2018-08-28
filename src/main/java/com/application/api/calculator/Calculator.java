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

    private String arithmeticEquation;
    private ParenthesesHandler parenthesesHandler;
    private OperatorHandler operatorHandler;
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
     * @param string
     * @return
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
     * @param sb
     */
    public void calculatePowerIfPossible(StringBuilder sb) {
        for (int i = 1; i < sb.length() - 1; i++) {
            if (sb.charAt(i) == '^') {
                if (!sb.substring(i-1, i + 1).equals(")^") && !sb.substring(i, i + 2).equals("^(")) { // if true then base and exponent are numbers
                    String base = operatorHandler.findLeftNumber(sb.toString(), i);
                    String exponent = operatorHandler.findRightNumber(sb.toString(), i);
                    String answer = "" + new BigDecimal(1.0).multiply(new BigDecimal(Math.pow(new BigDecimal(base).doubleValue(),new BigDecimal(exponent).doubleValue())), MathContext.DECIMAL128).toPlainString();
                    sb.replace(i - base.length(), i + exponent.length() + 1, answer);
                    i = 0;
                }
            }
        }
    }


    /**
     * @param string
     * @return
     */
    public String calculateMultiplyDivision(String string) {
        while (true) {
            int indexOfOperator = string.indexOf('*');
            int divide = string.indexOf('/');
            if (divide != -1) {
                if (indexOfOperator == -1 || indexOfOperator > divide) indexOfOperator = divide;
            }
            if (indexOfOperator == -1) break;
            string = calculateMultiplyDivisionOperator(string, string.charAt(indexOfOperator));
        }
        return string;
    }


    /**
     * @param string
     * @param operator
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


//    public String calculatePlusMinus(String string) throws NumberFormatException {
//        Queue<Double> queue = new LinkedList<>();
//        StringBuilder sb = new StringBuilder(string);
//        double sum = 0;
//        while (sb.length() > 0) {
//            String number = findNumberForSummingRecursively(sb.toString());
//            int n = number.length();
//            if (number.charAt(0) == '+') number = number.substring(1, number.length());
//            queue.add(Double.parseDouble(number));
//            sb.replace( sb.length() - n, sb.length(), "");
//        }
//        for (Double num : queue) {
//            sum += num;
//        }
//        return "" + sum;
//    }

    public String calculatePlusMinus(String string) throws NumberFormatException {
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
