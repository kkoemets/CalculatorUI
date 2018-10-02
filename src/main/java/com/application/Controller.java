package com.application;

import java.io.*;
import java.util.Scanner;

import com.application.api.calculator.Calculator;
import com.application.api.converter.VariableBase;
import com.application.api.mathml.MathMLConverter;
import com.application.api.mathml.WordMathWriter;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

//todo!!! save last save directory
//todo!!! quick-save file with shortcut  (already opened file)
//todo!!! NEW API TO COMPARE vars 1 < 2 etc...! urgent
//todo!!! critical design problem! remake UI
/**
 * NOTE: Actions for buttons etc. are set in FXML file!
 */
public class Controller {


    private final String DIRECTORY_HELP = "src/main/java/com/application/appdata/help/helpUI.txt";
    private final String DIRECTORY_TEMPLATES = "src/main/java/com/application/appdata/templates/";

    Stage primaryStage = Main.getPrimaryStage();

    private FileChooser fileOpener;
    private FileChooser fileSaver;
    private SimpleObjectProperty<File> lastKnownDirectoryProperty = new SimpleObjectProperty<>();

    @FXML
    private MenuButton templateMenu;

    @FXML
    private TextArea outputArea;

    @FXML
    private TextArea inputArea;

    @FXML
    private Text fileLocationText;


    @FXML
    void initialize() {
        outputArea.setEditable(false);
        initializeTemplateMenu();
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
            InputStream in = new FileInputStream(DIRECTORY_HELP);
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
        alert.setHeaderText("Below are commands for creating calculations");
        alert.setContentText(collect.toString());
    }


    @FXML
    private void showInstructions() {
        alert.showAndWait();
    }


    private void initializeTemplateMenu() {
        templateMenu.setTextAlignment(TextAlignment.CENTER);
        File folder = new File(DIRECTORY_TEMPLATES);
        // getting templates
        File[] listOfTemplates = folder.listFiles();
        // adding items to MenuButton
        for (File file : listOfTemplates) {
            String f = file.getPath();
            // name of a menu item will be .txt files name
            f = f.substring(f.lastIndexOf("\\") + 1, f.lastIndexOf("."));
            MenuItem menuItem = new MenuItem(f);
            final String templateName = f + ".txt"; // setOnAction requires final variables
            // creating action when menu item is pressed
            // main objective is to copy text from template file to input area
            menuItem.setOnAction(event -> {
                File template = new File(DIRECTORY_TEMPLATES + templateName);
                try {
                    Scanner scanner = new Scanner(template);
                    StringBuilder collect = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        collect.append(scanner.nextLine() + "\n");
                    }
                    inputArea.setText(collect.toString());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
            templateMenu.getItems().add(menuItem);
        }
    }


    @FXML
    private void parse() {
        StringBuilder sb = new StringBuilder(); // collects lines to output to output area
        TextInputParser textInputParser = new TextInputParser(new VariableBase());
        try {
            for (String stringToParse : inputArea.getText().split("\\n")) {
                sb.append(textInputParser.parse(stringToParse) + "\n");
            }
            outputArea.setText(sb.toString());
            outputArea.setScrollTop(Double.MAX_VALUE);
        } catch (IllegalArgumentException e) {
            sb.append(e.getMessage() + "\n");
            outputArea.setText(sb.toString());
            outputArea.setScrollTop(Double.MAX_VALUE);
        } catch (NullPointerException e) {
            sb.append(e.getMessage() + "\n");
            outputArea.setText(sb.toString());
            outputArea.setScrollTop(Double.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
            sb.append(e.getMessage() + "\n");
            outputArea.setText(sb.toString());
            outputArea.setScrollTop(Double.MAX_VALUE);
        }
    }

    @FXML
    public void formulasToMathMLToWord() {
        String lines = outputArea.getText();
        if (lines == null || lines.length() < 1) return;
        MathMLConverter converter = new MathMLConverter();
        for (String line : lines.split("\\n")) {
            converter.addLine(line);
        }
        converter.convert();
        WordMathWriter wordMathWriter = new WordMathWriter();
        for (String line : converter.getAllLinesAsList()) {
            wordMathWriter.addLine(line);
        }
        try {
            wordMathWriter.write();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
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
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(text);
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveLastKnownDirectory(File file) {
        lastKnownDirectoryProperty.setValue(file.getParentFile());
        fileLocationText.setText(file.getAbsolutePath());
    }
}