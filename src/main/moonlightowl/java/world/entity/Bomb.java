package main.moonlightowl.java.world.entity;

import main.moonlightowl.java.Assets;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class Bomb {
	public int x, y;
	private Rectangle shape;
	
	public Bomb(int x, int y){
		this.x = x; this.y = y;
		shape = new Rectangle(x-30, x-30, 60, 60);
	}
	
	public boolean contains(int x, int y){
		return shape.contains(x,y);
	}
	public int getX(){ return x; }
	public int getY(){ return y; }
	
	public void draw(Graphics2D g, Point camera){
        if(System.currentTimeMillis()%800<400)
			g.drawImage(Assets.imine_on, x-camera.x-30, y-camera.y-30, null);
		else
			g.drawImage(Assets.imine_off, x-camera.x-30, y-camera.y-30, null);
	}
}
