package com.example.demo;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import java.util.List;

public class Moss extends Plant{
    //Constants
    private static final double MOSS_BIO_GEN_APPR_NUM = 1;
    private static final double MOSS_BIO_GEN_SPREAD_PROC = 0.2;

    private static final double MOSS_MAX_CONG_LEVEL = 150;



    //Constructor
    Moss(SoilRectangle rectForPlant){
        super(rectForPlant);
        bioMassGen = Math.random()*(2*MOSS_BIO_GEN_SPREAD_PROC*MOSS_BIO_GEN_APPR_NUM)+MOSS_BIO_GEN_APPR_NUM*(1-MOSS_BIO_GEN_SPREAD_PROC);
    }


    //Static methods
    public static boolean dropSeed(SoilRectangle rectForPlant, List<Moss> mossList) {
        if ((rectForPlant.getPineCongestionLevel() < MOSS_MAX_CONG_LEVEL) && (rectForPlant.getFill() == Color.LIGHTGRAY)){
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


    //Methods
    public void process(List<Moss> mossList) {
        //Увеличиваем плодородие почвы под растением
        plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen);

        //Проверяем на превышение уровня загрязнённости
        if (plantPlaceRect.getPineCongestionLevel() >= MOSS_MAX_CONG_LEVEL) {
            plantPlaceRect.setFill(Color.LIGHTGRAY);
            mossList.remove(this);
        }

        //Размножиться
        else{
            ObservableList<SoilRectangle> potentialPlantPlaces = plantPlaceRect.getNearInRadius(1);
            int numOfPotentialPlantPlaces = potentialPlantPlaces.size();
            boolean[] isRectInspected = new boolean[numOfPotentialPlantPlaces];

            int plantPlaceIndex;
            do{
                plantPlaceIndex = (int)(Math.random()*numOfPotentialPlantPlaces);
                isRectInspected[plantPlaceIndex] = true;
            }while(!Moss.dropSeed(potentialPlantPlaces.get(plantPlaceIndex),mossList) && List.of(isRectInspected).contains(false));
        }
    }
}