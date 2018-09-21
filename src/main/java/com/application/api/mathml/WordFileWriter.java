package com.application.api.mathml;

import org.apache.poi.xwpf.usermodel.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class WordFileWriter {
    Process p;
    public void writeMathML(String string) throws IOException {
        StringBuilder sb = new StringBuilder(string);
        //Blank Document
        File wordFile = new File("src\\main\\java\\com\\application\\api\\mathml\\MathMLConverter.docx");
        XWPFDocument document= new XWPFDocument();
        FileOutputStream out = new FileOutputStream(wordFile);
        document.write(out);
        out.close();
        //
        try
        {
            p = Runtime
                    .getRuntime()
                    .exec("rundll32 url.dll,FileProtocolHandler "+ wordFile.getAbsolutePath());
            p.waitFor();
            Thread.sleep(6000);
            int startIndex;
            Robot r = new Robot();
            while ((startIndex = sb.indexOf("<math")) != -1) {
                int endIndex = sb.indexOf("</math>") + "</math>".length();
                writeTextToClipboard(sb.substring(startIndex,endIndex));
                System.out.println(sb.subSequence(startIndex,endIndex));
                pasteClipboard(r);
                alignLeft(r);
                r.keyPress(KeyEvent.VK_END);
                r.keyRelease(KeyEvent.VK_END);
                Thread.sleep(350);
                r.keyPress(KeyEvent.VK_ENTER);
                r.keyRelease(KeyEvent.VK_ENTER);
                sb.replace(0, endIndex, "");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            p.destroyForcibly();
            writeMathML(string);
        }
    }
    private void writeTextToClipboard(String s) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(s);
        clipboard.setContents(selection, selection);
    }

    private void pasteClipboard(Robot r) throws Exception {
        r.keyPress(KeyEvent.VK_CONTROL);
        Thread.sleep(150);
        r.keyPress(KeyEvent.VK_V);
        Thread.sleep(100);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.keyRelease(KeyEvent.VK_V);
    }

    private void alignLeft(Robot r) throws Exception {
        r.keyPress(KeyEvent.VK_CONTROL);
        Thread.sleep(10);
        r.keyPress(KeyEvent.VK_L);
        Thread.sleep(10);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.keyRelease(KeyEvent.VK_L);
    }
}
