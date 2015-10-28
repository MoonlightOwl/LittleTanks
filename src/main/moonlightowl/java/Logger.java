package main.moonlightowl.java;

/**
 * LittleTanks.Logger
 * Created by MoonlightOwl on 6/20/15.
 * ---
 * Trouble chronicles
 */

public class Logger {
    public static void log(String message){
        System.err.println(message);
    }

    public static void trace(Exception e){
        e.printStackTrace(System.err);
    }

    public static void error(String message){
        log("[ERROR] " + message);
    }
    public static void error(String message, Exception e){
        error(message);
        trace(e);
    }

    public static void warning(String message){
        log("[WARN] " + message);
    }
}
