package main.moonlightowl.java.gui;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.sound.Sound;
import main.moonlightowl.java.sound.SoundManager;
import main.moonlightowl.java.world.Item;
import main.moonlightowl.java.world.entity.Bomb;
import main.moonlightowl.java.world.entity.Tank;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;

/**
 * LittleTanks.GameScreen
 * Created by MoonlightOwl on 5/26/15.
 * ---
 * Shows game process, main game screen
 */

public class GameScreen extends Screen {
    // global
    private SoundManager soundManager;
    // game
    private Tank player;
    // interface
    private Label title, llifes, lshield, lscore, lammo, lvictory,
            lmines, lcrash, lminus, lmessage, lfreeze, lpause;
    private int minus_timer = 0, message_timer = 0;
    private boolean paused = false;
    // variables
    private int score;

    public GameScreen(){
        // init game
        player = new Tank();
        score = 0;
        // init interface
        llifes = new Label("@@@@@", 20, Const.HEIGHT-40, Assets.fgui, Assets.fmgui, Color.WHITE); llifes.setShadow(true);
        lshield = new Label("", 20, Const.HEIGHT-80, Assets.fgui, Assets.fmgui, Color.WHITE); lshield.setShadow(true);
        lscore = new Label("0", 20, 50, Assets.fgui, Assets.fmgui, Color.WHITE); lscore.setShadow(true);
        lammo = new Label(">> 10", Const.WIDTH-140, Const.HEIGHT-40, Assets.fgui, Assets.fmgui, Color.WHITE); lammo.setShadow(true);
        lmines = new Label("oo 0", Const.WIDTH-140, Const.HEIGHT-80, Assets.fgui, Assets.fmgui, Color.WHITE); lmines.setShadow(true);
        lfreeze = new Label("", Const.HALFWIDTH, 50, Assets.fsmall, Assets.fmsmall, Color.WHITE, true, Color.BLUE); lfreeze.setShadow(true);
        lcrash = new Label("Game OVER", Const.HALFWIDTH, Const.HALFHEIGHT-20, Assets.ftitle, Assets.fmtitle, Color.RED, true); lcrash.setShadow(true);
        lvictory = new Label("Victory!", Const.HALFWIDTH, Const.HALFHEIGHT-20, Assets.ftitle, Assets.fmtitle, Color.GREEN, true, new Color(0, 50, 10)); lvictory.setShadow(true);
        lpause = new Label("Pause...", Const.HALFWIDTH, Const.HALFHEIGHT-20, Assets.ftitle, Assets.fmtitle, Color.YELLOW, true, Color.BLACK); lpause.setShadow(true);
        lminus = new Label(":(", 40, Const.HEIGHT-80, Assets.fsmall, Assets.fmsmall, Color.RED, true); lminus.setShadow(true);
        lmessage = new Label("", Const.HALFWIDTH, Const.HEIGHT-80, Assets.fsmall, Assets.fmsmall, Color.BLACK, true, Color.RED); lmessage.setShadow(true);
    }

    // getters
    public boolean isPaused(){ return paused; }
    public int getScore(){ return score; }

    // setters
    public void setPaused(boolean paused){ this.paused = paused; }
    public void setScore(int score){ this.score = score; }
    public void setSoundManager(SoundManager soundManager){ this.soundManager = soundManager; }



    // processing interface



    // event handling
//    public void keyPressed(KeyEvent e) {
//        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
//            setVisible(false);
//        }
//        else if(!paused)
//            switch(e.getKeyCode()){
//                case KeyEvent.VK_LEFT:
//                    player.turn(270);
//                    moveTank(player, player.getX() - Const.TILE_SIZE, player.getY());
//                    break;
//                case KeyEvent.VK_RIGHT:
//                    player.turn(90);
//                    moveTank(player, player.getX() + Const.TILE_SIZE, player.getY());
//                    break;
//                case KeyEvent.VK_UP:
//                    player.turn(0);
//                    moveTank(player, player.getX(), player.getY() - Const.TILE_SIZE);
//                    break;
//                case KeyEvent.VK_DOWN:
//                    player.turn(180);
//                    moveTank(player, player.getX(), player.getY() + Const.TILE_SIZE);
//                    break;
//                case KeyEvent.VK_SPACE:
//                    if(!fireTank(player)) soundManager.play(Sound.NOAMMO);
//                    setAmmoCounter(player.getAmmo());
//                    break;
//                case KeyEvent.VK_C:
//                    if(gamestate == GAME){
//                        if(player.getBombs() > 0){
//                            player.changeBombs(-1);
//                            setMinesCounter(player.getBombs());
//                            world.bombs.add(new Bomb(player.getX()+30, player.getY()+30));
//                            soundManager.play(Sound.BEEP);
//                        }
//                    }
//                    break;
//                case KeyEvent.VK_ESCAPE:
//                    if(gamestate != MENU){
//                        if(nickname.isVisible()) nickname.setVisible(false);
//                        if(gamestate == GAME) newCameraTarget();
//                        gamestate = MENU;
//                    }
//                    else{
//                        if(about.isVisible()) about.setVisible(false);
//                        else if(scores.isVisible()) scores.setVisible(false);
//                        else if(packagename.isVisible()) packagename.setVisible(false);
//                        else gameQuit();
//                    }
//                    break;
//                case KeyEvent.VK_ENTER:   // end game
//                    if(gamestate == NICKNAME){
//                        scores.addRecord(nickname.getText(), score);
//                        scores.setVisible(true);
//                        nickname.setVisible(false);
//                        gamestate = MENU;
//                        //gameReset();
//                    } else if(gamestate == GAMEOVER) {
//                        minus_timer = 0;
//                        gamestate = MENU;
//                    } else if(gamestate == MENU){
//                        if(packagename.isVisible()){
//                            loadPackage(packagename.getText());
//                            packagename.setVisible(false);
//                        } else {
//                            if(about.isVisible()) about.setVisible(false);
//                            else if(scores.isVisible()) scores.setVisible(false);
//                            else activateMenuItem(menu.getSelected());
//                        }
//                    }
//                    break;
//                case KeyEvent.VK_OPEN_BRACKET:
//                    playSounds = !playSounds;
//                    if(playSounds) Sound.volume = Sound.Volume.LOW;
//                    else Sound.volume = Sound.Volume.MUTE;
//                    break;
//                case KeyEvent.VK_CLOSE_BRACKET:
//                    playMusic = !playMusic;
//                    if(playMusic) music.play();
//                    else music.stop();
//                    break;
//                case KeyEvent.VK_BACK_SLASH:
//                    playMusic = true;
//                    music.next();
//                    break;
//                case KeyEvent.VK_P:
//                    setPaused(true);
//                    break;
//                case KeyEvent.VK_E:
//                    if(player.removeFromInventory(Item.CANDY)){
//                        changeLife(1);
//                        addMessage("+1 life", Const.MESSAGE_TIME);
//                    }
//                    break;
//            }
//        else if(e.getKeyCode() == KeyEvent.VK_P)
//            setPaused(false);
//    }


    // screen processing
    public void update(){

    }

    public void draw(Graphics2D g){
        // world
        world.draw(g, camera.getPosition());
        player.draw(g, camera.getPosition());
        world.fx.draw(g, camera.getPosition());

        // UI
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
        lmines.draw(g);
        lfreeze.draw(g);
        // inventory
        Iterator<Item> ititems = player.inventory.iterator();
        int x = Const.WIDTH - 80;
        while(ititems.hasNext()){
            Item i = ititems.next();
            i.drawIcon(g, x, 10);
            x-=30;
        }

        if(paused){
            g.setColor(Const.OPAQUE_DARK_COLOR);
            g.fillRect(0, Const.HALFHEIGHT-120, Const.WIDTH, 120);
            lpause.draw(g);
        }
    }
}
