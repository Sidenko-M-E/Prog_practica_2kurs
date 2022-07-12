package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class App extends Application{

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setTitle("Симулятор смены природных сообществ");
        stage.setWidth(660);
        stage.setHeight(450);
        stage.setResizable(false);

        stage.getIcons().add(new Image(getClass().getResourceAsStream("mainStageIcon.png")));

        stage.show();
    }
}



