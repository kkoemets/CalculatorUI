package com.application.api.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConverterTest {
    Converter converter;
    @BeforeEach
    void setup() {
        converter = new Converter();
    }


    @Test
    void convertString() {
        converter.addVariable("p_d1", "2");
        converter.addVariable("p_d2", "2");
        converter.addVariable("p", "2");
        converter.addVariable("L", "2");
        assertEquals("2+2", converter.convertString("p_d1+p_d2"));
        assertEquals("((2+2))", converter.convertString("((p_d1+p_d2))"));
        assertEquals("(2*2^2)/8", converter.convertString("(p*L^2)/8"));
    }


    @Test
    void getVars() {
        converter.addVariable("p_d1", "2");
        converter.addVariable("p_d2", "2");
        converter.addVariable("p", "2");
        converter.addVariable("L", "2");
        String expected = "p=2\np_d1=2\np_d2=2\nL=2";
        assertEquals(expected, converter.getVars());
    }


    @Test
    void getVars_empty() {
        assertEquals("No variables have been set!", converter.getVars());
    }

    //TODO!!! implement setVar test method so that new var is being tested!
}