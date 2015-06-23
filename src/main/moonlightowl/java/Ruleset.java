package main.moonlightowl.java;

import main.moonlightowl.java.math.GMath;

/**
 * LittleTanks.Ruleset
 * Created by MoonlightOwl on 6/17/15.
 * ---
 * Gameplay parameters
 */

public class Ruleset {
    public static final int HIT_SCORE = 20, KILL_SCORE = 100, BOMB_KILL_SCORE = 120;
    public static final int BONUS_SCORE = 50, BONUS_BOMBS = 2, BONUS_FREEZE = 800,
        BONUS_SHIELD = 6;
    public static double FREEZE_COEF = 0.8;

    public static int score(int limit){ return GMath.rand.nextInt(limit); }
}
