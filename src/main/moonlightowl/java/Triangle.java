package main.moonlightowl.java;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Triangle{
    protected Polygon shape;
    protected int x, y, size, angle = 0;
    protected AffineTransform at;

    public Triangle(int x, int y, int size){
        setPosition(x, y);
        setSize(size);
        updateShape();
    }
    // get
    public int getSize(){ return size; }
    public int getAngle(){ return angle; }
    public Polygon getShape(){ return shape; }
    public int getX(){ return x; }
    public int getY(){ return y; }
    // set
    public void setAngle(int angle){
        this.angle = angle;
        if(this.angle>360){ this.angle-=360; }
        else if(this.angle<0){ this.angle+=360; }
        at.rotate(Math.toRadians(angle));
    }
    public void setPosition(int x, int y){
        this.x = x; this.y = y;
        updateShape();
    }
    public void setSize(int size){
        this.size = size;
        updateShape();
    }

    // other
    protected void updateShape(){
        int[] xs = {0, -(int)(0.5*size), (int)(0.5*size)};
        int[] ys = {-(int)(0.577*size), (int)(0.288*size), (int)(0.288*size)};
        shape = new Polygon(xs, ys, xs.length);
        at = AffineTransform.getTranslateInstance(x, y);
        at.rotate(Math.toRadians(angle));
    }

    public void draw(Graphics2D g, Color color, boolean filled, Point camera){
		// calculate new transform matrix
		if(camera != null){
			at.setToIdentity();
			at.translate(x-camera.x, y-camera.y);
			at.rotate(angle);
		}
        g.setColor(color);
        if(filled) g.fill(at.createTransformedShape(shape));
        else g.draw(at.createTransformedShape(shape));
    }
}