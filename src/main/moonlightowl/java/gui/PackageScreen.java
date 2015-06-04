package main.moonlightowl.java.gui;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.gui.component.Label;
import main.moonlightowl.java.gui.component.Query;
import main.moonlightowl.java.gui.component.Selector;
import main.moonlightowl.java.world.World;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * LittleTanks.PackageScreen
 * Created by MoonlightOwl on 6/4/15.
 * ---
 * Select your challenge!
 */

public class PackageScreen extends TextboxScreen {
    private Selector selector;

    public PackageScreen(World world, Camera camera, Label title){
        super(world, camera, title);

        Query query = new Query("Enter package name:", Const.HALFWIDTH, Const.HEIGHT-140,
                Assets.fgui, Assets.fmgui, Color.WHITE);
        setQuery(query);

        selector = new Selector(0, 200, 3, 3, Assets.fgui, Assets.fmgui, Color.YELLOW);
        selector.setActive(false);
    }


    public void setItems(String[] items){
        selector.setItems(items);
    }


    private void swapActive(){
        if(query.isActive()) {
            selector.setActive(true); query.setActive(false);
        } else {
            selector.setActive(false); query.setActive(true);
        }
    }
    public void keyPressed(KeyEvent e){
        switch(e.getKeyCode()){
            case KeyEvent.VK_ESCAPE:
                setText(""); setVisible(false); break;
            case KeyEvent.VK_ENTER:
                if(selector.isActive()){
                    setText(selector.currentItem());
                }
                setVisible(false); break;
            case KeyEvent.VK_DOWN:
                if(selector.size() > 0)
                    if((selector.isActive() && selector.lowerBoundaryReached()) || query.isActive())
                        swapActive();
                break;
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_UP: case KeyEvent.VK_LEFT:
                if(selector.size() > 0 && query.isActive()) swapActive();
                break;
            default:
                if(selector.isActive())
                    swapActive();
        }
        if(query.isActive()) query.keyPressed(e);
        else selector.keyPressed(e);
    }

    public void mouseClicked(MouseEvent e){
        String selected = selector.isActive() ? selector.currentItem() : "";
        if(selector.mouseClicked(e) == query.isActive())
            swapActive();
        //noinspection StringEquality
        if(selector.isActive() && selected == selector.currentItem()){
            setText(selected);
            setVisible(false);
        }
    }

    // render
    public void draw(Graphics2D g){
        super.draw(g);
        selector.draw(g);
    }
}
