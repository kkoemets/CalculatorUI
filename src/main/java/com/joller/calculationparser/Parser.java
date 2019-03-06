package com.joller.calculationparser;


import com.joller.calculationparser.converter.*;
import com.joller.calculator.Calculator;
import org.apache.poi.ss.formula.FormulaParseException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import static com.joller.calculationparser.converter.Command.*;

public class Parser {

    private Calculator calculator;
    private Converter converter;

    private Queue<Command> commandQueue;

    public Parser(Calculator calculator) {
        this.calculator = calculator;
        this.converter = new Converter();
    }

    private String removeComments(String commandLine) {
        int commentIndex;
        if ((commentIndex = commandLine.indexOf("//")) != -1) {
            commandLine = commandLine.substring(0, commentIndex);
        }
        return commandLine;
    }

    public String[] parse(String[] inputArray) throws IOException {
        commandQueue = new LinkedList<>();

        final String[] output = new String[inputArray.length];

        for (int i = 0; i < inputArray.length; i++) {

            String commandLine = inputArray[i];
            try {
                commandLine = removeComments(commandLine);

                final Command command = converter.getCommandType(commandLine);

                commandQueue.add(command);

                if (command == EMPTY) {
                    output[i] = "";
                } else if (command == COMMENT) {
                    output[i] = converter.convertComment(commandLine);
                } else {
                    commandLine = removeWhitespace(commandLine);

                    if (command == SET) {
                        output[i] = converter.convertSet(commandLine);
                    } else if (command == CALCF) {
                        ResultContainer rc = converter.convertCalcf(commandLine);

                        int precision = Integer.parseInt(rc.getPrecision());
                        calculator.setDecimalPlace(precision);

                        String toCalculate = rc.getUnCalculatedEquationWithoutVariables();

                        String result = calculator.calculate(toCalculate);

                        converter.convertSet("set:" + rc.getVariableName() + '=' + result + ',' +
                                rc.getUnit());

                        output[i] = rc.getVariableName() + '=' +
                                rc.getUnCalculatedEquationWithVariables() +
                                '=' + rc.getUnCalculatedEquationWithoutVariables() + '=' + result +
                                ' ' + rc.getUnit();
                    }
                }
            } catch (Exception e) {
                converter.deleteVariables();
                if (e instanceof ConverterException) {
                    output[i] = e.getMessage();
                    break;
                } else if (e instanceof FormulaParseException) {
                    ParserException pe = new ParserException(commandLine, "contains unknown variable" +
                            ", or missing operator or parenthesis");
                    output[i] = pe.getMessage();
                    break;
                } else if (e instanceof CommandFinderException) {
                    output[i] = e.getMessage();
                    break;
                } else {
                    e.printStackTrace();
                    throw new IllegalStateException("Something is wrong with \n" +
                            String.join("\n", inputArray) +
                            "\n on line " + (i + 1) +
                            "\n" + inputArray[i]);
                }
            }
        }
        converter.deleteVariables();
        return output;
    }

    public Queue<Command> getCommandQueue() {
        Queue<Command> q = new LinkedList<>();
        while (!commandQueue.isEmpty()) {
            q.add(commandQueue.poll());
        }
        return q;
    }

    private String removeWhitespace(String line) {
        return line.replaceAll("\\s+", "");
    }

    public String[] parse(String input) throws IOException {
        if (input == null || input.length() < 1) {
            throw new IllegalArgumentException(input + " is null or empty");
        }
        return parse(input.split("\n"));
    }
}

