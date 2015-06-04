package main.moonlightowl.java.gui;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.gui.component.Label;
import main.moonlightowl.java.gui.component.Query;
import main.moonlightowl.java.world.World;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * LittleTanks.GameoverScreen
 * Created by MoonlightOwl on 5/28/15.
 * ---
 * You see it, when you die.
 */

public class GameoverScreen extends TextboxScreen {
    public static final int TIMEOUT = 200;

    private Label lvictory, lcrash;
    private boolean waitingForInput;
    private int timeout;

    public GameoverScreen(World world, Camera camera){
        super(world, camera, null);

        Query query = new Query("Enter your nick name:", Const.HALFWIDTH, 400, Assets.fgui, Assets.fmgui, Color.WHITE);
        setQuery(query);

        lcrash = new Label("Game OVER", Const.HALFWIDTH, Const.HALFHEIGHT-20,
                Assets.ftitle, Assets.fmtitle, Color.RED, true);
        lcrash.setShadow(true);
        lvictory = new Label("Victory!", Const.HALFWIDTH, Const.HALFHEIGHT-20,
                Assets.ftitle, Assets.fmtitle, Color.GREEN, true, new Color(0, 50, 10));
        lvictory.setShadow(true);

        setTitle(lcrash);

        waitingForInput = false;
        timeout = TIMEOUT;
    }

    // getters
    public boolean inputReceived(){ return waitingForInput; }

    public void show(boolean victory, boolean goodScore){
        if(victory) setTitle(lvictory);
        else setTitle(lcrash);

        waitingForInput = goodScore;
        if(waitingForInput) {
            query.setVisible(true);
            timeout = 0;
        }
        else {
            query.setVisible(false);
            timeout = TIMEOUT;
        }

        setVisible(true);
    }

    public void mouseClicked(MouseEvent e){
        setVisible(false);
    }

    public void update(){
        if(!waitingForInput && timeout > 0){
            timeout--;
            if(timeout <= 0)
                setVisible(false);
        }
    }

    public void draw(Graphics2D g){
        // game world
        world.draw(g, camera.getPosition());
        // special effects
        world.fx.draw(g, camera.getPosition());
        // ui
        g.setColor(Const.OPAQUE_DARK_COLOR);
        g.fillRect(0, Const.HALFHEIGHT-120, Const.WIDTH, 120);
        title.draw(g);
        query.draw(g);
    }
}
