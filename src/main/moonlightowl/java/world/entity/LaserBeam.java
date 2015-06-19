package main.moonlightowl.java.world.entity;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.math.GMath;

import java.awt.*;

/**
 * LittleTanks.LaserBeam
 * Created by MoonlightOwl on 5/31/15.
 * ---
 * You can shoot it from your eyes!
 */

public class LaserBeam {
    private Point[] ray;
    private boolean horizontally = false, fromPlayer = false;
    private int time, offx, offy;

    public LaserBeam(int x, int y, int tx, int ty){
        // allign
        if((x-tx) != 0) horizontally = true;
        // array
        int length = Math.max(Math.abs(x - tx), Math.abs(y-ty));
        if(length < 1) length = 1;
        ray = new Point[length-1];
        for(int i=0; i<length-1; i++){
            if(x < tx) x++;
            else if(x > tx) x--;
            if(y < ty) y++;
            else if(y > ty) y--;
            ray[i] = new Point(x, y);
        }
        // time of life
        time = 10;
        // offset for image
        offx = Const.HALF_TILE - Assets.ibeamv.getWidth()/2;
        offy = Const.HALF_TILE - Assets.ibeamv.getHeight()/2;
    }


    // getters
    public int getTimeRemaining(){ return time; }
    public Point getRandomPoint(){
        if(ray.length > 0) return ray[GMath.rand.nextInt(ray.length)];
        else return null;
    }
    public Point[] getRay(){ return ray; }
    public boolean isFromPlayer(){ return fromPlayer; }

    // setters
    public void setFromPlayer(boolean fromPlayer){ this.fromPlayer = fromPlayer; }


    public void update(){
        if(time > 0){
            time--;
        }
    }

    public void draw(Graphics2D g, Point camera){
        for(Point point: ray) {
            if (horizontally)
                g.drawImage(Assets.ibeamh, point.x*Const.TILE_SIZE - camera.x + offy,
                                           point.y*Const.TILE_SIZE - camera.y + offx, null);
            else
                g.drawImage(Assets.ibeamv, point.x*Const.TILE_SIZE - camera.x + offx,
                                           point.y*Const.TILE_SIZE - camera.y + offy, null);
        }
    }
}
