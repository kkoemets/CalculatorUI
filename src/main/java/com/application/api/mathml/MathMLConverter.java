package com.application.api.mathml;
import com.application.api.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.*;


class TestRun {
    public static void main(String[] args) throws Exception {
        MathMLConverter mathML = new MathMLConverter();
        for (int i = 0; i < 1; i++) {
            mathML.addFormula("M_(Rd) = (f_cd*b*0.8*x*(d_1-0.5*0.8*x)+f_ycd*A_s2*(d_1-d_2))/10^6 = (16.7*300*0.8*281*(651-0.5*0.8*281)+435*628*(651-43))/10^6 = 772.7 kNm");
        }
        System.out.println(mathML.convert());
    }
}

public class MathMLConverter {
    private final String DIRECTORY_HTML = "src\\main\\java\\com\\application\\api\\mathml\\MathJax-master\\mathml.html";
    private File htmlFile;
    private StringBuilder formulaCollector;
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

    public MathMLConverter() {
        htmlFile = new File(DIRECTORY_HTML);
        System.setProperty("webdriver.chrome.driver","src\\main\\java\\com\\application\\api\\mathml\\chromedriver.exe");
        formulaCollector = new StringBuilder();
        driver = new ChromeDriver();
    }


    /**
     * Converts stored formulas into MathMLConverter
     * @return
     *          - string of MathMLConverter code
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
     * Loads HTML file into web-browser, so that JavaScript will be loaded and MathMLConverter can be extracted
     * @return
     *          - string of MathMLConverter code
     */
    private String getMathMLFromHtml() {
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


    /**
     * Extracts MathMLConverter from HTML file
     * @param sourceCode
     * @return
     *          - string of MathMLConverter code
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
     * Create template for replacing "$FORMULA" with collected formulas in formulaCollector (instance of StringBuilder)
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
