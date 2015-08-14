package main.moonlightowl.java;

/**
 * LittleTanks  ~  v0.3  ~  MoonlightOwl  ~  The main class of the Board element.
 */

import main.moonlightowl.java.gui.*;
import main.moonlightowl.java.gui.component.Label;
import main.moonlightowl.java.script.Script;
import main.moonlightowl.java.sound.Music;
import main.moonlightowl.java.sound.Sound;
import main.moonlightowl.java.sound.SoundManager;
import main.moonlightowl.java.world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
    private Camera camera;
    // game vars
    private int gamestate = MENU;
    private boolean playMusic = false;

    // interface
    private Screen currentScreen;
    private MenuScreen menuScreen;
    private AboutScreen aboutScreen;
    private ScoresScreen scoresScreen;
    private PackageScreen packageScreen;
    private GameoverScreen gameoverScreen;
    private GameScreen gameScreen;

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

        rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        // load sound
        soundManager = new SoundManager();
        music = new Music("./resources/music/");
        if(playMusic) music.play();

        // global game objects
        camera = new Camera();
        world = new World();

        // game screens
        Label title = new Label("LittleTanks", Const.HALFWIDTH, 140, Assets.ftitle, Assets.fmtitle, Const.TITLE_COLOR, true);
        title.setShadow(true);
        aboutScreen = new AboutScreen(world, camera, title);
        scoresScreen = new ScoresScreen(world, camera, title);
        menuScreen = new MenuScreen(world, camera, title);
        gameScreen = new GameScreen(world, camera);
        gameScreen.setSoundManager(soundManager);

        gameoverScreen = new GameoverScreen(world, camera);
        packageScreen = new PackageScreen(world, camera, title);
        packageScreen.setItems(gameScreen.getMission().missionList());

        camera.newTarget();

        // set game state
        currentScreen = menuScreen;
        setGameState(MENU);

        // update menu
        Mission mission = gameScreen.getMission();
        menuScreen.setPackageName(mission.getName(), mission.getLength());

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
    public void quitGame(){
        // save game data
        scoresScreen.save();
        // unload resources
        music.stop();
        soundManager.close();
        world.dispose();
        JFrame.getFrames()[0].dispose();
        System.exit(1);
    }

    private void setScreen(Screen screen){
        currentScreen.setVisible(false);
        currentScreen = screen;
        currentScreen.setVisible(true);
    }
    private void setGameState(int state){
        switch(state){
            case MENU: setScreen(menuScreen); break;
            case ABOUT: setScreen(aboutScreen); break;
            case SCORES: setScreen(scoresScreen); break;
            case GAME: setScreen(gameScreen); break;
            case PACKAGE: setScreen(packageScreen); break;
            case GAMEOVER: setScreen(gameoverScreen); break;
        }
        gamestate = state;
    }
    private void gameOver(){
        // play corresponding sound
        if(gameScreen.isVictory()) soundManager.play(Sound.WINNER);
        else soundManager.play(Sound.GAMEOVER);
        //
        gameoverScreen.show(gameScreen.isVictory(),
                gameScreen.getScore() > scoresScreen.worst());
        setGameState(GAMEOVER);
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
                    soundManager.toggle(); break;
                case KeyEvent.VK_CLOSE_BRACKET:
                    playMusic = !playMusic;
                    if(playMusic) music.play();
                    else music.stop();
                    break;
                case KeyEvent.VK_BACK_SLASH:
                    playMusic = true;
                    music.next();
                    break;
                default:
                    if(gamestate == GAME){
                        if(e.getKeyCode() == KeyEvent.VK_P && gameScreen.isPaused()){
                            camera.newTarget();
                        }
                    }
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
        if(currentScreen.isClosed()) {
            if (gamestate == MENU) quitGame();
        }
        else if(!currentScreen.isVisible()){
            switch(gamestate){
                case MENU:
                    switch(menuScreen.getSelected()){
                        case MenuScreen.PACKAGE: setGameState(PACKAGE); break;
                        case MenuScreen.NEWGAME:
                            setGameState(GAME);
                            gameScreen.restartMission();
                            break;
                        case MenuScreen.SCORES: setGameState(SCORES); break;
                        case MenuScreen.ABOUT: setGameState(ABOUT); break;
                        case MenuScreen.EXIT: quitGame(); break;
                        default: menuScreen.setVisible(true);
                    }
                    break;
                case PACKAGE:
                    setGameState(MENU);
                    if(gameScreen.loadMission(packageScreen.getText())){
                        Mission mission = gameScreen.getMission();
                        menuScreen.setPackageName(mission.getName(), mission.getLength());
                        scoresScreen.load("./scores/"+mission.getName()+".scr");
                    }
                    break;
                case GAME:
                    if(gameScreen.isGameOver())
                        gameOver();
                    else setGameState(MENU);
                    break;
                case GAMEOVER:
                    if(gameoverScreen.inputReceived()){
                        scoresScreen.addRecord(gameoverScreen.getText(), gameScreen.getScore());
                        setGameState(SCORES);
                    } else setGameState(MENU);
                    break;
                default:
                    setGameState(MENU);
            }

            // let the camera wandering around
            camera.newTarget();
        }

        // update camera position
        if(gamestate != GAME || gameScreen.isPaused()){
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
