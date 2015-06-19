package main.moonlightowl.java.world.entity;

/**
 * LittleTanks.PopupMessage
 * Created by MoonlightOwl on 6/19/15.
 * ---
 * A little text message on game field
 */

public class PopupMessage {
    private int x, y;
    private String message;

    public PopupMessage(int x, int y, String message){
        this.x = x; this.y = y;
        this.message = message;
    }

    public boolean compareXY(int x, int y){
        return x == this.x && y == this.y;
    }
    public int getX(){ return x; }
    public int getY(){ return y; }
    public String getText(){ return message; }
}
