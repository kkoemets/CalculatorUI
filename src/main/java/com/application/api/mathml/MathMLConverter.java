package com.application.api.mathml;

import fmath.ApplicationConfiguration;
import fmath.components.MathMLFormula;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

class Run {
    public static void main(String[] args) throws Exception {
        MathMLConverter mlConverter = new MathMLConverter();
        mlConverter.convertToImage("1+1");
    }
}

public class MathMLConverter {
    public void convertToImage(String mathml) throws IOException {
        String folderGeneratedImage = "src\\main\\java\\com\\application\\api\\mathml\\images";
        String folderFonts = "src\\main\\java\\com\\application\\api\\mathml\\fmath\\fonts";
        String folderGlyps = "src\\main\\java\\com\\application\\api\\mathml\\fmath\\glyphs";

        ApplicationConfiguration.setFolderUrlForFonts(folderFonts);
        ApplicationConfiguration.setFolderUrlForGlyphs(folderGlyps);
        ApplicationConfiguration.setWebApp(false);
        mathml = convertToMathML(mathml);
        System.out.println(mathml);
        MathMLFormula formula = new MathMLFormula();
        BufferedImage img = formula.drawImage(mathml);

        File file = new File(folderGeneratedImage + "/test.png");
        ImageIO.write(img, "png", file);
        System.out.println("--> Image generated in folder:" + folderGeneratedImage);

    }

    private String convertToMathML(String string) {
        // http://www.mathmlcentral.com/Tools/ToMathML.jsp
        System.setProperty("webdriver.chrome.driver","src\\main\\java\\com\\application\\api\\mathml\\chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        driver.get("http://www.mathmlcentral.com/Tools/ToMathML.jsp");
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        System.out.println("Website opened succesfully");

        WebElement formulaInput = driver.findElement(By.xpath("//*[@id=\"input\"]"));
        formulaInput.clear();
        formulaInput.sendKeys(string);

        WebElement generateBtn = driver.findElement(By.xpath("//*[@id=\"generateXhtml\"]"));
        generateBtn.click();

        WebElement mathmlOutput = driver.findElement(By.xpath("//*[@id=\"result\"]/pre"));
        String mathml = mathmlOutput.getText();
        driver.close();
        return mathml;
    }


}
