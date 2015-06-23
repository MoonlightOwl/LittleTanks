package main.moonlightowl.java.gui;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.Mission;
import main.moonlightowl.java.Ruleset;
import main.moonlightowl.java.gui.component.Popup;
import main.moonlightowl.java.math.GMath;
import main.moonlightowl.java.math.Point3D;
import main.moonlightowl.java.script.Script;
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
import java.awt.geom.Point2D;
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
    private HUD hud;
    private Popup textTrigger;
    // variables
    private int score;
    private int effectFreeze = 0;
    private int track_frequency = 20;
    private long enemySpawnTime = 0;
    private boolean paused = false;
    // levels
    private Mission mission;
    private int currentLevel = 0;

    public GameScreen(World world, Camera camera){
        super();
        setWorld(world);
        setCamera(camera);

        // init sripting system
        Script.init(this);

        // init game
        player = new Tank();
        score = 0;
        hud = new HUD(player);
        textTrigger = new Popup("Hello World!",
                Const.HALFWIDTH, Const.HALFHEIGHT + Const.TILE_SIZE + 20,
                Assets.fsmall, Assets.fmsmall);

        mission = new Mission("./levels/");
        loadMission("tutor");
    }


    /** Getters */
    public boolean isPaused(){ return paused; }
    public int getScore(){ return score; }
    public Mission getMission(){ return mission; }
    public boolean isVictory(){ return world.enemies.isEmpty(); }
    public boolean isGameOver(){
        return isVictory() || player.getLife() <= 0;
    }

    /** Setters */
    public void setPaused(boolean paused){ this.paused = paused; }
    public void setSoundManager(SoundManager soundManager){ this.soundManager = soundManager; }


    /** Manage levels */
    public boolean loadMission(String name){
        if(mission.load(name)){
            restartMission();
            return true;
        }
        else return false;
    }
    public void nextLevel(){
        interfaceReset();

        currentLevel++;
        world.reset();
        world.loadLevel(mission.getLevel(currentLevel));

        // load scripts if available
        String scriptfile = mission.getScript(currentLevel);
        if(scriptfile != null){
            Script.loadScript(scriptfile);
            Script.runInit();
        }

        // parameters
        track_frequency = world.level.isSnowy() ? 5 : 20;
        enemySpawnTime = System.currentTimeMillis();

        // place player in the world
        player.reset();
        Point sp = world.level.getStartPoint();
        int x = GMath.toPixel(sp.x);
        int y = GMath.toPixel(sp.y);
        player.setPosition(x, y);
        player.setStateTo(world.level.getStartState());

        // check if there was text message for us
        checkTextTrigger();

        // move camera to player
        int width = world.level.getPxWidth(), height = world.level.getPxHeight();
        camera.setBounds(width, height);
        camera.setPosition(width/2, height/2);

        // bonus points
        if(currentLevel > 1) {
            score += 10;
            hud.addMessage("New level! (+10 score)", Const.MESSAGE_TIME);
        }
    }
    public void restartMission(){
        setScore(0);
        currentLevel = 0;
        nextLevel();
    }


    /** Processing interface */
    public void interfaceReset(){
        hud.reset();
        effectFreeze = 0;
        hud.setLifeCounter(player.getLife());
        hud.setShieldCounter(player.getShield());
        hud.setAmmoCounter(player.getAmmo());
        hud.setBombsCounter(player.getBombs());
        hud.setFreezeCounter(effectFreeze);
    }
    public void setScore(int score){
        this.score = score;
        hud.setScoreCounter(score);
    }
    private void changeScore(int amount){
        setScore(score + amount);
    }
    public void changeScore(int score, String message){
        changeScore(score);
        hud.addMessage(message);
    }
    private void changeLife(int l){
        player.setLife(player.getLife() + l);
        hud.setLifeCounter(player.getLife());
    }
    private void changeShield(int shield){
        int amount = (player.getShield() + shield <= Tank.State.SHIELD_LIMIT ?
                shield : Tank.State.SHIELD_LIMIT - player.getShield());
        player.setShield(player.getShield() + amount);
        hud.setShieldCounter(player.getShield());
    }
    private void minusLife(int amount){
        changeScore(-50*amount, "hit!");
        changeLife(-amount);

        if(player.getLife() <= 0) setVisible(false);
        else hud.minusLife();
    }


    /** Actions */
    public void moveTank(Tank tank, int x, int y){
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
    public boolean fireTank(Tank tank){
        if(tank.isIdle()){
            if(tank.getAmmo()>0){
                int x = tank.getX()+Const.HALF_TILE, y = tank.getY()+Const.HALF_TILE;
                Point2D.Float delta = GMath.getVector(tank.getAngle());
                int dx = (int)(delta.x * Const.HALF_TILE),
                    dy = (int)(delta.y * Const.HALF_TILE);
                if(tank.getLevel() < Tank.LASER) {
                    Bullet bullet = new Bullet(x + dx, y + dy, delta.x, delta.y, tank.getLevel());
                    bullet.setFromPlayer(tank == player);
                    world.newBullet.add(bullet);
                }
                else if(tank.getLevel() == Tank.LASER){
                    // calculate target point
                    int tx = tank.getMapX(), ty = tank.getMapY();
                    while(world.level.isFlyable(tx, ty)){
                        tx += Math.signum(dx);
                        ty += Math.signum(dy);
                    }
                    activateMapTile(tx, ty);
                    LaserBeam beam = new LaserBeam(tank.getMapX(), tank.getMapY(), tx, ty);
                    beam.setFromPlayer(tank == player);
                    world.beams.add(beam);
                    for(int i=0; i<4; i++)
                        world.fx.add(tx*Const.TILE_SIZE-10+GMath.rand.nextInt(Const.HALF_TILE),
                                     ty*Const.TILE_SIZE-10+GMath.rand.nextInt(Const.HALF_TILE), FX.SMOKE);
                }
                // play sound
                if(tank == player || GMath.rand.nextBoolean()){
                    switch(tank.getLevel()){
                        case Tank.GUNFIGHTER: case Tank.BIGCALIBRE:
                            soundManager.play(Sound.SHOOT); break;
                        case Tank.LAUNCHER:
                            soundManager.play(Sound.LAUNCH);
                            for(int i=0; i<3; i++)
                                world.fx.add(x-dx-40+GMath.rand.nextInt(10),
                                             y-dy-40+GMath.rand.nextInt(10), FX.SMOKE);
                            break;
                        case Tank.LASER: soundManager.play(Sound.LASER); break;
                    }
                }
                // decrease ammo
                tank.changeAmmo(-1);
                // gun kick
                tank.fire();
            } else return false;
        }
        return true;
    }
    private void activateMapTile(int x, int y){
        activateMapTile(x, y, 0);
    }
    private void activateMapTile(int x, int y, int bullet){
        Tile tile = world.level.get(x, y);
        int px = GMath.toPixel(x), py = GMath.toPixel(y);
        switch(tile.get()){
            case Tile.DOOR:
                // bullet can open only unlocked doors
                if(bullet > 0){
                    if(tile.getStage() > 0 && tile.getStage() < 5)
                        world.level.setStage(x, y, tile.getStage() - bullet);
                }
                // switches can unlock & open doors
                else if(tile.getStage() == 5){
                    world.level.setStage(x, y, 0);
                    soundManager.play(Sound.LOCK);
                }
                break;
            case Tile.SAFE:
                // bullet can open only unlocked safes
                if (bullet > 0 && tile.getStage() != 0) break;
            case Tile.BOX:
                world.level.clear(x, y);
                world.level.drawSplash(Assets.iexpldec, px-20, py-20);
                synchronized(world.bonuses){
                    world.bonuses.add(new Bonus(px+Const.HALF_TILE, py+Const.HALF_TILE,
                            GMath.rand.nextInt(Bonus.COUNT)));
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
                    world.spawnRandomTank(px, py, stage);
                }
                break;
            case Tile.SANDSTONE:
                if (tile.getStage() > 0) {
                    if (bullet > 0) {
                        if(GMath.rand.nextBoolean())
                            world.level.setStage(x, y, tile.getStage() - bullet);
                    } else {
                        world.level.setStage(x, y, 0);
                    }
                }
                break;
        }
    }
    private void checkTextTrigger(){
        textTrigger.setVisible(false);
        for(PopupMessage popup: world.messages){
            if(popup.compareXY(player.getMapX(), player.getMapY())){
                textTrigger.setMessage(popup.getText());
                textTrigger.setVisible(true);
                break;
            }
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
                    player.turn(Math.PI);
                    moveTank(player, player.getX() - Const.TILE_SIZE, player.getY());
                    break;
                case KeyEvent.VK_RIGHT:
                    player.turn(0.0);
                    moveTank(player, player.getX() + Const.TILE_SIZE, player.getY());
                    break;
                case KeyEvent.VK_UP:
                    player.turn(Math.PI+Math.PI/2);
                    moveTank(player, player.getX(), player.getY() - Const.TILE_SIZE);
                    break;
                case KeyEvent.VK_DOWN:
                    player.turn(Math.PI/2);
                    moveTank(player, player.getX(), player.getY() + Const.TILE_SIZE);
                    break;
                case KeyEvent.VK_SPACE:
                    if(!fireTank(player)) soundManager.play(Sound.NOAMMO);
                    hud.setAmmoCounter(player.getAmmo());
                    break;
                case KeyEvent.VK_C:
                    if(player.getBombs() > 0){
                        player.changeBombs(-1);
                        hud.setBombsCounter(player.getBombs());
                        world.bombs.add(new Bomb(player.getX()+30, player.getY()+30));
                        soundManager.play(Sound.BEEP);
                    }
                    break;
                case KeyEvent.VK_P: setPaused(true); break;
                case KeyEvent.VK_E:
                    if(player.removeFromInventory(Item.CANDY)){
                        changeLife(1);
                        hud.addMessage("Mmm... Yummy!");
                    }
                    break;
            }
        else if(e.getKeyCode() == KeyEvent.VK_P)
            setPaused(false);
    }


    /** Screen processing */
    public void update(){
        // UI
        hud.update();

        // update game objects
        if(!paused) {
            // update special effects
            if(world.level.isSnowy()){
                if(GMath.rand.nextInt(20) == 1){
                    world.fx.add(GMath.rand.nextInt(Const.WIDTH),
                            GMath.rand.nextInt(Const.HEIGHT), FX.SNOWFLAKE);
                }
            }
            // spawn reinforcements
            if(world.level.enemyRespawnEnabled()){
                if(System.currentTimeMillis() - enemySpawnTime > world.level.getEnemyRespawnTime()){
                    int n = GMath.rand.nextInt(world.spawners.size());
                    Point3D spawner = world.spawners.get(n);
                    world.spawnRandomTank(GMath.toPixel(spawner.x),
                                          GMath.toPixel(spawner.y), spawner.z);
                    enemySpawnTime = System.currentTimeMillis();
                }
            }
            // update player
            int pcx = player.getMapX(), pcy = player.getMapY();
            player.update();
            world.level.setCollision(player.getMapX(), player.getMapY(), true);
            if(pcx != player.getMapX() || pcy != player.getMapY()) {
                world.level.setCollision(pcx, pcy, false);
                checkTextTrigger();
            }
            // move camera to player position
            if(Sound.EXPLODE.isPlaying())
                camera.setPosition(player.getX() - 3 + GMath.rand.nextInt(6) + Const.HALF_TILE,
                        player.getY() - 3 + GMath.rand.nextInt(6) + Const.HALF_TILE);
            else camera.setPosition(player.getX() + Const.HALF_TILE, player.getY() + Const.HALF_TILE);
            // effects
            if(effectFreeze > 0) {
                effectFreeze--;
                hud.setFreezeCounter(effectFreeze);
                // unfreeze all the enemies
                if(effectFreeze == 0){
                    synchronized (world.enemies){
                        for(Tank tank: world.enemies) tank.unfreeze();
                    }
                }
            }
            // tracks =)
            if(!player.isIdle() && GMath.rand.nextInt(track_frequency) == 0) {
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
                    // update & collision
                    pcx = t.getMapX(); pcy = t.getMapY();
                    t.update();
                    world.level.setCollision(t.getMapX(), t.getMapY(), true);
                    // if tank moved to an another tile
                    if(pcx != t.getMapX() || pcy != t.getMapY()) {
                        world.level.setCollision(pcx, pcy, false);
                    }
                    // scripted tank update
                    if (t.isIdle()) {
                        Script.runUpdateTank(t);
                    }
                    // bullet collision
                    synchronized (world.bullets) {
                        Iterator<Bullet> itbullets = world.bullets.iterator();
                        while (itbullets.hasNext()) {
                            Bullet b = itbullets.next();
                            if (world.level.getCollision((int) (b.getX() / Const.TILE_SIZE), (int) (b.getY() / Const.TILE_SIZE))) {
                                if (world.level.friendlyFireEnabled() || b.isFromPlayer())
                                    if (GMath.distance(b.getX(), b.getY(), t.getX() + 30, t.getY() + 30) < 25) {
                                        if (t.hit(b.getLevel())){
                                            soundManager.play(Sound.HIT);
                                            changeScore(Ruleset.score(Ruleset.HIT_SCORE));
                                        }
                                        itbullets.remove();
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
                                changeScore(Ruleset.score(Ruleset.BOMB_KILL_SCORE), "bomb kill!");
                            }
                        }
                    }
                    // laser collision
                    synchronized (world.beams){
                        for(LaserBeam l: world.beams){
                            for(Point p: l.getRay()){
                                if(world.level.friendlyFireEnabled() || l.isFromPlayer())
                                    if(t.getMapX() == p.x && t.getMapY() == p.y){
                                        if (t.hit(GMath.rand.nextInt(2))){
                                            soundManager.play(Sound.HIT);
                                            changeScore(Ruleset.score(Ruleset.HIT_SCORE));
                                        }
                                        break;
                                    }
                            }
                        }
                    }
                    // explosion
                    if (t.getLife() <= 0) {
                        world.level.setCollision(t.getMapX(), t.getMapY(), false);
                        //world.level.drawSplash(Assets.iexpldec, t.getX() - 20, t.getY() - 20);
                        AffineTransform at = t.getTransform();
                        at.rotate(GMath.rand.nextDouble() * Math.PI * 2.0, 50, 50);
                        world.level.drawSplash(Assets.iexpldec, at);
                        world.fx.add(t.getX() - 20, t.getY() - 20, FX.EXPLOSION);
                        // score points
                        int bonus = Ruleset.score(Ruleset.KILL_SCORE) * t.getLevel() + 10;
                        changeScore(bonus, "+" + bonus + " score");
                        // drop random bonus
                        if(GMath.rand.nextInt(100) < Const.BONUS_DROP_CHANCE){
                            synchronized(world.bonuses){
                                world.bonuses.add(new Bonus(t.getX()+Const.HALF_TILE,
                                        t.getY()+Const.HALF_TILE,
                                        GMath.rand.nextInt(Bonus.COUNT)));
                            }
                        }
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
                        if (player.getLife() <= 0){
                            world.fx.add(player.getX() - 20, player.getY() - 20, FX.EXPLOSION);
                            AffineTransform at = player.getTransform();
                            at.rotate(GMath.rand.nextDouble() * Math.PI * 2.0, 50, 50);
                            world.level.drawSplash(Assets.iexpldec, at);
                        }
                    }
                    // check level collision
                    else {
                        int x = GMath.toMap((int) b.getX()), y = GMath.toMap((int) b.getY());
                        if (!world.level.isFlyable(x, y)) {
                            // crush! destroy! swag!
                            activateMapTile(x, y, b.getLevel());
                            // bullet/rocket gone in sparkles/explosion
                            int px = x * Const.TILE_SIZE,
                                py = y * Const.TILE_SIZE;
                            switch (b.getLevel()) {
                                case 1:
                                case 2:
                                    world.fx.add(px - (int) Math.sin(b.getAngle()) * Const.HALF_TILE,
                                            py + (int) Math.cos(b.getAngle()) * Const.HALF_TILE, FX.SPARKLE);
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
                                hud.addMessage("+1 life");
                                break;
                            case Bonus.AMMO:
                                player.setAmmo(player.getAmmo() + 10);
                                hud.setAmmoCounter(player.getAmmo());
                                hud.addMessage("+10 ammo");
                                break;
                            case Bonus.SCORE:
                                int bonus = Ruleset.score(Ruleset.BONUS_SCORE);
                                changeScore(bonus, "+" + bonus + " score");
                                break;
                            case Bonus.BOMB:
                                player.changeBombs(Ruleset.BONUS_BOMBS);
                                hud.setBombsCounter(player.getBombs());
                                hud.addMessage("+"+ Ruleset.BONUS_BOMBS +" bombs");
                                break;
                            case Bonus.FREEZE:
                                effectFreeze += Ruleset.BONUS_FREEZE;
                                hud.addMessage("slow down");
                                soundManager.play(Sound.FREEZE);
                                // freeze all the enemies
                                synchronized (world.enemies) {
                                    for (Tank tank : world.enemies) tank.freeze(Ruleset.FREEZE_COEF);
                                }
                                break;
                            case Bonus.POWER:
                                int level;
                                do {
                                    level = GMath.rand.nextInt(Tank.MAX_LEVEL) + 1;
                                } while (level == player.getLevel());
                                player.setLevel(level);
                                hud.addMessage("random power");
                                break;
                            case Bonus.SHIELD:
                                changeShield(Ruleset.BONUS_SHIELD);
                                hud.addMessage("shields up");
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
                                double rifle = GMath.rand.nextBoolean() ? -0.25 : 0.25,
                                        dx = Math.cos(t.getAngle() - Math.PI / 2 + rifle) * 40,
                                        dy = Math.sin(t.getAngle() - Math.PI / 2 + rifle) * 40;
                                world.newBullet.add(
                                        new Bullet(t.getX() + Const.HALF_TILE + (int) dx,
                                                t.getY() + Const.HALF_TILE + (int) dy,
                                                (float) (dx / 20), (float) (dy / 20)));
                                t.fire(rifle < 0);
                            }
                        }
                    } else t.update(false);

                    // bullet collision
                    boolean removed = false;
                    synchronized (world.bullets) {
                        Iterator<Bullet> itbullets = world.bullets.iterator();
                        while (itbullets.hasNext()) {
                            Bullet b = itbullets.next();
                            if (GMath.toMap((int) b.getX()) == t.getMapX() &&
                                    GMath.toMap((int) b.getY()) == t.getMapY()) {
                                // hit!
                                t.changeLife(-1);
                                if (t.getLife() <= 0) {
                                    itturrets.remove();
                                    removed = true;
                                    // explode!
                                    world.fx.add((int) b.getX() - 50, (int) b.getY() - 50, FX.EXPLOSION);
                                    soundManager.play(Sound.EXPLODE);
                                }
                                itbullets.remove();
                                changeScore(Ruleset.score(Ruleset.HIT_SCORE));

                                if(removed) break;
                            }
                        }
                    }
                    if(!removed) {
                        // laser collision
                        synchronized (world.beams) {
                            for (LaserBeam l : world.beams) {
                                for (Point p : l.getRay()) {
                                    if (t.getMapX() == p.x && t.getMapY() == p.y) {
                                        t.changeLife(-GMath.rand.nextInt(2));
                                        if (t.getLife() <= 0) {
                                            itturrets.remove();
                                            removed = true;
                                            // explode!
                                            world.fx.add(t.getX() - 20, t.getY() - 20, FX.EXPLOSION);
                                            soundManager.play(Sound.EXPLODE);
                                        }
                                        changeScore(Ruleset.score(Ruleset.HIT_SCORE));
                                        break;
                                    }
                                }
                                if(removed) break;
                            }
                        }
                    }
                }
            }

            synchronized (world.beams) {
                Iterator<LaserBeam> itbeams = world.beams.iterator();
                while (itbeams.hasNext()) {
                    LaserBeam l = itbeams.next();
                    l.update();
                    //
                    if (l.getTimeRemaining() <= 0) {
                        itbeams.remove();
                    } else {
                        // check player collision
                        for(Point point: l.getRay()){
                            if(point.x == player.getMapX() && point.y == player.getMapY()){
                                if(GMath.rand.nextBoolean()) {
                                    if (player.getShield() > 0) changeShield(GMath.rand.nextInt(2));
                                    else {
                                        minusLife(GMath.rand.nextInt(2));
                                        soundManager.play(Sound.HIT);
                                    }
                                    if (player.getLife() <= 0)
                                        world.fx.add(player.getX() - 20, player.getY() - 20, FX.EXPLOSION);
                                }
                                break;
                            }
                        }
                        // a bit of smoke
                        if(GMath.rand.nextBoolean()){
                            Point point = l.getRandomPoint();
                            if(point != null)
                                world.fx.add(point.x*Const.TILE_SIZE-10+GMath.rand.nextInt(Const.HALF_TILE),
                                        point.y*Const.TILE_SIZE-10+GMath.rand.nextInt(Const.HALF_TILE), FX.SMOKE);
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
        hud.draw(g);
        if(paused){ hud.drawPaused(g); }

        textTrigger.draw(g);
    }
}
