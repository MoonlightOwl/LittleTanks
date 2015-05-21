package main.moonlightowl.java;

import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Bullet {
	private int level = 1;
	private double angle;
	private AffineTransform at;
	private Point2D.Float position, direction;
	private float speed = 6.0f;
	
	public Bullet(int x, int y, float dx, float dy){
		this(x, y, dx, dy, 1);
	}
	public Bullet(int x, int y, float dx, float dy, int level){
		position = new Point2D.Float(x, y);
		direction = new Point2D.Float(dx, dy);
		setLevel(level);
		angle = Math.atan2(dy, dx) + Math.PI/2;
		if(level == 3){
			at = AffineTransform.getTranslateInstance(x, y);
			at.rotate(angle);
			speed = 8.0f;
		} else speed = 6.0f;
	}
	
	// getters
	public float getX(){ return position.x; }
	public float getY(){ return position.y; }
	public int getLevel(){ return level; }
	public float getDx(){ return direction.x; }
	public float getDy(){ return direction.y; }
	public double getAngle(){ return angle; }
	
	// setters
	public void setPosition(int x, int y){ position.x = x; position.y = y; }
	public void setLevel(int level){ this.level = level; }
	
	// processing
	public void update(){
		position.x += direction.x * speed;
		position.y += direction.y * speed;
	}
	public void draw(Graphics2D g, Point camera){
		// bullets
		if(level < 3) g.drawImage(Assets.ibullet[level], 
			(int)(position.x-camera.x)-30, (int)(position.y-camera.y)-30, null);
		// rockets
		else if(level == 3){
			at.setToIdentity();
			at.translate(position.x - camera.x - 5, position.y - camera.y - 25);
			at.rotate(angle, 5, 22);
			g.drawImage(Assets.irocket, at, null);
		}
	}
}