package main.moonlightowl.java.gui;

import main.moonlightowl.java.world.World;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * LittleTanks.TextboxScreen
 * Created by MoonlightOwl on 5/27/15.
 * ---
 * Standart enter-something screen.
 */

public class TextboxScreen extends Screen {
    protected World world;
    protected Query query;
    protected Label title;

    public TextboxScreen(Query query){
        super();
        this.query = query;
    }

    // getters
    public String getText(){ return query.getText(); }

    // setters
    public void setText(String text){ query.setText(text); }

    // event handling
    public void keyPressed(KeyEvent e){
        query.keyPressed(e);

        switch (e.getKeyCode()){
            case KeyEvent.VK_ESCAPE:
                setText("");
            case KeyEvent.VK_ENTER:
                setVisible(false); break;
        }
    }

    // screen processing
    public void draw(Graphics2D g, Point camera){
        super.draw(g, camera);
        query.draw(g);
    }
}
