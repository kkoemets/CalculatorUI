package com.application.api.mathml;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.util.LinkedList;


/** Takes strings as input and outputs strings, mathematical strings as MathML code
 */
public class MathMLConverter {

    private final String DIRECTORY_HTML = "src\\main\\java\\com\\application\\api\\mathml\\MathJax-master\\mathml.html";
    private File htmlFile;
    private ChromeDriver driver;
    private final String HTML_TEMPLATE =
            "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<script type=\"text/javascript\" async src=\"MathJax.js?config=TeX-MML-AM_CHTML\"></script>" + // this line is need in HTML for MathJax to work
                    "<title></title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    "$FORMULA" + // important line - $FORMULA will be replaced with formulas
                    "\n" +
                    "</body>\n" +
                    "</html>";


    private LinkedList<String> textLines;
    private LinkedList<String> mathLines;
    private LinkedList<LineType> lineQueue;

    enum LineType {
        TEXT,
        MATH
    }


    public MathMLConverter() {
        htmlFile = new File(DIRECTORY_HTML);
        System.setProperty("webdriver.chrome.driver","src\\main\\java\\com\\application\\api\\mathml\\chromedriver.exe");
        driver = new ChromeDriver();

        textLines = new LinkedList<>();
        mathLines = new LinkedList<>();
        lineQueue = new LinkedList<>();
    }


    /** Converts stored math into MathMLConverter
     */
    public void convert() {
        try {
            createHtmlFileTemplate();
            addFormulasToHtml();
            mathLines = getMathMLFromHtml();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** Loads HTML file into web-browser, so that JavaScript will be loaded and MathMLConverter can be extracted
     * @return
     *          - list of MathML code, each entry is math as MathML
     */
    private LinkedList<String> getMathMLFromHtml() {
        driver.get(htmlFile.getAbsolutePath());
        try {
            while (!driver.getPageSource().contains("<math xmlns=\"http://www.w3.org/1998/Math/MathML\">")) {
                Thread.sleep(2500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Document doc = Jsoup.parse(driver.getPageSource());
        driver.close();
        return parseHtmlToMathML(doc.toString());
    }


    /** Extracts MathMLConverter from HTML file
     * @param sourceCode
     * @return
     *          - list of MathML code, each entry is math as MathML
     */
    private LinkedList<String> parseHtmlToMathML(String sourceCode) {
        StringBuilder sb = new StringBuilder(sourceCode);
        LinkedList<String> mathML = new LinkedList<>();
        int startIndex;
        while ((startIndex = sb.indexOf("<math xmlns=\"http://www.w3.org/1998/Math/MathML\">")) != -1) {
            sb.replace(0, startIndex, "");
            startIndex = 0;
            int endIndex = sb.indexOf("</math>") + "</math>".length();
            mathML.addLast(sb.substring(startIndex, endIndex));
            sb.replace(startIndex, endIndex, "");
        }
        return mathML;
    }


    /** Create template for replacing "$FORMULA" with collected formulas in formulaCollector (instance of StringBuilder)
     */
    private void createHtmlFileTemplate()  {
        writeToHtml(HTML_TEMPLATE);
    }


    /** Replaces "$FORMULA" with saved formulas in formulaCollector object (instance of StringBuilder)
     */
    private void addFormulasToHtml() {
        String htmlContent = readFromHtml();
        htmlContent = htmlContent.replace("$FORMULA", mathLinesToString());
        writeToHtml(htmlContent);
    }


    /** Reads HTML file and returns its code
     * @return
     *          - string representation of HTML code
     */
    private String readFromHtml() {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(htmlFile));
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                sb.append(string + "\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    /** Adds a line to the MathMLConvert. Separates text and math input,
     * string that contains '=' is considered as a math input
     * @param line
     */
    public void addLine(String line) {
        if (line.contains("=")) {
            addMathLine(line);
        } else {
            addTextLine(line);
        }
    }


    private void addTextLine(String line) {
        textLines.addLast(line);
        lineQueue.addLast(LineType.TEXT);
    }


    private void addMathLine(String line) {
        mathLines.addLast(line);
        lineQueue.addLast(LineType.MATH);
    }


    /** Appends all saved math lines to a string. Also adds necessary notation for JavaScript code to work.
     * @return
     *          - returns appended string of math lines with proper notation
     */
    private String mathLinesToString() {
        StringBuilder sb = new StringBuilder();
        for (String line : mathLines) {
            sb.append("`" + line + "`" + "\n");
        }
        return sb.toString();
    }


    /** Writes string to a HTML file.
     * @param string - to be written to the HTML file
     */
    private void writeToHtml(String string) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(htmlFile));
            bufferedWriter.write(string);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /** Returns all lines in the orrder they were added to the MathMLConverter
     * @return
     *          - LinkedList<String>
     */
    public LinkedList<String> getAllLinesAsList() {
        LinkedList allLines = new LinkedList();
        for (LineType lineType : lineQueue) {
            switch (lineType) {
                case MATH:
                    allLines.addLast(mathLines.pop());
                    break;
                case TEXT:
                    allLines.addLast(textLines.pop());
            }
        }
        return allLines;
    }
}
