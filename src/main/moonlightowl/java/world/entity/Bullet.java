package main.moonlightowl.java.world.entity;

import main.moonlightowl.java.Assets;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Bullet {
    protected int damage = 1, type = 1;
    protected double angle;
    protected AffineTransform at;
    protected Point2D.Float position, direction;
    protected float speed = 6.0f;
    protected boolean fromPlayer = false;

    public Bullet(int x, int y, float dx, float dy){
        this(x, y, dx, dy, 1);
    }
    public Bullet(int x, int y, float dx, float dy, int type){
        position = new Point2D.Float(x, y);
        direction = new Point2D.Float(dx, dy);
        setType(type); setDamage(type);
        angle = Math.atan2(dy, dx) + Math.PI/2;
        if(damage == 3){
            at = AffineTransform.getTranslateInstance(x, y);
            at.rotate(angle);
            speed = 8.0f;
        } else speed = 6.0f;
    }

    // getters
    public float getX(){ return position.x; }
    public float getY(){ return position.y; }
    public int getDamage(){ return damage; }
    public int getType(){ return type; }
    public float getDx(){ return direction.x; }
    public float getDy(){ return direction.y; }
    public double getAngle(){ return angle; }
    public boolean isFromPlayer(){ return fromPlayer; }

    // setters
    public void setPosition(int x, int y){ position.x = x; position.y = y; }
    public void setDamage(int damage){ this.damage = damage; }
    public void setType(int type){ this.type = type; }
    public void setFromPlayer(boolean fromPlayer){ this.fromPlayer = fromPlayer; }

    // processing
    public void update(){
        position.x += direction.x * speed;
        position.y += direction.y * speed;
    }
    public void draw(Graphics2D g, Point camera){
        g.drawImage(Assets.ibullet[damage],
            (int)(position.x-camera.x)-30, (int)(position.y-camera.y)-30, null);
    }
}