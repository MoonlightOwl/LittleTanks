package main.moonlightowl.java.math;

import main.moonlightowl.java.Const;

import java.util.Random;

/**
 * LittleTanks.GameMath
 * Created by MoonlightOwl on 5/24/15.
 * ---
 * Useful ingame calculations
 */

public class GMath {
    public static final Random rand = new Random(System.currentTimeMillis());

    public static int toMap(int coord){
        return coord / Const.TILE_SIZE;
    }
    public static int toPixel(int coord){
        return coord * Const.TILE_SIZE;
    }
}
