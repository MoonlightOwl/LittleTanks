package main.moonlightowl.java;

// Little Tanks constants collection

import java.awt.Color;

public final class Const {
    public final static int WIDTH = 800, HEIGHT = 600,
            START_LIFES_AMOUNT = 5,
            MAX_WIDTH = WIDTH/4, MAX_HEIGHT = HEIGHT/4,
            CRASY_RUSH = 28, CRAZY_SOLID = 0, CRAZY_WIRE = 1, CRAZY_CHASER = 2, CRAZY_MAGNET1 = 3, CRAZY_MAGNET2 = 4,
            CRASY_MAGNET_RAD = 100,
            BONUS_SIZE = 50, BONUS_LIFE = 0, BONUS_SPEEDUP = 1, BONUS_SPEEDDOWN = 2, BONUS_DESTRUCTION = 3,
            BONUS_GHOST = 4, BONUS_REVERSE = 5, BONUS_RUSH = 600,
            DOWN = 0, UP = 1, LEFT = 2, RIGHT = 3,
            MESSAGE_TIME = 150,
            SCOREBOARD_SIZE = 7, NICKNAME_LEN = 30;

    public final static Color TITLE_COLOR = Color.WHITE,
            MENU_COLOR = Color.BLACK, MENU_SELECTED_COLOR = Color.RED, MENU_SHADOW_COLOR = Color.WHITE,
            DEFAULT_CRAZY_COLOR = new Color(0x41A016), CRAZY_WIRE_COLOR = Color.CYAN, CRAZY_CHASER_COLOR = new Color(0xFAFFB3),
            CRAZY_MAGNET1_COLOR = new Color(0xFF231E), CRAZY_MAGNET2_COLOR = new Color(0x5788F5),
            BACK_COLOR = new Color(30, 45, 10), OPAQUE_COLOR = new Color(0, 0, 0, 100), OPAQUE_DARK_COLOR = new Color(22, 22, 22, 184);

    public final static double DEFAULT_SPEED = 1.0, MIN_SPEED = 0.1, MAX_SPEED = 3.5;
}