package main.moonlightowl.java.gui;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.gui.component.Label;
import main.moonlightowl.java.world.Item;
import main.moonlightowl.java.world.entity.Tank;

import java.awt.*;
import java.util.Iterator;

/**
 * LittleTanks.HUD
 * Created by MoonlightOwl on 6/2/15.
 * ---
 * On-screen game interface (indicators, score counter e.t.c.)
 */

public class HUD {
    private Tank player;

    private Label llifes, lshield, lscore, lammo,
            lbombs, lminus, lmessage, lfreeze, lpause;
    private int minus_timer = 0, message_timer = 0;

    public HUD(Tank player){
        this.player = player;

        llifes = new Label("@@@@@", 20, Const.HEIGHT-40, Assets.fgui, Assets.fmgui, Color.WHITE); llifes.setShadow(true);
        lshield = new Label("", 20, Const.HEIGHT-80, Assets.fgui, Assets.fmgui, Color.WHITE); lshield.setShadow(true);
        lscore = new Label("0", 20, 50, Assets.fgui, Assets.fmgui, Color.WHITE); lscore.setShadow(true);
        lammo = new Label(">> 10", Const.WIDTH-140, Const.HEIGHT-40, Assets.fgui, Assets.fmgui, Color.WHITE); lammo.setShadow(true);
        lbombs = new Label("== 0", Const.WIDTH-140, Const.HEIGHT-80, Assets.fgui, Assets.fmgui, Color.WHITE); lbombs.setShadow(true);
        lfreeze = new Label("", Const.HALFWIDTH, 50, Assets.fsmall, Assets.fmsmall, Color.WHITE, true, Color.BLUE); lfreeze.setShadow(true);
        lpause = new Label("Pause...", Const.HALFWIDTH, Const.HALFHEIGHT-20, Assets.ftitle, Assets.fmtitle, Color.YELLOW, true, Color.BLACK); lpause.setShadow(true);
        lminus = new Label(":(", 40, Const.HEIGHT-80, Assets.fsmall, Assets.fmsmall, Color.RED, true); lminus.setShadow(true);
        lmessage = new Label("", Const.HALFWIDTH, Const.HEIGHT-80, Assets.fsmall, Assets.fmsmall, Color.BLACK, true, Color.RED); lmessage.setShadow(true);
    }

    public void reset(){
        minus_timer = 0;
        message_timer = 0;
    }


    public void addMessage(String text){
        addMessage(text, Const.MESSAGE_TIME);
    }
    public void addMessage(String text, int time){
        lmessage.changeText(text);
        message_timer = time;
    }
    public void minusLife(){
        minus_timer = Const.MESSAGE_TIME;
    }
    public void setFreezeCounter(int freeze){
        if(freeze > 0)
            lfreeze.changeText("< " + Integer.toString(freeze) + " >");
        else
            lfreeze.changeText("");
        lfreeze.setX(Const.HALFWIDTH);
    }
    public void setAmmoCounter(int ammo){
        lammo.changeText(">> "+Integer.toString(ammo));
    }
    public void setBombsCounter(int bombs){
        lbombs.changeText("== " + Integer.toString(bombs));
    }
    public void setScoreCounter(int score){
        lscore.changeText(Integer.toString(score));
    }
    public void setLifeCounter(int count){
        String text = "";
        for(int i = 0; i<count; i++){
            text+="@";
        }
        llifes.changeText(text);
    }
    public void setShieldCounter(int count){
        String text = "";
        for(int i = 0; i<Math.ceil(count/2); i++){
            text+="*";
        }
        lshield.changeText(text);
    }


    public void update(){
        if(message_timer > 0)
            message_timer--;
        if(minus_timer > 0)
            minus_timer --;
    }


    public void draw(Graphics2D g){
        // shadowed background
        g.drawImage(Assets.ishadowLS, 0, 0, null);
        g.drawImage(Assets.ishadowLB, 0, Const.HEIGHT-142, null);
        g.drawImage(Assets.ishadowRS, Const.WIDTH-224, 0, null);
        g.drawImage(Assets.ishadowRB, Const.WIDTH-224, Const.HEIGHT-142, null);

        // indicators
        llifes.draw(g);
        lshield.draw(g);
        lscore.draw(g);
        lammo.draw(g);
        lbombs.draw(g);
        lfreeze.draw(g);

        // messages
        if(message_timer > 0)
            lmessage.draw(g);
        if(minus_timer > 0 && System.currentTimeMillis()%400 < 200)
            lminus.draw(g);

        // inventory
        Iterator<Item> ititems = player.inventory.iterator();
        int x = Const.WIDTH - 80;
        while(ititems.hasNext()){
            Item i = ititems.next();
            i.drawIcon(g, x, 10);
            x -= 32;
        }
    }
    public void drawPaused(Graphics2D g){
        g.setColor(Const.OPAQUE_DARK_COLOR);
        g.fillRect(0, Const.HALFHEIGHT-120, Const.WIDTH, 120);
        lpause.draw(g);
    }
}
