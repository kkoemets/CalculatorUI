package com.application;

import com.application.TextInputParser;
import com.application.api.converter.VariableBase;
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



    }
}