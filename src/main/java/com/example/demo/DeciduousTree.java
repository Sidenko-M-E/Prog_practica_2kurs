package com.example.demo;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.List;

public class DeciduousTree extends Plant{
    //Constants
    private static final double DTREE_BIO_GEN_APPR_NUM = 100;
    private static final double DTREE_BIO_GEN_SPREAD_PROC = 0.05;
    private static final double DTREE_STORED_BIO_MASS_PROC = 0.65;

    private static final double DTREE_MIN_FERT_LEVEL = 30;
    private static final double DTREE_MAX_CONG_LEVEL = 10;

    private static final int DTREE_DELETE_AGE = 105;
    private static final int DTREE_DRY_AGE = 100;
    private static final int DTREE_OLD_AGE = 80;
    private static final int DTREE_FULL_GROWN_AGE = 60;
    private static final int DTREE_MIDDLE_AGE = 10;

    private static final int DTREE_MIN_NUM_OF_SEEDS_TO_DROP = 1;
    private static final int DTREE_MAX_NUM_OF_SEEDS_TO_DROP = 5;

    private static final int DTREE_MIN_SEEDS_DROP_RADIUS = 5;
    private static final int DTREE_MAX_SEEDS_DROP_RADIUS = 6;

    private static final int DTREE_MIN_DIST_FROM_NEAR_DTREE = 4;
    private static final int DTREE_MIN_DIST_FROM_NEAR_CTREE = 5;



    //Properties
    private int age;
    private double storedBioMass;
    private final double bioMassGenFullGrown;
    private ObservableList<SoilRectangle> treeCroneCoveredSoil;


    //Constructor
    DeciduousTree(SoilRectangle rectForPlant){
        super(rectForPlant);
        age = 0;
        storedBioMass = 0;
        bioMassGenFullGrown = Math.random()*(2*DTREE_BIO_GEN_SPREAD_PROC*DTREE_BIO_GEN_APPR_NUM)+DTREE_BIO_GEN_APPR_NUM*(1-DTREE_BIO_GEN_SPREAD_PROC);
        treeCroneCoveredSoil = rectForPlant.getNearInRadius(1);
    }


    //Static methods
    public static boolean dropSeed(SoilRectangle rectForPlant, List<Moss> mossList, List<Grass> grassList, List<DeciduousTree> dtreeList) {
        if ((rectForPlant.getFertilityLevel() >= DTREE_MIN_FERT_LEVEL) && (rectForPlant.getPineCongestionLevel() < DTREE_MAX_CONG_LEVEL))
        {
            //Под другими лситвенными расти не будут, но будут расти рядом
            ObservableList<SoilRectangle> listOfNearDeciduous = rectForPlant.getNearInRadius(DTREE_MIN_DIST_FROM_NEAR_DTREE);
            for (SoilRectangle soilRect : listOfNearDeciduous) {
                if (soilRect.getFill() == Color.BROWN)
                    return false;
            }

            //Под другими хвойными расти не будут, но будут расти рядом
            ObservableList<SoilRectangle> listOfNearConiferous = rectForPlant.getNearInRadius(DTREE_MIN_DIST_FROM_NEAR_CTREE);
            for (SoilRectangle soilRect : listOfNearConiferous) {
                if (soilRect.getFill() == Color.LIGHTSEAGREEN)
                    return false;
            }

            //Будет расти на месте почвы
            if (rectForPlant.getFill() == Color.LIGHTGRAY) {
                plant(rectForPlant, dtreeList);
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
                plant(rectForPlant, dtreeList);
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
                plant(rectForPlant, dtreeList);
                return true;
            }

            //Не будет расти на месте кустарника и умирающих многолетних
        }
        return false;
    }

    public static void plant(SoilRectangle rectForPlant, List<DeciduousTree> dtreeList){
        rectForPlant.setFill(Color.BROWN);
        dtreeList.add(new DeciduousTree(rectForPlant));
    }


    //Methods
    public void process(List<Moss> mossList, List<Grass> grassList, List<DeciduousTree> dtreeList) {
        age++;

        //Проверяем на превышение уровня загрязнённости
        if (plantPlaceRect.getPineCongestionLevel() >= DTREE_MAX_CONG_LEVEL) {
            while(age < DTREE_DRY_AGE)
                age++;
        }

        if (age == DTREE_DELETE_AGE)
        {
            getPlantPlaceRect().setFill(Color.LIGHTGRAY);
            dtreeList.remove(this);
        }
        if (age >= DTREE_DRY_AGE)
        {
            //Убираем крону
            treeCroneCoveredSoil.clear();

            //Цвет - разгающийся
            plantPlaceRect.setFill(Color.DARKKHAKI);

            //Разложение ствола дерева
            plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+storedBioMass/(DTREE_DELETE_AGE-DTREE_DRY_AGE));
        }
        else if (age >= DTREE_OLD_AGE)
        {
            //Уменьшаем кол-во генерируемой биомассы
            bioMassGen = bioMassGenFullGrown*(DTREE_DRY_AGE -age)/(DTREE_DRY_AGE - DTREE_OLD_AGE);

            //Запасаем биомассу в растении
            storedBioMass += bioMassGen * DTREE_STORED_BIO_MASS_PROC;

            //Уменьшаем крону, при достижении возраста половины от старости к гибели
            if (age == DTREE_OLD_AGE+(DTREE_DRY_AGE -DTREE_OLD_AGE)/2) {
                treeCroneCoveredSoil.clear();
                treeCroneCoveredSoil = getPlantPlaceRect().getNearInRadius(1);
            }

            //Распределяем опад по площади кроны
            for (SoilRectangle soilRect : treeCroneCoveredSoil) {
                soilRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+
                        bioMassGen*(1-DTREE_STORED_BIO_MASS_PROC)/treeCroneCoveredSoil.size());
            }
        }
        else if (age >= DTREE_FULL_GROWN_AGE)
        {
            //Запасаем биомассу в растении
            storedBioMass += bioMassGen * DTREE_STORED_BIO_MASS_PROC;

            //Увеличиваем крону по достижении взрослого возраста
            if (age == DTREE_FULL_GROWN_AGE) {
                treeCroneCoveredSoil.clear();
                treeCroneCoveredSoil = getPlantPlaceRect().getNearInRadius(2);
            }

            //Распределяем опад по площади кроны
            for (SoilRectangle soilRect : treeCroneCoveredSoil) {
                soilRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+
                        bioMassGen*(1-DTREE_STORED_BIO_MASS_PROC)/treeCroneCoveredSoil.size());
            }

            //Оставляем потомство
            {
                ObservableList<SoilRectangle> potentialPlantPlaces = plantPlaceRect.getNearInRing(DTREE_MIN_SEEDS_DROP_RADIUS,DTREE_MAX_SEEDS_DROP_RADIUS);
                int numOfPotentialPlantPlaces = potentialPlantPlaces.size();
                boolean[] isRectInspected = new boolean[numOfPotentialPlantPlaces];

                int plantPlaceIndex;
                int numOfSeedsToDrop = Helper.randomWithRange(DTREE_MIN_NUM_OF_SEEDS_TO_DROP,DTREE_MAX_NUM_OF_SEEDS_TO_DROP);
                do{
                    plantPlaceIndex = Helper.randomWithRange(0,numOfPotentialPlantPlaces-1);
                    isRectInspected[plantPlaceIndex] = true;
                    if(DeciduousTree.dropSeed(potentialPlantPlaces.get(plantPlaceIndex), mossList, grassList, dtreeList))
                        numOfSeedsToDrop--;

                }while((numOfSeedsToDrop>0) && List.of(isRectInspected).contains(false));
            }
        }
        else if(age >= DTREE_MIDDLE_AGE)
        {
            //Увеличиваем кол-во генерируемой биомассы
            bioMassGen = bioMassGenFullGrown*(1-(DTREE_FULL_GROWN_AGE-age)/DTREE_FULL_GROWN_AGE);

            //Запасаем биомассу в растении
            storedBioMass += bioMassGen * DTREE_STORED_BIO_MASS_PROC;

            //Увеличиваем крону, при достижении среднего возраста
            if (age == DTREE_MIDDLE_AGE)
                treeCroneCoveredSoil = getPlantPlaceRect().getNearInRadius(1);

            //Распределяем опад по площади кроны
            for (SoilRectangle soilRect : treeCroneCoveredSoil) {
                soilRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+
                        bioMassGen*(1-DTREE_STORED_BIO_MASS_PROC)/treeCroneCoveredSoil.size());
            }

            //Оставляем потомство
            {
                ObservableList<SoilRectangle> potentialPlantPlaces = plantPlaceRect.getNearInRing(DTREE_MIN_SEEDS_DROP_RADIUS,DTREE_MAX_SEEDS_DROP_RADIUS);
                int numOfPotentialPlantPlaces = potentialPlantPlaces.size();
                boolean[] isRectInspected = new boolean[numOfPotentialPlantPlaces];

                int plantPlaceIndex;
                int numOfSeedsToDrop = Helper.randomWithRange(DTREE_MIN_NUM_OF_SEEDS_TO_DROP,DTREE_MAX_NUM_OF_SEEDS_TO_DROP);
                do{
                    plantPlaceIndex = Helper.randomWithRange(0,numOfPotentialPlantPlaces-1);
                    isRectInspected[plantPlaceIndex] = true;
                    if(DeciduousTree.dropSeed(potentialPlantPlaces.get(plantPlaceIndex), mossList, grassList, dtreeList))
                        numOfSeedsToDrop--;
                }while((numOfSeedsToDrop>0) && List.of(isRectInspected).contains(false));
            }

        }
        else
        {
            //Увеличиваем кол-во генерируемой биомассы
            bioMassGen = bioMassGenFullGrown*(1-(DTREE_FULL_GROWN_AGE-age)/DTREE_FULL_GROWN_AGE);

            //Запасаем биомассу в растении
            storedBioMass += bioMassGen * DTREE_STORED_BIO_MASS_PROC;

            //Опад в рамках одной клетки
            plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen*(1-DTREE_STORED_BIO_MASS_PROC));
        }
    }
}
