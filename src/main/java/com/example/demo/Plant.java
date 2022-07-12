package com.example.demo;


public abstract class Plant{
    //Properties
    protected SoilRectangle plantPlaceRect;
    protected double bioMassGen;


    //Constructor
    Plant(SoilRectangle rectForPlant){
        plantPlaceRect = rectForPlant;
        bioMassGen = 0;
    }


    //Methods
    public SoilRectangle getPlantPlaceRect() {
        return plantPlaceRect;
    }
    public double getBioMassGen()
    {
        return bioMassGen;
    }
    public void setPlantPlaceRect(SoilRectangle rectPlaceForPlant) {
        plantPlaceRect = rectPlaceForPlant;
    }
    public void setBioMassGen(double newValue){bioMassGen = newValue;}

}
