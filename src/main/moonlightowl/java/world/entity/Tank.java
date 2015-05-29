package main.moonlightowl.java.world.entity;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.math.GMath;
import main.moonlightowl.java.world.Item;
import main.moonlightowl.java.world.Level;

import java.awt.*;
import java.awt.geom.AffineTransform;

import java.util.LinkedList;
import java.util.Iterator;

public class Tank {
    public static final double TURN_SPEED = Math.PI/20;
    public static final int MOVE_SPEED = 5, MAX_LEVEL = 3,
        INIT_AMMO = 20, INIT_LIFE = 5, INIT_MINES = 0;
    public static final int GUNFIGHTER = 1, BIGCALIBRE = 2, LAUNCHER = 3;

    private Point position, targetposition, mappos;
    private double angle, targetangle;
    private int level, ammo, life, bombs, shield;
    private AffineTransform at;

    public LinkedList<Item> inventory = new LinkedList<Item>();

    public Tank(){
        mappos = new Point(0, 0);
        position = new Point(0, 0);
        targetposition = new Point(0, 0);
        at = new AffineTransform();
        setAngle(Math.PI/2);
        ammo = INIT_AMMO;
        life = INIT_LIFE;
        bombs = INIT_MINES;
        level = 1;
        shield = 0;
    }
    public Tank(int x, int y){
        this();
        setPosition(x, y);
    }
    public Tank(int x, int y, int level){
        this();
        setPosition(x, y);
        setLevel(level);
        switch(level){
            case 2: setLife(INIT_LIFE + 3); break;
            case 3: setLife(INIT_LIFE - 2); break;
        }
    }

    public void reset(){
        setPosition(0,0);
        setAngle(0.0);
        setLife(INIT_LIFE);
        setAmmo(INIT_AMMO);
        setBombs(INIT_MINES);
        setShield(0);
        setLevel(1);
        inventory.clear();
    }

    // getters
    public int getX(){ return position.x; }
    public int getY(){ return position.y; }
    public int getMapX(){ return mappos.x; }
    public int getMapY(){ return mappos.y; }
    public double getAngle(){ return angle; }
    public boolean isIdle(){ return position.equals(targetposition) && angle == targetangle; }
    public int getAmmo(){ return ammo; }
    public int getLife(){ return life; }
    public int getBombs(){ return bombs; }
    public int getLevel(){ return level; }
    public AffineTransform getTransform(){
        AffineTransform a = AffineTransform.getTranslateInstance(position.x, position.y);
        at.rotate(angle+Math.PI/2, Const.HALF_TILE, Const.HALF_TILE);
        return a;
    }
    public int getShield(){ return shield; }
    public boolean inventoryContains(int type){
        for(Item i: inventory){
            if(i.getType() == type) return true;
        }
        return false;
    }

    // setters
    public void setPosition(int x, int y){ 
        position.x = x; position.y = y;
        targetposition.x = x; targetposition.y = y;
        mappos.x = GMath.toMap(x);
        mappos.y = GMath.toMap(y);
    }
    public void setAngle(double angle){
        this.angle = angle;
        targetangle = angle;
    }
    public void setAmmo(int a){ ammo = a; }
    public void changeAmmo(int delta){ ammo += delta; }
    public void setLife(int l){ life = l; }
    public void changeLife(int delta){ life += delta; }
    public void setBombs(int m){ bombs = m; }
    public void changeBombs(int delta){ bombs += delta; }
    public void setLevel(int level){ this.level = level; }
    public void setShield(int sh){ this.shield = (sh >= 0 ? sh : 0); }
    public void changeShield(int shield){ setShield(this.shield + shield); }
    public boolean removeFromInventory(int type){
        Iterator<Item> it = inventory.iterator();
        while(it.hasNext()){
            Item i = it.next();
            if(i.getType() == type){ it.remove(); return true; }
        }
        return false;
    }

    public boolean hit(int damage){
        if(getShield() > 0){ changeShield(-damage); return false; }
        else{ changeLife(-damage); return true; }
    }
    public void move(int dx, int dy){
        targetposition.x = dx;
        targetposition.y = dy;
    }
    public void turn(double angle){
        targetangle = angle;
    }

    // math
    private Point minus(Point a, Point b){
        return new Point(a.x-b.x, a.y-b.y);
    }
    private double len(Point a){
        return Math.sqrt(a.x*a.x + a.y*a.y);
    }
    private Point add(Point a, Point b){
        return new Point(a.x+b.x, a.y+b.y);
    }
    private Point normalize(Point a){
        return new Point(a.x == 0? 0 : (int)Math.signum(a.x)*MOVE_SPEED,
                         a.y == 0? 0 : (int)Math.signum(a.y)*MOVE_SPEED);
    }

    // update
    public void update(){
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
        if(!position.equals(targetposition)){
            Point delta = minus(targetposition,position);
            if(len(delta) < MOVE_SPEED){
                position = add(position, delta);
            }
            else {
                position = add(position, normalize(delta));
            }
        }
        mappos.x = GMath.toMap(position.x);
        mappos.y = GMath.toMap(position.y);
    }

    // to screen
    public void draw(Graphics2D g, Point camera){
        at.setToIdentity();
        at.translate(position.x-camera.x, position.y-camera.y);
        at.rotate(angle+Math.PI/2, Const.HALF_TILE, Const.HALF_TILE);
        g.drawImage(Assets.itank[level], at, null);

        if(shield > 0)
            g.drawImage(Assets.ishield, position.x-camera.x-20, position.y-camera.y-20, null);
    }
}
