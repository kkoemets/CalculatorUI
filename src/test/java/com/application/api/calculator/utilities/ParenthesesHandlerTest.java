package com.application.api.calculator.utilities;

import com.application.api.calculator.utilities.ParenthesesHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParenthesesHandlerTest {

    ParenthesesHandler parenthesesHandler;

    @BeforeEach
    void setup() {
        parenthesesHandler = ParenthesesHandler.getInstance();
    }

    @Test
    void findFormulaInParentheses() {
        String str = "3+(1+2*(2+1(1+2)^2)))";
        str = parenthesesHandler.findParentheses(str);
        assertEquals("(1+2)",str);
    }


    @Test
    void findFormulaInParentheses_NotNumbers() {
        String str = "as+(1ds+dds2*(2sdf+sdf1(1fsdf+gfdg2)^gfdg2)))";
        str = parenthesesHandler.findParentheses(str);
        assertEquals("(1fsdf+gfdg2)",str);
    }


    @Test
    void getFormulaEndIndex() {
        String str = "(1+2*(2+1(1+2)^2)))";
        int end = parenthesesHandler.getFormulaEndIndex(str);
        assertEquals(13, end);
    }

    @Test
    void getFormulaStartIndex() {
        String str = "3+(1+2*(2+1(1+2)^2)))";
        int start = parenthesesHandler.getFormulaStartIndex(str);
        assertEquals(11, start);
    }

    @Test
    void removeParentheses() {
        assertEquals("1+2*(2+1(1+2", parenthesesHandler.removeParentheses("(1+2*(2+1(1+2)"));
    }
}