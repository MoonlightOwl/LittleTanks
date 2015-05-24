package main.moonlightowl.java.world.entity;

import main.moonlightowl.java.Const;
import main.moonlightowl.java.math.Primitive;
import main.moonlightowl.java.math.Triangle;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Graphics2D;

public class Bonus extends Primitive {
	public static final int COUNT = 7;
	public static final int LIFE = 0, AMMO = 1, SCORE = 2, 
		MINE = 3, FREEZE = 4, POWER = 5, SHIELD = 6;
	
    private Triangle shape;
    protected int type;
    private Rectangle rect;
    private double targetx, targety;
	private boolean flying = false;
	private double angle = 0, angle_speed = Math.PI/100;

    public Bonus(int x, int y, int type){
        setType(type);
        changeColor();
        shape = new Triangle(x, y, Const.BONUS_SIZE);
		rect = new Rectangle(0, 0, Const.BONUS_SIZE, Const.BONUS_SIZE);
        setPosition(x, y);
    }
    // get
    public int getType(){ return type; }
    public Rectangle getRect(){ return rect; }
    public boolean contains(int px, int py){
        return rect.contains(px,py);
    }
	public int getAngle(){
		return shape.getAngle();
	}
    // set
    @Override
    public void setPosition(int x, int y){
        super.setPosition(x, y);
        shape.setPosition(x, y);
		rect.x = x-Const.BONUS_SIZE/2; rect.y = y-Const.BONUS_SIZE/2;
		targetx = x; targety = y;
    }
	public void setAngle(int angle){
        shape.setAngle(angle);
	}
    public void setType(int type){
        this.type = type;
    }
    //other
	public void trow(int x, int y){
		targetx = x; targety = y;
		flying = true;
	}
    public void changeColor(){
        switch(this.type){
            case 0: setColor(new Color(118, 223, 54, 230)); break;    // life
            case 1: setColor(new Color(255, 48, 5, 200)); break;      // ammo
            case 2: setColor(new Color(2, 67, 154, 177)); break;      // score points
            case 3: setColor(new Color(255, 224, 0, 182)); break;     // mine
            case 4: setColor(new Color(218, 239, 255, 255)); break;   // freeze
            case 5: setColor(new Color(255, 39, 160, 182)); break;    // power
			case 6: setColor(new Color(0, 213, 228, 150)); break;     // shield
        }
    }
	public void update(){
		setAngle(getAngle()+2);
		angle += angle_speed;
		if(angle >= Math.PI){ angle = 0; }
		shape.setPosition(getX(), getY()-(int)(Math.sin(angle)*(flying?50:16)));
	}

    public void draw(Graphics2D g, Point camera){
        g.setColor(Const.OPAQUE_COLOR);
        g.fillOval(rect.x-camera.x, rect.y-camera.y, Const.BONUS_SIZE, Const.BONUS_SIZE);
        shape.draw(g, getColor(), true, camera);
        shape.draw(g, Color.black, false, camera);
    }
}
