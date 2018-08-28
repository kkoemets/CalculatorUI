package com.application.api.common;

import java.util.*;


/**
 *  OperatorHandler handles mathematical operators and parentheses that are between numbers.
 */
public class OperatorHandler {
    private static OperatorHandler uniqueInstance;
    public final HashSet<Character> operators = createOperatorsSet();

    public static OperatorHandler getInstance() {
        if (uniqueInstance == null) {
            synchronized(OperatorHandler.class) {
                if (uniqueInstance == null)
                    uniqueInstance = new OperatorHandler();
            }
        }
        return uniqueInstance;
    }


    private HashSet createOperatorsSet() {
        Character[] operatorsArray = new Character[]{'*','/','+','-','(',')','^'};
        HashSet<Character> operators = new HashSet<>();
        Collections.addAll(operators, operatorsArray);
        return operators;
    }


    /**
     * Returns the index on the operator in the string.
     * @param string arithmetic equation that we are trying to inspect
     * @param operator operator we are trying to locate in the parameter string
     * @return returns index of the first instance of the operator given as parameter
     */
    public int findIndexOfOperator(String string, char operator) {
        // checking if the operator parameter is legit
        if(!operators.contains(operator)) throw new IllegalArgumentException(operator + "is not a mathematical operator!");
        int index;
        // checking if operator defined in the method parameter is in the string
        if((index = string.indexOf(operator)) == -1) throw new IllegalArgumentException("Mathematical operator \"" + operator + "\" not found!");
        return index;
    }


    /**
     * Returns the number on the left side of the operator.
     * @param string arithmetic equation that we are trying to inspect
     * @param indexOfOperator index of the operator that interests us in that string
     * @return returns the number on the left side of the operator
     */
    public String findLeftNumber(String string, int indexOfOperator) {
        String leftSubString = string.substring(0, indexOfOperator);
        for (int i = indexOfOperator - 1; 0 <= i; i--) {
            if (operators.contains(leftSubString.charAt(i))) { // an operator is met, returning result
                return leftSubString.substring(i + 1, indexOfOperator);
            }
        }
        return leftSubString; // if left side is empty, returning empty string
    }


    /**
     * Returns a number on the right side of the operator.
     * @param string arithmetic equation that we are trying to inspect
     * @param indexOfOperator index of the operator that interests us in that string
     * @return returns the number on the right side of the operator
     */
    public String findRightNumber(String string, int indexOfOperator) {
        String rightSubString = string.substring(indexOfOperator + 1, string.length());
        // There's a special case for power symbol and dealing with negatives - we have to consider that exponent could be a negative number
        // so moving 1 index to the right, just in case a negative sign
        int j = 0; // 0 by default
        if (string.charAt(indexOfOperator) == '^') j++; // trying to skip '-' sign by incrementing - w/o weird bugs there should be an number
        //
        for (int i = j; i < rightSubString.length(); i++) {
            if (operators.contains(rightSubString.charAt(i))) {
                return rightSubString.substring(0, i);
            }
        }
        return rightSubString; // if right side is empty, returning empty string
    }
}
