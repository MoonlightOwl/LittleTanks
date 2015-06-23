package main.moonlightowl.java.script.entity;

import main.moonlightowl.java.gui.GameScreen;
import main.moonlightowl.java.math.GMath;
import main.moonlightowl.java.world.entity.Tank;

/**
 * LittleTanks.TankSI
 * Created by MoonlightOwl on 6/22/15.
 * ---
 * Public script interface for tanks
 */

public class TankSI {
    private Tank tank;
    private GameScreen gameScreen;

    public TankSI(GameScreen gameScreen){
        this.gameScreen = gameScreen;
    }

    public Tank getTank(){ return tank; }
    public void setTank(Tank tank){ this.tank = tank; }

    public int getAmmo(){ return tank.getAmmo(); }
    public int getBombs(){ return tank.getBombs(); }
    public int getLifes(){ return tank.getLife(); }
    public int getShield(){ return tank.getShield(); }

    public void move(int dx, int dy){
        gameScreen.moveTank(tank, tank.getX() + GMath.toPixel(dx),
                                  tank.getY() + GMath.toPixel(dy));
        if (dx < 0) tank.turn(Math.PI);
        else if (dy < 0) tank.turn(Math.PI+Math.PI/2);
        else if (dx > 0) tank.turn(0.0);
        else if (dy > 0) tank.turn(Math.PI/2);
    }
    public void fire(){
        gameScreen.fireTank(tank);
    }
}
