package main.moonlightowl.java.world.entity;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.math.GMath;
import main.moonlightowl.java.world.Item;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.LinkedList;

public class Tank {
    public static final int MAX_LEVEL = 4;
    public static final State DEFAULT_STATE = new State();
    public static final int GUNFIGHTER = 1, BIGCALIBRE = 2, LAUNCHER = 3, LASER = 4;

    private Point position, targetposition, mappos;
    private double angle, targetangle;
    private State state;
    private AffineTransform at;
    private int damage;

    public LinkedList<Item> inventory = new LinkedList<Item>();

    public Tank(){
        state = new State();
        mappos = new Point(0, 0);
        position = new Point(0, 0);
        targetposition = new Point(0, 0);
        at = new AffineTransform();
        setAngle(Math.PI/2);
        damage = 0;
    }
    public Tank(int x, int y){
        this();
        setPosition(x, y);
    }
    public Tank(int x, int y, int level){
        this(x, y);
        setLevel(level);
        switch(level){
            case Tank.BIGCALIBRE: changeLife(3); break;
            case Tank.LAUNCHER: changeLife(2); break;
            case Tank.LASER: changeLife(1); break;
        }
    }

    public void reset(){
        setPosition(0,0);
        setAngle(0.0);
        state.setTo(DEFAULT_STATE);
        inventory.clear();
        damage = 0;
    }

    // getters
    public int getX(){ return position.x; }
    public int getY(){ return position.y; }
    public int getMapX(){ return mappos.x; }
    public int getMapY(){ return mappos.y; }
    public double getAngle(){ return angle; }
    public boolean isIdle(){ return position.equals(targetposition) && angle == targetangle; }
    public int getAmmo(){ return state.ammo; }
    public int getLife(){ return state.life; }
    public int getBombs(){ return state.bombs; }
    public int getLevel(){ return state.level; }
    public int getShield(){ return state.shield; }
    public AffineTransform getTransform(){
        AffineTransform a = AffineTransform.getTranslateInstance(position.x, position.y);
        a.rotate(angle+Math.PI/2, Const.HALF_TILE, Const.HALF_TILE);
        return a;
    }
    public boolean inventoryContains(int type){
        for(Item i: inventory){
            if(i.getType() == type) return true;
        }
        return false;
    }
    public boolean isDamaged(){ return damage > state.life; }

    // setters
    public void setPosition(int x, int y){ 
        position.x = x; position.y = y;
        targetposition.x = x; targetposition.y = y;
        mappos.x = GMath.toMap(x+Const.HALF_TILE);
        mappos.y = GMath.toMap(y+Const.HALF_TILE);
    }
    public void setAngle(double angle){
        this.angle = angle;
        targetangle = angle;
    }
    public void setStateTo(State newState){ state.setTo(newState); }
    public void setAmmo(int a){ state.ammo = a; }
    public void changeAmmo(int delta){ state.ammo += delta; }
    public void setLife(int l){ state.life = l; }
    public void changeLife(int delta){ state.life += delta; }
    public void setBombs(int m){ state.bombs = m; }
    public void changeBombs(int delta){ state.bombs += delta; }
    public void setLevel(int level){ state.level = level; }
    public void setShield(int sh){ state.shield = (sh >= 0 ? sh : 0); }
    public void changeShield(int shield){ setShield(state.shield + shield); }
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
        else{ changeLife(-damage); this.damage += damage; return true; }
    }
    public void move(int dx, int dy){
        targetposition.x = dx;
        targetposition.y = dy;
    }
    public void turn(double angle){
        targetangle = angle;
        if(targetangle < 0.0) targetangle += GMath.PI2;
        else if(targetangle >= GMath.PI2) targetangle -= GMath.PI2;
    }
    public void freeze(double coef){
        state.moveSpeed *= coef;
        state.turnSpeed *= coef;
    }
    public void unfreeze(){
        state.moveSpeed = State.MOVE_SPEED;
        state.turnSpeed = State.TURN_SPEED;
    }
    public void fire(){
        if(state.level == GUNFIGHTER || state.level == BIGCALIBRE){
            int nx = getX() + (int)(Math.cos(getAngle() + Math.PI) * 3);
            int ny = getY() + (int)(Math.sin(getAngle() + Math.PI) * 3);
            position.x = nx; position.y = ny;
        }
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
        double normal = len(a) / state.moveSpeed;
        return new Point(a.x == 0? 0 : (int)(a.x/normal),
                         a.y == 0? 0 : (int)(a.y/normal));
    }


    // update
    public void update(){
        if(angle != targetangle){
            double delta = targetangle - angle;
            if(delta > Math.PI) delta -= GMath.PI2;
            if(delta < -Math.PI) delta += GMath.PI2;

            if(Math.abs(delta) < state.turnSpeed){
                angle += delta;
            }
            else {
                angle += state.turnSpeed * Math.signum(delta);
            }
            if(angle < 0.0) angle += GMath.PI2;
            else if(angle >= GMath.PI2) angle -= GMath.PI2;
        }

        if(!position.equals(targetposition)){
            Point delta = minus(targetposition,position);
            if(len(delta) < state.moveSpeed){
                position = add(position, delta);
            }
            else {
                position = add(position, normalize(delta));
            }
        }

        mappos.x = GMath.toMap(position.x+Const.HALF_TILE);
        mappos.y = GMath.toMap(position.y+Const.HALF_TILE);
    }

    // render
    public void draw(Graphics2D g, Point camera){
        at.setToIdentity();
        at.translate(position.x-2-camera.x, position.y+10-camera.y);
        at.rotate(angle+Math.PI/2, Const.HALF_TILE+2, Const.HALF_TILE-4);
        g.drawImage(Assets.itankshadow, at, null);
        at.setToIdentity();
        at.translate(position.x-camera.x, position.y-camera.y);
        at.rotate(angle+Math.PI/2, Const.HALF_TILE, Const.HALF_TILE);
        g.drawImage(Assets.itank[state.level], at, null);

        if(state.shield > 0)
            g.drawImage(Assets.ishield, position.x-camera.x-20, position.y-camera.y-20, null);
    }


    public static class State {
        public static final double TURN_SPEED = Math.PI/20;
        public static final int MOVE_SPEED = 5,
                INIT_AMMO = 20, INIT_LIFE = 5, INIT_BOMBS = 0, INIT_SHIELD = 0,
                SHIELD_LIMIT = 20;

        public int level, ammo, life, bombs, shield;
        public int shieldLimit, moveSpeed;
        public double turnSpeed;

        public State(){
            level = 1;
            ammo = INIT_AMMO; life = INIT_LIFE; bombs = INIT_BOMBS; shield = INIT_SHIELD;
            shieldLimit = SHIELD_LIMIT;
            moveSpeed = MOVE_SPEED;
            turnSpeed = TURN_SPEED;
        }
        public State(State source){
            setTo(source);
        }

        public void setTo(State source){
            this.level = source.level;
            this.ammo = source.ammo;
            this.life = source.life;
            this.bombs = source.bombs;
            this.shield = source.shield;
            this.shieldLimit = source.shieldLimit;
            this.moveSpeed = source.moveSpeed;
            this.turnSpeed = source.turnSpeed;
        }
    }
}
