package main.moonlightowl.java.world;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.world.Level;

import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Item {
	public static final int KEY = 0, CANDY = 1;
	public static BufferedImage[] image = {Assets.ikey, Assets.icandy};
	
	private static double angle_speed = Math.PI/100;
	
	private int type, x, y, fy;
	private double angle;
	
	public Item(int x, int y, int type){
		this.x = x; this.y = y;
		this.type = type;
		this.angle = 0;
	}
	
	// getter
	public int getType(){ return type; }
	public boolean contains(int x, int y){
		if(x >= this.x- Level.TILE_HALF && x <= this.x+Level.TILE_HALF){
			if(y >= this.y-Level.TILE_HALF && y <= this.y+Level.TILE_HALF){
				return true;
			}
		}
		return false;
	}
	
	public void update(){
		angle += angle_speed;
		if(angle >= Math.PI){ angle = 0; }
		fy = y - (int)(Math.sin(angle)*20);
	}
	public void draw(Graphics2D g, Point camera){
		g.setColor(Const.OPAQUE_COLOR);
        g.fillOval(x-camera.x-Const.BONUS_SIZE/2, 
				   y-camera.y-Const.BONUS_SIZE/2, Const.BONUS_SIZE, Const.BONUS_SIZE);
		g.drawImage(image[type], x-camera.x-Level.TILE_HALF, 
								 fy-camera.y-Level.TILE_HALF, null);
	}
	public void drawIcon(Graphics2D g, int x, int y){
		g.drawImage(image[type], x, y, null);
	}
}