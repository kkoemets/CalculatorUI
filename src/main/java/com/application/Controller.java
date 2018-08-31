package com.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    @FXML
    private MenuButton templateMenu;

    @FXML
    private TextArea outputArea;

    @FXML
    private TextArea inputArea;



    @FXML
    void initialize() {
        initializeTemplateMenu();
        initializeInstructionsTextAndAlert();
    }

    private Alert alert;
    void initializeInstructionsTextAndAlert() {
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

    @FXML
    private void parse() {
        Converter converter = new Converter();
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
                        str = Utils.clean(str);
                        String varName = str.substring(str.indexOf(":") + 1, str.indexOf('='));
                        str = Utils.clean(str.substring(str.indexOf('=') + 1, str.length()));
                        converter.setVar(varName + "=" + str);
                        sb.append(varName + '=' + converter.getVarBase().get(Utils.clean(varName)) + "\n");
                    } else if ((str.substring(0, 5).equals("calcf") && str.contains("="))) {
                        // setting decimal place from calcf(x): <-- where x is the parameter
                        String stringForSettingDecimalPlace = str.substring(0, str.indexOf(':'));
                        calculator.setDecimalPlace(Integer.parseInt(stringForSettingDecimalPlace.substring(stringForSettingDecimalPlace.indexOf('(') + 1, stringForSettingDecimalPlace.indexOf(')'))));
                        // getting varname, which is after : and before =
                        String varName = str.substring(str.indexOf(":") + 1, str.indexOf('='));
                        // saving formula to string (not converted)
                        String unParsedFormula = Utils.clean(str.substring(str.indexOf('=') + 1, str.length()));
                        // saving formula
                        str = converter.convertString(unParsedFormula);
                        // calculating formula and saving to varbase
                        converter.setVar(varName + "=" + calculator.calculate(str));
                        sb.append(varName + "=" + unParsedFormula + "=" + str + "=" + converter.getVarBase().get(Utils.clean(varName)) + "\n");
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
            outputArea.setText("Incorrect variable declaration on line: "+ lineCounter);
        }

    }
}

