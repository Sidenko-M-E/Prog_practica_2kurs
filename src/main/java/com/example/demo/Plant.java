package com.example.demo;


public abstract class Plant{

    protected SoilRectangle plantPlaceRect;
    protected double bioMassGen;

    Plant(SoilRectangle rectForPlant){
        plantPlaceRect = rectForPlant;
        bioMassGen = 0;
    }

    public SoilRectangle getPlantPlaceRect() {
        return plantPlaceRect;
    }
    public double getBioGen()
    {
        return bioMassGen;
    }

}
