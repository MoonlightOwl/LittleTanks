package main.moonlightowl.java.script.entity;

import main.moonlightowl.java.gui.GameScreen;
import main.moonlightowl.java.world.World;

/**
 * LittleTanks.WorldSI
 * Created by MoonlightOwl on 6/23/15.
 * ---
 * Public script interface for game world
 */

public class WorldSI {
    private GameScreen gameScreen;
    private World world;

    public WorldSI(GameScreen gameScreen){
        this.gameScreen = gameScreen;
    }

    public World getWorld(){ return world; }
    public void setWorld(World world){ this.world = world; }

    public int getScore(){ return gameScreen.getScore(); }
}
