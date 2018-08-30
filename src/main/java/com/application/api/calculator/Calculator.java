package com.application.api.calculator;

import com.application.api.calculator.utilities.CalculatorImplExcel;

import java.io.IOException;
import java.math.BigDecimal;

public class Calculator {

    private int decimalPlace;

    public Calculator() {
        decimalPlace = 2; // default
    }


    public void setDecimalPlace(int decimalPlace) {
        this.decimalPlace = decimalPlace;
    }


    /**
     * This method should be used to calculate equations
     * @param string input equation
     * @return final rounded calculation, default is 2 numbers after decimals
     */
    public String calculate(String string) throws IOException {
        CalculatorImplExcel calculatorImplExcel = new CalculatorImplExcel(); // formula will be calculated in an Excel sheet
        double answerWithExcel = 0;
        answerWithExcel = calculatorImplExcel.calculate(string); // calculating in excel
        return new BigDecimal(answerWithExcel).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).toPlainString();
    }
}