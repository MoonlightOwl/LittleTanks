package main.moonlightowl.java.script.entity;

import main.moonlightowl.java.gui.GameScreen;
import main.moonlightowl.java.world.Tile;
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

    public int width(){
        return world.level.getWidth();
    }
    public int height(){
        return world.level.getHeight();
    }
    public Tile getTile(int x, int y){
        return world.level.get(x, y);
    }
    public boolean isPassable(int x, int y){ return getTile(x, y).isPassable(); }
    public boolean isFlyable(int x, int y){ return getTile(x, y).isFlyable(); }

    public void message(String text){
        gameScreen.popupMessage(text);
    }
    public void defeat(){
        gameScreen.gameOver();
    }

    public int getScore(){ return gameScreen.getScore(); }
}
