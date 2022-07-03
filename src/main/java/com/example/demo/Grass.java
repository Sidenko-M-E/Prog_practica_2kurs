package com.example.demo;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.List;

public class Grass extends Plant{

    private final double GRASS_BIO_GEN_APPR_NUM = 2;
    private final double GRASS_BIO_GEN_SPREAD_PROC = 0.2;


    Grass(SoilRectangle rectForPlant){
        super(rectForPlant);
        bioMassGen = Math.random()*(2*GRASS_BIO_GEN_SPREAD_PROC*GRASS_BIO_GEN_APPR_NUM)+GRASS_BIO_GEN_APPR_NUM*(1-GRASS_BIO_GEN_SPREAD_PROC);
    }

    public static boolean dropSeed(SoilRectangle rectForPlant, List<Moss> mossList, List<Grass> grassList) {
        if ((rectForPlant.getFertilityLevel() >= 6) &&
                (rectForPlant.getPineCongestionLevel() <= 20))
        {
            if (rectForPlant.getFill() == Color.LIGHTGRAY)
            {
                plant(rectForPlant, grassList);
                return true;
            }
            else if (rectForPlant.getFill() == Color.DARKOLIVEGREEN) {
                for (int i=0;i< mossList.size();i++)
                {
                    if (mossList.get(i).getPlantPlaceRect() == rectForPlant)
                    {
                        mossList.remove(i);
                        break;
                    }
                }
                plant(rectForPlant, grassList);
                return true;
            }
            else
                return false;
        }
        else
            return false;
    }

    public static void plant(SoilRectangle rectForPlant, List<Grass> grassList){
        rectForPlant.setFill(Color.LIGHTGREEN);
        grassList.add(new Grass(rectForPlant));
    }

    public void process(List<Moss> mossList, List<Grass> grassList) {
        plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen);

        ObservableList<SoilRectangle> potentialPlantPlaces = plantPlaceRect.getNearInRadius(2);
        int numOfPotentialPlantPlaces = potentialPlantPlaces.size();
        boolean[] isRectInspected = new boolean[numOfPotentialPlantPlaces];

        int plantPlaceIndex;
        int numOfSeedsToDrop = Helper.randomWithRange(1,3);
        do{
            plantPlaceIndex = Helper.randomWithRange(0,numOfPotentialPlantPlaces-1);
            isRectInspected[plantPlaceIndex] = true;
            if(Grass.dropSeed(potentialPlantPlaces.get(plantPlaceIndex), mossList, grassList))
                numOfSeedsToDrop--;

        }while((numOfSeedsToDrop>0) && List.of(isRectInspected).contains(false));
    }
}
