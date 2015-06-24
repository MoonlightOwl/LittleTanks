package main.moonlightowl.java.world;

/**
 * Level map and its graphical representation
 */

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.Const;
import main.moonlightowl.java.math.GMath;
import main.moonlightowl.java.world.entity.Tank;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

import java.util.HashMap;
import java.util.HashSet;

public class Level {
    private Tile BACK = new Tile(Tile.GRASS);
    // level map data
    private Map map;
    private BufferedImage imap, ishadow, irender;
    private HashMap<Integer, HashSet<Point>> links;
    private HashSet<Point> changeList;
    private boolean[][] collisionMap;
    // level parameters
    private int width, height, pxwidth, pxheight;
    private Tile borderTile = new Tile(Tile.WALL);
    private Point startPoint;
    private Tank.State startState;
    private Rectangle pxbounds;
    private boolean snowy, friendlyFire = false;
    private int enemyRespawnTime;

    public Level(){
        this(5, 5);
    }
    public Level(int width, int height){
        this.width = width; pxwidth = GMath.toPixel(width);
        this.height = height; pxheight = GMath.toPixel(height);
        // init
        pxbounds = new Rectangle(0, 0, pxwidth, pxheight);
        collisionMap = new boolean[width][height];
        links = new HashMap<Integer, HashSet<Point>>();
        changeList = new HashSet<Point>();
        startPoint = new Point(1, 1);
        startState = new Tank.State();
        snowy = false;
        enemyRespawnTime = 0;
        // init images (render layers)
        imap = new BufferedImage(pxwidth, pxheight,
                BufferedImage.TYPE_INT_ARGB);
        ishadow = new BufferedImage(pxwidth, pxheight,
                BufferedImage.TYPE_INT_ARGB);
        irender = new BufferedImage(pxwidth, pxheight,
                BufferedImage.TYPE_INT_ARGB);
        // generate map
        map = new Map(width, height);
        map.fill(BACK);
        render();
    }

    // renderFinal
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
                if(GMath.rand.nextBoolean()) g.drawImage(Assets.ibarrel, x, y, null);
                else g.drawImage(Assets.ibarrelside, x, y, null);
                break;
            case Tile.HOLE: g.drawImage(Assets.ihole, x, y, null); break;
            case Tile.METAL:
                g.drawImage(Assets.imetal, x, y, null);
                if(GMath.rand.nextInt(20) == 1) g.drawImage(Assets.isplash_text, x, y, null);
                break;
            case Tile.GRID:
                drawTile(g, x, y, BACK);
                g.drawImage(Assets.igrid, x, y, null);
                break;
            case Tile.CONCRETE:
                drawTile(g, x, y, BACK);
                g.drawImage(Assets.iconcrete, x, y, null);
                break;
            case Tile.SAND:
                g.drawImage(Assets.isand, x, y, null);
                if(GMath.rand.nextInt(15) == 1) g.drawImage(Assets.icactus, x, y, null);
                break;
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
            case Tile.FLOORTILES: g.drawImage(Assets.ifloor_tiles, x, y, null); break;
            case Tile.COBBLESTONE: g.drawImage(Assets.icobblestone, x, y, null); break;
            case Tile.SNOW:
                g.drawImage(Assets.isnow, x, y, null);
                if(GMath.rand.nextInt(12) == 1) g.drawImage(Assets.idrygrass, x, y, null);
                break;
            case Tile.RUSTBLOCK: g.drawImage(Assets.irustblock, x, y, null); break;
            case Tile.TEXT:
                drawTile(g, x, y, BACK);
                g.drawImage(Assets.iquestion, x, y, null);
                break;
            case Tile.PLASTIC: g.drawImage(Assets.iplastic, x, y, null); break;
            case Tile.SPAWN:
                drawTile(g, x, y, BACK);
                g.drawImage(Assets.ispawn, x, y, null);
                break;
        }
        if(isSnowy() && tile.castShadow()){
            if(GMath.rand.nextInt(6) == 0)
                g.drawImage(Assets.isnowcap[GMath.rand.nextInt(Assets.isnowcap.length)], x, y, null);
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
            if(!map.get(x+1, y).castShadow())
                g.drawImage(Assets.ishadowr, (x+1)*Const.TILE_SIZE, y*Const.TILE_SIZE, null);
            if(!map.get(x, y+1).castShadow())
                g.drawImage(Assets.ishadowd, x*Const.TILE_SIZE, (y+1)*Const.TILE_SIZE, null);
        }
    }
    public void renderTiles(){
        Graphics2D g = imap.createGraphics();
        // clear map
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, imap.getWidth(), imap.getHeight());
        g.setComposite(AlphaComposite.SrcOver);
        // draw new map
        for(int x=0; x<map.getWidth(); x++){
            for(int y=0; y<map.getHeight(); y++){
                drawTile(g, GMath.toPixel(x), GMath.toPixel(y), map.get(x, y));
            }
        }
        g.dispose();
    }
    public void renderShadows(){
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
    private void renderFinal(){
        Graphics g = irender.createGraphics();
        g.drawImage(imap, 0, 0, null);
        g.drawImage(ishadow, 0, 0, null);
        g.dispose();
    }

    private void redrawTile(int x, int y){
        changeList.add(new Point(x, y));
    }
    public void renderChanges(){
        if(!changeList.isEmpty()) {
            Graphics g = imap.createGraphics();
            for (Point p : changeList) {
                drawTile(g, GMath.toPixel(p.x), GMath.toPixel(p.y), map.get(p.x, p.y));
            }
            g.dispose();
            changeList.clear();

            renderShadows();
            renderFinal();
        }
    }

    public void render(){
        changeList.clear();
        renderTiles();
        renderShadows();
        renderFinal();
    }
    public void drawSplash(BufferedImage image, AffineTransform at){
        Graphics2D g = imap.createGraphics();
        g.drawImage(image, at, null);
        g.dispose();
        renderFinal();
    }
    public void drawSplash(BufferedImage image, int x, int y){
        Graphics g = imap.createGraphics();
        g.drawImage(image, x, y, null);
        g.dispose();
        renderFinal();
    }

    // getters
    public boolean contains(int x, int y){
        if(x >= 0 && x < width)
            if(y >= 0 && y < height) return true;
        return false;
    }
    public boolean containsPixel(int x, int y){ return pxbounds.contains(x, y); }

    public Tile get(int x, int y){
        return (contains(x, y) ? map.get(x, y) : borderTile);
    }
    public Tile getBackground(){ return new Tile(BACK); }
    public int getStage(int x, int y){ return get(x, y).getStage(); }
    public Point getStartPoint(){ return startPoint; }
    public Tank.State getStartState(){ return startState; }
    public int getWidth(){ return width; }
    public int getHeight(){ return height; }
    public int getPxWidth(){ return pxwidth; }
    public int getPxHeight(){ return pxheight; }
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
    public boolean getCollision(int x, int y) {
        return contains(x, y) && collisionMap[x][y];
    }
    public boolean isSnowy(){ return snowy; }
    public int getEnemyRespawnTime(){ return enemyRespawnTime; }
    public boolean enemyRespawnEnabled(){ return enemyRespawnTime > 0; }
    public boolean friendlyFireEnabled(){ return friendlyFire; }


    // setters
    public void set(int x, int y, Tile tile){
        if(contains(x, y)){
            map.set(x, y, tile);
            redrawTile(x, y);
        }
    }
    public void clear(int x, int y){
        set(x, y, BACK);
    }
    public void setBackground(Tile tile){ BACK.copyFrom(tile); }
    public void setStartPoint(int x, int y){
        startPoint.x = x;
        startPoint.y = y;
    }
    public void setStartState(Tank.State state){ startState = state; }
    public boolean setStage(int x, int y, int stage){
        if(contains(x, y)){
            Tile tile = get(x,y);
            tile.setStage(stage >= 0 ? stage : 0);

            if(tile.get() == Tile.SANDSTONE || tile.get() == Tile.DOOR){
                if(tile.castShadow() && stage <= 3)
                    tile.setCastShadow(false);
                if(stage <= 0){
                    tile.setPassable(true);
                    tile.setFlyable(true);
                }
            }

            redrawTile(x, y);
            return true;
        } else return false;
    }
    public boolean addLink(int a, int b, int c, int d){
        if(contains(a, b) && contains(c, d)){
            Integer key = a * map.getWidth() + b;
            Point value = new Point(c, d);
            if(!links.containsKey(key)){ links.put(key, new HashSet<Point>()); }
            links.get(key).add(value);
            return true;
        }
        return false;
    }
    public void setCollision(int x, int y, boolean collision){
        if(contains(x, y)) collisionMap[x][y] = collision;
    }
    public void setSnowy(boolean snowy){ this.snowy = snowy; }
    public void setEnemyRespawnTime(int time){ enemyRespawnTime = time; }
    public void allowFriendlyFire(boolean friendlyFire){ this.friendlyFire = friendlyFire; }


    // render
    public void draw(Graphics2D g, Point camera){
        g.drawImage(irender, -camera.x, -camera.y, null);
    }
}
