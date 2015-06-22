package main.moonlightowl.java;

import java.io.File;
import java.io.FileFilter;

/**
 * LittleTanks.Mission
 * Created by MoonlightOwl on 5/27/15.
 * ---
 * Collection of levels
 */

public class Mission {
    public static final int MAXIMAL = 100;

    private File root, sourceroot;
    private String name;
    private int len;

    public Mission(String path){
        root = new File(path); sourceroot = new File(root, "script");
        name = ""; len = 0;
    }

    // getters
    public boolean exists(String name){
        File firstLevel = new File(root, name + "1.dat");
        return firstLevel.exists();
    }
    public int getLength(){ return len; }
    public String getName(){ return name; }
    public String getLevel(int index){
        return new File(root, name + index + ".dat").getPath();
    }
    public String getScript(int index){
        File script;
        if(index > 0)
            script = new File(sourceroot, name + index + ".lua");
        else
            script = new File(sourceroot, "default.lua");
        if(script.exists())
            return script.getPath();
        else if(index > 0)
            return getScript(0);
        else
            return null;
    }

    public String[] missionList(){
        FileFilter filter = new FileFilter(){
            public boolean accept(File file){
                return file.getName().matches(".*\\D+1\\.dat");
            }
        };
        File[] filelist = root.listFiles(filter);
        String[] names = new String[filelist.length];
        for(int i=0; i<filelist.length; i++)
            names[i] = filelist[i].getName().replaceFirst("\\d+\\.dat", "");
        return names;
    }

    public boolean load(String name){
        if(exists(name)) {
            this.name = name;
            this.len = 1;

            File levelFile;
            for (int c = 2; c < MAXIMAL; c++) {
                levelFile = new File(root, name + c + ".dat");
                if (levelFile.exists()) len++;
                else break;
            }
            return true;
        }
        else return false;
    }
}
