package com.application.api.calculator;

import com.application.api.calculator.utilities.ParenthesesHandler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    Calculator calculator;
    ParenthesesHandler parenthesesHandler;

    @BeforeEach
    void setup() {
        calculator = new Calculator();
        parenthesesHandler = ParenthesesHandler.getInstance();
    }


    @Test
    void calculatePlusMinus() throws Exception {
        assertEquals("0", calculator.calculatePlusMinus("1-1"));
        //
        assertEquals("76.25", calculator.calculatePlusMinus("115.0-3-2-2.0-12-24.0+2+2+0.25"));
        assertEquals("0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", calculator.calculatePlusMinus("0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000005714936956411374911078917741526705-0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000005714936956411374911078917741526705"));
    }


    @Test
    void calculateMultiplyDivision() {
        assertEquals("1", calculator.calculateMultiplyDivision("2*2/4"));
        assertEquals("1+1", calculator.calculateMultiplyDivision("1+2*2/4"));
        assertEquals("559.1688311688311688311688311688311", calculator.calculateMultiplyDivision("23*52/2*24*24/44/14*1"));
        assertEquals("-1-0.5", calculator.calculateMultiplyDivision("-1-1/2"));
        assertEquals("115-3-2-2-12-24+2+2+0.25", calculator.calculateMultiplyDivision("23*5-3-2-2/2*2-12-4*24/4+2+2+3/12*1"));
    }


    @Test
    void calculateEquation() {
        assertEquals("4", calculator.calculateEquation("2^2"));
        assertEquals("372.8", calculator.calculateEquation("(2.2+12*23-23*1-1+2+12+2.2)+2*5/10+(2.2+12*2*2*2-1*2/2+2.2)+2*5/10+1^2"));
        assertEquals("9", calculator.calculateEquation("(1.5+1.5)^2"));
        assertEquals("4", calculator.calculateEquation("(8*2^2)/8"));
        assertEquals("23", calculator.calculateEquation("2+(5*(2/2)+(2+2)^2)"));
        assertEquals("16384", calculator.calculateEquation("(2+2)^(4+(1+2))"));
        assertEquals("5", calculator.calculateEquation("25^(1/2)"));
        assertEquals("25", calculator.calculateEquation("(25^(1/2))^2"));
        assertEquals("0.25", calculator.calculateEquation("(2)^(-2)"));
        assertEquals("0.0000000000000000000000000000002508630777577274978450195672866494", calculator.calculateEquation("((2.2+12*23-23*1-1+2+12+2.2)+2*5/10+(2.2+12*2*2*2-1*2/2+2.2)+2*5/10+1^2)^(-(5/10+(2.2+2*2*2-1*2/2+2.2)))")); // java.lang.NumberFormatException: For input string: "2.508630777577306E"
        assertEquals("10000000000000000000000", calculator.calculateEquation("10^22"));
        assertEquals("560859.8476131146308034658432006836", calculator.calculateEquation("3.0135^12"));
        assertEquals("314563768664.6061654276962302797930", calculator.calculateEquation("3.0135^12*3.0135^12"));
        assertEquals("0.000005373000069134446940773815582531361", calculator.calculateEquation("3.0135^(-2)*3.0135^(-9)"));
        assertEquals("0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000005714936956411374911078917741526705", calculator.calculateEquation("2^(-333)"));
        assertEquals("0.000000059604644775390625", calculator.calculateEquation("2^(-24)"));
    }


    @Test
    void calculateEquation_test() {

    }
}

//  Assertions.assertThrows(NumberFormatException.class, () -> {
//        Integer.parseInt("One");
//        });