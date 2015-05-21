package main.moonlightowl.java;

/*
 * LittleTanks  ~  v0.3  ~  MoonlightOwl  ~  The main class of the Board element.
 */

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.Toolkit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Random;
import java.util.NoSuchElementException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.io.File;

import java.lang.*;

public class Board extends JPanel implements ActionListener{
	// gamestate constants
	public static final int MENU = 0, GAME = 1, NICKNAME = 2, GAMEOVER = 3, PAUSE = 4;
	// global objects
    private Random rand = new Random(System.currentTimeMillis());
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
    private Level level;
    private Tank player;
	
	private Point camera, camtarget;
	private Point2D.Double camposition;
	private double camangle;
	private static final double CAM_TURN_SPEED = 1.0, CAM_MOVE_SPEED = 0.2;
	
	private final LinkedList<Bullet> bullets, newBullet;
	private final ArrayList<Bonus> bonuses;
	private final ArrayList<Tank> enemies, newEnemies;
	private final ArrayList<Mine> mines;
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
		soundManager = new SoundManager();
	
		// set event listeners
        addKeyListener(new KAdapter());
        addMouseListener(new MAdapter());
        addMouseMotionListener(new MMAdapter());
        setFocusable(true);

        // init variables
        setDoubleBuffered(false);
		System.setProperty("sun.java2d.opengl", "True");

		// graphics
		camera = new Point(0, 0);
		camtarget = new Point(0, 0);
		camposition = new Point2D.Double(0, 0);
		camangle = 0.0;

		// intefrace
        title = new Label("LittleTanks", Const.WIDTH/2, 140, Assets.ftitle, Assets.fmtitle, Const.TITLE_COLOR, true); title.setShadow(true);
        menu = new Menu(Assets.fmenu, Assets.fmmenu, Const.WIDTH/2, Const.HEIGHT/2-60, 70);
        menu.addItem("< level >"); menu.addItem("Fight!"); menu.addItem("Scores"); menu.addItem("About"); menu.addItem("Quit");
        llifes = new Label("@@@@@", 20, Const.HEIGHT-40, Assets.fgui, Assets.fmgui, Color.WHITE); llifes.setShadow(true);
		lshield = new Label("", 20, Const.HEIGHT-80, Assets.fgui, Assets.fmgui, Color.WHITE); lshield.setShadow(true);
        lscore = new Label("0", 20, 50, Assets.fgui, Assets.fmgui, Color.WHITE); lscore.setShadow(true);
		lammo = new Label(">> 10", Const.WIDTH-140, Const.HEIGHT-40, Assets.fgui, Assets.fmgui, Color.WHITE); lammo.setShadow(true);
		lmines = new Label("@@ 0", Const.WIDTH-140, Const.HEIGHT-80, Assets.fgui, Assets.fmgui, Color.WHITE); lmines.setShadow(true);
		lfreeze = new Label("", Const.WIDTH/2, 50, Assets.fsmall, Assets.fmsmall, Color.WHITE, true, Color.BLUE); lfreeze.setShadow(true);
        lcrash = new Label("Game OVER", Const.WIDTH/2, Const.HEIGHT/2-20, Assets.ftitle, Assets.fmtitle, Color.RED, true); lcrash.setShadow(true);
		lvictory = new Label("Victory!", Const.WIDTH/2, Const.HEIGHT/2-20, Assets.ftitle, Assets.fmtitle, Color.GREEN, true, new Color(0, 50, 10)); lvictory.setShadow(true);
		lpause = new Label("Pause...", Const.WIDTH/2, Const.HEIGHT/2-20, Assets.ftitle, Assets.fmtitle, Color.YELLOW, true, Color.BLACK); lpause.setShadow(true);
        lminus = new Label(":(", 40, Const.HEIGHT-80, Assets.fsmall, Assets.fmsmall, Color.RED, true); lminus.setShadow(true);
        lmessage = new Label("", Const.WIDTH/2, Const.HEIGHT-80, Assets.fsmall, Assets.fmsmall, Color.BLACK, true, Color.RED); lmessage.setShadow(true);

        about = new About();
        about.addLine("Experimental project (Java, Swing)", Const.WIDTH/2, 250, Assets.fsmall, Assets.fmsmall, Color.WHITE, true, true);
        about.addLine("Programmer: MoonlightOwl", Const.WIDTH/2, 290, Assets.fgui, Assets.fmgui, Color.WHITE, true, true);
        about.addLine("BetaTester: Polina", Const.WIDTH/2, 330, Assets.fgui, Assets.fmgui, Color.WHITE, true, true);
        about.addLine("Contact:", Const.WIDTH/2, 400, Assets.fsmall, Assets.fmsmall, Color.WHITE, true, true);
        about.addLine("murky.owl@gmail.com", Const.WIDTH/2, 440, Assets.fgui, Assets.fmgui, Color.CYAN, true, true);
        about.addLine("20 April 2015", Const.WIDTH/2, 480, Assets.fgui, Assets.fmgui, Color.WHITE, true, true);
        about.addLine("Neverland", Const.WIDTH/2, 520, Assets.fgui, Assets.fmgui, Color.WHITE, true, true);

		// game variables
		gamestate = MENU;
		player = new Tank();
		// no "diamonds" for 1.6 back compatibility
		enemies = new ArrayList<Tank>(); newEnemies = new ArrayList<Tank>();
		bullets = new LinkedList<Bullet>(); newBullet = new LinkedList<Bullet>(); 
		bonuses = new ArrayList<Bonus>();
		mines = new ArrayList<Mine>();
		loadLevel("levels/test1.dat");
		
		newCameraTarget();
		
		setAmmoCounter(player.getAmmo());
        scores = new Scores("scores/scores.txt", Assets.fgui, Assets.fmgui);
        nickname = new Query("Enter your nick name:", Const.WIDTH/2, 400, Assets.fgui, Assets.fmgui, Color.WHITE);
		packagename = new Query("Enter package name:", Const.WIDTH/2, 400, Assets.fgui, Assets.fmgui, Color.WHITE);
		loadPackage("level");

		// PLAY!
        Timer timer = new Timer(20, this);
		timer.setInitialDelay(500);
		timer.start();
        //timer.scheduleAtFixedRate(new ScheduleTask(), 1000, 20);  // old threads model
        //timer.scheduleAtFixedRate(new DrawShedule(), 1010, 40);
		music = new Music("./resources/music/");
		if(playMusic) music.play();
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
		music.stop();
        scores.saveScores();
		fx.dispose();
		soundManager.close();
        JFrame.getFrames()[0].dispose();
        System.exit(1);
    }
	public void resetGame(){
		resetGame("levels/test1.dat");
	}
    public void resetGame(String filename) {
		loadLevel(filename);
        score = 0;
        setLifeCounter(player.getLife());
		setShieldCounter(player.getShield());
		setScoreCounter(score);
		setAmmoCounter(player.getAmmo());
		setMinesCounter(player.getMines());
		synchronized(bullets){ bullets.clear(); }
		synchronized(newBullet){ newBullet.clear(); }
		synchronized(bonuses){ bonuses.clear(); }
		synchronized(mines){ mines.clear(); }
        minus_timer = 0;
		message_timer = 0;
		effectFreeze = 0;
    }

	public void nextLevel(){
		levelnum++;
		loadLevel("levels/"+levelpackage+levelnum+".dat");
		if(levelnum > 1) score += 10;
		addMessage("New level! (+10 score)", 100);
		setLifeCounter(player.getLife());
		setShieldCounter(player.getShield());
		setScoreCounter(score);
		setAmmoCounter(player.getAmmo());
		setMinesCounter(player.getMines());
		synchronized(bullets){ bullets.clear(); }
		synchronized(newBullet){ newBullet.clear(); }
		synchronized(bonuses){ bonuses.clear(); }
		synchronized(mines){ mines.clear(); }
        minus_timer = 0;

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
		level = new Level(filename);
		Point sp = level.getStartPoint();
		player.reset();
		int x = sp.x * Level.TILE_SIZE;
		int y = sp.y * Level.TILE_SIZE;
		player.setPosition(x, y);
		setCamera(x, y);
		// load enemy tanks
		synchronized(enemies){ enemies.clear(); }
		synchronized(newEnemies){ newEnemies.clear(); }

		for(Point3D spawner: level.getSpawners()){
			Tank enemy = new Tank(spawner.x*Level.TILE_SIZE, spawner.y*Level.TILE_SIZE, spawner.z);
			enemy.setAmmo(1000);
			enemies.add(enemy);
		}
		// create new FX map
		fx = new FX(level.getPxWidth(), level.getPxHeight());
	}
	
	private void setCamera(int x, int y){
		camera.x = x - Const.WIDTH/2;
		camera.y = y - Const.HEIGHT/2;
	}
	private void newCameraTarget(){
		camposition.x = camera.x + Const.WIDTH/2;
		camposition.y = camera.y + Const.HEIGHT/2;
		camtarget.x = rand.nextInt(level.getPxWidth()-240) + 120;
		camtarget.y = rand.nextInt(level.getPxHeight()-240) + 120;
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
		lfreeze.setX(Const.WIDTH/2);
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
			int mx = x/Level.TILE_SIZE, my = y/Level.TILE_SIZE;
			// move if possible
			if(level.isPassable(mx, my) && !level.collisionMap[mx][my]){
				tank.move(x, y);
				// pressure plates
				if(level.get(mx, my).get() == Tile.PLATE){
					if(level.getStage(mx, my) == 0){
						level.setStage(mx, my, 1);
						// search for links and activate them
						HashSet<Point> dest = level.getLink(mx, my);
						if(dest != null){
							for(Point destination: dest){
								activateMapTile(destination.x, destination.y);
							}
						}
					}
				}
			// open if closed (for players only =))
			} else if(tank == player) { 
				int tile = level.get(mx, my).get();
				if(tile == Tile.DOOR){
					int stage = level.getStage(mx, my);
					if(stage > 0){
						if(player.inventoryContains(Item.KEY)){
							level.setStage(mx, my, level.getStage(mx, my)-1);
							if(stage == 5) soundManager.play(Sound.LOCK);
						}
					}
				} else if(tile == Tile.SAFE){
					if(level.getStage(mx, my) > 0){
						if(player.inventoryContains(Item.KEY)){
							level.setStage(mx, my, 0);
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
				newBullet.add(new Bullet(x, y, dx, dy, tank.getLevel()));
				// play sound
				if(tank == player || rand.nextBoolean()){
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
                resetGame();
				levelnum = 0; nextLevel();
                break;
            case 2: scores.setVisible(true); break;
            case 3: about.setVisible(true); break;
            case 4: gameQuit(); break;
        }
	}
	private void activateMapTile(int x, int y){
		Tile tile = level.get(x, y);
		int px = x*Level.TILE_SIZE, py = y*Level.TILE_SIZE;
		switch(tile.get()){
			case Tile.DOOR:
				if(tile.getStage() == 5){
					level.setStage(x, y, 0);
					soundManager.play(Sound.LOCK);
				}
				break;
			case Tile.SAFE:
			case Tile.BOX:
				level.set(x, y, level.getBackground());
				level.drawSplash(Assets.iexpldec, px-20, py-20);
				synchronized(bonuses){ bonuses.add(new Bonus(px+30, py+30, rand.nextInt(Bonus.COUNT))); }
				fx.add(px-20, py-20, FX.SMALLEXPLOSION);
				break;
			case Tile.BARREL:
				level.set(x, y, level.getBackground());
				level.drawSplash(Assets.iexpldec, px-20, py-20);
				synchronized(newBullet){
					for(int c=0; c<rand.nextInt(3)+4; c++){
						double angle = rand.nextDouble() * Math.PI * 2.0,
							   speed = rand.nextDouble()/5.0 + 0.8;
						newBullet.add(new Bullet(px+30, py+30,
							(float)(Math.cos(angle)*speed), (float)(Math.sin(angle)*speed)));
					}
				}
				fx.add(px-20, py-20, FX.EXPLOSION);
				soundManager.play(Sound.EXPLODE);
				break;
			case Tile.SPAWN:
				int stage = level.getStage(x, y);
				if(stage > 0){
					Tank enemy = new Tank(px, py, stage);
					enemy.setAmmo(1000);
					synchronized(newEnemies) { newEnemies.add(enemy); }
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
						moveTank(player, player.getX() - Level.TILE_SIZE, player.getY());
					} break;
				case KeyEvent.VK_RIGHT:
					if(gamestate == GAME){ 
						player.turn(90);
						moveTank(player, player.getX() + Level.TILE_SIZE, player.getY());
					} break;
				case KeyEvent.VK_UP:
					if(gamestate == GAME){ 
						player.turn(0);
						moveTank(player, player.getX(), player.getY() - Level.TILE_SIZE);
					} break;
				case KeyEvent.VK_DOWN:
					if(gamestate == GAME){ 
						player.turn(180);
						moveTank(player, player.getX(), player.getY() + Level.TILE_SIZE);
					} break;
				case KeyEvent.VK_SPACE:
					if(gamestate == GAME){
						if(!fireTank(player)) soundManager.play(Sound.NOAMMO);
						setAmmoCounter(player.getAmmo());
					}
					break;
				case KeyEvent.VK_C:
					if(gamestate == GAME){
						if(player.getMines() > 0){
							player.changeMines(-1);
							setMinesCounter(player.getMines());
							mines.add(new Mine(player.getX()+30, player.getY()+30));
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
						//resetGame();
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
						addMessage("+1 life", 100);
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
        level.collisionMap[player.getMapX()][player.getMapY()] = false;
        player.update();
		level.collisionMap[player.getMapX()][player.getMapY()] = true;
		// camera
		if(gamestate == GAME){
			if(!Sound.EXPLODE.isPlaying())
				setCamera(player.getX()+30, player.getY()+30);
			else
				setCamera(player.getX()+rand.nextInt(6)-3, player.getY()+rand.nextInt(6)-3);
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
			if(rand.nextInt(60) == 0){
				level.drawSplash(Assets.itrack, player.getTransform());
			}
			// add projectiles
			synchronized(newBullet){
				if(newBullet.size() > 0){
					for(Bullet bullet: newBullet){
						synchronized(bullets){
							bullets.add(bullet);
						}
					}
					newBullet.clear();
				}
			}
			// add new tanks
			synchronized(newEnemies){
				if(newEnemies.size() > 0){
					for(Tank enemy: newEnemies){
						synchronized(enemies){
							enemies.add(enemy);
						}
					}
					newEnemies.clear();
				}
			}
		
			try {
				// scroll all enemies
				synchronized(enemies){
					Iterator<Tank> itenemies = enemies.iterator();
					while(itenemies.hasNext()){
						Tank t = itenemies.next();
						level.collisionMap[t.getMapX()][t.getMapY()] = false;
						t.update();
						level.collisionMap[t.getMapX()][t.getMapY()] = true;
						// random movement
						if(t.isIdle()){
							int action = rand.nextInt(effectFreeze == 0 ? 10 : 100);
							if(action == 0){
								int dx = 0, dy = 0;
								if(rand.nextBoolean()) dx = Level.TILE_SIZE*(rand.nextBoolean()? -1:1);
								else dy = Level.TILE_SIZE*(rand.nextBoolean()? -1:1);
								moveTank(t, t.getX()+dx, t.getY()+dy);
								if(dx<0) t.turn(270);
								else if(dy<0) t.turn(0);
								else if(dx>0) t.turn(90);
								else if(dy>0) t.turn(180);
							} else if(action == 2) fireTank(t);
						}
						// bullet collision
						synchronized(bullets){
							Iterator<Bullet> itbullets = bullets.iterator();
							while(itbullets.hasNext()){
								Bullet b = itbullets.next();
								if(level.collisionMap[(int)(b.getX()/Level.TILE_SIZE)][(int)(b.getY()/Level.TILE_SIZE)]){
									if(distance(b.getX(), b.getY(), t.getX()+30, t.getY()+30) < 25){
										t.changeLife(-b.getLevel()); soundManager.play(Sound.HIT);
										itbullets.remove();
										// score points
										int bonus = rand.nextInt(50);
										changeScore(bonus);
									}
								}
							}
						}
						// mine collision
						synchronized(mines){
							Iterator<Mine> itmines = mines.iterator();
							while(itmines.hasNext()){
								Mine m = itmines.next();
								if(distance(m.getX(), m.getY(), t.getX()+30, t.getY()+30) < 30){
									// death!
                                    AffineTransform at = t.getTransform();
                                    at.rotate(rand.nextDouble() * Math.PI * 2.0);
									level.drawSplash(Assets.iexpldec, at);
									t.setLife(0);
									itmines.remove();
									// score points
									int bonus = rand.nextInt(100);
									changeScore(bonus);
									addMessage("mine death!", 100);
								}
							}
						}
						// explosion
						if(t.getLife() <= 0){
							level.collisionMap[t.getMapX()][t.getMapY()] = false;
							level.drawSplash(Assets.iexpldec, t.getX()-20, t.getY()-20);
							fx.add(t.getX()-20, t.getY()-20, FX.EXPLOSION);
							// score points
							int bonus = rand.nextInt(100)*t.getLevel() + 10;
							changeScore(bonus);
							addMessage("+"+bonus+" score", 100);
							// game over, man, it's over
							soundManager.play(Sound.EXPLODE);
							itenemies.remove();
						}
					}
					// if all enemies were destroyed - we are champions!
					if(enemies.size() == 0 && gamestate == GAME){
						if(levelnum >= MAXLEVEL){
							gameOver(true);
						} else {
							nextLevel();
						}
					}
				}
				// scroll throught all the bullets
				synchronized(bullets){
					Iterator<Bullet> itbullets = bullets.iterator();
					while(itbullets.hasNext()){
						Bullet b = itbullets.next();
					
						// rocket smoke trail
						if(b.getLevel() == Tank.LAUNCHER && rand.nextBoolean())
							fx.add((int)b.getX()-50+rand.nextInt(40),
								   (int)b.getY()-50+rand.nextInt(40), FX.SMOKE);
						
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
							int x = (int)(b.getX()/Level.TILE_SIZE), y = (int)(b.getY()/Level.TILE_SIZE);
							if(!level.isFlyable(x, y)){
								// crush! destroy! swag!
								Tile tile = level.get(x, y);
								int px = x*Level.TILE_SIZE, py = y*Level.TILE_SIZE;
								switch(tile.get()){
									case Tile.BOX:
									case Tile.BARREL:
										activateMapTile(x, y);
										break;
									case Tile.SANDSTONE:
										if(tile.getStage() > 0){
											if(rand.nextBoolean()){
												level.setStage(x, y, tile.getStage()-b.getLevel());
											}
										}
										break;
									case Tile.DOOR:
										if(tile.getStage() > 0 && tile.getStage() < 5){
											level.setStage(x, y, tile.getStage()-b.getLevel());
										}
										break;
									case Tile.SAFE:
										if(tile.getStage() == 0){
											level.set(x, y, level.getBackground());
											level.drawSplash(Assets.iexpldec, px-20, py-20);
											synchronized(bonuses){ bonuses.add(new Bonus(px+30, py+30, rand.nextInt(Bonus.COUNT))); }
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
				synchronized(bonuses){
					Iterator<Bonus> itbonuses = bonuses.iterator();
					while(itbonuses.hasNext()){
						Bonus s = itbonuses.next();
						s.update();
						if(s.contains(player.getX()+30, player.getY()+30)){
							// hack! slash! loot!
							switch(s.getType()){
								case Bonus.LIFE:
									changeLife(1);
									addMessage("+1 life", 100);
									break;
								case Bonus.AMMO:
									player.setAmmo(player.getAmmo()+10); setAmmoCounter(player.getAmmo());
									addMessage("+10 ammo", 100);
									break;
								case Bonus.SCORE:
									int bonus = rand.nextInt(50);
									changeScore(bonus);
									addMessage("+"+bonus+" score", 100);
									break;
								case Bonus.MINE:
									player.changeMines(2); setMinesCounter(player.getMines());
									addMessage("+2 mines", 100);
									break;
								case Bonus.FREEZE:
									effectFreeze += 1000;
									addMessage("slow down", 100);
									soundManager.play(Sound.FREEZE);
									break;
								case Bonus.POWER:
									int level;
									do {
										level = rand.nextInt(Tank.MAX_LEVEL)+1;
									} while(level == player.getLevel());
									player.setLevel(level);
									addMessage("random power", 100);
									break;
								case Bonus.SHIELD:
									changeShield(8);
									addMessage("shields up", 100);
									soundManager.play(Sound.SHIELD);
									break;
							}
							soundManager.play(Sound.PICKUP);
							itbonuses.remove();
						}
					}
				}

                // TODO: total refactoring!!!!!
				synchronized(level.getItems()){
					Iterator<Item> ititems = level.getItems().iterator();
					while(ititems.hasNext()){
						Item i = ititems.next();
						i.update();
						// collect items to player inventory
						if(i.contains(player.getX()+30, player.getY()+30)){
							player.inventory.add(i);
							soundManager.play(Sound.PICKUP);
							ititems.remove();
						}
					}
				}
			} catch(NoSuchElementException e){ System.out.println("WTF! Again (shedule)..."); }
			
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
        level.draw(g2, camera);
		synchronized(mines){
            for(Mine m: mines) {
                m.draw(g2, camera);
            }
		}
		// player
		player.draw(g2, camera);
		
		try {
			synchronized(enemies){
                for(Tank t: enemies) {
                    t.draw(g2, camera);
                }
			}
			synchronized(bonuses){
                for(Bonus z: bonuses) {
                    z.draw(g2, camera);
                }
			}
			synchronized(level.getItems()){
                for(Item i: level.getItems()) {
                    i.draw(g2, camera);
                }
			}
			synchronized(bullets){
                for(Bullet b: bullets) {
                    b.draw(g2, camera);
                }
			}
		} catch(NoSuchElementException e){ System.out.println("WFT! Again (paint)..."); }
		
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
		        g.fillRect(0, Const.HEIGHT/2-120, Const.WIDTH, 120);
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

    /*class DrawShedule extends TimerTask{
        public void run(){
            repaint();
        }
    }*/
}
