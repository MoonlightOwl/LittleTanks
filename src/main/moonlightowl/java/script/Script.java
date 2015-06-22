package main.moonlightowl.java.script;

import main.moonlightowl.java.Const;
import main.moonlightowl.java.Logger;
import main.moonlightowl.java.script.entity.TankSI;
import main.moonlightowl.java.world.World;
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

    public static void init(){
        globals = JsePlatform.standardGlobals();
        globals.set("Const", CoerceJavaToLua.coerce(new Object(){
            public int WIDTH = Const.WIDTH, HEIGHT = Const.HEIGHT, TILE = Const.TILE_SIZE;
        }));
    }

    public static boolean canExecute(){
        return globals != null && chunk != null;
    }

    public static void loadScript(String filename){
        if(globals == null) init();
        try {
            chunk = globals.loadfile(filename);
            chunk.call();
        } catch(LuaError e){ log(e.getMessage()); }
    }
    public static void unloadScript(){
        globals = null;
        chunk = null;
    }

    public static void runInit(World world){
        if(canExecute()){
            try {
                globals.get("init").invoke(new LuaValue[]{CoerceJavaToLua.coerce(world)});
            } catch(LuaError e){
                log(e.getMessage());
            }
        }
    }
    public static void runUpdateTank(TankSI tank){
        if(canExecute()){
            try {
                globals.get("updateTank").invoke(new LuaValue[]{CoerceJavaToLua.coerce(tank)});
            } catch(LuaError e){
                log(e.getMessage());
            }
        }
    }

    public static void log(String message){
        Logger.log("[Lua Script] " + message);
    }
}
