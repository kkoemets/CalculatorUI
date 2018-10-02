package com.application.api.mathml;

import java.util.LinkedList;
import java.io.*;
import org.apache.poi.xwpf.usermodel.*;

import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTR;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;




/** Takes strings as input and outputs them to Word
 * https://stackoverflow.com/questions/46623554/add-latex-type-equation-in-word-docx-using-apache-poi
 */
public class WordMathWriter {

    private LinkedList<String> lines; // Lines to be copy pasted to Word file
    private File wordFile = new File("src\\main\\java\\com\\application\\api\\mathml\\MathMLConverter.docx");
    static File stylesheet = new File("src\\main\\java\\com\\application\\api\\mathml\\MML2OMML.XSL");
    static TransformerFactory tFactory = TransformerFactory.newInstance();
    static StreamSource stylesource = new StreamSource(stylesheet);


    public WordMathWriter() {
        lines = new LinkedList<>();
    }


    /** Writes lines to Word file
     * If MathML is detected then it will be transformed accordingly
     */
    public void write() throws Exception  {
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph paragraph;
        XWPFRun run;
        CTOMath ctOMath;
        CTP ctp;
        if (lines.isEmpty()) return;
        for (String line : lines) {
            paragraph = document.createParagraph();
            run = paragraph.createRun();
            if(line.contains("<math xmlns")) {
                ctOMath = getOMML(line);
                ctp = paragraph.getCTP();
                ctp.setOMathArray(new CTOMath[]{ctOMath});
            } else {
                run.setText(line);
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(wordFile);
            document.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("MathML parsed");
    }


    /** Microsoft provides XSLT stylesheets for transforming OMML to MathML (OMML2MML.XSL) as well as for transforming MathML to OMML (MML2OMML.XSL) using XSLT.
     *  Using this we can transform MathML to OMML using XSLT.
     * @param mathML
     * @return
     * @throws Exception
     */
    private CTOMath getOMML(String mathML) throws Exception {
        Transformer transformer = tFactory.newTransformer(stylesource);

        StringReader stringreader = new StringReader(mathML);
        StreamSource source = new StreamSource(stringreader);

        StringWriter stringwriter = new StringWriter();
        StreamResult result = new StreamResult(stringwriter);
        transformer.transform(source, result);

        String ooML = stringwriter.toString();
        stringwriter.close();

        CTOMathPara ctOMathPara = CTOMathPara.Factory.parse(ooML);
        CTOMath ctOMath = ctOMathPara.getOMathArray(0);

        //for making this to work with Office 2007 Word also, special font settings are necessary
        XmlCursor xmlcursor = ctOMath.newCursor();
        while (xmlcursor.hasNextToken()) {
            XmlCursor.TokenType tokentype = xmlcursor.toNextToken();
            if (tokentype.isStart()) {
                if (xmlcursor.getObject() instanceof CTR) {
                    CTR cTR = (CTR)xmlcursor.getObject();
                    cTR.addNewRPr2().addNewRFonts().setAscii("Cambria Math");
                    cTR.getRPr2().getRFonts().setHAnsi("Cambria Math");
                }
            }
        }
        return ctOMath;
    }


    public LinkedList getLines() {
        return lines;
    }


    public void addLine(String line) {
        lines.addLast(line);
    }
}