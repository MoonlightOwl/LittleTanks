package main.moonlightowl.java.gui;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.world.World;

import java.awt.*;

/**
 * LittleTanks.GameScreen
 * Created by MoonlightOwl on 5/26/15.
 * ---
 * Shows game process, main game screen
 */

public class GameScreen extends Screen {
    public World world;
    private Label title, llifes, lshield, lscore, lammo, lvictory,
            lmines, lcrash, lminus, lmessage, lfreeze, lpause;
    private int minus_timer = 0, message_timer = 0;
    private boolean pause = false;

    public GameScreen(){
        llifes = new Label("@@@@@", 20, Const.HEIGHT-40, Assets.fgui, Assets.fmgui, Color.WHITE); llifes.setShadow(true);
        lshield = new Label("", 20, Const.HEIGHT-80, Assets.fgui, Assets.fmgui, Color.WHITE); lshield.setShadow(true);
        lscore = new Label("0", 20, 50, Assets.fgui, Assets.fmgui, Color.WHITE); lscore.setShadow(true);
        lammo = new Label(">> 10", Const.WIDTH-140, Const.HEIGHT-40, Assets.fgui, Assets.fmgui, Color.WHITE); lammo.setShadow(true);
        lmines = new Label("@@ 0", Const.WIDTH-140, Const.HEIGHT-80, Assets.fgui, Assets.fmgui, Color.WHITE); lmines.setShadow(true);
        lfreeze = new Label("", Const.HALFWIDTH, 50, Assets.fsmall, Assets.fmsmall, Color.WHITE, true, Color.BLUE); lfreeze.setShadow(true);
        lcrash = new Label("Game OVER", Const.HALFWIDTH, Const.HALFHEIGHT-20, Assets.ftitle, Assets.fmtitle, Color.RED, true); lcrash.setShadow(true);
        lvictory = new Label("Victory!", Const.HALFWIDTH, Const.HALFHEIGHT-20, Assets.ftitle, Assets.fmtitle, Color.GREEN, true, new Color(0, 50, 10)); lvictory.setShadow(true);
        lpause = new Label("Pause...", Const.HALFWIDTH, Const.HALFHEIGHT-20, Assets.ftitle, Assets.fmtitle, Color.YELLOW, true, Color.BLACK); lpause.setShadow(true);
        lminus = new Label(":(", 40, Const.HEIGHT-80, Assets.fsmall, Assets.fmsmall, Color.RED, true); lminus.setShadow(true);
        lmessage = new Label("", Const.HALFWIDTH, Const.HEIGHT-80, Assets.fsmall, Assets.fmsmall, Color.BLACK, true, Color.RED); lmessage.setShadow(true);
    }

    public void draw(Graphics2D g, Point camera){

    }
}
