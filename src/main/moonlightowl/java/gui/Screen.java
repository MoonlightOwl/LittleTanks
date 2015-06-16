package main.moonlightowl.java.gui;

import main.moonlightowl.java.gui.component.Label;
import main.moonlightowl.java.world.World;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * LittleTanks.Screen
 * Created by MoonlightOwl on 5/26/15.
 * ---
 * Basic interface screen interface (:P)
 */

public abstract class Screen {
    protected Camera camera;
    protected World world;
    protected Label title;
    protected boolean visible, closed;

    public Screen(){
        visible = false;
        closed = false;
    }
    public Screen(World world, Camera camera, Label title){
        this();
        setWorld(world);
        setCamera(camera);
        setTitle(title);
    }

    // setters
    public void setCamera(Camera camera){ this.camera = camera; }
    public void setWorld(World world){ this.world = world; }
    public void setTitle(Label title){ this.title = title;}
    public void setVisible(boolean visible){ this.visible = visible; }
    public void setClosed(boolean closed){ this.closed = closed; }

    // getters
    public Camera getCamera(){ return camera; }
    public World getWorld(){ return world; }
    public boolean isVisible(){ return visible; }
    public boolean isClosed(){ return closed; }

    // event handling
    public void mouseClicked(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void keyPressed(KeyEvent e) {}

    // screen processing
    public void update() {}
    public void draw(Graphics2D g) {
        // game world
        world.draw(g, camera.getPosition());
        // special effects
        world.fx.draw(g, camera.getPosition());
        // ui
        title.draw(g);
    }
}
