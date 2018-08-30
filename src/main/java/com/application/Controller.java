package com.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import com.application.api.calculator.Calculator;
import com.application.api.Utils;
import com.application.api.converter.Converter;
import com.application.api.converter.VariableBase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.TextAlignment;
import org.apache.poi.ss.formula.FormulaParseException;

public class Controller {

    private Calculator calculator = new Calculator();
    private Converter converter = new Converter();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button saveFormulaBtn;

    @FXML
    private Button loadFormulaBtn;

    @FXML
    private Button templatesBtn;

    @FXML
    private MenuButton templateMenu;

    @FXML
    private TextArea outputArea;

    @FXML
    private TextArea inputArea;

    @FXML
    private Button parseBtn;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu helpMenu;

    @FXML
    void initialize() {
        inputArea.setPromptText("Type your command here...");
        initializeParseBtn();
        initializeTemplateMenu();
        initializeMenuBar();
    }


    private void initializeMenuBar() {
        // helpMenu
        MenuItem commands = new MenuItem("How-to");
        File template = new File("src/main/java/com/application/help/helpUI.txt");
        StringBuilder collect = new StringBuilder();
        try {
            Scanner scanner = new Scanner(template);

            while (scanner.hasNextLine()) {
                collect.append(scanner.nextLine() + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        commands.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("How-to");
            alert.setHeaderText("Below are commands for creating calculations");
            alert.setContentText(collect.toString());
            alert.showAndWait();
        });
        helpMenu.getItems().add(commands);
    }


    private void initializeTemplateMenu() {
        templateMenu.setTextAlignment(TextAlignment.CENTER);
        File folder = new File("src/main/java/com/application/templates/");
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
                File template = new File("src/main/java/com/application/templates/"+ templateName);
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


    private void initializeParseBtn() {
        parseBtn.setOnAction(event -> {
            VariableBase variableBase = new VariableBase();
            StringBuilder sb = new StringBuilder();
            int lineCounter = 0;
            try {
                for (String str : inputArea.getText().split("\\n")) {
                    lineCounter++;
                    Utils.clean(str);
                    try {
                        if (str.equals("")) {
                            sb.append("\n");
                        } else if (str.substring(0, str.indexOf(":")).equals("calc")) {
                            sb.append(calculator.calculate(str.substring(str.indexOf(":") + 1, str.length())) + "\n");
                        } else if (str.substring(0, str.indexOf(":")).equals("comment")) {
                            sb.append(str.substring("comment:".length() + 1) + "\n");
                        } else if (str.substring(0, str.indexOf(":")).equals("set")) {
                            str = str.substring(str.indexOf(":") + 1, str.length());
                            str = Utils.clean(str);
                            sb.append(converter.setVar(str) + "\n");
                        } else if ((str.substring(0, 5).equals("calcf") && str.contains("="))) {
                            String stringForSettingDecimalPlace = str.substring(0, str.indexOf(':'));
                            calculator.setDecimalPlace(Integer.parseInt(stringForSettingDecimalPlace.substring(stringForSettingDecimalPlace.indexOf('(') + 1, stringForSettingDecimalPlace.indexOf(')'))));
                            String varName = str.substring(str.indexOf(":") + 1, str.indexOf('=') + 1);
                            str = converter.convertString(Utils.clean(str.substring(str.indexOf('=') + 1, str.length())));
                            sb.append(converter.setVar(varName + calculator.calculate(str)) + "\n");
                        } else if (str.substring(0, str.indexOf(":")).equals("getVars")) {
                            sb.append("List of saved variables:\n" + converter.getVars() + "\n");
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
                outputArea.setText("Incorrect formula on line :" + lineCounter + "\n Check for syntax mistakes!");
            } catch (NumberFormatException e) {
                outputArea.setText("Incorrent variable declaration on line: "+ lineCounter);
            }
        });
    }
}

