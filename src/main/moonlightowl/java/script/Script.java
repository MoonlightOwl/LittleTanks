package main.moonlightowl.java.script;

import main.moonlightowl.java.Const;
import main.moonlightowl.java.Logger;
import main.moonlightowl.java.gui.GameScreen;
import main.moonlightowl.java.script.entity.TankSI;
import main.moonlightowl.java.script.entity.WorldSI;
import main.moonlightowl.java.world.entity.Tank;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * LittleTanks.Script
 * Created by MoonlightOwl on 6/21/15.
 * ---
 * All that was scripted going to handle here
 */

public class Script {
    private static Globals globals;
    private static LuaValue chunk;
    private static TankSI tankSI;
    private static WorldSI worldSI;
    private static GameScreen gScreen;

    public static void init(GameScreen gameScreen){
        gScreen = gameScreen;
        globals = JsePlatform.standardGlobals();
        globals.set("Const", CoerceJavaToLua.coerce(new Object(){
            public int WIDTH = Const.WIDTH, HEIGHT = Const.HEIGHT, TILE = Const.TILE_SIZE;
        }));
        tankSI = new TankSI(gScreen);
        worldSI = new WorldSI(gScreen);
        worldSI.setWorld(gameScreen.getWorld());
    }

    public static boolean canExecute(){
        return globals != null && chunk != null;
    }

    public static void loadScript(String filename){
        try {
            chunk = globals.loadfile(filename);
            chunk.call();
        } catch(LuaError e){ log(e.getMessage()); }
    }
    public static void unloadScript(){
        globals = null;
        chunk = null;
    }

    public static boolean run(String method, Object arg){
        if(canExecute()){
            try {
                globals.get(method).invoke(new LuaValue[]{CoerceJavaToLua.coerce(arg)});
                return true;
            } catch(LuaError e){
                log(e.getMessage());
            }
        }
        return false;
    }
    public static void runInit(){
        run("init", worldSI);
    }
    public static void runUpdateTank(Tank tank){
        tankSI.setTank(tank);
        run("updateTank", tankSI);
    }
    public static void runUpdateWorld(){
        run("updateWorld", worldSI);
    }

    public static void log(String message){
        Logger.log("[Lua Script] " + message);
    }
}
