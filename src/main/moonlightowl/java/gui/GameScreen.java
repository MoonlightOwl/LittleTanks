package main.moonlightowl.java.gui;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.Mission;
import main.moonlightowl.java.gui.component.Label;
import main.moonlightowl.java.math.GMath;
import main.moonlightowl.java.sound.Sound;
import main.moonlightowl.java.sound.SoundManager;
import main.moonlightowl.java.world.FX;
import main.moonlightowl.java.world.Item;
import main.moonlightowl.java.world.Tile;
import main.moonlightowl.java.world.World;
import main.moonlightowl.java.world.entity.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
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
    private Label llifes, lshield, lscore, lammo,
    lmines, lminus, lmessage, lfreeze, lpause;
    // variables
    private int score;
    private int effectFreeze = 0;
    private int minus_timer = 0, message_timer = 0;
    private boolean paused = false;

    // levels
    private Mission mission;
    private int currentLevel = 0;

    public GameScreen(World world, Camera camera){
        super();
        setWorld(world);
        setCamera(camera);

        // init interface
        llifes = new Label("@@@@@", 20, Const.HEIGHT-40, Assets.fgui, Assets.fmgui, Color.WHITE); llifes.setShadow(true);
        lshield = new Label("", 20, Const.HEIGHT-80, Assets.fgui, Assets.fmgui, Color.WHITE); lshield.setShadow(true);
        lscore = new Label("0", 20, 50, Assets.fgui, Assets.fmgui, Color.WHITE); lscore.setShadow(true);
        lammo = new Label(">> 10", Const.WIDTH-140, Const.HEIGHT-40, Assets.fgui, Assets.fmgui, Color.WHITE); lammo.setShadow(true);
        lmines = new Label("== 0", Const.WIDTH-140, Const.HEIGHT-80, Assets.fgui, Assets.fmgui, Color.WHITE); lmines.setShadow(true);
        lfreeze = new Label("", Const.HALFWIDTH, 50, Assets.fsmall, Assets.fmsmall, Color.WHITE, true, Color.BLUE); lfreeze.setShadow(true);
        lpause = new Label("Pause...", Const.HALFWIDTH, Const.HALFHEIGHT-20, Assets.ftitle, Assets.fmtitle, Color.YELLOW, true, Color.BLACK); lpause.setShadow(true);
        lminus = new Label(":(", 40, Const.HEIGHT-80, Assets.fsmall, Assets.fmsmall, Color.RED, true); lminus.setShadow(true);
        lmessage = new Label("", Const.HALFWIDTH, Const.HEIGHT-80, Assets.fsmall, Assets.fmsmall, Color.BLACK, true, Color.RED); lmessage.setShadow(true);

        // init game
        player = new Tank();
        score = 0;

        mission = new Mission("./levels/");
        loadMission("test");
    }


    // getters
    public boolean isPaused(){ return paused; }
    public int getScore(){ return score; }
    public Mission getMission(){ return mission; }
    public boolean isVictory(){ return world.enemies.isEmpty(); }

    // setters
    public void setPaused(boolean paused){ this.paused = paused; }
    public void setSoundManager(SoundManager soundManager){ this.soundManager = soundManager; }
    public void setScore(int score){
        this.score = score;
        setScoreCounter(score);
    }
    private void changeScore(int amount){
        setScore(score + amount);
    }


    /** Manage levels */
    public boolean loadMission(String name){
        if(mission.load(name)){
            restartMission();
            return true;
        }
        else return false;
    }
    public void nextLevel(){
        currentLevel++;
        world.reset();
        world.loadLevel(mission.getLevel(currentLevel));

        // place player in the world
        player.reset();
        Point sp = world.level.getStartPoint();
        int x = GMath.toPixel(sp.x);
        int y = GMath.toPixel(sp.y);
        player.setPosition(x, y);

        // move camera to player
        int width = world.level.getPxWidth(), height = world.level.getPxHeight();
        camera.setBounds(width, height);
        camera.setPosition(width/2, height/2);

        // bonus points
        if(currentLevel > 1) {
            score += 10;
            addMessage("New level! (+10 score)", Const.MESSAGE_TIME);
        }

        //
        interfaceReset();
    }
    public void restartMission(){
        currentLevel = 0;
        nextLevel();
    }


    /** Processing interface */
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
        lmines.changeText("== "+Integer.toString(mines));
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
            //gameOver(false);
            setVisible(false);
        }
        else minus_timer = 100;
    }


    /** Actions */
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
                world.fx.add(px-20, py-20, FX.SMALLEXPLOSION);
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
                world.fx.add(px-20, py-20, FX.EXPLOSION);
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


    /** Event handling */
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            setVisible(false);
        }
        else if(!paused)
            switch(e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    player.turn(270);
                    moveTank(player, player.getX() - Const.TILE_SIZE, player.getY());
                    break;
                case KeyEvent.VK_RIGHT:
                    player.turn(90);
                    moveTank(player, player.getX() + Const.TILE_SIZE, player.getY());
                    break;
                case KeyEvent.VK_UP:
                    player.turn(0);
                    moveTank(player, player.getX(), player.getY() - Const.TILE_SIZE);
                    break;
                case KeyEvent.VK_DOWN:
                    player.turn(180);
                    moveTank(player, player.getX(), player.getY() + Const.TILE_SIZE);
                    break;
                case KeyEvent.VK_SPACE:
                    if(!fireTank(player)) soundManager.play(Sound.NOAMMO);
                    setAmmoCounter(player.getAmmo());
                    break;
                case KeyEvent.VK_C:
                    if(player.getBombs() > 0){
                        player.changeBombs(-1);
                        setMinesCounter(player.getBombs());
                        world.bombs.add(new Bomb(player.getX()+30, player.getY()+30));
                        soundManager.play(Sound.BEEP);
                    }
                    break;
                case KeyEvent.VK_P: setPaused(true); break;
                case KeyEvent.VK_E:
                    if(player.removeFromInventory(Item.CANDY)){
                        changeLife(1);
                        addMessage("+1 life", Const.MESSAGE_TIME);
                    }
                    break;
            }
        else if(e.getKeyCode() == KeyEvent.VK_P)
            setPaused(false);
    }


    // screen processing
    public void update(){
        // move camera to player position
        if(Sound.EXPLODE.isPlaying())
            camera.setPosition(player.getX() - 3 + GMath.rand.nextInt(6),
                               player.getY() - 3 + GMath.rand.nextInt(6));
        else camera.setPosition(player.getX(), player.getY());

        // messages
        if(message_timer > 0)
            message_timer--;

        // update game objects
        if(!paused) {
            // update player
            world.level.setCollision(player.getMapX(), player.getMapY(), false);
            player.update();
            world.level.setCollision(player.getMapX(), player.getMapY(), true);
            // effects
            if (effectFreeze > 0) {
                effectFreeze--;
                setFreezeCounter(effectFreeze);
            }
            // tracks =)
            if (GMath.rand.nextInt(60) == 0) {
                world.level.drawSplash(Assets.itrack, player.getTransform());
            }
            // add new projectiles
            synchronized (world.newBullet) {
                if (world.newBullet.size() > 0) {
                    for (Bullet bullet : world.newBullet) {
                        synchronized (world.bullets) {
                            world.bullets.add(bullet);
                        }
                    }
                    world.newBullet.clear();
                }
            }
            // add new tanks
            synchronized (world.newEnemies) {
                if (world.newEnemies.size() > 0) {
                    for (Tank enemy : world.newEnemies) {
                        synchronized (world.enemies) {
                            world.enemies.add(enemy);
                        }
                    }
                    world.newEnemies.clear();
                }
            }

            // scroll all enemies
            synchronized (world.enemies) {
                Iterator<Tank> itenemies = world.enemies.iterator();
                while (itenemies.hasNext()) {
                    Tank t = itenemies.next();
                    world.level.setCollision(t.getMapX(), t.getMapY(), false);
                    t.update();
                    world.level.setCollision(t.getMapX(), t.getMapY(), true);
                    // random movement
                    if (t.isIdle()) {
                        int action = GMath.rand.nextInt(effectFreeze == 0 ? 10 : 100);
                        if (action == 0) {
                            int dx = 0, dy = 0;
                            if (GMath.rand.nextBoolean()) dx = Const.TILE_SIZE * (GMath.rand.nextBoolean() ? -1 : 1);
                            else dy = Const.TILE_SIZE * (GMath.rand.nextBoolean() ? -1 : 1);
                            moveTank(t, t.getX() + dx, t.getY() + dy);
                            if (dx < 0) t.turn(270);
                            else if (dy < 0) t.turn(0);
                            else if (dx > 0) t.turn(90);
                            else if (dy > 0) t.turn(180);
                        } else if (action == 2) fireTank(t);
                    }
                    // bullet collision
                    synchronized (world.bullets) {
                        Iterator<Bullet> itbullets = world.bullets.iterator();
                        while (itbullets.hasNext()) {
                            Bullet b = itbullets.next();
                            if (world.level.getCollision((int) (b.getX() / Const.TILE_SIZE), (int) (b.getY() / Const.TILE_SIZE))) {
                                if (GMath.distance(b.getX(), b.getY(), t.getX() + 30, t.getY() + 30) < 25) {
                                    if (t.hit(b.getLevel())) soundManager.play(Sound.HIT);
                                    itbullets.remove();
                                    // score points
                                    int bonus = GMath.rand.nextInt(50);
                                    changeScore(bonus);
                                }
                            }
                        }
                    }
                    // mine collision
                    synchronized (world.bombs) {
                        Iterator<Bomb> itmines = world.bombs.iterator();
                        while (itmines.hasNext()) {
                            Bomb m = itmines.next();
                            if (GMath.distance(m.getX(), m.getY(), t.getX() + 30, t.getY() + 30) < 30) {
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
                    if (t.getLife() <= 0) {
                        world.level.setCollision(t.getMapX(), t.getMapY(), false);
                        world.level.drawSplash(Assets.iexpldec, t.getX() - 20, t.getY() - 20);
                        world.fx.add(t.getX() - 20, t.getY() - 20, FX.EXPLOSION);
                        // score points
                        int bonus = GMath.rand.nextInt(100) * t.getLevel() + 10;
                        changeScore(bonus);
                        addMessage("+" + bonus + " score", Const.MESSAGE_TIME);
                        // game over, man, it's over
                        soundManager.play(Sound.EXPLODE);
                        itenemies.remove();
                    }
                }
                // if all enemies were destroyed - we are champions!
                if (world.enemies.isEmpty()) {
                    if (currentLevel >= mission.getLength()) {
                        setVisible(false);
                    } else {
                        nextLevel();
                    }
                }
            }

            // scroll throught all the bullets
            synchronized (world.bullets) {
                Iterator<Bullet> itbullets = world.bullets.iterator();
                while (itbullets.hasNext()) {
                    Bullet b = itbullets.next();

                    // rocket smoke trail
                    if (b.getLevel() == Tank.LAUNCHER && GMath.rand.nextBoolean())
                        world.fx.add((int) b.getX() - 50 + GMath.rand.nextInt(40),
                                (int) b.getY() - 50 + GMath.rand.nextInt(40), FX.SMOKE);

                    b.update();

                    // check player collision
                    if (GMath.distance(b.getX(), b.getY(),
                            player.getX() + Const.HALF_TILE,
                            player.getY() + Const.HALF_TILE) < Const.HALF_TILE) {
                        itbullets.remove();

                        if (player.getShield() > 0) changeShield(-b.getLevel());
                        else {
                            minusLife(b.getLevel());
                            soundManager.play(Sound.HIT);
                        }
                        if (player.getLife() <= 0) world.fx.add(player.getX() - 20, player.getY() - 20, FX.EXPLOSION);
                    }
                    // check level collision
                    else {
                        int x = GMath.toMap((int) b.getX()), y = GMath.toMap((int) b.getY());
                        if (!world.level.isFlyable(x, y)) {
                            // crush! destroy! swag!
                            Tile tile = world.level.get(x, y);
                            int px = (int) b.getX(), py = (int) b.getY();
                            switch (tile.get()) {
                                case Tile.BOX:
                                case Tile.BARREL:
                                    activateMapTile(x, y);
                                    break;
                                case Tile.SANDSTONE:
                                    if (tile.getStage() > 0) {
                                        if (GMath.rand.nextBoolean()) {
                                            world.level.setStage(x, y, tile.getStage() - b.getLevel());
                                        }
                                    }
                                    break;
                                case Tile.DOOR:
                                    if (tile.getStage() > 0 && tile.getStage() < 5) {
                                        world.level.setStage(x, y, tile.getStage() - b.getLevel());
                                    }
                                    break;
                                case Tile.SAFE:
                                    if (tile.getStage() == 0) {
                                        world.level.clear(x, y);
                                        world.level.drawSplash(Assets.iexpldec, px - 20, py - 20);
                                        synchronized (world.bonuses) {
                                            world.bonuses.add(
                                                    new Bonus(px + 30, py + 30, GMath.rand.nextInt(Bonus.COUNT)));
                                        }
                                        world.fx.add(px - 20, py - 20, FX.SMALLEXPLOSION);
                                    }
                                    break;
                            }
                            // bullet/rocket gone in sparkles/explosion
                            switch (b.getLevel()) {
                                case 1:
                                case 2:
                                    world.fx.add(px - (int) Math.sin(b.getAngle()) * 30,
                                            py + (int) Math.cos(b.getAngle()) * 30, FX.SPARKLE);
                                    break;
                                case 3:
                                    world.fx.add((int) b.getX() - 50, (int) b.getY() - 50, FX.EXPLOSION);
                                    soundManager.play(Sound.EXPLODE);
                                    break;
                            }
                            itbullets.remove();
                        }
                    }
                }
            }
            synchronized (world.bonuses) {
                Iterator<Bonus> itbonuses = world.bonuses.iterator();
                while (itbonuses.hasNext()) {
                    Bonus s = itbonuses.next();
                    s.update();
                    if (s.contains(player.getX() + 30, player.getY() + 30)) {
                        // hack! slash! loot!
                        switch (s.getType()) {
                            case Bonus.LIFE:
                                changeLife(1);
                                addMessage("+1 life", Const.MESSAGE_TIME);
                                break;
                            case Bonus.AMMO:
                                player.setAmmo(player.getAmmo() + 10);
                                setAmmoCounter(player.getAmmo());
                                addMessage("+10 ammo", Const.MESSAGE_TIME);
                                break;
                            case Bonus.SCORE:
                                int bonus = GMath.rand.nextInt(50);
                                changeScore(bonus);
                                addMessage("+" + bonus + " score", Const.MESSAGE_TIME);
                                break;
                            case Bonus.MINE:
                                player.changeBombs(2);
                                setMinesCounter(player.getBombs());
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
                                    level = GMath.rand.nextInt(Tank.MAX_LEVEL) + 1;
                                } while (level == player.getLevel());
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

            synchronized (world.items) {
                Iterator<Item> ititems = world.items.iterator();
                while (ititems.hasNext()) {
                    Item i = ititems.next();
                    i.update();
                    // collect items to player inventory
                    if (i.contains(player.getX() + Const.HALF_TILE, player.getY() + Const.HALF_TILE)) {
                        player.inventory.add(i);
                        soundManager.play(Sound.PICKUP);
                        ititems.remove();
                    }
                }
            }

            synchronized (world.turrets) {
                Iterator<Turret> itturrets = world.turrets.iterator();
                while (itturrets.hasNext()) {
                    Turret t = itturrets.next();

                    // shoot and crush!
                    if (GMath.distance(player.getX(), player.getY(), t.getX(), t.getY()) < Turret.DETECT_RADIUS) {
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
                    synchronized (world.bullets) {
                        Iterator<Bullet> itbullets = world.bullets.iterator();
                        while (itbullets.hasNext()) {
                            Bullet b = itbullets.next();
                            if (GMath.toMap((int) b.getX()) == t.getMapX() &&
                                    GMath.toMap((int) b.getY()) == t.getMapY()) {
                                // hit!
                                t.changeLife(-1);
                                if (t.getLife() == 0) {
                                    itturrets.remove();
                                    // explode!
                                    world.fx.add((int) b.getX() - 50, (int) b.getY() - 50, FX.EXPLOSION);
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
        }
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
        // message
        if(message_timer > 0)
            lmessage.draw(g);
        // inventory
        Iterator<Item> ititems = player.inventory.iterator();
        int x = Const.WIDTH - 80;
        while(ititems.hasNext()){
            Item i = ititems.next();
            i.drawIcon(g, x, 10);
            x-=30;
        }

        // "game paused" bar
        if(paused){
            g.setColor(Const.OPAQUE_DARK_COLOR);
            g.fillRect(0, Const.HALFHEIGHT-120, Const.WIDTH, 120);
            lpause.draw(g);
        }
    }
}
