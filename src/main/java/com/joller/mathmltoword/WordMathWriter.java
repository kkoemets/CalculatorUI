package com.joller.mathmltoword;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;


/** Takes strings as input and outputs them to Word
 * https://stackoverflow.com/questions/46623554/add-latex-type-equation-in-word-docx-using-apache-poi
 */
public class WordMathWriter {



    private final String DIRECTORY_DOCX_FILE = "./MathMLConverter.docx";
    private static final String DIRECTORY_XSL_FILE = "MML2OMML.XSL";

    private LinkedList<String> lines; // Lines to be copy pasted to Word file


    public WordMathWriter() {
        lines = new LinkedList<>();
    }

    /** Writes lines to Word file
     * If MathML is detected then it will be transformed accordingly
     */
    public void write() throws Exception  {
        Date now = new Date();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
        String strDate = sdfDate.format(now);

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
            FileOutputStream out = new FileOutputStream(new File("./mathml-" + strDate + ".docx"));
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
        File stylesheet = getFile(DIRECTORY_XSL_FILE);

        TransformerFactory tFactory = TransformerFactory.newInstance();
        StreamSource stylesource = new StreamSource(stylesheet);

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

    private File getFile(String directoryResources) {
        // Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(directoryResources).getFile());
        return new File(file.getAbsoluteFile().toString().replace("%20", " "));
    }
}