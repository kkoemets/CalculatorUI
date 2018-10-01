package com.application.api.mathml;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;


/** Takes strings as input and outputs them to Word by copy-pasting them
 */
public class WordWriter {

    private LinkedList<String> lines; // Lines to be copy pasted to Word file
    private File wordFile; //Blank Document
    private Robot r;


    public WordWriter() {
        wordFile = null; // defined in createBlankFile()
        lines = new LinkedList<>();
        try {
            r = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }


    /** Open Word file, inserts each line to clipboard memory and pastes to Word file
     */
    public void write()  {
        if (lines.isEmpty()) return;
        createBlankFile();
        openBlankFile();
        try { Thread.sleep(6000); } catch (InterruptedException e) { }
        while (!lines.isEmpty()) {
            copyPaste(lines.pop());
            Break();
        }
    }


    /** Moves away from last pasted string and sleeps a bit
     * Is necessary for copy-pasting stability (MS Word bottleneck)
     */
    private void Break() {
        try {
            alignLeft();
            r.keyPress(KeyEvent.VK_END);
            r.keyRelease(KeyEvent.VK_END);
            Thread.sleep(350);
            r.keyPress(KeyEvent.VK_ENTER);
            r.keyRelease(KeyEvent.VK_ENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /** Writes string to clipboard and pastes it
     * @param line
     *              - string
     */
    private void copyPaste(String line) {
        try {
                writeTextToClipboard(line);
                pasteClipboard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** Creates blank Word file
     */
    private void createBlankFile() {
        XWPFDocument document; // Word file in Java
        wordFile = new File("src\\main\\java\\com\\application\\api\\mathml\\MathMLConverter.docx");
        document= new XWPFDocument();
        try {
            FileOutputStream out = new FileOutputStream(wordFile);
            document.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /** Opens blank Word file
     */
    private void openBlankFile() {
        try {
            Process p = Runtime
                    .getRuntime()
                    .exec("rundll32 url.dll,FileProtocolHandler "+ wordFile.getAbsolutePath());
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public LinkedList getLines() {
        return lines;
    }


    public void addLine(String line) {
        lines.addLast(line);
    }


    /** Stores string to memory
     * @param s
     *           - string
     */
    private void writeTextToClipboard(String s) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(s);
        clipboard.setContents(selection, selection);
    }


    private void pasteClipboard() throws Exception {
        r.keyPress(KeyEvent.VK_CONTROL);
        Thread.sleep(150);
        r.keyPress(KeyEvent.VK_V);
        Thread.sleep(100);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.keyRelease(KeyEvent.VK_V);
    }


    private void alignLeft() throws Exception {
        r.keyPress(KeyEvent.VK_CONTROL);
        Thread.sleep(10);
        r.keyPress(KeyEvent.VK_L);
        Thread.sleep(10);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.keyRelease(KeyEvent.VK_L);
    }
}
