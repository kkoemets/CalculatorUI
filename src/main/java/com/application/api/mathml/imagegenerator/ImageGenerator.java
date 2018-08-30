package com.application.api.mathml.imagegenerator;

import com.application.api.mathml.parser.Parser;
import fmath.ApplicationConfiguration;
import fmath.components.MathMLFormula;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ImageGenerator {

    private static String folderGeneratedImage = "src\\main\\java\\com\\application\\api\\mathml\\imagegenerator\\images";
    private static String folderFonts = "src\\main\\java\\com\\application\\api\\mathml\\fmath\\example\\fonts";
    private static String folderGlyps = "src\\main\\java\\com\\application\\api\\mathml\\fmath\\example\\glyphs";


    public void createFormulaImage(String mathml) throws IOException {
        ApplicationConfiguration.setFolderUrlForFonts(folderFonts);
        ApplicationConfiguration.setFolderUrlForGlyphs(folderGlyps);
        ApplicationConfiguration.setWebApp(false);

        MathMLFormula formula = new MathMLFormula();
        BufferedImage img = formula.drawImage(mathml);

        File file = new File(folderGeneratedImage + "/test.png");
        ImageIO.write(img, "png", file);
        System.out.println("--> Image generated in folder:" + folderGeneratedImage);
    }
}
