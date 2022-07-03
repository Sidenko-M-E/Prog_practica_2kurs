package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SoilRectangle extends Rectangle {
    private static final int SIZE = 10;
    private final int cordX;
    private final int cordY;
    private double fertilityLevel;
    private double pineCongestionLevel;


    public SoilRectangle(int x, int y) {
        super(SIZE, SIZE);
        cordX = x;
        cordY = y;
        fertilityLevel = 0;
        pineCongestionLevel = 0;
        setFill(Color.LIGHTGRAY);
    }

    public int getCordX() {
        return cordX;
    }
    public int getCordY() {
        return cordY;
    }
    public double getFertilityLevel()
    {
        return fertilityLevel;
    }
    public double getPineCongestionLevel(){
        return pineCongestionLevel;
    }

    public void setFertilityLevel(double newValue)
    {
        fertilityLevel = newValue;
    }
    public void setPineCongestionLevel(double newValue){ pineCongestionLevel = newValue; }

    public void setToUncolonized(){
        fertilityLevel = 0;
        pineCongestionLevel = 0;
        setFill(Color.LIGHTGRAY);
    }
    public ObservableList<SoilRectangle> getNearInRadius(int radius){

        int rowLimit = (int) Math.sqrt(((TilePane)getParent()).getChildren().size())-1;
        int columnLimit = rowLimit;
        int i = getCordX();
        int j = getCordY();
        ObservableList<SoilRectangle> returnListOfNear = FXCollections.observableArrayList();

        for(var x = Math.max(0, i-radius); x <= Math.min(i+radius, rowLimit); x++) {
            for(var y = Math.max(0, j-radius); y <= Math.min(j+radius, columnLimit); y++) {
                if(x != i || y != j)
                    returnListOfNear.add((SoilRectangle) (((TilePane)getParent()).getChildren()).get(x+y*(columnLimit+1)));
            }
        }

        return returnListOfNear;
    }

    public ObservableList<SoilRectangle> getNearInRing(int innerRadius, int outerRadius){

        int rowLimit = (int) Math.sqrt(((TilePane)getParent()).getChildren().size()-1);
        ObservableList<SoilRectangle> returnListOfNear;

        SoilRectangle centerRect = (SoilRectangle) ((TilePane)getParent()).getChildren().get(getCordX()+getCordY()*rowLimit);
        returnListOfNear = centerRect.getNearInRadius(outerRadius);
        returnListOfNear.removeAll(centerRect.getNearInRadius(innerRadius));

        return returnListOfNear;
    }
}
