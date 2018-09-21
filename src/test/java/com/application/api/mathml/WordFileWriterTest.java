package com.application.api.mathml;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.*;

class WordFileWriterTest {

    @Test
    void test() throws Exception {
        WordFileWriter fw = new WordFileWriter();
        File file = new File("src\\test\\java\\com\\application\\api\\mathml\\mathml.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        StringBuilder mathMLCode = new StringBuilder();
        while (((line = reader.readLine())!= null)) {
            mathMLCode.append(line);
        }
        fw.writeMathML(mathMLCode.toString());
    }

}