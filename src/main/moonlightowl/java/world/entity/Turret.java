package main.moonlightowl.java.world.entity;

import main.moonlightowl.java.Assets;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * LittleTanks.Turret
 * Created by MoonlightOwl on 5/23/15.
 */

public class Turret {
    private static double IDLE_TURN = 0.1, ACTIVE_TURN = 0.5;

    private int x, y;
    private double angle;
    private AffineTransform at;

    public Turret(int x, int y){
        this.x = x; this.y = y;
        angle = 0;
        at = new AffineTransform();
    }

    // getters
    public int getX(){ return x; }
    public int getY(){ return y; }

    public void update(){
        angle += IDLE_TURN;
    }

    // render
    public void draw(Graphics2D g, Point camera){
        at.setToIdentity();
        at.translate(x-camera.x, y-camera.y);
        at.rotate(angle, 20, 30);
        g.drawImage(Assets.iturret_tower, at, null);
    }
}
