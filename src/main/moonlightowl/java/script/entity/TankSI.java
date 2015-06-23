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

    public int getX(){ return tank.getMapX(); }
    public int getY(){ return tank.getMapY(); }
    public double getAngle(){ return tank.getAngle(); }
    public int getAmmo(){ return tank.getAmmo(); }
    public int getBombs(){ return tank.getBombs(); }
    public int getLifes(){ return tank.getLife(); }
    public int getShield(){ return tank.getShield(); }

    public void turn(double angle){
        tank.turn(angle);
    }
    public void move(int dx, int dy){
        gameScreen.moveTank(tank, tank.getX() + GMath.toPixel(dx),
                                  tank.getY() + GMath.toPixel(dy));
        tank.turn(Math.atan2(dy, dx));
    }
    public void moveTo(int x, int y){
        gameScreen.moveTank(tank, GMath.toPixel(x), GMath.toPixel(y));
        tank.turn(Math.atan2(tank.getY() - GMath.toPixel(y),
                             tank.getX() - GMath.toPixel(x)) + Math.PI);
    }
    public void fire(){
        gameScreen.fireTank(tank);
    }
}
