package main.moonlightowl.java.math;

public class Point3D{
	public int x, y, z;
	
	public Point3D(int x, int y){
		this(x, y, 0);
	}
	public Point3D(int x, int y, int z){
		this.x = x; this.y = y; this.z = z;
	}
	
	public double length(){
		return Math.sqrt(x*x + y*y + z*z);
	}
}