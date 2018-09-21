package com.application.api.mathml;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.*;


class TestRun {
    public static void main(String[] args) throws Exception {
        MathML mathML = new MathML();;
        mathML.addFormula("3*e^pi/5-3/5");
        mathML.addFormula("3*e^pi/5-3/5");
        mathML.addFormula("3*e^pi/5-3/5");
        System.out.println(mathML.convert());
    }
}

public class MathML {

    private final String DIRECTORY_HTML = "src\\main\\java\\com\\application\\api\\mathml\\mathml.html";
    private final File htmlFile = new File(DIRECTORY_HTML);
    private StringBuilder formulaCollector;
    private ChromeDriver driver;
    private final String HTML_TEMPLATE =
            "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<script type=\"text/javascript\"\n" +
                    "  src=\"https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-MML-AM_HTMLorMML\">\n" +
                    "</script>\n" +
                    "<title></title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    "$FORMULA" + // important line - $FORMULA will be replaced with formulas
                    "\n" +
                    "</body>\n" +
                    "</html>";

    public MathML() {
        System.setProperty("webdriver.chrome.driver","src\\main\\java\\com\\application\\api\\mathml\\chromedriver.exe");
        formulaCollector = new StringBuilder();
        driver = new ChromeDriver();
    }

    /**
     * Converts stored formulas into MathML
     * @return
     *          - string of MathML code
     */
    public String convert() {
        String converted = "";
        try {
            createHtmlFileTemplate();
            addFormulasToHtml();
            converted = getMathMLFromHtml();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return converted;
    }

    /**
     * Loads HTML file into web-browser, so that JavaScript will be loaded and MathML can be extracted
     * @return
     *          - string of MathML code
     */
    private String getMathMLFromHtml() {
        driver.get(htmlFile.getAbsolutePath());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Document doc = Jsoup.parse(driver.getPageSource());
        driver.close();
        return parseHtmlToMathML(doc.toString());
    }


    /**
     * Extracts MathML from HTML file
     * @param sourceCode
     * @return
     *          - string of MathML code
     */
    private String parseHtmlToMathML(String sourceCode) {
        StringBuilder sb = new StringBuilder(sourceCode);
        StringBuilder collector = new StringBuilder();
        int startIndex;
        while ((startIndex = sb.indexOf("<math xmlns=\"http://www.w3.org/1998/Math/MathML\">")) != -1) {
            sb.replace(0, startIndex, "");
            startIndex = 0;
            int endIndex = sb.indexOf("</math>") + "</math>".length();
            collector.append(sb.substring(startIndex, endIndex) + "\n");
            sb.replace(startIndex,endIndex,"");
        }
        return collector.toString();
    }


    /**
     * Create template for replacing "$FORMULA" with collected formulas in formulaCollector object (instance of StringBuilder)
     */
    private void createHtmlFileTemplate()  {
        writeToHtml(HTML_TEMPLATE);
    }


    /**
     * Replaces "$FORMULA" with saved formulas in formulaCollector object (instance of StringBuilder)
     */
    private void addFormulasToHtml() {
        String htmlContent = readFromHtml();
        htmlContent = htmlContent.replace("$FORMULA", formulaCollector.toString());
        writeToHtml(htmlContent);
    }

    /**
     * @return
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

    public void addFormula(String formula) {
        formulaCollector.append("`" + formula + "`" + "\n<br>\n"); // '`' need for JavaScript to recognize the formula
    }


    /**
     * @param string
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
}
