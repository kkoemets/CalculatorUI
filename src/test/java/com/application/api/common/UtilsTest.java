package com.application.api.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {

    @Test
    void cleanString_basic() {
        String string = "/ * /// 2323 +-*";
        assertEquals("/*///2323+-*", Utils.clean(string));
    }

    @Test
    void cleanString_empty() {
        String string = "";
        assertEquals("", Utils.clean(string));
    }

    @Test
    void cleanString_space() {
        String string = " ";
        assertEquals("", Utils.clean(string));
    }


    @Test
    void replaceComas() {
        String string = "2323,12+23,23";
        assertEquals("2323.12+23.23", Utils.replaceComas(string));
    }
}
