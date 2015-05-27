package main.moonlightowl.java.gui;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * LittleTanks.AboutScreen
 * Created by MoonlightOwl on 5/26/15.
 * ---
 * Info about projects and creators
 */

public class AboutScreen extends Screen {
    private About about;

    public AboutScreen(){
        super();
        about = new About();
        about.addLine("Experimental project (Java, Swing)", Const.HALFWIDTH, 250, Assets.fsmall, Assets.fmsmall, Color.WHITE, true, true);
        about.addLine("Programmer: MoonlightOwl", Const.HALFWIDTH, 290, Assets.fgui, Assets.fmgui, Color.WHITE, true, true);
        about.addLine("BetaTester: Polina", Const.HALFWIDTH, 330, Assets.fgui, Assets.fmgui, Color.WHITE, true, true);
        about.addLine("Contact:", Const.HALFWIDTH, 400, Assets.fsmall, Assets.fmsmall, Color.WHITE, true, true);
        about.addLine("murky.owl@gmail.com", Const.HALFWIDTH, 440, Assets.fgui, Assets.fmgui, Color.CYAN, true, true);
        about.addLine("20 April 2015", Const.HALFWIDTH, 480, Assets.fgui, Assets.fmgui, Color.WHITE, true, true);
        about.addLine("Neverland", Const.HALFWIDTH, 520, Assets.fgui, Assets.fmgui, Color.WHITE, true, true);
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

    public void draw(Graphics2D g, Point camera){
        super.draw(g, camera);
        about.draw(g);
    }
}
