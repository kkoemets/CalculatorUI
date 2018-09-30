package com.application.api.mathml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

class MathMLConverterTest {

    MathMLConverter converter;

    @BeforeEach
    void setup() {
        converter = new MathMLConverter();
    }

    @Test
    void test_funcionality() {
        converter.addLine("hello");
        converter.addLine("f_(cd)=a+b");
        converter.convert();
        LinkedList<String> lines = converter.getAllLinesAsList();
        WordWriter wordWriter = new WordWriter();
        for (String line : lines) {
            wordWriter.addLine(line);
        }
        wordWriter.write();
    }

}