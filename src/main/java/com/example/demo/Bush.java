package com.example.demo;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;

import java.util.List;

public class Bush extends Plant{
    public static double BUSH_BIO_GEN_APPR_NUM = 2;
    public static double BUSH_BIO_GEN_SPREAD_PROC = 0.2;
    public static double BUSH_STORED_BIO_MASS_PROC = 0.2;
    public static int BUSH_OLD_AGE = 20;
    private static int BUSH_FULL_GROWN_AGE = 10;


    int age;
    double storedBioMass;
    double bioMassGenFullGrown;

    Bush(SoilRectangle rectForPlant){
        super(rectForPlant);
        age = 0;
        storedBioMass = 0;
        bioMassGenFullGrown = Math.random()*(2*BUSH_BIO_GEN_SPREAD_PROC*BUSH_BIO_GEN_APPR_NUM)+BUSH_BIO_GEN_APPR_NUM*(1-BUSH_BIO_GEN_SPREAD_PROC);
    }

    public static boolean dropSeed(SoilRectangle rectForPlant, List<Moss> mossList, List<Grass> grassList, List<Bush> bushList) {
        if ((rectForPlant.getFertilityLevel() >= 15) &&
                (rectForPlant.getPineCongestionLevel() <= 25))
        {
            if (rectForPlant.getFill() == Color.LIGHTGRAY)
            {
                plant(rectForPlant, bushList);
                return true;
            }
            else if (rectForPlant.getFill() == Color.DARKOLIVEGREEN)
            {
                for (int i=0;i< mossList.size();i++)
                {
                    if (mossList.get(i).getPlantPlaceRect() == rectForPlant)
                    {
                        mossList.remove(i);
                        break;
                    }
                }
                plant(rectForPlant, bushList);
                return true;
            }
            else if(rectForPlant.getFill() == Color.LIGHTGREEN)
            {
                for (int i=0;i< grassList.size();i++)
                {
                    if (grassList.get(i).getPlantPlaceRect() == rectForPlant)
                    {
                        grassList.remove(i);
                        break;
                    }
                }
                plant(rectForPlant, bushList);
                return true;
            }
        }
        return false;
    }

    public static void plant(SoilRectangle rectForPlant, List<Bush> bushList){
        rectForPlant.setFill(Color.CHOCOLATE);
        bushList.add(new Bush(rectForPlant));
    }

    public void process(List<Moss> mossList, List<Grass> grassList, List<Bush> bushList) {
        age++;
        
        if (age >= BUSH_OLD_AGE)
        {
            if (age == BUSH_OLD_AGE)
            {
                plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen/3);
                bioMassGen = bioMassGen*2/3;
            }
            else if (age == BUSH_OLD_AGE+1)
            {
                plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen/2);
                bioMassGen=bioMassGen/2;
            }
            else
            {
                plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen);
                getPlantPlaceRect().setFill(Color.LIGHTGRAY);
                bushList.remove(this);
            }
        }
        else if (age >= BUSH_FULL_GROWN_AGE)
        {
            storedBioMass += bioMassGen * BUSH_STORED_BIO_MASS_PROC;
            plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen*(1-BUSH_STORED_BIO_MASS_PROC));

            ObservableList<SoilRectangle> potentialPlantPlaces = plantPlaceRect.getNearInRing(3,5);
            int numOfPotentialPlantPlaces = potentialPlantPlaces.size();
            boolean[] isRectInspected = new boolean[numOfPotentialPlantPlaces];

            int plantPlaceIndex;
            int numOfSeedsToDrop = Helper.randomWithRange(1,2);
            do{
                plantPlaceIndex = Helper.randomWithRange(0,numOfPotentialPlantPlaces-1);
                isRectInspected[plantPlaceIndex] = true;
                if(Bush.dropSeed(potentialPlantPlaces.get(plantPlaceIndex), mossList, grassList, bushList))
                    numOfSeedsToDrop--;

            }while((numOfSeedsToDrop>0) && List.of(isRectInspected).contains(false));
        }
        else
        {
            bioMassGen = bioMassGenFullGrown*(1-0.1*(BUSH_FULL_GROWN_AGE-age));
            storedBioMass += bioMassGen * BUSH_STORED_BIO_MASS_PROC;
            plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen*(1-BUSH_STORED_BIO_MASS_PROC));
        }
    }
}
