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

    private final Timeline PROGRESS_BAR_TIMELINE = new Timeline(new KeyFrame(Duration.seconds(0), event -> yearPassed(1)),new KeyFrame(Duration.seconds(0.5)));

    private List<Moss> mossList = new ArrayList<>();
    private List<Grass> grassList = new ArrayList<>();
    private List<Bush> bushList = new ArrayList<>();
    private List<DeciduousTree> dtreeList = new ArrayList<>();
    private List<ConiferousTree> ctreeList = new ArrayList<>();



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

        if (PROGRESS_BAR_TIMELINE.getStatus() == Animation.Status.RUNNING)
            PROGRESS_BAR_TIMELINE.stop();
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

        PROGRESS_BAR_TIMELINE.setCycleCount(Animation.INDEFINITE);
        PROGRESS_BAR_TIMELINE.play();
    }
    @FXML private void stopSimButtonClick() {

        if (PROGRESS_BAR_TIMELINE.getStatus() == Animation.Status.RUNNING)
            PROGRESS_BAR_TIMELINE.stop();
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
                    dtreeList.get(j).process(mossList, grassList,dtreeList);

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
                    ctreeList.get(j).process(mossList, grassList, ctreeList);

                    if (ctreeList.size() < ctreeListSizeBeforeProcessing)
                    {
                        j--;
                        numOfProcessedPlantsFromList--;
                    }
                }

                CallWind(2,8,3, 2, 2);

                if ((int)(Math.round(progressBar.getProgress()*200)) >= 70)
                    CallWind(4,0,0,0,0);


                logTextArea.appendText("----------------------ГОД: " + (int)(Math.round(progressBar.getProgress()*200)) + "----------------------\n");
                logTextArea.appendText("мох: "+ mossList.size()+" трава: " + grassList.size() + " кустарник: " + bushList.size() + "\n" +
                        "лиственных деревьев: " + dtreeList.size() + "\nхвойных деревьев: " + ctreeList.size()+"\n\n");


                if (progressBar.getProgress() >= 1.00){
                    if (PROGRESS_BAR_TIMELINE.getStatus() == Animation.Status.RUNNING)
                        PROGRESS_BAR_TIMELINE.stop();

                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                    infoAlert.setTitle("Поздравляем!!!");
                    infoAlert.setHeaderText("Вы закончили симуляцию!");
                    infoAlert.show();
                }
            }

            else
            {
                if (PROGRESS_BAR_TIMELINE.getStatus() == Animation.Status.RUNNING)
                    PROGRESS_BAR_TIMELINE.stop();

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
            DeciduousTree.dropSeed((SoilRectangle) field.getChildren().get(plantPlaceColumn + plantPlaceRow*FIELD_RANG), mossList, grassList, dtreeList);
        }

        //CTree section
        for  (int i=1; i<=numOfCTreePlantedSeeds; i++)
        {
            plantPlaceColumn = Helper.randomWithRange(0, FIELD_RANG-1);
            plantPlaceRow = Helper.randomWithRange(0, FIELD_RANG-1);
            ConiferousTree.dropSeed((SoilRectangle) field.getChildren().get(plantPlaceColumn + plantPlaceRow*FIELD_RANG), mossList, grassList, ctreeList);
        }
    }
}