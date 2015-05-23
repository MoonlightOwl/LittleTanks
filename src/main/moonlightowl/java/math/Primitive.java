package main.moonlightowl.java.math;

import java.awt.Color;

public class Primitive {
    private int x,y;
    private Color color;

    // set
    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }
    public void setColor(Color color){
        this.color = color;
    }
    // get
    public int getX(){ return x; }
    public int getY(){ return y; }
    public Color getColor(){ return color; }
}
