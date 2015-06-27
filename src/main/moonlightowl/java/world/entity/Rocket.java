package main.moonlightowl.java.world.entity;

import main.moonlightowl.java.Assets;

import java.awt.*;

/**
 * LittleTanks.${CLASS}
 * Created by MoonlightOwl on 6/27/15.
 */
public class Rocket extends Bullet {
    public Rocket(int x, int y, float dx, float dy){
        super(x, y, dx, dy, Tank.LAUNCHER);
    }

    public void draw(Graphics2D g, Point camera){
        at.setToIdentity();
        at.translate(position.x - camera.x - 5, position.y - camera.y - 25);
        at.rotate(angle, 5, 22);
        g.drawImage(Assets.irocket, at, null);
    }
}
