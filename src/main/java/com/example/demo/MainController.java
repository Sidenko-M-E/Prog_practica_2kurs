package com.example.demo;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;


public class MainController {

    private static final int FIELD_RANG = 23;
    private static final int FIELD_SIZE = FIELD_RANG*FIELD_RANG;

    List<Moss> mossList = new ArrayList<>();

    private final Timeline progressBarTimeline = new Timeline(
            new KeyFrame(
                    Duration.seconds(0),
                    event -> yearPassed(1)
            ),
            new KeyFrame(
                    Duration.seconds(0.5)
            )
    );

    @FXML private TilePane field;
    @FXML private ProgressBar progressBar;

    public void initialize( ){

        progressBar.setProgress(0.0);

        for (int i = 0; i < FIELD_RANG; i++)
            for (int j = 0; j < FIELD_RANG; j++)
                field.getChildren().add(new SoilRectangle(i,j));

        Moss.plant((SoilRectangle) field.getChildren().get(11*FIELD_RANG + 11), mossList);
    }

    @FXML private void oneYearStepButtonClick() {
        yearPassed(1);
    }
    @FXML private void tenYearStepButtonClick() {
        yearPassed(10);
    }
    @FXML private void resetSimButtonClick() {

        if (progressBarTimeline.getStatus() == Animation.Status.RUNNING)
            progressBarTimeline.stop();

        progressBar.setProgress(0.0);

        mossList.clear();
        for (int i =0; i<FIELD_SIZE; i++)
            ((SoilRectangle) field.getChildren().get(i)).setFill(Color.LIGHTGRAY);

        Moss.plant((SoilRectangle) field.getChildren().get(11*FIELD_RANG + 11), mossList);
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

                int numOfProcessedPlantsFromList = mossList.size();
                for (int j=0;j<numOfProcessedPlantsFromList;j++)
                    mossList.get(j).process(field, mossList);

                //CallWind();



                if (progressBar.getProgress() >= 1.00){
                    if (progressBarTimeline.getStatus() == Animation.Status.RUNNING)
                        progressBarTimeline.stop();

                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                    infoAlert.setTitle("Поздравляем!!!");
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
        int numOfMossPlantedSeeds = getRndIntInRange(1, 1);
        for  (int i=1; i<=numOfMossPlantedSeeds; i++)
        {
            plantPlaceColumn = getRndIntInRange(0, FIELD_RANG-1);
            plantPlaceRow = getRndIntInRange(0, FIELD_RANG-1);
            Moss.dropSeed((SoilRectangle) field.getChildren().get(plantPlaceColumn + plantPlaceRow*FIELD_RANG), mossList);
        }

        //Grass section
    }

    public static int getRndIntInRange(int min, int max){
        return (int)(Math.random()*((max-min)+1))+min;
    }
}