package main.moonlightowl.java.world;

import main.moonlightowl.java.Const;
import main.moonlightowl.java.math.GMath;
import main.moonlightowl.java.math.Point3D;
import main.moonlightowl.java.world.entity.*;

import java.awt.*;
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
    public final LinkedList<LaserBeam> beams;
    public final ArrayList<Bonus> bonuses;
    public final ArrayList<Tank> enemies, newEnemies;
    public final ArrayList<Bomb> bombs;
    public final ArrayList<Point3D> spawners;
    public final ArrayList<Item> items;
    public final ArrayList<Turret> turrets;
    public FX fx;

    public World(){
        level = new Level();
        // no "diamonds" for 1.6 back compatibility
        enemies = new ArrayList<Tank>(); newEnemies = new ArrayList<Tank>();
        bullets = new LinkedList<Bullet>(); newBullet = new LinkedList<Bullet>();
        beams = new LinkedList<LaserBeam>();
        bonuses = new ArrayList<Bonus>();
        bombs = new ArrayList<Bomb>();
        spawners = new ArrayList<Point3D>();
        items = new ArrayList<Item>();
        turrets = new ArrayList<Turret>();
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
                            turrets.add(new Turret(GMath.toPixel(x), GMath.toPixel(y)));
                        default:
                            // set tile
                            level.set(x, y, new Tile(ch));

                            // set additional data
                            switch(ch){
                                // start point
                                case '@': level.setStartPoint(x, y); break;
                                // enemy spawner
                                case '&': spawners.add(new Point3D(x, y, Tank.GUNFIGHTER)); break;
                                case '%': spawners.add(new Point3D(x, y, Tank.BIGCALIBRE)); break;
                                case '$': spawners.add(new Point3D(x, y, Tank.LAUNCHER)); break;
                                case '?': spawners.add(new Point3D(x, y, Tank.LASER)); break;
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
                } else if(data[0].equals("snowy")){
                    if(data[1].equals("true")){
                        level.setSnowy(true);
                    }
                }
            }
            // arrangement of the tanks
            for(Point3D spawner: spawners){
                spawnRandomTank(GMath.toPixel(spawner.x), GMath.toPixel(spawner.y), spawner.z);
            }
        } catch(Exception e) {
            System.out.println("[ERROR] Something went wrong, when loading '" + filename + "' level map...");
            e.printStackTrace();
        } finally {
            try {
                if(reader != null) reader.close();
            } catch(Exception e) { e.printStackTrace(); }

            level.render();

            // create fx map for level
            if(fx != null) fx.dispose();
            fx = new FX(level.getPxWidth(), level.getPxHeight());
        }
    }

    /** Random tanks for your fun! */
    public void spawnRandomTank(int x, int y, int level){
        Tank enemy = new Tank(x, y, level);
        // weapon and shields
        enemy.setAmmo(GMath.rand.nextInt(1024)+10);
        enemy.setBombs(GMath.rand.nextInt(4));
        if(GMath.rand.nextInt(10) == 1) enemy.setShield(GMath.rand.nextInt(10));
        // go!
        enemies.add(enemy);
    }

    /** Unloading all objects, and clear lists */
    public synchronized void reset(){
        bullets.clear();
        newBullet.clear();
        beams.clear();
        bonuses.clear();
        bombs.clear();
        enemies.clear();
        newEnemies.clear();
        spawners.clear();
        items.clear();
        turrets.clear();
    }

    /** Draw the world! */
    public void draw(Graphics2D g, Point camera){
        level.draw(g, camera);

        synchronized(bombs){
            for(Bomb m: bombs) {
                m.draw(g, camera);
            }
        }
        synchronized(enemies){
            for(Tank t: enemies) {
                t.draw(g, camera);
            }
        }
        synchronized(turrets){
            for(Turret t: turrets) {
                t.draw(g, camera);
            }
        }
        synchronized(bonuses){
            for(Bonus z: bonuses) {
                z.draw(g, camera);
            }
        }
        synchronized(items){
            for(Item i: items) {
                i.draw(g, camera);
            }
        }
        synchronized(bullets){
            for(Bullet b: bullets) {
                b.draw(g, camera);
            }
        }
        synchronized(beams){
            for(LaserBeam l: beams) {
                l.draw(g, camera);
            }
        }
    }

    /** Dispose all disposable */
    public void dispose(){
        fx.dispose();
    }
}
