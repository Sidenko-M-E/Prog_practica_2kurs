package com.example.demo;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.List;

public class Bush extends Plant{
    //Constants
    private static final double BUSH_BIO_GEN_APPR_NUM = 10;
    private static final double BUSH_BIO_GEN_SPREAD_PROC = 0.2;
    private static final double BUSH_STORED_BIO_MASS_PROC = 0.4;

    private static final double BUSH_MIN_FERT_LEVEL = 20;
    private static final double BUSH_MAX_CONG_LEVEL = 50;

    private static final int BUSH_DELETE_AGE = 23;
    private static final int BUSH_DRY_AGE = 20;
    private static final int BUSH_OLD_AGE = 15;
    private static final int BUSH_FULL_GROWN_AGE = 5;

    private static final int BUSH_MIN_NUM_OF_SEEDS_TO_DROP = 1;
    private static final int BUSH_MAX_NUM_OF_SEEDS_TO_DROP = 2;

    private static final int BUSH_MIN_SEEDS_DROP_RADIUS = 3;
    private static final int BUSH_MAX_SEEDS_DROP_RADIUS = 5;



    //Properties
    private int age;
    private double storedBioMass;
    private final double bioMassGenFullGrown;


    //Constructor
    Bush(SoilRectangle rectForPlant){
        super(rectForPlant);
        age = 0;
        storedBioMass = 0;
        bioMassGenFullGrown = Math.random()*(2*BUSH_BIO_GEN_SPREAD_PROC*BUSH_BIO_GEN_APPR_NUM)+BUSH_BIO_GEN_APPR_NUM*(1-BUSH_BIO_GEN_SPREAD_PROC);
    }


    //Static methods
    public static boolean dropSeed(SoilRectangle rectForPlant, List<Moss> mossList, List<Grass> grassList, List<Bush> bushList) {
        if ((rectForPlant.getFertilityLevel() >= BUSH_MIN_FERT_LEVEL) &&
                (rectForPlant.getPineCongestionLevel() < BUSH_MAX_CONG_LEVEL))
        {

            //Под другими кустарниками расти не будет
            ObservableList<SoilRectangle> listOfNear = rectForPlant.getNearInRadius(2);
            for (SoilRectangle soilRect : listOfNear) {
                if (soilRect.getFill() == Color.CHOCOLATE)
                    return false;
            }

            //Будет расти на месте почвы
            if (rectForPlant.getFill() == Color.LIGHTGRAY) {
                plant(rectForPlant, bushList);
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
                plant(rectForPlant, bushList);
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
                plant(rectForPlant, bushList);
                return true;
            }

            //Не будет расти на месте деревьев и кустарника, и умирающих многолетних
            return false;
        }
        return false;
    }

    public static void plant(SoilRectangle rectForPlant, List<Bush> bushList){
        rectForPlant.setFill(Color.CHOCOLATE);
        bushList.add(new Bush(rectForPlant));
    }


    //Methods
    public void process(List<Moss> mossList, List<Grass> grassList, List<Bush> bushList) {
        age++;

        //Проверяем на превышение уровня загрязнённости
        if (plantPlaceRect.getPineCongestionLevel() >= BUSH_MAX_CONG_LEVEL) {
            while(age<BUSH_DRY_AGE)
                age++;
        }

        if (age == BUSH_DELETE_AGE)
        {
            getPlantPlaceRect().setFill(Color.LIGHTGRAY);
            bushList.remove(this);
        }
        else if(age >= BUSH_DRY_AGE)
        {
            //Цвет - сухостоя
            plantPlaceRect.setFill(Color.DARKKHAKI);

            //Разложение древенистой части кустарника
            plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+storedBioMass/((double)(BUSH_DELETE_AGE-BUSH_DRY_AGE)));
        }
        else if (age >= BUSH_OLD_AGE)
        {
            //Уменьшаем кол-во генерируемой биомассы
            bioMassGen = bioMassGenFullGrown*(BUSH_DRY_AGE-age)/(BUSH_DRY_AGE-BUSH_OLD_AGE);

            //Запасаем биомассу в растении
            storedBioMass += bioMassGen * BUSH_STORED_BIO_MASS_PROC;

            //Увеличиваем плодородие почвы под растением
            plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen*(1-BUSH_STORED_BIO_MASS_PROC));
        }
        else if (age >= BUSH_FULL_GROWN_AGE)
        {
            //Запасаем биомассу в растении
            storedBioMass += bioMassGen * BUSH_STORED_BIO_MASS_PROC;

            //Увеличиваем плодородие почвы под растением
            plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen*(1-BUSH_STORED_BIO_MASS_PROC));

            //Оставляем потомство
            {
                ObservableList<SoilRectangle> potentialPlantPlaces = plantPlaceRect.getNearInRing(BUSH_MIN_SEEDS_DROP_RADIUS,BUSH_MAX_SEEDS_DROP_RADIUS);
                int numOfPotentialPlantPlaces = potentialPlantPlaces.size();
                boolean[] isRectInspected = new boolean[numOfPotentialPlantPlaces];

                int plantPlaceIndex;
                int numOfSeedsToDrop = Helper.randomWithRange(BUSH_MIN_NUM_OF_SEEDS_TO_DROP, BUSH_MAX_NUM_OF_SEEDS_TO_DROP);
                do{
                    plantPlaceIndex = Helper.randomWithRange(0,numOfPotentialPlantPlaces-1);
                    isRectInspected[plantPlaceIndex] = true;
                    if(Bush.dropSeed(potentialPlantPlaces.get(plantPlaceIndex), mossList, grassList, bushList))
                        numOfSeedsToDrop--;

                }while((numOfSeedsToDrop>0) && List.of(isRectInspected).contains(false));
            }
        }
        else
        {
            //Увеличиваем кол-во генерируемой биомассы
            bioMassGen = bioMassGenFullGrown*(1-(BUSH_FULL_GROWN_AGE-age)/BUSH_FULL_GROWN_AGE);

            //Запасаем биомассу в растении
            storedBioMass += bioMassGen * BUSH_STORED_BIO_MASS_PROC;

            //Увеличиваем плодородие почвы под растением
            plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen*(1-BUSH_STORED_BIO_MASS_PROC));
        }
    }
}
