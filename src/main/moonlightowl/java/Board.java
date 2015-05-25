package main.moonlightowl.java;

/**
 * LittleTanks  ~  v0.3  ~  MoonlightOwl  ~  The main class of the Board element.
 */

import main.moonlightowl.java.gui.*;
import main.moonlightowl.java.gui.Label;
import main.moonlightowl.java.gui.Menu;
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
import java.awt.geom.Point2D;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Board extends JPanel implements ActionListener{
    // gamestate constants
    public static final int MENU = 0, GAME = 1, NICKNAME = 2, GAMEOVER = 3, PAUSE = 4;
    // global objects
    private Music music;
    private FX fx;
    private SoundManager soundManager;
    // game vars
    private boolean playMusic = false, playSounds = true;
    private int effectFreeze = 0;
    private int score = 0;
    private int gamestate = MENU;
    private boolean isVictory = false;
    private int levelnum = 1;
    private String levelpackage = "level";
    private int MAXLEVEL = 1;
    // game objects
    private World world;
    private Tank player;

    // camera
    private Point camera, camtarget;
    private Point2D.Double camposition;
    private double camangle;
    private static final double CAM_TURN_SPEED = 1.0, CAM_MOVE_SPEED = 0.2;

    // interface
    private Menu menu;
    private Label title, llifes, lshield, lscore, lammo, lvictory,
                  lmines, lcrash, lminus, lmessage, lfreeze, lpause;
    private int minus_timer = 0, message_timer = 0;
    private About about;
    private Scores scores;
    private Query nickname, packagename;

    public Board(){
        // load resources
        Assets.load(this);

        // set event listeners
        addKeyListener(new KAdapter());
        addMouseListener(new MAdapter());
        addMouseMotionListener(new MMAdapter());
        setFocusable(true);

        // init variables
        setDoubleBuffered(false);
        System.setProperty("sun.java2d.opengl", "True");

        // intefrace
        title = new Label("LittleTanks", Const.HALFWIDTH, 140, Assets.ftitle, Assets.fmtitle, Const.TITLE_COLOR, true); title.setShadow(true);
        menu = new Menu(Assets.fmenu, Assets.fmmenu, Const.HALFWIDTH, Const.HALFHEIGHT-60, 70);
        menu.addItem("< level >"); menu.addItem("Fight!"); menu.addItem("Scores"); menu.addItem("About"); menu.addItem("Quit");
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

        about = new About();
        about.addLine("Experimental project (Java, Swing)", Const.HALFWIDTH, 250, Assets.fsmall, Assets.fmsmall, Color.WHITE, true, true);
        about.addLine("Programmer: MoonlightOwl", Const.HALFWIDTH, 290, Assets.fgui, Assets.fmgui, Color.WHITE, true, true);
        about.addLine("BetaTester: Polina", Const.HALFWIDTH, 330, Assets.fgui, Assets.fmgui, Color.WHITE, true, true);
        about.addLine("Contact:", Const.HALFWIDTH, 400, Assets.fsmall, Assets.fmsmall, Color.WHITE, true, true);
        about.addLine("murky.owl@gmail.com", Const.HALFWIDTH, 440, Assets.fgui, Assets.fmgui, Color.CYAN, true, true);
        about.addLine("20 April 2015", Const.HALFWIDTH, 480, Assets.fgui, Assets.fmgui, Color.WHITE, true, true);
        about.addLine("Neverland", Const.HALFWIDTH, 520, Assets.fgui, Assets.fmgui, Color.WHITE, true, true);

        nickname = new Query("Enter your nick name:", Const.HALFWIDTH, 400, Assets.fgui, Assets.fmgui, Color.WHITE);
        packagename = new Query("Enter package name:", Const.HALFWIDTH, 400, Assets.fgui, Assets.fmgui, Color.WHITE);

        scores = new Scores("scores/scores.txt", Assets.fgui, Assets.fmgui);

        // set camera
        camera = new Point(0, 0);
        camtarget = new Point(0, 0);
        camposition = new Point2D.Double(0, 0);
        camangle = 0.0;

        // init game variables
        gamestate = MENU;
        player = new Tank();
        world = new World();
        loadLevel("levels/test1.dat");
        loadPackage("level");

        newCameraTarget();

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

    private void gameOver(boolean victory){
        if(score > scores.worst()){
            nickname.setVisible(true);
            gamestate = NICKNAME;
        } else {
            gamestate = GAMEOVER;
            minus_timer = 200;
        }
        if(!victory) soundManager.play(Sound.GAMEOVER);
        else soundManager.play(Sound.WINNER);
        isVictory = victory;
        // for pause and menu screens
        newCameraTarget();
    }
    private void gameQuit(){
        // save game data
        scores.saveScores();
        // unload resources
        fx.dispose();
        music.stop();
        soundManager.close();
        JFrame.getFrames()[0].dispose();
        System.exit(1);
    }
    public void gameReset(){
        // load default level
        loadLevel("levels/test1.dat");
        // reset game parameters
        score = 0;
        setScoreCounter(score);
    }
    public void interfaceReset(){
        minus_timer = 0;
        message_timer = 0;
        effectFreeze = 0;
        setLifeCounter(player.getLife());
        setShieldCounter(player.getShield());
        setAmmoCounter(player.getAmmo());
        setMinesCounter(player.getBombs());
        setFreezeCounter(effectFreeze);
    }

    public void nextLevel(){
        levelnum++;
        loadLevel("levels/"+levelpackage+levelnum+".dat");
        // bonus points
        if(levelnum > 1) score += 10;
        addMessage("New level! (+10 score)", Const.MESSAGE_TIME);
        // new level - new music
        if(playMusic) music.next();
    }

    private void loadPackage(String name){
        int max = getMaxLevel(name);
        if(max > 0){
            MAXLEVEL = max;
            levelpackage = name;
            menu.setName(0, "[ " + levelpackage + "-" + MAXLEVEL + " ]");
            scores.saveScores();
            scores.loadScores("scores/"+levelpackage+".txt");
        }
    }
    private int getMaxLevel(String name){
        File file;
        int max = 0;
        for(int i=1; i<100; i++){
            file = new File("levels/"+name+i+".dat");
            if(file.exists()) max++;
            else break;
        }
        return max;
    }

    public void loadLevel(String filename){
        // load world data
        world.reset();
        world.loadLevel(filename);
        // place player in the world
        player.reset();
        Point sp = world.level.getStartPoint();
        int x = GMath.toPixel(sp.x);
        int y = GMath.toPixel(sp.y);
        player.setPosition(x, y);
        // move camera to player
        setCamera(x, y);
        // create new FX map
        if(fx != null) fx.dispose();
        fx = new FX(world.level.getPxWidth(), world.level.getPxHeight());
        // update interface
        interfaceReset();
    }

    private void setCamera(int x, int y){
        camera.x = x - Const.HALFWIDTH;
        camera.y = y - Const.HALFHEIGHT;
    }
    private void newCameraTarget(){
        camposition.x = camera.x + Const.HALFWIDTH;
        camposition.y = camera.y + Const.HALFHEIGHT;
        camtarget.x = GMath.rand.nextInt(world.level.getPxWidth()-240) + 120;
        camtarget.y = GMath.rand.nextInt(world.level.getPxHeight()-240) + 120;
    }
    private void moveCameraToTarget(){
        double targetangle = Math.atan2(camtarget.y - camposition.y,
                                        camtarget.x - camposition.x);
        if(camangle != targetangle){
            double delta = targetangle - camangle;
            if(delta > Math.PI) delta -= Math.PI*2;
            if(delta < -Math.PI) delta += Math.PI*2;

            if(Math.abs(delta) < CAM_TURN_SPEED){
                camangle += delta;
            }
            else {
                camangle += CAM_TURN_SPEED * Math.signum(delta);
            }
            if(camangle < 0.0) camangle += Math.PI*2;
            else if(camangle >= Math.PI*2) camangle -= Math.PI*2;
        }

        double dist = distance((float)camposition.x, (float)camposition.y,
                    camtarget.x, camtarget.y);
        if(dist < CAM_MOVE_SPEED){
            newCameraTarget();
        } else {
            camposition.x += Math.cos(camangle)*CAM_MOVE_SPEED;
            camposition.y += Math.sin(camangle)*CAM_MOVE_SPEED;
            setCamera((int)camposition.x, (int)camposition.y);
        }
    }

    // processing GUI indicators & player parameters
    private void changeLife(int l){
        player.setLife(player.getLife() + l);
        setLifeCounter(player.getLife());
    }
    private void setLifeCounter(int count){
        String text = "";
        for(int i = 0; i<count; i++){
            text+="@";
        }
        llifes.changeText(text);
    }
    private void changeShield(int s){
        int amount = (player.getShield() + s <= 20 ? s : 20 - player.getShield());
        player.setShield(player.getShield() + amount);
        setShieldCounter(player.getShield());
    }
    private void setShieldCounter(int count){
        String text = "";
        for(int i = 0; i<Math.ceil(count/2); i++){
            text+="*";
        }
        lshield.changeText(text);
    }
    private void setAmmoCounter(int ammo){
        lammo.changeText(">> "+Integer.toString(ammo));
    }
    private void setMinesCounter(int mines){
        lmines.changeText("@@ "+Integer.toString(mines));
    }
    private void changeScore(int amount){
        if(gamestate == GAME){
            score += amount;
            setScoreCounter(score);
        }
    }
    private void setScoreCounter(int score){
        lscore.changeText(Integer.toString(score));
    }
    private void setFreezeCounter(int freeze){
        if(freeze > 0)
            lfreeze.changeText("< " + Integer.toString(freeze) + " >");
        else
            lfreeze.changeText("");
        lfreeze.setX(Const.HALFWIDTH);
    }
    private void addMessage(String text, int time){
        lmessage.changeText(text);
        message_timer = time;
    }
    private void minusLife(int amount){
        changeScore(-50*amount);
        changeLife(-amount);

        if(player.getLife() <= 0){
            gameOver(false);
        }
        else minus_timer = 100;
    }

    public double distance(float x1, float y1, float x2, float y2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

    // tank operations
    private void moveTank(Tank tank, int x, int y){
        if(tank.isIdle()){
            int mx = GMath.toMap(x), my = GMath.toMap(y);
            // move if possible
            if(world.level.isPassable(mx, my) && !world.level.getCollision(mx,my)){
                tank.move(x, y);
                // pressure plates
                if(world.level.get(mx, my).get() == Tile.PLATE){
                    if(world.level.getStage(mx, my) == 0){
                        world.level.setStage(mx, my, 1);
                        // search for links and activate them
                        HashSet<Point> dest = world.level.getLink(mx, my);
                        if(dest != null){
                            for(Point destination: dest){
                                activateMapTile(destination.x, destination.y);
                            }
                        }
                    }
                }
            // open if closed (for players only =))
            } else if(tank == player) {
                int tile = world.level.get(mx, my).get();
                if(tile == Tile.DOOR){
                    int stage = world.level.getStage(mx, my);
                    if(stage > 0){
                        if(player.inventoryContains(Item.KEY)){
                            world.level.setStage(mx, my, world.level.getStage(mx, my)-1);
                            if(stage == 5) soundManager.play(Sound.LOCK);
                        }
                    }
                } else if(tile == Tile.SAFE){
                    if(world.level.getStage(mx, my) > 0){
                        if(player.inventoryContains(Item.KEY)){
                            world.level.setStage(mx, my, 0);
                            soundManager.play(Sound.LOCK);
                        }
                    }
                }
            }
        }
    }
    private boolean fireTank(Tank tank){
        if(tank.isIdle()){
            if(tank.getAmmo()>0){
                int x = tank.getX()+30, y = tank.getY()+30;
                float dx = 0, dy = 0;
                switch((int)tank.getAngle()){
                    case 0: dy = -1.0f; y = y - 30; break;
                    case 90: dx = 1.0f; x = x + 30; break;
                    case 180: dy = 1.0f; y = y + 30; break;
                    case 270: dx = -1.0f; x = x - 30; break;
                }
                world.newBullet.add(new Bullet(x, y, dx, dy, tank.getLevel()));
                // play sound
                if(tank == player || GMath.rand.nextBoolean()){
                    switch(tank.getLevel()){
                        case 1: case 2: soundManager.play(Sound.SHOOT); break;
                        case 3: soundManager.play(Sound.LAUNCH); break;
                    }
                }
                // decrease ammo
                tank.changeAmmo(-1);
            } else return false;
        }
        return true;
    }

    private void activateMenuItem(int index){
        switch(index){
            case 0: packagename.setVisible(true); break;
            case 1: // new game
                gamestate = GAME;
                gameReset();
                levelnum = 0; nextLevel();
                break;
            case 2: scores.setVisible(true); break;
            case 3: about.setVisible(true); break;
            case 4: gameQuit(); break;
        }
    }
    private void activateMapTile(int x, int y){
        Tile tile = world.level.get(x, y);
        int px = GMath.toPixel(x), py = GMath.toPixel(y);
        switch(tile.get()){
            case Tile.DOOR:
                if(tile.getStage() == 5){
                    world.level.setStage(x, y, 0);
                    soundManager.play(Sound.LOCK);
                }
                break;
            case Tile.SAFE:
            case Tile.BOX:
                world.level.clear(x, y);
                world.level.drawSplash(Assets.iexpldec, px-20, py-20);
                synchronized(world.bonuses){
                    world.bonuses.add(new Bonus(px+30, py+30, GMath.rand.nextInt(Bonus.COUNT)));
                }
                fx.add(px-20, py-20, FX.SMALLEXPLOSION);
                break;
            case Tile.BARREL:
                world.level.clear(x, y);
                world.level.drawSplash(Assets.iexpldec, px-20, py-20);
                synchronized(world.newBullet){
                    for(int c = 0; c < GMath.rand.nextInt(3)+4; c++){
                        double angle = GMath.rand.nextDouble() * Math.PI * 2.0,
                               speed = GMath.rand.nextDouble()/5.0 + 0.8;
                        world.newBullet.add(new Bullet(px+30, py+30,
                            (float)(Math.cos(angle)*speed), (float)(Math.sin(angle)*speed)));
                    }
                }
                fx.add(px-20, py-20, FX.EXPLOSION);
                soundManager.play(Sound.EXPLODE);
                break;
            case Tile.SPAWN:
                int stage = world.level.getStage(x, y);
                if(stage > 0){
                    Tank enemy = new Tank(px, py, stage);
                    enemy.setAmmo(1000);
                    synchronized(world.newEnemies) { world.newEnemies.add(enemy); }
                }
                break;
        }
    }

    // update & render
    private class KAdapter extends KeyAdapter{
        @Override
        public void keyReleased(KeyEvent e) {
        }
        @Override
        public void keyPressed(KeyEvent e) {
            if(gamestate == NICKNAME) nickname.keyPressed(e);
            else if(packagename.isVisible()) packagename.keyPressed(e);
            else if(gamestate == MENU) menu.keyPressed(e);

            switch(e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if(gamestate == GAME){
                        player.turn(270);
                        moveTank(player, player.getX() - Const.TILE_SIZE, player.getY());
                    } break;
                case KeyEvent.VK_RIGHT:
                    if(gamestate == GAME){
                        player.turn(90);
                        moveTank(player, player.getX() + Const.TILE_SIZE, player.getY());
                    } break;
                case KeyEvent.VK_UP:
                    if(gamestate == GAME){
                        player.turn(0);
                        moveTank(player, player.getX(), player.getY() - Const.TILE_SIZE);
                    } break;
                case KeyEvent.VK_DOWN:
                    if(gamestate == GAME){
                        player.turn(180);
                        moveTank(player, player.getX(), player.getY() + Const.TILE_SIZE);
                    } break;
                case KeyEvent.VK_SPACE:
                    if(gamestate == GAME){
                        if(!fireTank(player)) soundManager.play(Sound.NOAMMO);
                        setAmmoCounter(player.getAmmo());
                    }
                    break;
                case KeyEvent.VK_C:
                    if(gamestate == GAME){
                        if(player.getBombs() > 0){
                            player.changeBombs(-1);
                            setMinesCounter(player.getBombs());
                            world.bombs.add(new Bomb(player.getX()+30, player.getY()+30));
                            soundManager.play(Sound.BEEP);
                        }
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    if(gamestate != MENU){
                        if(nickname.isVisible()) nickname.setVisible(false);
                        if(gamestate == GAME) newCameraTarget();
                        gamestate = MENU;
                    }
                    else{
                        if(about.isVisible()) about.setVisible(false);
                        else if(scores.isVisible()) scores.setVisible(false);
                        else if(packagename.isVisible()) packagename.setVisible(false);
                        else gameQuit();
                    }
                    break;
                case KeyEvent.VK_ENTER:   // end game
                    if(gamestate == NICKNAME){
                        scores.addRecord(nickname.name(), score);
                        scores.setVisible(true);
                        nickname.setVisible(false);
                        gamestate = MENU;
                        //gameReset();
                    } else if(gamestate == GAMEOVER) {
                        minus_timer = 0;
                        gamestate = MENU;
                    } else if(gamestate == MENU){
                        if(packagename.isVisible()){
                            loadPackage(packagename.name());
                            packagename.setVisible(false);
                        } else {
                            if(about.isVisible()) about.setVisible(false);
                            else if(scores.isVisible()) scores.setVisible(false);
                            else activateMenuItem(menu.getSelected());
                        }
                    }
                    break;
                case KeyEvent.VK_OPEN_BRACKET:
                    playSounds = !playSounds;
                    if(playSounds) Sound.volume = Sound.Volume.LOW;
                    else Sound.volume = Sound.Volume.MUTE;
                    break;
                case KeyEvent.VK_CLOSE_BRACKET:
                    playMusic = !playMusic;
                    if(playMusic) music.play();
                    else music.stop();
                    break;
                case KeyEvent.VK_BACK_SLASH:
                    playMusic = true;
                    music.next();
                    break;
                case KeyEvent.VK_P:
                    if(gamestate == GAME){ gamestate = PAUSE; newCameraTarget(); }
                    else if(gamestate == PAUSE){ gamestate = GAME; }
                    break;
                case KeyEvent.VK_E:
                    if(player.removeFromInventory(Item.CANDY)){
                        changeLife(1);
                        addMessage("+1 life", Const.MESSAGE_TIME);
                    }
                    break;
            }
        }
    }

    private void MouseClick(MouseEvent e){
        if(gamestate == MENU){
            if(e.getButton() == MouseEvent.BUTTON1){
                if(!about.isVisible() && !scores.isVisible() && !packagename.isVisible()){
                    activateMenuItem(menu.getSelected());
                }
                else if(scores.isVisible()){ scores.setVisible(false); }
                else if(about.isVisible()){ about.setVisible(false); }
            }
            else if(e.getButton() == MouseEvent.BUTTON3){
                if(scores.isVisible()){ scores.setVisible(false); }
                else if(about.isVisible()){ about.setVisible(false); }
            }
        }
        else{
            if(gamestate == NICKNAME){     // end game
                scores.addRecord(nickname.name(), score);
                nickname.setVisible(false);
                gamestate = MENU;
            } else if(gamestate == GAMEOVER){
                minus_timer = 0;
                gamestate = MENU;
            }
        }
    }

    private class MAdapter extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e) {
            //MouseClick(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            MouseClick(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
    private class MMAdapter extends MouseMotionAdapter{

        @Override
        public void mouseMoved(MouseEvent e){
            if(gamestate == MENU){
                menu.mouseMoved(e);
            }
        }
        @Override
        public void mouseDragged(MouseEvent e){
            mouseMoved(e);
        }
    }

    public void actionPerformed(ActionEvent ae){
        // update player & camera
        world.level.setCollision(player.getMapX(), player.getMapY(), false);
        player.update();
        world.level.setCollision(player.getMapX(), player.getMapY(), true);
        // camera
        if(gamestate == GAME){
            if(!Sound.EXPLODE.isPlaying())
                setCamera(player.getX() + 30, player.getY() + 30);
            else
                setCamera(player.getX() + 27 + GMath.rand.nextInt(6),
                          player.getY() + 27 + GMath.rand.nextInt(6));
        } else {
            moveCameraToTarget();
        }
        if(gamestate != PAUSE){
            // effects
            if(effectFreeze > 0){
                effectFreeze--;
                setFreezeCounter(effectFreeze);
            }
            // tracks =)
            if(GMath.rand.nextInt(60) == 0){
                world.level.drawSplash(Assets.itrack, player.getTransform());
            }
            // add projectiles
            synchronized(world.newBullet){
                if(world.newBullet.size() > 0){
                    for(Bullet bullet: world.newBullet){
                        synchronized(world.bullets){
                            world.bullets.add(bullet);
                        }
                    }
                    world.newBullet.clear();
                }
            }
            // add new tanks
            synchronized(world.newEnemies){
                if(world.newEnemies.size() > 0){
                    for(Tank enemy: world.newEnemies){
                        synchronized(world.enemies){
                            world.enemies.add(enemy);
                        }
                    }
                    world.newEnemies.clear();
                }
            }

            try {
                // scroll all enemies
                synchronized(world.enemies){
                    Iterator<Tank> itenemies = world.enemies.iterator();
                    while(itenemies.hasNext()){
                        Tank t = itenemies.next();
                        world.level.setCollision(t.getMapX(), t.getMapY(), false);
                        t.update();
                        world.level.setCollision(t.getMapX(), t.getMapY(), true);
                        // random movement
                        if(t.isIdle()){
                            int action = GMath.rand.nextInt(effectFreeze == 0 ? 10 : 100);
                            if(action == 0){
                                int dx = 0, dy = 0;
                                if(GMath.rand.nextBoolean()) dx = Const.TILE_SIZE*(GMath.rand.nextBoolean()? -1:1);
                                else dy = Const.TILE_SIZE*(GMath.rand.nextBoolean()? -1:1);
                                moveTank(t, t.getX()+dx, t.getY()+dy);
                                if(dx<0) t.turn(270);
                                else if(dy<0) t.turn(0);
                                else if(dx>0) t.turn(90);
                                else if(dy>0) t.turn(180);
                            } else if(action == 2) fireTank(t);
                        }
                        // bullet collision
                        synchronized(world.bullets){
                            Iterator<Bullet> itbullets = world.bullets.iterator();
                            while(itbullets.hasNext()){
                                Bullet b = itbullets.next();
                                if(world.level.getCollision((int)(b.getX()/Const.TILE_SIZE), (int)(b.getY()/Const.TILE_SIZE))){
                                    if(distance(b.getX(), b.getY(), t.getX()+30, t.getY()+30) < 25){
                                        if(t.hit(b.getLevel())) soundManager.play(Sound.HIT);
                                        itbullets.remove();
                                        // score points
                                        int bonus = GMath.rand.nextInt(50);
                                        changeScore(bonus);
                                    }
                                }
                            }
                        }
                        // mine collision
                        synchronized(world.bombs){
                            Iterator<Bomb> itmines = world.bombs.iterator();
                            while(itmines.hasNext()){
                                Bomb m = itmines.next();
                                if(distance(m.getX(), m.getY(), t.getX()+30, t.getY()+30) < 30){
                                    // death!
                                    AffineTransform at = t.getTransform();
                                    at.rotate(GMath.rand.nextDouble() * Math.PI * 2.0);
                                    world.level.drawSplash(Assets.iexpldec, at);
                                    t.setLife(0);
                                    itmines.remove();
                                    // score points
                                    int bonus = GMath.rand.nextInt(100);
                                    changeScore(bonus);
                                    addMessage("mine death!", Const.MESSAGE_TIME);
                                }
                            }
                        }
                        // explosion
                        if(t.getLife() <= 0){
                            world.level.setCollision(t.getMapX(), t.getMapY(), false);
                            world.level.drawSplash(Assets.iexpldec, t.getX()-20, t.getY()-20);
                            fx.add(t.getX()-20, t.getY()-20, FX.EXPLOSION);
                            // score points
                            int bonus = GMath.rand.nextInt(100)*t.getLevel() + 10;
                            changeScore(bonus);
                            addMessage("+"+bonus+" score", Const.MESSAGE_TIME);
                            // game over, man, it's over
                            soundManager.play(Sound.EXPLODE);
                            itenemies.remove();
                        }
                    }
                    // if all enemies were destroyed - we are champions!
                    if(world.enemies.size() == 0 && gamestate == GAME){
                        if(levelnum >= MAXLEVEL){
                            gameOver(true);
                        } else {
                            nextLevel();
                        }
                    }
                }
                // scroll throught all the bullets
                synchronized(world.bullets){
                    Iterator<Bullet> itbullets = world.bullets.iterator();
                    while(itbullets.hasNext()){
                        Bullet b = itbullets.next();

                        // rocket smoke trail
                        if(b.getLevel() == Tank.LAUNCHER && GMath.rand.nextBoolean())
                            fx.add((int)b.getX() - 50 + GMath.rand.nextInt(40),
                                   (int)b.getY() - 50 + GMath.rand.nextInt(40), FX.SMOKE);

                        b.update();

                        // check player collision
                        if(gamestate == GAME && distance(b.getX(), b.getY(), player.getX()+30, player.getY()+30)<25){
                            itbullets.remove();
                            if(player.getShield() > 0) changeShield(-b.getLevel());
                            else{ minusLife(b.getLevel()); soundManager.play(Sound.HIT); }
                            if(player.getLife() <= 0) fx.add(player.getX()-20, player.getY()-20, FX.EXPLOSION);
                        }
                        // check level collision
                        else {
                            int x = GMath.toMap((int)b.getX()), y = GMath.toMap((int)b.getY());
                            if(!world.level.isFlyable(x, y)){
                                // crush! destroy! swag!
                                Tile tile = world.level.get(x, y);
                                int px = (int)b.getX(), py = (int)b.getY();
                                switch(tile.get()){
                                    case Tile.BOX:
                                    case Tile.BARREL:
                                        activateMapTile(x, y);
                                        break;
                                    case Tile.SANDSTONE:
                                        if(tile.getStage() > 0){
                                            if(GMath.rand.nextBoolean()){
                                                world.level.setStage(x, y, tile.getStage()-b.getLevel());
                                            }
                                        }
                                        break;
                                    case Tile.DOOR:
                                        if(tile.getStage() > 0 && tile.getStage() < 5){
                                            world.level.setStage(x, y, tile.getStage()-b.getLevel());
                                        }
                                        break;
                                    case Tile.SAFE:
                                        if(tile.getStage() == 0){
                                            world.level.clear(x, y);
                                            world.level.drawSplash(Assets.iexpldec, px-20, py-20);
                                            synchronized(world.bonuses){
                                                world.bonuses.add(new Bonus(px+30, py+30, GMath.rand.nextInt(Bonus.COUNT)));
                                            }
                                            fx.add(px-20, py-20, FX.SMALLEXPLOSION);
                                        }
                                        break;
                                }
                                // bullet/rocket gone in sparkles/explosion
                                switch(b.getLevel()){
                                    case 1: case 2:
                                        fx.add(px - (int)Math.sin(b.getAngle())*30,
                                            py + (int)Math.cos(b.getAngle())*30, FX.SPARKLE);
                                        break;
                                    case 3:
                                        fx.add((int)b.getX()-50, (int)b.getY()-50, FX.EXPLOSION);
                                        soundManager.play(Sound.EXPLODE);
                                        break;
                                }
                                itbullets.remove();
                            }
                        }
                    }
                }
                synchronized(world.bonuses){
                    Iterator<Bonus> itbonuses = world.bonuses.iterator();
                    while(itbonuses.hasNext()){
                        Bonus s = itbonuses.next();
                        s.update();
                        if(s.contains(player.getX()+30, player.getY()+30)){
                            // hack! slash! loot!
                            switch(s.getType()){
                                case Bonus.LIFE:
                                    changeLife(1);
                                    addMessage("+1 life", Const.MESSAGE_TIME);
                                    break;
                                case Bonus.AMMO:
                                    player.setAmmo(player.getAmmo()+10); setAmmoCounter(player.getAmmo());
                                    addMessage("+10 ammo", Const.MESSAGE_TIME);
                                    break;
                                case Bonus.SCORE:
                                    int bonus = GMath.rand.nextInt(50);
                                    changeScore(bonus);
                                    addMessage("+"+bonus+" score", Const.MESSAGE_TIME);
                                    break;
                                case Bonus.MINE:
                                    player.changeBombs(2); setMinesCounter(player.getBombs());
                                    addMessage("+2 bombs", Const.MESSAGE_TIME);
                                    break;
                                case Bonus.FREEZE:
                                    effectFreeze += 1000;
                                    addMessage("slow down", Const.MESSAGE_TIME);
                                    soundManager.play(Sound.FREEZE);
                                    break;
                                case Bonus.POWER:
                                    int level;
                                    do {
                                        level = GMath.rand.nextInt(Tank.MAX_LEVEL)+1;
                                    } while(level == player.getLevel());
                                    player.setLevel(level);
                                    addMessage("random power", Const.MESSAGE_TIME);
                                    break;
                                case Bonus.SHIELD:
                                    changeShield(8);
                                    addMessage("shields up", Const.MESSAGE_TIME);
                                    soundManager.play(Sound.SHIELD);
                                    break;
                            }
                            soundManager.play(Sound.PICKUP);
                            itbonuses.remove();
                        }
                    }
                }

                synchronized(world.items){
                    Iterator<Item> ititems = world.items.iterator();
                    while(ititems.hasNext()){
                        Item i = ititems.next();
                        i.update();
                        // collect items to player inventory
                        if(i.contains(player.getX()+Const.HALF_TILE, player.getY()+Const.HALF_TILE)){
                            player.inventory.add(i);
                            soundManager.play(Sound.PICKUP);
                            ititems.remove();
                        }
                    }
                }

                synchronized(world.turrets){
                    Iterator<Turret> itturrets = world.turrets.iterator();
                    while(itturrets.hasNext()){
                        Turret t = itturrets.next();

                        // shoot and crush!
                        if(distance(player.getX(), player.getY(), t.getX(), t.getY()) < Turret.DETECT_RADIUS) {
                            t.update(true);
                            if (GMath.rand.nextInt(4) == 1) {
                                synchronized (world.newBullet) {
                                    double rifle = GMath.rand.nextBoolean() ? -0.3 : 0.3,
                                            dx = Math.cos(t.getAngle() - Math.PI / 2 + rifle) * 40,
                                            dy = Math.sin(t.getAngle() - Math.PI / 2 + rifle) * 40;
                                    world.newBullet.add(
                                            new Bullet(t.getX() + Const.HALF_TILE + (int) dx,
                                                       t.getY() + Const.HALF_TILE + (int) dy,
                                                       (float) (dx / 20), (float) (dy / 20)));
                                }
                            }
                        } else t.update(false);
                        // bullet collision
                        synchronized(world.bullets) {
                            Iterator<Bullet> itbullets = world.bullets.iterator();
                            while(itbullets.hasNext()) {
                                Bullet b = itbullets.next();
                                if(GMath.toMap((int)b.getX()) == t.getMapX() &&
                                    GMath.toMap((int)b.getY()) == t.getMapY()) {
                                    // hit!
                                    t.changeLife(-1);
                                    if(t.getLife() == 0){
                                        itturrets.remove();
                                        // explode!
                                        fx.add((int)b.getX()-50, (int)b.getY()-50, FX.EXPLOSION);
                                        soundManager.play(Sound.EXPLODE);
                                    }
                                    itbullets.remove();
                                    // score points
                                    int bonus = GMath.rand.nextInt(20);
                                    changeScore(bonus);
                                }
                            }
                        }
                    }
                }
            } catch(NoSuchElementException e){ System.out.println("WTF! Again (shedule)..."); }

            world.level.renderChanges();
            fx.update();
        }

        // paint all
        repaint();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHints(rh);
        //

        // draw background
        g.setColor(Const.BACK_COLOR);
        g.fillRect(0, 0, Const.WIDTH, Const.HEIGHT);

        // draw level
        world.level.draw(g2, camera);
        synchronized(world.bombs){
            for(Bomb m: world.bombs) {
                m.draw(g2, camera);
            }
        }
        // player
        player.draw(g2, camera);

        synchronized(world.enemies){
            for(Tank t: world.enemies) {
                t.draw(g2, camera);
            }
        }
        synchronized(world.turrets){
            for(Turret t: world.turrets) {
                t.draw(g2, camera);
            }
        }
        synchronized(world.bonuses){
            for(Bonus z: world.bonuses) {
                z.draw(g2, camera);
            }
        }
        synchronized(world.items){
            for(Item i: world.items) {
                i.draw(g2, camera);
            }
        }
        synchronized(world.bullets){
            for(Bullet b: world.bullets) {
                b.draw(g2, camera);
            }
        }

        // draw effects
        fx.draw(g2, camera);

        // draw gui
        if(gamestate == MENU){
            title.draw(g2);
            if(about.isVisible()) about.draw(g2);
            else if(scores.isVisible()) scores.draw(g2);
            else if(packagename.isVisible()) packagename.draw(g2);
            else menu.draw(g2);
        }
        else{
            // shadowed background
            g2.drawImage(Assets.ishadowLS, 0, 0, null);
            g2.drawImage(Assets.ishadowLB, 0, Const.HEIGHT-142, null);
            g2.drawImage(Assets.ishadowRS, Const.WIDTH-224, 0, null);
            g2.drawImage(Assets.ishadowRB, Const.WIDTH-224, Const.HEIGHT-142, null);
            // indicators
            llifes.draw(g2);
            lshield.draw(g2);
            lscore.draw(g2);
            lammo.draw(g2);
            lmines.draw(g2);
            lfreeze.draw(g2);
            // inventory
            Iterator<Item> ititems = player.inventory.iterator();
            int x = Const.WIDTH - 80;
            while(ititems.hasNext()){
                Item i = ititems.next();
                i.drawIcon(g2, x, 10);
                x-=30;
            }
            // gui
            if(gamestate == NICKNAME){ 
                nickname.draw(g2);
                if(isVictory) lvictory.draw(g2);
                else lcrash.draw(g2);
            }
            else if(gamestate == GAMEOVER){
                if(minus_timer>0){
                    if(isVictory) lvictory.draw(g2);
                    else lcrash.draw(g2);
                    minus_timer--;
                } else gamestate = MENU;
            }
            else if(gamestate == PAUSE){
                g.setColor(Const.OPAQUE_DARK_COLOR);
                g.fillRect(0, Const.HALFHEIGHT-120, Const.WIDTH, 120);
                lpause.draw(g2);
            }
            else{
                if(minus_timer>0){
                    if(minus_timer%10>5) lminus.draw(g2);
                    minus_timer--;
                }
                if(message_timer>0){
                    lmessage.draw(g2);
                    message_timer--;
                }
            }
        }

        //
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }
}
