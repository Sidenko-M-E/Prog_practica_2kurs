package com.example.demo;

import javafx.collections.ObservableList;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import java.util.List;

public class Moss extends Plant{

    private final double MOSS_BIO_GEN_APPR_NUM = 1;
    private final double MOSS_BIO_GEN_SPREAD_PROC = 0.2;


    Moss(SoilRectangle rectForPlant){
        super(rectForPlant);
        bioMassGen = Math.random()*(2*MOSS_BIO_GEN_SPREAD_PROC*MOSS_BIO_GEN_APPR_NUM)+MOSS_BIO_GEN_APPR_NUM*(1-MOSS_BIO_GEN_SPREAD_PROC);
    }

    public static boolean dropSeed(SoilRectangle rectForPlant, List<Moss> mossList) {
        if ((rectForPlant.getPineCongestionLevel() <= 10) && (rectForPlant.getFill() == Color.LIGHTGRAY)){
            plant(rectForPlant, mossList);
            return true;
        }
        else
            return false;
    }

    public static void plant(SoilRectangle rectForPlant, List<Moss> mossList){
        rectForPlant.setFill(Color.DARKOLIVEGREEN);
        mossList.add(new Moss(rectForPlant));
    }

    public void process(TilePane field, List<Moss> mossList) {
        plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen);

        ObservableList<SoilRectangle> potentialPlantPlaces = plantPlaceRect.getNearInRadius(field.getChildren(), 1);
        int numOfPotentialPlantPlaces = potentialPlantPlaces.size();
        boolean[] isRectInspected = new boolean[numOfPotentialPlantPlaces];

        int plantPlaceIndex;
        do{
            plantPlaceIndex = (int)(Math.random()*numOfPotentialPlantPlaces);
            isRectInspected[plantPlaceIndex] = true;
        }while(!Moss.dropSeed(potentialPlantPlaces.get(plantPlaceIndex),mossList) && List.of(isRectInspected).contains(false));
    }
}