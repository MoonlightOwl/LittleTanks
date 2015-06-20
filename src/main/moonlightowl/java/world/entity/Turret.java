package main.moonlightowl.java.world.entity;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.math.GMath;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * LittleTanks.Turret
 * Created by MoonlightOwl on 5/23/15.
 */

public class Turret {
    private static int FIRE_DELAY = 10;
    private static double IDLE_TURN = 0.01, ACTIVE_TURN = 0.05;
    public static int INIT_LIFE = 6, DETECT_RADIUS = Const.TILE_SIZE*4;

    private int x, y, life, mapx, mapy;
    private double angle;
    private AffineTransform at;
    private int fire;

    public Turret(int x, int y){
        this.x = x; this.y = y;
        mapx = GMath.toMap(x); mapy = GMath.toMap(y);
        life = INIT_LIFE;
        angle = 0;
        at = new AffineTransform();
        fire = 0;
    }

    // getters
    public int getX(){ return x; }
    public int getY(){ return y; }
    public int getMapX(){ return mapx; }
    public int getMapY(){ return mapy; }
    public int getLife(){ return life; }
    public double getAngle(){ return angle; }

    // setters
    public void changeLife(int amount){ life = life+amount > 0 ? life+amount : 0; }
    public void fire(boolean rifle){ fire = FIRE_DELAY * (rifle ? -1: 1); }

    public void update(boolean active){
        if(active) angle += ACTIVE_TURN;
        else angle += IDLE_TURN;
    }

    // render
    public void draw(Graphics2D g, Point camera){
        at.setToIdentity();
        at.translate(x - camera.x + 10, y - camera.y - 12);
        at.rotate(angle, 20, 40);
        g.drawImage(Assets.iturret_tower, at, null);
        if(fire != 0){ drawFire(g, camera); fire += (fire < 0 ? 1: -1); }
    }
    public void drawFire(Graphics2D g, Point camera){
        at.setToIdentity();
        if(fire < 0) {
            at.translate(x - camera.x + 12, y - camera.y - 28);
            at.rotate(angle, 18, 56);
        } else {
            at.translate(x - camera.x + 36, y - camera.y - 28);
            at.rotate(angle, -6, 56);
        }
        g.drawImage(Assets.iturret_fire, at, null);
    }
}
