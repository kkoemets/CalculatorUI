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
        WordMathWriter wordMathWriter = new WordMathWriter();
        for (String line : lines) {
            wordMathWriter.addLine(line);
        }
        try {
            wordMathWriter.write();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}