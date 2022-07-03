package com.example.demo;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.List;

public class ConiferousTree extends Plant{
    public static double CTREE_BIO_GEN_APPR_NUM = 2;
    public static double CTREE_BIO_GEN_SPREAD_PROC = 0.2;
    public static double CTREE_STORED_BIO_MASS_PROC = 0.8;

    public static double CTREE_NEEDLES_PROP_IN_LITT = 0.5;
    public static int CTREE_OLD_AGE = 20;
    private static int CTREE_FULL_GROWN_AGE = 10;


    private int age;
    private double storedBioMass;
    private double bioMassGenFullGrown;

    private ObservableList<SoilRectangle> treeCroneCoveredSoil;

    ConiferousTree(SoilRectangle rectForPlant){
        super(rectForPlant);
        age = 0;
        storedBioMass = 0;
        bioMassGenFullGrown = Math.random()*(2*CTREE_BIO_GEN_SPREAD_PROC*CTREE_BIO_GEN_APPR_NUM)+CTREE_BIO_GEN_APPR_NUM*(1-CTREE_BIO_GEN_SPREAD_PROC);
        treeCroneCoveredSoil = rectForPlant.getNearInRadius(1);
    }

    public static boolean dropSeed(SoilRectangle rectForPlant, List<Moss> mossList, List<Grass> grassList, List<Bush> bushList, List<ConiferousTree> ctreeList) {
        if (rectForPlant.getFertilityLevel() >= 30)
        {
            ObservableList<SoilRectangle> listOfNear = rectForPlant.getNearInRadius(3);
            for (SoilRectangle soilRect : listOfNear) {
                if (soilRect.getFill() == Color.BROWN || soilRect.getFill() == Color.FORESTGREEN)
                    return false;
            }

            if (rectForPlant.getFill() == Color.LIGHTGRAY)
            {
                plant(rectForPlant, ctreeList);
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
                plant(rectForPlant, ctreeList);
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
                plant(rectForPlant, ctreeList);
                return true;
            }
            return false;
        }
        else
            return false;
    }

    public static void plant(SoilRectangle rectForPlant, List<ConiferousTree> ctreeList){
        rectForPlant.setFill(Color.FORESTGREEN);
        ctreeList.add(new ConiferousTree(rectForPlant));
    }

    public void process(List<Moss> mossList, List<Grass> grassList, List<Bush> bushList, List<ConiferousTree> ctreeList) {
        age++;

        //Расчитать в завиисимости от возраста умирания
        if (age >= CTREE_OLD_AGE)
        {
            if (age == CTREE_OLD_AGE)
            {
                plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen/3);
                bioMassGen = bioMassGen*2/3;
            }
            else if (age == CTREE_OLD_AGE+1)
            {
                plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen/2);
                bioMassGen=bioMassGen/2;
            }
            else
            {
                plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen);
                getPlantPlaceRect().setFill(Color.LIGHTGRAY);
                ctreeList.remove(this);
            }
        }
        else if (age >= CTREE_FULL_GROWN_AGE)
        {
            storedBioMass += bioMassGen * CTREE_STORED_BIO_MASS_PROC;

            for (SoilRectangle soilRect : treeCroneCoveredSoil) {
                plantPlaceRect.setPineCongestionLevel(plantPlaceRect.getPineCongestionLevel()+
                        bioMassGen*(1-CTREE_STORED_BIO_MASS_PROC)*CTREE_NEEDLES_PROP_IN_LITT/treeCroneCoveredSoil.size());
                plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+
                        bioMassGen*(1-CTREE_STORED_BIO_MASS_PROC)*(1-CTREE_NEEDLES_PROP_IN_LITT)/treeCroneCoveredSoil.size());
            }

            ObservableList<SoilRectangle> potentialPlantPlaces = plantPlaceRect.getNearInRing(4,6);
            int numOfPotentialPlantPlaces = potentialPlantPlaces.size();
            boolean[] isRectInspected = new boolean[numOfPotentialPlantPlaces];

            int plantPlaceIndex;
            int numOfSeedsToDrop = Helper.randomWithRange(1,2);
            do{
                plantPlaceIndex = Helper.randomWithRange(0,numOfPotentialPlantPlaces-1);
                isRectInspected[plantPlaceIndex] = true;
                if(ConiferousTree.dropSeed(potentialPlantPlaces.get(plantPlaceIndex), mossList, grassList, bushList, ctreeList))
                    numOfSeedsToDrop--;

            }while((numOfSeedsToDrop>0) && List.of(isRectInspected).contains(false));
        }
        else
        {
            bioMassGen = bioMassGenFullGrown*(1-0.1*(CTREE_FULL_GROWN_AGE-age));
            storedBioMass += bioMassGen * CTREE_STORED_BIO_MASS_PROC;
            plantPlaceRect.setPineCongestionLevel(plantPlaceRect.getPineCongestionLevel()+bioMassGen*(1-CTREE_STORED_BIO_MASS_PROC)*CTREE_NEEDLES_PROP_IN_LITT);
            plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen*(1-CTREE_STORED_BIO_MASS_PROC)*(1-CTREE_NEEDLES_PROP_IN_LITT));
        }
    }
}
