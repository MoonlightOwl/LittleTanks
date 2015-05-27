package main.moonlightowl.java;

/**
 * LittleTanks  ~  v0.3  ~  MoonlightOwl  ~  The main class of the Board element.
 */

import main.moonlightowl.java.gui.*;
import main.moonlightowl.java.gui.Label;
import main.moonlightowl.java.math.GMath;
import main.moonlightowl.java.sound.Music;
import main.moonlightowl.java.sound.Sound;
import main.moonlightowl.java.sound.SoundManager;
import main.moonlightowl.java.world.FX;
import main.moonlightowl.java.world.Item;
import main.moonlightowl.java.world.Tile;
import main.moonlightowl.java.world.World;
import main.moonlightowl.java.world.entity.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Board extends JPanel implements ActionListener{
    // gamestate constants
    public static final int MENU = 0, GAME = 1, ABOUT = 2, SCORES = 3,
        GAMEOVER = 4, PACKAGE = 5;

    private RenderingHints rh;
    // global objects
    private Music music;
    private SoundManager soundManager;
    // game objects
    private World world;
    private Tank player;
    private Camera camera;
    // game vars
    private int gamestate = MENU;
    private boolean playMusic = false, playSounds = true;

    // interface
    private Label title;
    private Screen currentScreen;
    private MenuScreen menuScreen;
    private AboutScreen aboutScreen;
    private ScoresScreen scoresScreen;
    private TextboxScreen gameoverScreen, packageScreen;
    private GameScreen gameScreen;
    private Query nickname, packagename;

    public Board(){
        // load resources
        Assets.load(this);

        // set event listeners
        addKeyListener(new KAdapter());
        addMouseListener(new MAdapter());
        addMouseMotionListener(new MMAdapter());
        setFocusable(true);

        // init system variables
        setDoubleBuffered(false);
        System.setProperty("sun.java2d.opengl", "True");

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        // global game objects
        camera = new Camera();
        player = new Tank();
        world = new World();

        camera.newTarget();

        // game screens
        title = new Label("LittleTanks", Const.HALFWIDTH, 140, Assets.ftitle, Assets.fmtitle, Const.TITLE_COLOR, true);
        title.setShadow(true);
        aboutScreen = new AboutScreen(world, camera, title);
        scoresScreen = new ScoresScreen(world, camera, title);
        menuScreen = new MenuScreen(world, camera, title);
        gameScreen = new GameScreen(world, camera);
        gameScreen.setSoundManager(soundManager);

        nickname = new Query("Enter your nick name:", Const.HALFWIDTH, 400, Assets.fgui, Assets.fmgui, Color.WHITE);
        gameoverScreen = new TextboxScreen(world, camera, title, nickname);

        packagename = new Query("Enter package name:", Const.HALFWIDTH, 400, Assets.fgui, Assets.fmgui, Color.WHITE);
        packageScreen = new TextboxScreen(world, camera, title, packagename);

        // set game state
        setGameState(MENU);

        // load sound
        soundManager = new SoundManager();
        music = new Music("./resources/music/");
        if(playMusic) music.play();

        // PLAY! (Starting update & draw timer thread)
        Timer timer = new Timer(20, this);
        timer.setInitialDelay(500);
        timer.start();
    }

    public void addNotify() {
        super.addNotify();
        initGame();
    }
    public void initGame(){

    }
    private void quitGame(){
        // save game data
        scoresScreen.save();
        // unload resources
        music.stop();
        soundManager.close();
        world.dispose();
        JFrame.getFrames()[0].dispose();
        System.exit(1);
    }

    private void setGameState(int state){
        switch(state){
            case MENU: currentScreen = menuScreen; break;
            case ABOUT: currentScreen = menuScreen; break;
            case SCORES: currentScreen = scoresScreen; break;
            case GAME: currentScreen = gameScreen; break;
            case PACKAGE: currentScreen = packageScreen; break;
            case GAMEOVER: currentScreen = gameoverScreen; break;
        }
        gamestate = state;
    }
    private void gameOver(boolean victory){
        setGameState(GAMEOVER);
        // play corresponding sound
        if(!victory) soundManager.play(Sound.GAMEOVER);
        else soundManager.play(Sound.WINNER);
        // camera begins to wandering around
        camera.newTarget();
    }

    /** Listen for events */
    private class KAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            currentScreen.keyPressed(e);

            // processing some global key codes
            switch(e.getKeyCode()){
                case KeyEvent.VK_OPEN_BRACKET:
                    soundManager.mute(); break;
                case KeyEvent.VK_CLOSE_BRACKET:
                    playMusic = !playMusic;
                    if(playMusic) music.play();
                    else music.stop();
                    break;
                case KeyEvent.VK_BACK_SLASH:
                    playMusic = true;
                    music.next();
                    break;
            }
        }
    }
    private class MAdapter extends MouseAdapter{
        @Override
        public void mouseReleased(MouseEvent e) {
            currentScreen.mouseClicked(e);
        }
    }
    private class MMAdapter extends MouseMotionAdapter{
        @Override
        public void mouseMoved(MouseEvent e){
            currentScreen.mouseMoved(e);
        }
        @Override
        public void mouseDragged(MouseEvent e){
            mouseMoved(e);
        }
    }

    /** Update game */
    public void actionPerformed(ActionEvent ae){
        // update current screen state
        currentScreen.update();

        // change screen, if needed
        if(!currentScreen.isVisible()){
            switch(gamestate){
                case MENU:
                    quitGame();
                    break;
            }

            // let the camera wandering around
            camera.newTarget();
        }

        // update camera position
        if(gamestate != GAME){
            camera.moveToTarget();
        }

        // update world
        world.level.renderChanges();
        world.fx.update();

        // render all
        repaint();
    }

    /** Render game */
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(rh);
        //

        // draw background
        g2.setColor(Const.BACK_COLOR);
        g2.fillRect(0, 0, Const.WIDTH, Const.HEIGHT);

        // draw screen
        currentScreen.draw(g2);

        //
        Toolkit.getDefaultToolkit().sync();
    }
}
