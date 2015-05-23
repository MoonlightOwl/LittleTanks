package main.moonlightowl.java.world;

// Game objects collection

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.math.Point3D;

import java.io.*;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Level {
	public static final int TILE_SIZE = 60, TILE_HALF = TILE_SIZE/2;
	private Tile BACK = new Tile(Tile.GRASS);
	// level map data
    private Map map;
	public boolean[][] collisionMap;
	private BufferedImage imap, ishadow, irender;
	private Tile borderTile = new Tile(Tile.WALL);
	// level parameters
	private Point startPoint = new Point(0, 0);
	private Rectangle bounds;
	// level lists/maps
	private ArrayList<Point3D> spawners = new ArrayList<Point3D>();
	private ArrayList<Item> items = new ArrayList<Item>();
	private HashMap<Integer, HashSet<Point>> links = new HashMap<Integer, HashSet<Point>>();
	// utils
    private Random rand = new Random(System.currentTimeMillis());

	public Level(){
		defaultMap();
	}
    public Level(String filename){
		load(filename);
    }
	public void defaultMap(){
		map = new Map();
		map.fill(BACK);
		setStartPoint(5, 5);
		generateMapImage();
		collisionMap = new boolean[5][5];
	}
	private void generateMapImage(){
		int width = map.getWidth() * TILE_SIZE,
		    height = map.getHeight() * TILE_SIZE;
		imap = new BufferedImage(width, height,
								 BufferedImage.TYPE_INT_ARGB);
		ishadow = new BufferedImage(width, height,
								 BufferedImage.TYPE_INT_ARGB);
		irender = new BufferedImage(width, height,
								 BufferedImage.TYPE_INT_ARGB);
		// tiles						
		Graphics g = imap.createGraphics();
		for(int x=0; x<map.getWidth(); x++){
			for(int y=0; y<map.getHeight(); y++){
				drawTile(g, x*TILE_SIZE, y*TILE_SIZE, map.get(x, y));
			}
		}
		g.dispose();
		// shadows
		g = ishadow.createGraphics();
		for(int x=0; x<map.getWidth()-1; x++){
			for(int y=0; y<map.getHeight()-1; y++){
				drawShadow(g, x, y);
			}
		}
		g.dispose();
		// render
		render();
		bounds = new Rectangle(0, 0, width, height);
	}
	private void drawTile(Graphics g, int x, int y, Tile tile){
		switch(tile.get()){
			case Tile.GRASS: g.drawImage(Assets.igrass, x, y, null); break;
			case Tile.WALL: g.drawImage(Assets.iwall, x, y, null); break;
			case Tile.SANDSTONE: 
				drawTile(g, x, y, BACK);
				g.drawImage(Assets.isandstone[tile.getStage()], x, y, null); 
				break;
			case Tile.BOX: g.drawImage(Assets.ibox, x, y, null); break;	
			case Tile.BARREL:
				drawTile(g, x, y, BACK);
				if(rand.nextBoolean()) g.drawImage(Assets.ibarrel, x, y, null);
				else g.drawImage(Assets.ibarrelside, x, y, null);
				break;
			case Tile.HOLE: g.drawImage(Assets.ihole, x, y, null); break;
			case Tile.METAL: 
				g.drawImage(Assets.imetal, x, y, null); 
				if(rand.nextInt(20) == 1) g.drawImage(Assets.isplash_text, x, y, null);
				break;
			case Tile.GRID:
				drawTile(g, x, y, BACK);
				g.drawImage(Assets.igrid, x, y, null); 
				break;
			case Tile.CONCRETE:
				drawTile(g, x, y, BACK);
				g.drawImage(Assets.iconcrete, x, y, null); 
				break;
			case Tile.SAND: g.drawImage(Assets.isand, x, y, null); break;	
			case Tile.BUSH:
				drawTile(g, x, y, BACK);
				g.drawImage(Assets.ibush, x, y, null); 
				break;
			case Tile.DOOR: g.drawImage(Assets.idoor[tile.getStage()], x, y, null); break;
			case Tile.SAFE: g.drawImage(Assets.isafe[tile.getStage()], x, y, null); break;
			case Tile.PLATE:
				drawTile(g, x, y, BACK);
				g.drawImage(Assets.ibutton[tile.getStage()], x, y, null); 
				break;
            case Tile.TURRET:
                drawTile(g, x, y, BACK);
                g.drawImage(Assets.iturret_base, x, y, null);
                break;
			case Tile.SPAWN:
				drawTile(g, x, y, BACK);
				g.drawImage(Assets.ispawn, x, y, null); 
				break;
		}
	}
	public void drawShadow(Graphics g, int x, int y){
		//if(map.get(x,y).isPassable()){
		//	if(map.get(x-1, y).castShadow()) 
		//		g.drawImage(Assets.ishadowr, x*TILE_SIZE, y*TILE_SIZE, null);
		//	if(map.get(x, y-1).castShadow()) 
		//		g.drawImage(Assets.ishadowd, x*TILE_SIZE, y*TILE_SIZE, null);
		//}
		// alternative for fun =)
		if(map.get(x, y).castShadow()){
			if(map.get(x+1, y).isPassable())
				g.drawImage(Assets.ishadowr, (x+1)*TILE_SIZE, y*TILE_SIZE, null);
			if(map.get(x, y+1).isPassable())
				g.drawImage(Assets.ishadowd, x*TILE_SIZE, (y+1)*TILE_SIZE, null);
		}
	}
	public void correctShadows(){
		Graphics2D g = ishadow.createGraphics();
		// clear map
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, ishadow.getWidth(), ishadow.getHeight());
		g.setComposite(AlphaComposite.SrcOver);
		// draw new map
		for(int x=0; x<map.getWidth()-1; x++){
			for(int y=0; y<map.getHeight()-1; y++){
				drawShadow(g, x, y);
			}
		}
		g.dispose();
	}
	public void drawSplash(BufferedImage image, AffineTransform at){
		Graphics2D g = imap.createGraphics();
		g.drawImage(image, at, null);
		render();
		g.dispose();
	}
	public void drawSplash(BufferedImage image, int x, int y){
		Graphics g = imap.createGraphics();
		g.drawImage(image, x, y, null);
		render();
		g.dispose();
	}
	private void render(){
		Graphics g = irender.createGraphics();
		g.drawImage(imap, 0, 0, null);
		g.drawImage(ishadow, 0, 0, null);
		g.dispose();
	}
	
	public void load(String filename){
		BufferedReader reader = null;
		// clear lists
		spawners.clear();
		items.clear();
		links.clear();
		// read data from file
		try {
			// open file reader
			File file = new File(filename);
			reader = new BufferedReader(new FileReader(file));
			// get level size
			String line = reader.readLine();
			int width = Integer.parseInt(line);
			line = reader.readLine();
			int height = Integer.parseInt(line);
			// create new blank map
			map = new Map(width, height); map.fill(BACK);
			collisionMap = new boolean[width][height];
			// read tile data
			for(int y = 0; y<height && (line = reader.readLine())!=null; y++){
				for(int x = 0; x<line.length(); x++){
					// create new tile by char
					char ch = line.charAt(x);
					
					// special cases
					switch(ch){
						// empty tile
						case ' ':
							map.set(x, y, BACK); 
							break;
						// items
						case 'k': 
							items.add(new Item(x*TILE_SIZE+TILE_HALF, y*TILE_SIZE+TILE_HALF, Item.KEY)); 
							map.set(x, y, BACK); 
							break;
						case 'c': 
							items.add(new Item(x*TILE_SIZE+TILE_HALF, y*TILE_SIZE+TILE_HALF, Item.CANDY)); 
							map.set(x, y, BACK); 
							break;
                        // entities
                        case 'T':


						default:
							// set tile
							map.set(x, y, new Tile(ch));
							
							// set additional data
							switch(ch){
								// start point
								case '@': setStartPoint(x, y); break;
								// enemy spawner
								case '&': spawners.add(new Point3D(x, y, 1));
										  map.setStage(x, y, 1); break;
								case '%': spawners.add(new Point3D(x, y, 2));
										  map.setStage(x, y, 2); break;
								case '$': spawners.add(new Point3D(x, y, 3));
										  map.setStage(x, y, 3); break;
							}
					}
				}
			}
			// processing given map parameters
			while((line = reader.readLine())!=null){
				String[] data = line.split("\\s*(\\s|:)\\s*");
				// homemade switch =P
				if(data[0].equals("background")){
					Tile tile = new Tile(data[1].charAt(0));
					BACK.set(tile.get());
					
				} else if(data[0].equals("link")){
					try {
						int[] coord = new int[4];
						for(int c = 0; c < 4; c++){
							coord[c] = Integer.parseInt(data[c+1]);
						}
						addLink(coord[0], coord[1], coord[2], coord[3]);
					}
					catch(NumberFormatException e){
						System.out.println("[ERROR] '"+filename+"': wrong link coords.");
					}
				}
			}
		} catch(Exception e) {
			// print error message
			System.out.println("[ERROR] Something went wrong, when loading level map...");
			e.printStackTrace();
			// create default map
			defaultMap();
		} finally {
			try {
                if(reader != null) reader.close();
			} catch(Exception e) { e.printStackTrace(); }
			generateMapImage();
		}
	}
	
	// getters
	public boolean contains(int x, int y){
		if(x>=0 && x<map.getWidth())
			if(y>=0 && y<map.getHeight()) return true;
		return false;
	}
	public boolean containsPx(int x, int y){ return bounds.contains(x, y); }
	
	public Tile get(int x, int y){
		return (contains(x, y) ? map.get(x, y) : borderTile);
	}
	public Tile getBackground(){ return new Tile(BACK); }
	public int getStage(int x, int y){ return get(x, y).getStage(); }
	public Point getStartPoint(){ return startPoint; }
	public ArrayList<Point3D> getSpawners(){ return spawners; }
	public int getPxWidth(){ return irender.getWidth(); }
	public int getPxHeight(){ return irender.getHeight(); }
	
	public boolean isFlyable(int x, int y){ 
		return get(x, y).isFlyable();
	}
	public boolean isPassable(int x, int y){
		return get(x, y).isPassable();
	}
	public HashSet<Point> getLink(int x, int y){
		if(contains(x,y)){
			int key = x * map.getWidth() + y;
			return links.get(key);
		}
		return null;
	}
    public ArrayList<Item> getItems(){ return items; }
	
	// setters
	public void set(int x, int y, Tile tile){
		if(contains(x, y)){
			// change tile data
			map.set(x, y, tile);
			// draw tile map
			Graphics g = imap.createGraphics();
			drawTile(g, x*TILE_SIZE, y*TILE_SIZE, tile);
			g.dispose();
			// draw shadow map
			correctShadows();
			// combine
			render();
		}
	}
	public void setStartPoint(int x, int y){
		startPoint.x = x;
		startPoint.y = y;
	}
	public boolean setStage(int x, int y, int stage){
		if(contains(x, y)){
			Tile tile = get(x,y);
			tile.setStage(stage >= 0 ? stage : 0);
			if(tile.get() == Tile.SANDSTONE || tile.get() == Tile.DOOR){
				if(tile.castShadow() && stage <= 3) 
					tile.setCastShadow(false);
					// draw shadow map
					correctShadows();
				if(stage <= 0){
					tile.setPassable(true);
					tile.setFlyable(true);
					// draw shadow map
					correctShadows();
				}
			}
			// draw tile map
			Graphics g = imap.createGraphics();
			drawTile(g, x*TILE_SIZE, y*TILE_SIZE, tile);
			g.dispose();
			// re-render
			render();
			//
			return true;
		} else return false;
	}
	private boolean addLink(int a, int b, int c, int d){
		if(contains(a, b) && contains(c, d)){
			Integer key = a * map.getWidth() + b;
			Point value = new Point(c, d);
			if(!links.containsKey(key)){ links.put(key, new HashSet<Point>()); }
			links.get(key).add(value);
			return true;
		}
		return false;
	}

    public void draw(Graphics2D g, Point camera){
        g.drawImage(irender, -camera.x, -camera.y, null);
    }
}
