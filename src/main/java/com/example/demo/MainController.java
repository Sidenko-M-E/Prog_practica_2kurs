package com.example.demo;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class MainController {

    private static final int FIELD_RANG = 23;
    private int counter = 0;

    private Timeline progressBarTimeline = new Timeline(
            new KeyFrame(
                    Duration.seconds(0),
                    event -> yearPassed(1)
            ),
            new KeyFrame(
                    Duration.seconds(0.5)
            )
    );

    @FXML private Pane rootPane;
    @FXML private TilePane field;
    @FXML private Button oneYearStepButton;
    @FXML private Button tenYearStepButton;
    @FXML private Button resetSimButton;
    @FXML private Button runSimButton;
    @FXML private Button stopSimButton;
    @FXML private ProgressBar progressBar;

    public void initialize( ) throws Exception {

        int fieldSize = FIELD_RANG*FIELD_RANG;

        for (int i = 1; i <= fieldSize; i++) {
            Rectangle rect = new Rectangle(10,10){{
                setFill(Color.LIGHTGRAY);}
            };
            field.getChildren().add(rect);
            progressBar.setProgress(0.0);
        }
    }

    @FXML private void oneYearStepButtonClick() {
        yearPassed(1);
    }
    @FXML private void tenYearStepButtonClick() {
        yearPassed(10);
    }
    @FXML private void resetSimButtonClick() {

        System.out.println("resetSimButton");
        progressBar.setProgress(0.0);
    }
    @FXML private void runSimButtonClick() {

        progressBarTimeline.setCycleCount(Animation.INDEFINITE);
        progressBarTimeline.play();
    }
    @FXML private void stopSimButtonClick() {

        if (progressBarTimeline.getStatus() == Animation.Status.RUNNING)
            progressBarTimeline.stop();
    }

    private void yearPassed(int numberOfYearsPassed) {
        for (int i=0; i<numberOfYearsPassed; i++)
        {
            if (progressBar.getProgress() < 1.00)
            {
                progressBar.setProgress(progressBar.getProgress() + 0.01);


                //Обработка симуляции
                CallWind();


                if (progressBar.getProgress() >= 1.00){
                    if (progressBarTimeline.getStatus() == Animation.Status.RUNNING)
                        progressBarTimeline.stop();

                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                    infoAlert.setTitle("Удар - поднятие!!!");
                    infoAlert.setHeaderText("Вы закончили симуляцию!");
                    infoAlert.show();
                }
            }

            else
            {
                if (progressBarTimeline.getStatus() == Animation.Status.RUNNING)
                    progressBarTimeline.stop();

                Alert attentionAlert = new Alert(Alert.AlertType.ERROR);
                attentionAlert.setTitle("Внимание!!!");
                attentionAlert.setHeaderText("Симуляция уже закончила свою работу.");
                attentionAlert.show();
                break;
            }

        }
    }

    private void CallWind(){
        int plantPlaceColumn;
        int plantPlaceRow;

        //Moss section
        int numOfMossPlantedSeeds = getRndIntInRange(1, 3);
        for  (int i=1; i<=numOfMossPlantedSeeds; i++)
        {
            plantPlaceColumn = getRndIntInRange(0, FIELD_RANG-1);
            plantPlaceRow = getRndIntInRange(0, FIELD_RANG-1);
            PlantSeed(new Moss(plantPlaceColumn, plantPlaceRow));
        }

        //Grass section
    }

    public static int getRndIntInRange(int min, int max){
        return (int)(Math.random()*((max-min)+1))+min;
    }

    private boolean PlantSeed(Moss plantSeed)
    {

    }
}