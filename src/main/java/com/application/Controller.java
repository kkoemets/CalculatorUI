package com.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import com.application.api.calculator.Calculator;
import com.application.api.common.Utils;
import com.application.api.converter.Converter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.TextAlignment;

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
        initializeTemplatesBtn();
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

    private void initializeTemplatesBtn() {

    }



    private void initializeOutputAre() {
    }

    private void initializeParseBtn() {
        parseBtn.setOnAction(event -> {
            StringBuilder sb = new StringBuilder();
            for (String str : inputArea.getText().split("\\n")) {
                if (str.equals("")) {
                    sb.append("\n");
                } else if (str.indexOf(" ") == -1) {
                    sb.append("Unknown command on this line\n");
                } else if (str.substring(0, str.indexOf(" ")).equals("calc")) {
                    sb.append(calculator.calculateEquation(Utils.clean(str.substring(str.indexOf(' ') + 1, str.length())))+ "\n");
                } else if (str.substring(0, str.indexOf(" ")).equals("comment")) {
                    sb.append(str.substring("comment ".length()) + "\n");
                } else if (str.substring(0, str.indexOf(" ")).equals("set")) {
                    str = str.substring(str.indexOf(' '), str.length());
                    str = Utils.clean(str);
                    sb.append(converter.setVar(str) + "\n");
                } else if ((str.substring(0, str.indexOf(" ")).equals("calcf") && str.contains("="))) {
                    try {
                        String strDef = str.substring(str.indexOf(' ') + 1,str.indexOf('=') + 1);
                        str = converter.convertString(Utils.clean(str.substring(str.indexOf('=') + 1, str.length())));
                        sb.append(converter.setVar(strDef + calculator.calculateEquation(str)+ "\n"));
                    } catch (Exception e) {
                        sb.append(e.getMessage() + "\n");
                        e.printStackTrace();
                    }
                } else if(str.substring(0, str.indexOf(" ")).equals("getVars")) {
                    // method in converter
                } else {
                    sb.append("Unknown command on this line\n");
                }
            }
            outputArea.setText(sb.toString());
        });
    }
}

