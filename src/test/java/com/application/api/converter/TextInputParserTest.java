package com.application.api.converter;

import com.application.TextInputParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TextInputParserTest {

    static TextInputParser textInputParser;
    @BeforeAll
    static void setup() {
        textInputParser = new TextInputParser(new VariableBase());
    }

    @Test
    void parse_test_input() {
        String input = "set: f_(ck)= 20, MPa // betooni tugevusklass";
        System.out.println(textInputParser.parse(input));
        input = "calcf(1): f_(cd) = f_(ck)/1.5, MPa // betooni tugevusklass";
        System.out.println(textInputParser.parse(input));




    }
}