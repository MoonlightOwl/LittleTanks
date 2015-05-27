package main.moonlightowl.java.gui;

import main.moonlightowl.java.Const;
import main.moonlightowl.java.math.GMath;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * LittleTanks.Camera
 * Created by MoonlightOwl on 5/27/15.
 * ---
 * Your eye in the game world
 */

public class Camera {
    private static final double TURN_SPEED = 1.0, MOVE_SPEED = 0.2;

    private int width, height;
    private Point position, target;
    private Point2D.Double precise;
    private double angle;

    public Camera(){ this(0, 0); }
    public Camera(int x, int y){
        position = new Point(x, y);
        precise = new Point2D.Double(x, y);
        target = new Point(x, y);
        angle = 0.0;
    }

    // getters
    public Point getPosition(){ return position; }

    // setters
    public void setBounds(int width, int height){
        this.width = width; this.height = height;
    }
    public void setPosition(int x, int y){
        position.x = x - Const.HALFWIDTH;
        position.y = y - Const.HALFHEIGHT;
    }

    public void newTarget(){
        precise.x = position.x + Const.HALFWIDTH;
        precise.y = position.y + Const.HALFHEIGHT;
        target.x = GMath.rand.nextInt(width-240) + 120;
        target.y = GMath.rand.nextInt(height-240) + 120;
    }
    public void moveToTarget(){
        double targetangle = Math.atan2(target.y - precise.y,
                target.x - precise.x);
        if(angle != targetangle){
            double delta = targetangle - angle;
            if(delta > Math.PI) delta -= GMath.PI2;
            if(delta < -Math.PI) delta += GMath.PI2;

            if(Math.abs(delta) < TURN_SPEED){
                angle += delta;
            }
            else {
                angle += TURN_SPEED * Math.signum(delta);
            }
            if(angle < 0.0) angle += GMath.PI2;
            else if(angle >= GMath.PI2) angle -= GMath.PI2;
        }

        double dist = GMath.distance(precise.x, precise.y, target.x, target.y);
        if(dist < MOVE_SPEED){
            newTarget();
        } else {
            precise.x += Math.cos(angle) * MOVE_SPEED;
            precise.y += Math.sin(angle) * MOVE_SPEED;
            setPosition((int)precise.x, (int)precise.y);
        }
    }
}
