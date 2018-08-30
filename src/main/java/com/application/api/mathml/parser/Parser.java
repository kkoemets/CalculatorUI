package com.application.api.mathml.parser;

import com.application.api.common.OperatorHandler;
import com.application.api.common.Utils;
import com.application.api.mathml.imagegenerator.ImageGenerator;

import java.io.IOException;
import java.util.HashSet;

class Run {
    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        // (2.2+12*23-23*1-1+2+12+2.2)+2*5/10+(2.2+12*2*2*2-1*2/2+2.2)
//        String parsed = parser.parse("1+1*1");
        String parsed;
//        parsedDiv = parser.parseDivisionSign("1+1/(1+1)");
//        parser.createImage(parsed);
//        System.out.println(parsedDiv);
        parsed = parser.parse("(a+b)/(c*d)*(c*d)+1+(a+b)/(c*d)");
        System.out.println(parsed);
//        parser.createImage("<mfrac><mn>1</mn><mn>1</mn></mfrac>");
        parser.createImage(parsed);
//        parser.createImage("<mfrac><mn>(a+b)</mn><mn>(c*d)</mn></<mfrac><mn><mfrac></mn><mn>(c</mn></mfrac>*d)+1+<mfrac><mn>(a+b)</mn><mn>(c*d)</mn></mfrac>");
    }
}

/**
 * Parses Java string to MathML code
 */
public class Parser {
    private final String mathMLBeginningForMSWORD = "<math mode=\"display\" xmlns=\"http://www.w3.org/1998/Math/MathML\"><semantics>";
    private String mathMLEndingForMSWORD = "</semantics></math>";

    private final String mathMLBeginningForImage = "<math xmlns='http://www.w3.org/1998/Math/MathML'>";
    private final String mathMLEndingForImage = "</math>";

    private ImageGenerator imageGenerator = new ImageGenerator();

    public HashSet<Character> operators = operatorsWithoutParentheses();

    private HashSet<Character> operatorsWithoutParentheses() {
        HashSet<Character> operators = (HashSet<Character>)OperatorHandler.getInstance().operators.clone();
        operators.remove('(');
        operators.remove(')');
        System.out.println(operators);
        return operators;
    }


    public String parseDivisionSign(String string) {
        StringBuilder sb = new StringBuilder(string);
        for (int index = 0; index <sb.length(); index++) {
            if (sb.charAt(index) == '/' && !(sb.charAt(index - 1) == '<')) {
                int startIndexOfDivision = findStartIndexOfDivision(sb, index);
                int endIndexOfDivision = findEndIndexOfDivision(sb, index);
                sb.replace(startIndexOfDivision, endIndexOfDivision + 1, "<mfrac>" +
                        "<mn>" + sb.substring(startIndexOfDivision, index) + "</mn>" +
                        "<mn>" + sb.substring(index + 1, endIndexOfDivision + 1) + "</mn>" +
                        "</mfrac>");
                index = 0;
            }
        }
        return sb.toString();
    }

    private int findStartIndexOfDivision(StringBuilder sb, int indexOfDivision) {
        if (sb.charAt(indexOfDivision - 1) == ')') {
            return  findStartIndexOfDivisionWhenParenthesis(sb, indexOfDivision);
        }
        for (int i = indexOfDivision - 1; 0 < i; i--) {
            if (operators.contains(sb.charAt(i))) {
                return i + 1;
            }
        }
        return 0;
    }


    private int findStartIndexOfDivisionWhenParenthesis(StringBuilder sb, int indexOfDivision) {
        int count = 0; // counts ')' and '(' chars, if its 0 then we have our left side of division
            for (int i = indexOfDivision - 1; 0 < i; i--) {
                if (sb.charAt(i) == ')') {
                    count++;
                }
                if (sb.charAt(i) == '(') {
                    count--;
                }
                if (count == 0) {
                    return i;
                }
            }
        return 0;
    }


    private int findEndIndexOfDivision(StringBuilder sb, int indexOfDivision) {
        if (sb.charAt(indexOfDivision - 1) == ')') {
            return  findEndIndexOfDivisionWhenParenthesis(sb, indexOfDivision);
        }
        for (int i = indexOfDivision + 1; i < sb.length(); i++) {
            if (operators.contains(sb.charAt(i))) {
                return i - 1;
            }
        }
        return 0;
    }


    private int findEndIndexOfDivisionWhenParenthesis(StringBuilder sb, int indexOfDivision) {
        int count = 0; // counts ')' and '(' chars, if its 0 then we have our left side of division
        for (int i = indexOfDivision + 1; i < sb.length(); i++) {
            if (sb.charAt(i) == '(') {
                count++;
            }
            if (sb.charAt(i) == ')') {
                count--;
            }
            if (count == 0) {
                return i;
            }
        }
        return 0;
    }


    public String parseUnparsed(String string) {
        int count = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char currentChar = string.charAt(i);
            if (currentChar == '<') count++;
            if (currentChar == '>') count--;
            if(count == 0 && currentChar != '<' && currentChar != '>') {
                sb.append("<mn>" + currentChar + "</mn>");
            } else sb.append(currentChar);
        }
        return sb.toString();
    }


    public String parse(String string) {
        Utils.clean(string);
        string = parseDivisionSign(string);
        System.out.println(string);
        string = parseUnparsed(string);
        return string;
    }


    public void createImage(String mathml) throws IOException {
        mathml = mathMLBeginningForImage + mathml + mathMLEndingForImage;
        imageGenerator.createFormulaImage(mathml);
    }

}