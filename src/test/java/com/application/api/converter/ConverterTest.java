package com.application.api.converter;

import com.application.api.calculator.Calculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConverterTest {

    @Test
    void setup() throws Exception {
        Converter converter = new Converter();
        VariableBase variableBase = new VariableBase();
        Calculator calculator = new Calculator();
        variableBase.add("a","4","mm");
        variableBase.add("a","5","mm");
        String eq = "a+a";
        eq = converter.convertString(eq,variableBase);
        eq = calculator.calculate(eq);
        assertEquals("10.00", eq);

    }

    @Test
    void convertString() {
        Converter converter = new Converter();
        VariableBase variableBase = new VariableBase();
        String result = converter.convertString("pi()", variableBase);
        System.out.println(result);
    }
}