package com.joller;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import uk.ac.ed.ph.asciimath.parser.AsciiMathParser;
import com.joller.asciitomathml.XmlUtilities;
import com.joller.calculationparser.Parser;

import com.joller.calculationparser.converter.Command;
import com.joller.calculator.Calculator;
import com.joller.mathmltoword.WordMathWriter;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;

import static com.joller.calculationparser.converter.Command.COMMENT;


/**
 * NOTE: Actions for buttons etc. are set in FXML file!
 */
public class Controller {


    private final String DIRECTORY_HELP = "/helpUI.txt";

    Stage primaryStage = Main.getPrimaryStage();

    private FileChooser fileOpener;
    private FileChooser fileSaver;
    private SimpleObjectProperty<File> lastKnownDirectoryProperty = new SimpleObjectProperty<>();
    private Parser parser = new Parser(new Calculator());

    @FXML
    private TextArea outputArea;

    @FXML
    private TextArea inputArea;

    @FXML
    private Text fileLocationText;


    @FXML
    void initialize() {
        outputArea.setEditable(false);
        initializeInstructionsTextAndAlert();
        initializeTextFileOpener();
        initializeTextFileSaver();
    }


    @FXML
    void initializeTextFileOpener() {
        fileOpener = new FileChooser();
        fileOpener.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);

        fileOpener.setTitle("Open Saved Calculations");
        // setting to open only .txt files
        fileOpener.getExtensionFilters().add(new FileChooser.ExtensionFilter("Normal text file","*.txt"));
    }

    @FXML
    void initializeTextFileSaver(){
        fileSaver = new FileChooser();
        fileSaver.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
        fileSaver.setTitle("Save Calculations");
        fileSaver.getExtensionFilters().add(new FileChooser.ExtensionFilter("Normal text file","*.txt"));
    }


    private Alert alert;
    void initializeInstructionsTextAndAlert() {
        StringBuilder collect = new StringBuilder();
        try {
            InputStream in = getClass().getResourceAsStream(DIRECTORY_HELP);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                collect.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Instructions");
        alert.setContentText(collect.toString());
    }


    @FXML
    private void showInstructions() {
        alert.showAndWait();
    }


    @FXML
    private void openFile() {
        File file = fileOpener.showOpenDialog((primaryStage));
        try {
            if (file != null) {
                saveLastKnownDirectory(file);
                String savedText = readFile(file);
                displayFileInInputArea(savedText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFile(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }
    private void displayFileInInputArea(String string) {
        inputArea.clear();
        inputArea.setText(string);
    }

    @FXML
    private void saveFile() {
        try {
            File file = fileSaver.showSaveDialog(primaryStage);
            if (file != null) {
                saveLastKnownDirectory(file);
                String text = inputArea.getText();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(file), StandardCharsets.UTF_8));
                bufferedWriter.write(text);
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveLastKnownDirectory(File file) {
        lastKnownDirectoryProperty.setValue(file.getParentFile());
        fileLocationText.setText(file.getAbsolutePath());
    }

    boolean isMathMLParsingAllowed = false;

    @FXML
    private void parse() throws IOException {
        isMathMLParsingAllowed = false;

        String[] input = inputArea.getText().split("\n");
        String[] output;
        try {
            output = parser.parse(input);
            isMathMLParsingAllowed = true;
        } catch (IllegalStateException e) {
            output = new String[] {e.getMessage()};
        }
        StringBuilder sb = new StringBuilder();
        for (String line : output) {
            sb.append(line + "\n");
        }
        outputArea.setText(sb.toString());
        outputArea.setScrollTop(Double.MAX_VALUE);
    }

    @FXML
    public void formulasToMathMLToWord() {
        if (outputArea.getText().equals("Word file created!")
                || outputArea.getText().equals("Please parse again before trying to create a Word file...")) {
            outputArea.setText("Please parse again before trying to create a Word file...");
            return;
        }

        if (!isMathMLParsingAllowed) {
            alert.setTitle("Warning Dialog");
            outputArea.setText("Cannot parse your calculations to Word!" +
                    "\nHave you fixed errors in your calculations?");
            return;
        }
        String textToConvertToWord = outputArea.getText();
        isMathMLParsingAllowed = false;
        outputArea.setText("Creating Word file...");
        Queue<Command> commandQueue = parser.getCommandQueue();
        try {
            String lines = textToConvertToWord;
            if (lines == null || lines.length() < 1) return;

            final AsciiMathParser mathParser = new AsciiMathParser();
            List<String> listOfMathML = new ArrayList<>();

            for (String line : lines.split("\\n")) {
                if (commandQueue.poll() == COMMENT) {
                    listOfMathML.add(line);
                } else {
                    Document result = mathParser.parseAsciiMath(line);
                    listOfMathML.add(XmlUtilities.serializeMathmlDocument(result));
                }
            }

            WordMathWriter wordMathWriter = new WordMathWriter();
            for (String line : listOfMathML) {
                wordMathWriter.addLine(line);
            }

            wordMathWriter.write();
        } catch (Exception e) {
            e.printStackTrace();
            outputArea.setText("Oops... something went wrong!"
                    + "\n" + e.getMessage());
            return;
        }
        outputArea.setText("Word file created!");
    }

}
