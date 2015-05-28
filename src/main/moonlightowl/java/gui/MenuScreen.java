package main.moonlightowl.java.gui;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.gui.component.Menu;
import main.moonlightowl.java.gui.component.Label;
import main.moonlightowl.java.world.World;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * LittleTanks.MenuScreen
 * Created by MoonlightOwl on 5/26/15.
 * ---
 * Game menu
 */

public class MenuScreen extends Screen {
    public static final int NOTHING = -1, PACKAGE = 0, NEWGAME = 1, SCORES = 2, ABOUT = 3, EXIT = 4;
    private Menu menu;

    public MenuScreen(World world, Camera camera, Label title) {
        super(world, camera, title);
        menu = new Menu(Assets.fmenu, Assets.fmmenu, Const.HALFWIDTH, Const.HALFHEIGHT-60, 70);
        menu.addItem("[ level ]");
        menu.addItem("Fight!");
        menu.addItem("Scores");
        menu.addItem("About");
        menu.addItem("Quit");
    }

    // getters
    public int getSelected(){
        return menu.getSelected();
    }

    // setters
    public void setPackageName(String name, int length){
        menu.setName(0, "[ " + name + "-" + Integer.toString(length) + " ]");
    }

    public void mouseMoved(MouseEvent e) {
        menu.mouseMoved(e);
    }
    public void mouseClicked(MouseEvent e) {
        if(menu.getSelected() != Menu.NOTHING){
            setVisible(false);
        }
    }
    public void keyPressed(KeyEvent e) {
        menu.keyPressed(e);
        switch(e.getKeyCode()){
            // exit menu
            case KeyEvent.VK_ESCAPE:
                setClosed(true);
                setVisible(false); break;
            // select one of menu items
            case KeyEvent.VK_ENTER:
                if(menu.getSelected() != Menu.NOTHING) setVisible(false);
                break;
        }
    }

    public void draw(Graphics2D g){
        // world
        world.draw(g, camera.getPosition());
        world.fx.draw(g, camera.getPosition());
        // shadow
        g.setColor(Const.OPAQUE_DARK_COLOR);
        g.fillRect(0, 40, Const.WIDTH, 120);
        // interface
        title.draw(g);
        menu.draw(g);
    }
}
