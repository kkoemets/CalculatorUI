package com.joller.calculator;

import java.io.IOException;
import java.math.BigDecimal;

public class Calculator {

    private int decimalPlace;
    private CalculatorImplExcel calculatorImplExcel;

    public Calculator() {
        decimalPlace = 2; // default
        calculatorImplExcel = new CalculatorImplExcel();
    }

    public void setDecimalPlace(int decimalPlace) {
        this.decimalPlace = decimalPlace;
    }

    public int getDecimalPlace() {
        return decimalPlace;
    }

    /**
     * This method should be used to calculate equations
     * @param string input equation, e.g. 2+2
     * @return final rounded calculation, default is 2 numbers after decimals
     */
    public String calculate(String string) throws IOException {
        // formula will be calculated in an Excel sheet
        double answerWithExcel;
        answerWithExcel = calculatorImplExcel.calculate(string); // calculating in excel
        return new BigDecimal(answerWithExcel).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    public String[] calculate(String[] strings) throws IOException {
        String[] output = new String[strings.length];
        double[] answers = calculatorImplExcel.calculate(strings);

        for (int i = 0; i < strings.length; i++) {
            output[i] = new BigDecimal(answers[i])
                    .setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).toPlainString();
        }

        return output;
    }
}