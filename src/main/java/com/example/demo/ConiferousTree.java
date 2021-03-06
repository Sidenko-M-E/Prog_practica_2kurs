package com.example.demo;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.List;

public class ConiferousTree extends Plant{
    //Constants
    private static final double CTREE_BIO_GEN_APPR_NUM = 100;
    private static final double CTREE_BIO_GEN_SPREAD_PROC = 0.05;
    private static final double CTREE_STORED_BIO_MASS_PROC = 0.9;
    private static final double CTREE_NEEDLES_PROP_IN_LITT = 0.8;

    private static final double CTREE_MIN_FERT_LEVEL = 80;

    private static final int CTREE_FULL_GROWN_AGE = 60;
    private static final int CTREE_MIDDLE_AGE = 10;


    private static final int CTREE_MIN_NUM_OF_SEEDS_TO_DROP = 1;
    private static final int CTREE_MAX_NUM_OF_SEEDS_TO_DROP = 5;

    private static final int CTREE_MIN_SEEDS_DROP_RADIUS = 4;
    private static final int CTREE_MAX_SEEDS_DROP_RADIUS = 6;

    private static final int CTREE_MIN_DIST_FROM_NEAR_DTREE = 2;
    private static final int CTREE_MIN_DIST_FROM_NEAR_CTREE = 3;



    //Properties
    private int age;
    private double storedBioMass;
    private final double bioMassGenFullGrown;
    private ObservableList<SoilRectangle> treeCroneCoveredSoil;


    //Constructor
    ConiferousTree(SoilRectangle rectForPlant){
        super(rectForPlant);
        age = 0;
        storedBioMass = 0;
        bioMassGenFullGrown = Math.random()*(2*CTREE_BIO_GEN_SPREAD_PROC* CTREE_BIO_GEN_APPR_NUM) + CTREE_BIO_GEN_APPR_NUM *(1-CTREE_BIO_GEN_SPREAD_PROC);
    }


    //Static methods
    public static boolean dropSeed(SoilRectangle rectForPlant, List<Moss> mossList, List<Grass> grassList, List<ConiferousTree> ctreeList) {
        if (rectForPlant.getFertilityLevel() >= CTREE_MIN_FERT_LEVEL)
        {
            //Под другими лиственными расти не будут, но будут расти рядом
            ObservableList<SoilRectangle> listOfNearDeciduous = rectForPlant.getNearInRadius(CTREE_MIN_DIST_FROM_NEAR_DTREE);
            for (SoilRectangle soilRect : listOfNearDeciduous) {
                if (soilRect.getFill() == Color.BROWN)
                    return false;
            }

            //Под другими хвойными расти не будут, но будут расти рядом
            ObservableList<SoilRectangle> listOfNearConiferous = rectForPlant.getNearInRadius(CTREE_MIN_DIST_FROM_NEAR_CTREE);
            for (SoilRectangle soilRect : listOfNearConiferous) {
                if (soilRect.getFill() == Color.LIGHTSEAGREEN)
                    return false;
            }

            //Будет расти на месте почвы
            if (rectForPlant.getFill() == Color.LIGHTGRAY) {
                plant(rectForPlant, ctreeList);
                return true;
            }

            //Будет расти на месте мха
            else if (rectForPlant.getFill() == Color.DARKOLIVEGREEN) {
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

            //Будет расти на месте травы
            else if(rectForPlant.getFill() == Color.LIGHTGREEN) {
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

            //Не будет расти на месте кустарника
        }
        return false;
    }

    public static void plant(SoilRectangle rectForPlant, List<ConiferousTree> ctreeList){
        rectForPlant.setFill(Color.LIGHTSEAGREEN);
        ctreeList.add(new ConiferousTree(rectForPlant));
    }


    //Methods
    public void process(List<Moss> mossList, List<Grass> grassList, List<ConiferousTree> ctreeList) {
        age++;

        if (age >= CTREE_FULL_GROWN_AGE)
        {
            //Запасаем биомассу в растении
            storedBioMass += bioMassGen * CTREE_STORED_BIO_MASS_PROC;

            //Увеличиваем крону по достижении взрослого возраста
            if (age == CTREE_FULL_GROWN_AGE) {
                treeCroneCoveredSoil.clear();
                treeCroneCoveredSoil = getPlantPlaceRect().getNearInRadius(2);
            }

            //Распределяем опад по площади кроны
            for (SoilRectangle soilRect : treeCroneCoveredSoil) {
                soilRect.setPineCongestionLevel(plantPlaceRect.getPineCongestionLevel()+
                        bioMassGen*(1-CTREE_STORED_BIO_MASS_PROC)*CTREE_NEEDLES_PROP_IN_LITT/treeCroneCoveredSoil.size());
                soilRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+
                        bioMassGen*(1-CTREE_STORED_BIO_MASS_PROC)*(1-CTREE_NEEDLES_PROP_IN_LITT)/treeCroneCoveredSoil.size());
            }
        }
        else if(age >= CTREE_MIDDLE_AGE)
        {
            //Увеличиваем кол-во генерируемой биомассы
            bioMassGen = bioMassGenFullGrown*(1-(CTREE_FULL_GROWN_AGE-age)/CTREE_FULL_GROWN_AGE);

            //Запасаем биомассу в растении
            storedBioMass += bioMassGen * CTREE_STORED_BIO_MASS_PROC;

            //Увеличиваем крону, при достижении среднего возраста
            if (age == CTREE_MIDDLE_AGE)
                treeCroneCoveredSoil = getPlantPlaceRect().getNearInRadius(1);

            //Распределяем опад по площади кроны
            for (SoilRectangle soilRect : treeCroneCoveredSoil) {
                soilRect.setPineCongestionLevel(plantPlaceRect.getPineCongestionLevel()+
                        bioMassGen*(1-CTREE_STORED_BIO_MASS_PROC)*CTREE_NEEDLES_PROP_IN_LITT/treeCroneCoveredSoil.size());
                soilRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+
                        bioMassGen*(1-CTREE_STORED_BIO_MASS_PROC)*(1-CTREE_NEEDLES_PROP_IN_LITT)/treeCroneCoveredSoil.size());
            }

            //Оставляем потомство раз в 4 года
            if (age % 4 == 0) {
                ObservableList<SoilRectangle> potentialPlantPlaces = plantPlaceRect.getNearInRing(CTREE_MIN_SEEDS_DROP_RADIUS,CTREE_MAX_SEEDS_DROP_RADIUS);
                int numOfPotentialPlantPlaces = potentialPlantPlaces.size();
                boolean[] isRectInspected = new boolean[numOfPotentialPlantPlaces];

                int plantPlaceIndex;
                int numOfSeedsToDrop = Helper.randomWithRange(CTREE_MIN_NUM_OF_SEEDS_TO_DROP,CTREE_MAX_NUM_OF_SEEDS_TO_DROP);
                do{
                    plantPlaceIndex = Helper.randomWithRange(0,numOfPotentialPlantPlaces-1);
                    isRectInspected[plantPlaceIndex] = true;
                    if(ConiferousTree.dropSeed(potentialPlantPlaces.get(plantPlaceIndex), mossList, grassList, ctreeList))
                        numOfSeedsToDrop--;

                }while((numOfSeedsToDrop>0) && List.of(isRectInspected).contains(false));
            }

        }
        else
        {
            //Увеличиваем кол-во генерируемой биомассы
            bioMassGen = bioMassGenFullGrown*(1-(CTREE_FULL_GROWN_AGE-age)/(CTREE_FULL_GROWN_AGE));

            //Запасаем биомассу в растении
            storedBioMass += bioMassGen * CTREE_STORED_BIO_MASS_PROC;

            //Опад в рамках одной клетки
            plantPlaceRect.setPineCongestionLevel(plantPlaceRect.getPineCongestionLevel()+bioMassGen*(1-CTREE_STORED_BIO_MASS_PROC)*CTREE_NEEDLES_PROP_IN_LITT);
            plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen*(1-CTREE_STORED_BIO_MASS_PROC)*(1-CTREE_NEEDLES_PROP_IN_LITT));
        }
    }
}
