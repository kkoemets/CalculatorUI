package com.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;


public class Main extends Application {

    private static Stage primaryStage; // **Declare static Stage**

    private static void setPrimaryStage(Stage stage) {
        Main.primaryStage = stage;
    }

    static public Stage getPrimaryStage() {
        return Main.primaryStage;
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getClassLoader().getResource("com/application/fxml/CalculatorUI.fxml"));
        primaryStage.setTitle("CalculatorUI - Kristjan Koemets");
        primaryStage.setScene(new Scene(root));
        //
        primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Close program?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
            } else {
                event.consume();
            }
        });
        //
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
