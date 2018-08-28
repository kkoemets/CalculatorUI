package com.application.api.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperatorHandlerTest {
    private OperatorHandler operatorHandler;

    @BeforeEach
    void setup() {
        operatorHandler = operatorHandler.getInstance();
    }


    @Test
    void findIndexOfOperator() {
        assertEquals(3, operatorHandler.findIndexOfOperator("434+532", '+'));
        assertEquals(5, operatorHandler.findIndexOfOperator("44+52*12", '*'));
        assertEquals(8, operatorHandler.findIndexOfOperator("44+52*12/11", '/'));
        assertEquals(11, operatorHandler.findIndexOfOperator("44+52*12/11-", '-'));
        assertEquals(2, operatorHandler.findIndexOfOperator("44-52*12/11-", '-'));
        assertEquals(6, operatorHandler.findIndexOfOperator("598.04/44/14*1", '/'));
    }


    @Test
    void findIndexOfOperator_noOperator() {
        assertThrows(IllegalArgumentException.class, () -> {
            operatorHandler.findIndexOfOperator("44+52*12/11", '-');
        });
        assertThrows(IllegalArgumentException.class, () -> {
            operatorHandler.findIndexOfOperator("44+52*12/11", 's');
        });
    }


    @Test
    void findLeftNumber() {
        String equation = "11-12";
        String answer = operatorHandler.findLeftNumber(equation, operatorHandler.findIndexOfOperator(equation, '-'));
        assertEquals("11", answer);

        equation = "-11/12";
        answer = operatorHandler.findLeftNumber(equation, operatorHandler.findIndexOfOperator(equation, '/'));
        assertEquals("11", answer);

        equation = "23*5-3-2-2/2*2-12-4*24/4+2+2+4/14*1";
        answer = operatorHandler.findLeftNumber(equation, operatorHandler.findIndexOfOperator(equation, '*'));
        assertEquals("23", answer);
    }


    @Test
    void findLeftNumber_NotNumber() {
        String equation = "aa-12";
        String answer = operatorHandler.findLeftNumber(equation, operatorHandler.findIndexOfOperator(equation, '-'));
        assertEquals("aa", answer);

    }


    @Test
    void findLeftNumber_noNumber() {
        String equation = "+52";
        String answer = operatorHandler.findLeftNumber(equation, operatorHandler.findIndexOfOperator(equation, '+'));
        assertEquals("", answer);
    }


    @Test
    void findRightNumber() {
        String equation = "44+52*12/11-";
        int indexOperator = operatorHandler.findIndexOfOperator(equation, '+');
        assertEquals("52", operatorHandler.findRightNumber(equation, indexOperator));
        //
        equation = "23*5-3-2-2/2*2-12-4*24/4+2+2+4/14*1";
        indexOperator = operatorHandler.findIndexOfOperator(equation, '+');
        assertEquals("2", operatorHandler.findRightNumber(equation, indexOperator));
        //
        equation = "598.04/44/14*1";
        indexOperator = operatorHandler.findIndexOfOperator(equation, '/');
        assertEquals("44", operatorHandler.findRightNumber("598.04/44/14*1", indexOperator));

    }


    @Test
    void findRightNumber_notNumber() {
        String equation = "44+bb12/11-";
        int indexOperator = operatorHandler.findIndexOfOperator(equation, '+');
        assertEquals("bb12", operatorHandler.findRightNumber(equation, indexOperator));
    }


    @Test
    void findRightNumber_noNumber() {
        String arithmeticEquation = "44+52*12/11-";
        String answer = operatorHandler.findRightNumber(arithmeticEquation, operatorHandler.findIndexOfOperator(arithmeticEquation, '-'));
        assertEquals("", answer);
    }
}