package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
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

    public ObservableList<SoilRectangle> getNearInRadius(ObservableList<Node> field, int radius){

        int rowLimit = (int) Math.sqrt(field.size())-1;
        int columnLimit = rowLimit;
        int i = getCordX();
        int j = getCordY();
        ObservableList<SoilRectangle> returnListOfNear = FXCollections.observableArrayList();

        for(var x = Math.max(0, i-radius); x <= Math.min(i+radius, rowLimit); x++) {
            for(var y = Math.max(0, j-radius); y <= Math.min(j+radius, columnLimit); y++) {
                if(x != i || y != j)
                {
                    System.out.println(x + " " + y);
                    returnListOfNear.add((SoilRectangle) field.get(x+y*(columnLimit+1)));
                }
            }
        }

        return returnListOfNear;
    }
}
