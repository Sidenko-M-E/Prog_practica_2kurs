package com.example.demo;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.List;

public class DeciduousTree extends Plant{
    //Constants
    private static double DTREE_BIO_GEN_APPR_NUM = 100;
    private static double DTREE_BIO_GEN_SPREAD_PROC = 0.05;
    private static double DTREE_STORED_BIO_MASS_PROC = 0.65;

    private static double DTREE_MIN_FERT_LEVEL = 30;
    private static double DTREE_MAX_CONG_LEVEL = 10;

    private static int DTREE_DELETE_AGE = 105;
    private static int DTREE_DRY_AGE = 100;
    private static int DTREE_OLD_AGE = 80;
    private static int DTREE_FULL_GROWN_AGE = 60;
    private static int DTREE_MIDDLE_AGE = 10;


    //Properties
    private int age;
    private double storedBioMass;
    private double bioMassGenFullGrown;
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
    public static boolean dropSeed(SoilRectangle rectForPlant, List<Moss> mossList, List<Grass> grassList, List<Bush> bushList, List<DeciduousTree> dtreeList) {
        if ((rectForPlant.getFertilityLevel() >= DTREE_MIN_FERT_LEVEL) && (rectForPlant.getPineCongestionLevel() < DTREE_MAX_CONG_LEVEL))
        {
            //Под другими лситвенными расти не будут, но будут расти рядом
            ObservableList<SoilRectangle> listOfNearDeciduous = rectForPlant.getNearInRadius(4);
            for (SoilRectangle soilRect : listOfNearDeciduous) {
                if (soilRect.getFill() == Color.BROWN)
                    return false;
            }

            //Под другими хвойными расти не будут, но будут расти рядом
            ObservableList<SoilRectangle> listOfNearConiferous = rectForPlant.getNearInRadius(5);
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
            return false;
        }
        else
            return false;
    }

    public static void plant(SoilRectangle rectForPlant, List<DeciduousTree> dtreeList){
        rectForPlant.setFill(Color.BROWN);
        dtreeList.add(new DeciduousTree(rectForPlant));
    }


    //Methods
    public void process(List<Moss> mossList, List<Grass> grassList, List<Bush> bushList, List<DeciduousTree> dtreeList) {
        age++;

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
                ObservableList<SoilRectangle> potentialPlantPlaces = plantPlaceRect.getNearInRing(5,6);
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
        }
        else if(age >= DTREE_MIDDLE_AGE)
        {
            //Увеличиваем кол-во генерируемой биомассы
            bioMassGen = bioMassGenFullGrown*(1-(double)(DTREE_FULL_GROWN_AGE-age)/(double)DTREE_FULL_GROWN_AGE);

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
                ObservableList<SoilRectangle> potentialPlantPlaces = plantPlaceRect.getNearInRing(5,6);//
                int numOfPotentialPlantPlaces = potentialPlantPlaces.size();
                boolean[] isRectInspected = new boolean[numOfPotentialPlantPlaces];

                int plantPlaceIndex;
                int numOfSeedsToDrop = Helper.randomWithRange(1,5);//
                do{
                    plantPlaceIndex = Helper.randomWithRange(0,numOfPotentialPlantPlaces-1);
                    isRectInspected[plantPlaceIndex] = true;
                    if(DeciduousTree.dropSeed(potentialPlantPlaces.get(plantPlaceIndex), mossList, grassList, bushList, dtreeList))
                        numOfSeedsToDrop--;
                }while((numOfSeedsToDrop>0) && List.of(isRectInspected).contains(false));
            }

        }
        else
        {
            //Увеличиваем кол-во генерируемой биомассы
            bioMassGen = bioMassGenFullGrown*(1-(double)(DTREE_FULL_GROWN_AGE-age)/(double)DTREE_FULL_GROWN_AGE);

            //Запасаем биомассу в растении
            storedBioMass += bioMassGen * DTREE_STORED_BIO_MASS_PROC;

            //Опад в рамках одной клетки
            plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen*(1-DTREE_STORED_BIO_MASS_PROC));
        }
    }
}
