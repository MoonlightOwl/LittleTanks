package main.moonlightowl.java.world;

import main.moonlightowl.java.Const;
import main.moonlightowl.java.math.GMath;
import main.moonlightowl.java.math.Point3D;
import main.moonlightowl.java.world.entity.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * LittleTanks.World
 * Created by MoonlightOwl on 5/24/15.
 * ---
 * Game objects, entities and level map
 */

public class World {
    public Level level;
    public final LinkedList<Bullet> bullets, newBullet;
    public final ArrayList<Bonus> bonuses;
    public final ArrayList<Tank> enemies, newEnemies;
    public final ArrayList<Bomb> bombs;
    public final ArrayList<Point3D> spawners = new ArrayList<Point3D>();
    public final ArrayList<Item> items = new ArrayList<Item>();

    public World(){
        level = new Level();
        // no "diamonds" for 1.6 back compatibility
        enemies = new ArrayList<Tank>(); newEnemies = new ArrayList<Tank>();
        bullets = new LinkedList<Bullet>(); newBullet = new LinkedList<Bullet>();
        bonuses = new ArrayList<Bonus>();
        bombs = new ArrayList<Bomb>();
    }

    /** Loading game objects from file */
    public synchronized void loadLevel(String filename){
        // clear old data
        reset();

        // read data from file
        BufferedReader reader = null;
        try {
            // open file reader
            File file = new File(filename);
            reader = new BufferedReader(new FileReader(file));
            // get level size
            String line = reader.readLine();
            int width = Integer.parseInt(line);
            line = reader.readLine();
            int height = Integer.parseInt(line);
            // create new blank map
            level = new Level(width, height);
            // read tile data
            for(int y = 0; (y<height) && (line = reader.readLine())!=null; y++){
                for(int x = 0; (x<line.length()) && (x<width); x++){
                    char ch = line.charAt(x);

                    switch(ch){
                        // empty tile - no need to do something
                        case ' ': break;
                        // add items
                        case 'k':
                            items.add(new Item(GMath.toPixel(x) + Const.HALF_TILE,
                                               GMath.toPixel(y) + Const.HALF_TILE, Item.KEY));
                            break;
                        case 'c':
                            items.add(new Item(GMath.toPixel(x) + Const.HALF_TILE,
                                    GMath.toPixel(y) + Const.HALF_TILE, Item.CANDY));
                            break;
                        // add entities
                        case 'T':
                            // TODO: turret entity
                        default:
                            // set tile
                            level.set(x, y, new Tile(ch));

                            // set additional data
                            switch(ch){
                                // start point
                                case '@': level.setStartPoint(x, y); break;
                                // enemy spawner
                                case '&': spawners.add(new Point3D(x, y, 1)); break;
                                case '%': spawners.add(new Point3D(x, y, 2)); break;
                                case '$': spawners.add(new Point3D(x, y, 3)); break;
                            }
                    }
                }
            }
            // processing given map parameters
            while((line = reader.readLine())!=null){
                String[] data = line.split("\\s*(\\s|:)\\s*");
                // homemade switch =P
                if(data[0].equals("background")){
                    level.setBackground(new Tile(data[1].charAt(0)));

                } else if(data[0].equals("link")){
                    try {
                        int[] coord = new int[4];
                        for(int c = 0; c < 4; c++){
                            coord[c] = Integer.parseInt(data[c+1]);
                        }
                        level.addLink(coord[0], coord[1], coord[2], coord[3]);
                    }
                    catch(NumberFormatException e){
                        System.out.println("[ERROR] '"+filename+"': wrong link coords.");
                    }
                }
            }
            // arrangement of the tanks
            for(Point3D spawner: spawners){
                Tank enemy = new Tank(GMath.toPixel(spawner.x), GMath.toPixel(spawner.y), spawner.z);
                enemy.setAmmo(1000);
                enemies.add(enemy);
            }
        } catch(Exception e) {
            // print error message
            System.out.println("[ERROR] Something went wrong, when loading '" + filename + "' level map...");
            e.printStackTrace();
        } finally {
            try {
                if(reader != null) reader.close();
            } catch(Exception e) { e.printStackTrace(); }

            level.render();
        }
    }

    /** Unloading all objects, and clear lists */
    public synchronized void reset(){
        bullets.clear();
        newBullet.clear();
        bonuses.clear();
        bombs.clear();
        enemies.clear();
        newEnemies.clear();
        spawners.clear();
        items.clear();
    }
}
