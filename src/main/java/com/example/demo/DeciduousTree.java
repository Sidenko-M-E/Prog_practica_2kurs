package com.example.demo;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.List;

public class DeciduousTree extends Plant{
    public static double DTREE_BIO_GEN_APPR_NUM = 2;
    public static double DTREE_BIO_GEN_SPREAD_PROC = 0.2;
    public static double DTREE_STORED_BIO_MASS_PROC = 0.2;
    public static int DTREE_OLD_AGE = 20;
    private static int DTREE_FULL_GROWN_AGE = 10;


    private int age;
    private double storedBioMass;
    private double bioMassGenFullGrown;

    private ObservableList<SoilRectangle> treeCroneCoveredSoil;

    DeciduousTree(SoilRectangle rectForPlant){
        super(rectForPlant);
        age = 0;
        storedBioMass = 0;
        bioMassGenFullGrown = Math.random()*(2*DTREE_BIO_GEN_SPREAD_PROC*DTREE_BIO_GEN_APPR_NUM)+DTREE_BIO_GEN_APPR_NUM*(1-DTREE_BIO_GEN_SPREAD_PROC);
        treeCroneCoveredSoil = rectForPlant.getNearInRadius(1);
    }

    public static boolean dropSeed(SoilRectangle rectForPlant, List<Moss> mossList, List<Grass> grassList, List<Bush> bushList, List<DeciduousTree> dtreeList) {
        if ((rectForPlant.getFertilityLevel() >= 30) &&
                (rectForPlant.getPineCongestionLevel() <= 15))
        {
            ObservableList<SoilRectangle> listOfNear = rectForPlant.getNearInRadius(3);
            for (SoilRectangle soilRect : listOfNear) {
                if (soilRect.getFill() == Color.BROWN || soilRect.getFill() == Color.FORESTGREEN)
                    return false;
            }

            if (rectForPlant.getFill() == Color.LIGHTGRAY)
            {
                plant(rectForPlant, dtreeList);
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
                plant(rectForPlant, dtreeList);
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
                plant(rectForPlant, dtreeList);
                return true;
            }
            return false;
        }
        else
            return false;
    }

    public static void plant(SoilRectangle rectForPlant, List<DeciduousTree> dtreeList){
        rectForPlant.setFill(Color.BROWN);
        dtreeList.add(new DeciduousTree(rectForPlant));
    }

    public void process(List<Moss> mossList, List<Grass> grassList, List<Bush> bushList, List<DeciduousTree> dtreeList) {
        age++;

        //Расчитать в завиисимости от возраста умирания
        if (age >= DTREE_OLD_AGE)
        {
            if (age == DTREE_OLD_AGE)
            {
                plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen/3);
                bioMassGen = bioMassGen*2/3;
            }
            else if (age == DTREE_OLD_AGE+1)
            {
                plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen/2);
                bioMassGen=bioMassGen/2;
            }
            else
            {
                plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen);
                getPlantPlaceRect().setFill(Color.LIGHTGRAY);
                dtreeList.remove(this);
            }
        }
        else if (age >= DTREE_FULL_GROWN_AGE)
        {
            storedBioMass += bioMassGen * DTREE_STORED_BIO_MASS_PROC;

            for (SoilRectangle soilRect : treeCroneCoveredSoil) {
                plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+
                        bioMassGen*(1-DTREE_STORED_BIO_MASS_PROC)/treeCroneCoveredSoil.size());
            }

            ObservableList<SoilRectangle> potentialPlantPlaces = plantPlaceRect.getNearInRing(4,6);
            int numOfPotentialPlantPlaces = potentialPlantPlaces.size();
            boolean[] isRectInspected = new boolean[numOfPotentialPlantPlaces];

            int plantPlaceIndex;
            int numOfSeedsToDrop = Helper.randomWithRange(1,2);
            do{
                plantPlaceIndex = Helper.randomWithRange(0,numOfPotentialPlantPlaces-1);
                isRectInspected[plantPlaceIndex] = true;
                if(DeciduousTree.dropSeed(potentialPlantPlaces.get(plantPlaceIndex), mossList, grassList, bushList, dtreeList))
                    numOfSeedsToDrop--;

            }while((numOfSeedsToDrop>0) && List.of(isRectInspected).contains(false));
        }
        else
        {
            bioMassGen = bioMassGenFullGrown*(1-0.1*(DTREE_FULL_GROWN_AGE-age));
            storedBioMass += bioMassGen * DTREE_STORED_BIO_MASS_PROC;
            plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen*(1-DTREE_STORED_BIO_MASS_PROC));
        }
    }
}
