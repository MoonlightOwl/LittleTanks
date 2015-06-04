package main.moonlightowl.java;

/**
 * LittleTanks.Const
 * ---
 * Constants and parameters collection
 */

import java.awt.Color;

public final class Const {
    public final static int WIDTH = 800, HEIGHT = 600, HALFWIDTH = WIDTH/2, HALFHEIGHT = HEIGHT/2,
            TILE_SIZE = 60, HALF_TILE = TILE_SIZE/2,
            BONUS_SIZE = 50,
            MESSAGE_TIME = 120,
            SCOREBOARD_SIZE = 7, NICKNAME_LEN = 30,
            BONUS_DROP_CHANCE = 80;

    public final static Color TITLE_COLOR = Color.WHITE,
            MENU_COLOR = Color.BLACK, MENU_SELECTED_COLOR = Color.RED, MENU_SHADOW_COLOR = Color.WHITE,
            BACK_COLOR = new Color(30, 45, 10), OPAQUE_COLOR = new Color(0, 0, 0, 100), OPAQUE_DARK_COLOR = new Color(22, 22, 22, 184);

    public static String defaultScoreTable = "scores/scores.scr";
}