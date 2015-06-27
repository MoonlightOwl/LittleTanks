package main.moonlightowl.java.script.entity;

import main.moonlightowl.java.Const;
import main.moonlightowl.java.gui.GameScreen;
import main.moonlightowl.java.math.GMath;
import main.moonlightowl.java.world.Tile;
import main.moonlightowl.java.world.World;
import main.moonlightowl.java.world.entity.Bonus;

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
    public void spawn(int x, int y, int level){
        world.spawnRandomTank(GMath.toPixel(x), GMath.toPixel(y), level);
    }
    public void bonus(int x, int y){ bonus(x, y, GMath.rand.nextInt(Bonus.COUNT)); }
    public void bonus(int x, int y, int type){
        world.bonuses.add(new Bonus(GMath.toPixel(x)+ Const.HALF_TILE,
                                    GMath.toPixel(y)+ Const.HALF_TILE, type));
    }

    public int getScore(){ return gameScreen.getScore(); }
}
