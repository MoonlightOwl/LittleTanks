package main.moonlightowl.java.gui;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.world.World;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * LittleTanks.ScoresScreen
 * Created by MoonlightOwl on 5/26/15.
 * ---
 * Highscores table
 */

public class ScoresScreen extends Screen {
    private Scores scores;

    public ScoresScreen(World world, Camera camera, Label title){
        super(world, camera, title);
        scores = new Scores(Const.defaultScoreTable, Assets.fgui, Assets.fmgui);
    }

    // getters
    public int worst(){ return scores.worst(); }

    public void load(String filename){
        save();
        scores.loadScores(filename);
    }
    public void save(){
        scores.saveScores();
    }

    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()){
            case KeyEvent.VK_ESCAPE:
                setVisible(false); break;
        }
    }
    public void mouseClicked(MouseEvent e){
        setVisible(false);
    }

    public void draw(Graphics2D g){
        super.draw(g);
        scores.draw(g);
    }
}
