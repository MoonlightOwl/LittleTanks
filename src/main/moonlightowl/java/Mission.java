package main.moonlightowl.java;

import java.io.File;

/**
 * LittleTanks.Mission
 * Created by MoonlightOwl on 5/27/15.
 * ---
 * Collection of levels
 */

public class Mission {
    public static final int MAXIMAL = 100;

    private File root;
    private String name;
    private int len;

    public Mission(String path){
        root = new File(path);
        name = ""; len = 0;
    }

    // getters
    public boolean exists(String name){
        File firstLevel = new File(root, name + "0.dat");
        return firstLevel.exists();
    }
    public int getLength(){ return len; }
    public String getName(){ return name; }
    public String getLevel(int index){
        return new File(root, name + index + ".dat").getPath();
    }

    public boolean load(String name){
        if(exists(name)) {
            this.name = name;
            this.len = 1;

            File levelFile;
            for (int c = 1; c < MAXIMAL; c++) {
                levelFile = new File(root, name + c + ".dat");
                if (levelFile.exists()) len++;
                else break;
            }
            return true;
        }
        else return false;
    }
}
