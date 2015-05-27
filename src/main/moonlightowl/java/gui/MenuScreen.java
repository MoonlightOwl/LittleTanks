package main.moonlightowl.java.gui;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
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
    private Menu menu;

    public MenuScreen(World world, Camera camera, Label title) {
        super(world, camera, title);
        menu = new Menu(Assets.fmenu, Assets.fmmenu, Const.HALFWIDTH, Const.HALFHEIGHT-60, 70);
        menu.addItem("< level >");
        menu.addItem("Fight!");
        menu.addItem("Scores");
        menu.addItem("About");
        menu.addItem("Quit");
    }

    public void mouseMoved(MouseEvent e) {
        menu.mouseMoved(e);
    }
    public void mouseClicked(MouseEvent e) {
        if(menu.getSelected() != Menu.INACTIVE){
            setVisible(false);
        }
    }
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()){
            // exit menu
            case KeyEvent.VK_ESCAPE:
                setVisible(false); break;
            // select one of menu items
            case KeyEvent.VK_ENTER:
                if(menu.getSelected() != Menu.INACTIVE) setVisible(false);
                break;
        }
    }

    public void draw(Graphics2D g){
        super.draw(g);
        menu.draw(g);
    }
}
