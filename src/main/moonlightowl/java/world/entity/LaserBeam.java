package main.moonlightowl.java.world.entity;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.math.GMath;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * LittleTanks.LaserBeam
 * Created by MoonlightOwl on 5/31/15.
 * ---
 * You can shoot it from your eyes!
 */

public class LaserBeam {
    private Point[] ray;
    private boolean fromPlayer;
    private int time, x, y;
    private double angle;
    private AffineTransform at;

    public LaserBeam(int x, int y, int length, double angle){
        // init
        this.angle = angle; fromPlayer = false; time = 10;
        this.x = x; this.y = y;

        // generate "ray"
        at = AffineTransform.getTranslateInstance(GMath.toMap(x), GMath.toMap(y));
        at.rotate(angle);
        if(length < 0) length = 0;
        ray = new Point[length];
        for(int c=0; c<length; c++){
            at.translate(0, 1);
            ray[c] = new Point((int)at.getTranslateX(), (int)at.getTranslateY());
        }
    }


    // getters
    public int getTimeRemaining(){ return time; }
    public Point getRandomPoint(){
        if(ray.length > 0) return ray[GMath.rand.nextInt(ray.length)];
        else return null;
    }
    public Point[] getRay(){ return ray; }
    public boolean isFromPlayer(){ return fromPlayer; }

    // setters
    public void setFromPlayer(boolean fromPlayer){ this.fromPlayer = fromPlayer; }


    public void update(){
        if(time > 0){
            time--;
        }
    }

    public void draw(Graphics2D g, Point camera){
        at.setToIdentity();
        at.translate(x - camera.x - 15, y - camera.y - 42);
        at.rotate(angle, 15, 42);

        for (Point point : ray) {
            at.translate(0, Const.TILE_SIZE);
            g.drawImage(Assets.ibeamv, at, null);
        }
    }
}
