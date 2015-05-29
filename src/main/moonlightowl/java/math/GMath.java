package main.moonlightowl.java.math;

import main.moonlightowl.java.Const;

import java.awt.geom.Point2D;
import java.util.Random;

/**
 * LittleTanks.GameMath
 * Created by MoonlightOwl on 5/24/15.
 * ---
 * Useful ingame calculations
 */

public class GMath {
    public static final Random rand = new Random(System.currentTimeMillis());
    public static final double PI2 = Math.PI*2;

    public static int toMap(int coord){
        return coord / Const.TILE_SIZE;
    }
    public static int toPixel(int coord){
        return coord * Const.TILE_SIZE;
    }

    public static double distance(float x1, float y1, float x2, float y2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }
    public static double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

    public static Point2D.Float getVector(double angle){
        Point2D.Float vector = new Point2D.Float();
        vector.x = (float)Math.cos(angle);
        vector.y = (float)Math.sin(angle);
        return vector;
    }
}
