package com.example.demo;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;


public class MainController {

    private static final int FIELD_RANG = 23;
    private static final int FIELD_SIZE = FIELD_RANG*FIELD_RANG;

    List<Moss> mossList = new ArrayList<>();
    List<Grass> grassList = new ArrayList<>();
    List<Bush> bushList = new ArrayList<>();
    List<DeciduousTree> dtreeList = new ArrayList<>();
    List<ConiferousTree> ctreeList = new ArrayList<>();


    private final Timeline progressBarTimeline = new Timeline(new KeyFrame(Duration.seconds(0),event -> yearPassed(1)),new KeyFrame(Duration.seconds(0.5)));

    @FXML private TilePane field;
    @FXML private ProgressBar progressBar;
    @FXML private TextArea logTextArea;
    @FXML private VBox simArea;

    public void initialize( ){

        logTextArea.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observableValue, Object o, Object t1) {
                logTextArea.setScrollTop(Double.MAX_VALUE);
            }
        });

        progressBar.setProgress(0.0);

        for (int i = 0; i < FIELD_RANG; i++)
            for (int j = 0; j < FIELD_RANG; j++)
                field.getChildren().add(new SoilRectangle(j,i));

        Moss.plant((SoilRectangle) field.getChildren().get(11+11*23), mossList);
    }

    @FXML private void oneYearStepButtonClick() {
        yearPassed(1);
    }
    @FXML private void tenYearStepButtonClick() {
        yearPassed(10);
    }
    @FXML private void resetSimButtonClick() {

        logTextArea.clear();

        if (progressBarTimeline.getStatus() == Animation.Status.RUNNING)
            progressBarTimeline.stop();
        progressBar.setProgress(0.0);

        mossList.clear();
        grassList.clear();
        bushList.clear();
        dtreeList.clear();
        ctreeList.clear();

        for (int i =0; i<FIELD_SIZE; i++)
        {
            ((SoilRectangle) field.getChildren().get(i)).setToUncolonized();
        }
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
                progressBar.setProgress(progressBar.getProgress() + 0.005);


                int numOfProcessedPlantsFromList = mossList.size();
                int mossListSizeBeforeProcessing;
                for (int j=0; j < numOfProcessedPlantsFromList; j++)
                {
                    mossListSizeBeforeProcessing = mossList.size();
                    mossList.get(j).process(mossList);

                    if (mossList.size() < mossListSizeBeforeProcessing)
                    {
                        j--;
                        numOfProcessedPlantsFromList--;
                    }
                }


                numOfProcessedPlantsFromList = grassList.size();
                int grassListSizeBeforeProcessing;
                for (int j=0; j < numOfProcessedPlantsFromList; j++)
                {
                    grassListSizeBeforeProcessing = grassList.size();
                    grassList.get(j).process(mossList, grassList);

                    if (grassList.size() < grassListSizeBeforeProcessing)
                    {
                        j--;
                        numOfProcessedPlantsFromList--;
                    }
                }


                numOfProcessedPlantsFromList = bushList.size();
                int bushListSizeBeforeProcessing;
                for (int j=0; j < numOfProcessedPlantsFromList; j++)
                {
                    bushListSizeBeforeProcessing = bushList.size();
                    bushList.get(j).process(mossList, grassList, bushList);

                    if (bushList.size() < bushListSizeBeforeProcessing)
                    {
                        j--;
                        numOfProcessedPlantsFromList--;
                    }
                }


                numOfProcessedPlantsFromList = dtreeList.size();
                int dtreeListSizeBeforeProcessing;
                for (int j=0; j < numOfProcessedPlantsFromList; j++)
                {
                    dtreeListSizeBeforeProcessing = dtreeList.size();
                    dtreeList.get(j).process(mossList, grassList, bushList, dtreeList);

                    if (dtreeList.size() < dtreeListSizeBeforeProcessing)
                    {
                        j--;
                        numOfProcessedPlantsFromList--;
                    }
                }


                numOfProcessedPlantsFromList = ctreeList.size();
                int ctreeListSizeBeforeProcessing;
                for (int j=0; j < numOfProcessedPlantsFromList; j++)
                {
                    ctreeListSizeBeforeProcessing = ctreeList.size();
                    ctreeList.get(j).process(mossList, grassList, bushList, ctreeList);

                    if (ctreeList.size() < ctreeListSizeBeforeProcessing)
                    {
                        j--;
                        numOfProcessedPlantsFromList--;
                    }
                }


                logTextArea.appendText("-----------------YEAR "+ +(int)(progressBar.getProgress()*200) + "---------------------\n");
                logTextArea.appendText("moss: "+ mossList.size()+" grass: " + grassList.size() + " bush: " + bushList.size() + "\n" +
                        "deciduous tree: " + dtreeList.size() + " coniferous tree: " + ctreeList.size()+"\n\n");


                CallWind(2,8,3, 2, 2);

                if (progressBar.getProgress()*200 >= 70)
                    CallWind(4,0,0,0,0);

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

    private void CallWind(int numOfMossPlantedSeeds, int numOfGrassPlantedSeeds, int numOfBushPlantedSeeds, int numOfDTreePlantedSeeds, int numOfCTreePlantedSeeds){
        int plantPlaceColumn;
        int plantPlaceRow;

        //Moss section
        for  (int i=1; i<=numOfMossPlantedSeeds; i++)
        {
            plantPlaceColumn = Helper.randomWithRange(0, FIELD_RANG-1);
            plantPlaceRow = Helper.randomWithRange(0, FIELD_RANG-1);
            Moss.dropSeed((SoilRectangle) field.getChildren().get(plantPlaceColumn + plantPlaceRow*FIELD_RANG), mossList);
        }

        //Grass section
        for  (int i=1; i<=numOfGrassPlantedSeeds; i++)
        {
            plantPlaceColumn = Helper.randomWithRange(0, FIELD_RANG-1);
            plantPlaceRow = Helper.randomWithRange(0, FIELD_RANG-1);
            Grass.dropSeed((SoilRectangle) field.getChildren().get(plantPlaceColumn + plantPlaceRow*FIELD_RANG), mossList, grassList);
        }

        //Bush section
        for  (int i=1; i<=numOfBushPlantedSeeds; i++)
        {
            plantPlaceColumn = Helper.randomWithRange(0, FIELD_RANG-1);
            plantPlaceRow = Helper.randomWithRange(0, FIELD_RANG-1);
            Bush.dropSeed((SoilRectangle) field.getChildren().get(plantPlaceColumn + plantPlaceRow*FIELD_RANG), mossList, grassList, bushList);
        }

        //DTree section
        for  (int i=1; i<=numOfDTreePlantedSeeds; i++)
        {
            plantPlaceColumn = Helper.randomWithRange(0, FIELD_RANG-1);
            plantPlaceRow = Helper.randomWithRange(0, FIELD_RANG-1);
            DeciduousTree.dropSeed((SoilRectangle) field.getChildren().get(plantPlaceColumn + plantPlaceRow*FIELD_RANG), mossList, grassList, bushList, dtreeList);
        }

        //CTree section
        for  (int i=1; i<=numOfCTreePlantedSeeds; i++)
        {
            plantPlaceColumn = Helper.randomWithRange(0, FIELD_RANG-1);
            plantPlaceRow = Helper.randomWithRange(0, FIELD_RANG-1);
            ConiferousTree.dropSeed((SoilRectangle) field.getChildren().get(plantPlaceColumn + plantPlaceRow*FIELD_RANG), mossList, grassList, bushList, ctreeList);
        }
    }
}