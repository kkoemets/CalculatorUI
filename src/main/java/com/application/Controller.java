package com.application;

import java.io.*;
import java.util.Scanner;

import com.application.api.calculator.Calculator;
import com.application.api.Utils;
import com.application.api.converter.Converter;
import com.application.api.converter.VariableBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.formula.FormulaParseException;

//todo!!! warning when overwriting variable in vBase?
//todo!!! line numbers! maybe something simpler in the beginning
//todo!!! save last save directory
//todo!!! quick-save file with shortcut  (already opened file)
//todo!!! error messages make no sense!!!
//todo!!! declaring variables on different lines seems unreasonable, maybe try something else? '|'
//todo!!! NEW API TO COMPARE vars 1 < 2 etc...! urgent
//todo!!! critical bug!  Unused input [qrt(200/651)] after attempting to parse the formula [1+8qrt(200/651)] with variable 's'
//todo!!! critical design problem! remake UI
//todo!!! urgent!!! ASCII TO MATHML
/**
 * NOTE: Actions for buttons etc. are set in FXML file!
 */
public class Controller {


    //directories
    private final String DIRECTORY_HELP = "src/main/java/com/application/appdata/help/helpUI.txt";
    private final String DIRECTORY_TEMPLATES = "src/main/java/com/application/appdata/templates/";
    //

    private Calculator calculator = new Calculator();

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
        File template = new File(DIRECTORY_HELP);
        StringBuilder collect = new StringBuilder();
        try {
            Scanner scanner = new Scanner(template);

            while (scanner.hasNextLine()) {
                collect.append(scanner.nextLine() + "\n");
            }
        } catch (FileNotFoundException e) {
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
        Converter converter = new Converter(); // converts variables to numbers from variablebase
        VariableBase variableBase = new VariableBase(); // stores variable names, values and units
        StringBuilder sb = new StringBuilder(); // collects lines to output to output area
        int lineCounter = 0; // counts lines
        try {
            // linebreak splits into individual lines to parse
            for (String stringToParse : inputArea.getText().split("\\n")) {
                lineCounter++;
                if (stringToParse.contains("//")) { // removing possible comments
                    stringToParse = stringToParse.substring(0, stringToParse.indexOf("//"));
                }
                try {
                    if (stringToParse.equals("") ) { // if line is empty
                        sb.append("\n");
                    } else if (stringToParse.substring(0, stringToParse.indexOf(":")).equals("c")) { // if the lines is meant as a comment
                        // removing syntax and appending to stringbuilder
                        sb.append(stringToParse.substring(stringToParse.indexOf(":") + 1) + "\n");
                    } else if (stringToParse.substring(0, stringToParse.indexOf(":")).equals("set")) { // if the lines is meant as a variable setter
                        stringToParse = Utils.clean(stringToParse);
                        // parsing variable name
                        String varName = stringToParse.substring(stringToParse.indexOf(':') + 1, stringToParse.indexOf('='));
                        // parsing variables value
                        String value = stringToParse.substring(stringToParse.indexOf('=') + 1, stringToParse.lastIndexOf(','));
                        // parsing variables unit
                        String unit = stringToParse.substring(stringToParse.lastIndexOf(',') + 1);
                        // adding variable to variable base
                        variableBase.add(varName,value,unit);
                        // showing result in output area
                        sb.append(varName + " = " + variableBase.getValue(varName) + ' ' + variableBase.getUnit(varName) + "\n");
                    } else if ((stringToParse.substring(0, 5).equals("calcf") && stringToParse.contains("="))) {
                        stringToParse = Utils.clean(stringToParse);
                        // setting decimal place from calcf(x): <-- where x is the parameter
                        String decimalPlaceNumber = stringToParse.substring(0, stringToParse.indexOf(':'));
                        calculator.setDecimalPlace(Integer.parseInt(decimalPlaceNumber.substring(decimalPlaceNumber.indexOf('(') + 1, decimalPlaceNumber.indexOf(')'))));
                        // parsing varname, which is after : and before =
                        String varName = stringToParse.substring(stringToParse.indexOf(':') + 1, stringToParse.indexOf('='));
                        // parsing formula (not converted)
                        String unCalculatedFormula = stringToParse.substring(stringToParse.indexOf('=') + 1, stringToParse.lastIndexOf(','));
                        // parsing variables unit
                        String unit = stringToParse.substring(stringToParse.lastIndexOf(',') + 1);
                        // saving formula
                        String calculatedFormula = converter.convertString(unCalculatedFormula, variableBase);
                        // calculating formula and saving to varbase
                        variableBase.add(varName, calculator.calculate(calculatedFormula), unit);
                        sb.append(varName + " = " + unCalculatedFormula + " = " + calculatedFormula + " = " + variableBase.getValue(varName) + " " + variableBase.getUnit(varName) + "\n");
                    } else if (stringToParse.substring(0, stringToParse.indexOf(":")).equals("getVars")) {
                        sb.append("List of saved variables:\n" + variableBase.getVariableBaseListed() + "\n");
                    } else {
                        sb.append("Unknown command on line: " + lineCounter + "\n");
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    sb.append("Unknown command on line: " + lineCounter + "\n");
                }
            }
            outputArea.setText(sb.toString());
        } catch (IOException e) {
            System.out.println("Looks like there was a problem a problem creating/reading the Excel file. Check the directory\n");
            outputArea.setText("ERROR 01");
        } catch (FormulaParseException e) {
            e.printStackTrace();
            outputArea.setText("Incorrect formula on line :" + lineCounter + "\n Check for syntax mistakes!");
        } catch (NumberFormatException e) {
            outputArea.setText("Incorrect variable declaration on line: "+ lineCounter);
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

