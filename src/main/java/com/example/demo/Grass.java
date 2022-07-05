package com.example.demo;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import java.util.List;

public class Grass extends Plant{
    //Constants
    private static double GRASS_BIO_GEN_APPR_NUM = 4;
    private static double GRASS_BIO_GEN_SPREAD_PROC = 0.2;


    private static double GRASS_MIN_FERT_LEVEL = 5;
    private static double GRASS_MAX_CONG_LEVEL = 8;



    //Constructor
    Grass(SoilRectangle rectForPlant){
        super(rectForPlant);
        bioMassGen = Math.random()*(2*GRASS_BIO_GEN_SPREAD_PROC*GRASS_BIO_GEN_APPR_NUM)+GRASS_BIO_GEN_APPR_NUM*(1-GRASS_BIO_GEN_SPREAD_PROC);
    }


    //Static methods
    public static boolean dropSeed(SoilRectangle rectForPlant, List<Moss> mossList, List<Grass> grassList) {
        if ((rectForPlant.getFertilityLevel() >= GRASS_MIN_FERT_LEVEL) &&
                (rectForPlant.getPineCongestionLevel() < GRASS_MAX_CONG_LEVEL))
        {
            //Будет расти на месте почвы
            if (rectForPlant.getFill() == Color.LIGHTGRAY) {
                plant(rectForPlant, grassList);
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
                plant(rectForPlant, grassList);
                return true;
            }

            //Не будет расти на месте деревьев, кустарника и другой травы, а также умирающих многолетних
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


    //Methods
    public void process(List<Moss> mossList, List<Grass> grassList) {
        //Увеличиваем плодородие почвы под растением
        plantPlaceRect.setFertilityLevel(plantPlaceRect.getFertilityLevel()+bioMassGen);

        //Проверяем на превышение уровня загрязнённости
        if (plantPlaceRect.getPineCongestionLevel() >= GRASS_MAX_CONG_LEVEL) {
            plantPlaceRect.setFill(Color.LIGHTGRAY);
            grassList.remove(this);
        }

        //Оставляем потомство
        else{
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
}
