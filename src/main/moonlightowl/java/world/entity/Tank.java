package main.moonlightowl.java.world.entity;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.world.Item;
import main.moonlightowl.java.world.Level;

import java.awt.*;
import java.awt.geom.AffineTransform;

import java.util.LinkedList;
import java.util.Iterator;

public class Tank {
	public static final double TURN_SPEED = 10.0;
	public static final int MOVE_SPEED = 5, MAX_LEVEL = 3,
		INIT_AMMO = 20, INIT_LIFE = 5, INIT_MINES = 0;
	public static final int GUNFIGHTER = 1, BIGCALIBRE = 2, LAUNCHER = 3;
	
    private Point position, targetposition, mappos;
	private double angle, targetangle;
	private int level, ammo, life, mines, shield;
	private AffineTransform at;
	
	public LinkedList<Item> inventory = new LinkedList<Item>();

	public Tank(){
		mappos = new Point(0, 0);
		position = new Point(0, 0);
		targetposition = new Point(0, 0);
		at = new AffineTransform();
		setAngle(0.0);
		ammo = INIT_AMMO;
		life = INIT_LIFE;
		mines = INIT_MINES;
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

    // getters
    public int getX(){ return position.x; }
    public int getY(){ return position.y; }
	public int getMapX(){ return mappos.x; }
	public int getMapY(){ return mappos.y; }
	public double getAngle(){ return angle; }
	public boolean isIdle(){ return position.equals(targetposition) && angle == targetangle; }
	public int getAmmo(){ return ammo; }
	public int getLife(){ return life; }
	public int getMines(){ return mines; }
	public int getLevel(){ return level; }
	public AffineTransform getTransform(){
		AffineTransform a = AffineTransform.getTranslateInstance(position.x, position.y);
		a.rotate(Math.toRadians(angle), 30, 30);
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
		mappos.x = x / Level.TILE_SIZE;
		mappos.y = y / Level.TILE_SIZE;
	}
	public void setAngle(double angle){
		this.angle = angle;
		targetangle = angle;
	}
	public void setAmmo(int a){ ammo = a; }
	public void changeAmmo(int delta){ ammo += delta; }
	public void setLife(int l){ life = l; }
	public void changeLife(int delta){ life += delta; }
	public void setMines(int m){ mines = m; }
	public void changeMines(int delta){ mines += delta; }
	public void setLevel(int level){ this.level = level; }
	public void addShield(int shield){ this.shield += shield; }
	public void setShield(int sh){ this.shield = (sh >= 0 ? sh : 0); }
	public void minusShield(int shield){ this.shield = (shield < this.shield ? this.shield-shield : 0); }
	public boolean removeFromInventory(int type){
		Iterator<Item> it = inventory.iterator();
		while(it.hasNext()){
			Item i = it.next();
			if(i.getType() == type){ it.remove(); return true; }
		}
		return false;
	}
	
	public void move(int dx, int dy){
		targetposition.x = dx;
		targetposition.y = dy;
	}
	public void turn(double angle){
		targetangle = angle;
	}
	
    public void reset(){ 
		setPosition(0,0); 
		setAngle(0.0);
		setLife(INIT_LIFE);
		setAmmo(INIT_AMMO);
		setMines(INIT_MINES);
		level = 1;
		shield = 0;
		inventory.clear();
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
			if(delta > 180.0) delta -= 360.0;
			if(delta < -180.0) delta += 360.0;
			
			if(Math.abs(delta) < TURN_SPEED){
				angle += delta;
			}
			else {
				angle += TURN_SPEED * Math.signum(delta);
			}
			if(angle < 0.0) angle += 360.0;
			else if(angle >= 360.0) angle -= 360.0;
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
		mappos.x = position.x / Level.TILE_SIZE;
		mappos.y = position.y / Level.TILE_SIZE;
    }

    // to screen
    public void draw(Graphics2D g, Point camera){
		at.setToIdentity();
		at.translate(position.x-camera.x, position.y-camera.y);
		at.rotate(Math.toRadians(angle), 30, 30);
        g.drawImage(Assets.itank[level], at, null);

		if(shield > 0) 
			g.drawImage(Assets.ishield, position.x-camera.x-20, position.y-camera.y-20, null);
    }
}
