package main.moonlightowl.java.world;

/**
 * Game map for Tank Destroy!
 */

public class Map {
    public static final int DEFAULT_SIZE = 10;

    private Tile[][] data;
    private int width, height;

    public Map(){
        this(DEFAULT_SIZE, DEFAULT_SIZE);
    }
    public Map(int width, int height){
        create(width, height);
    }

    private void create(int width, int height){
        data = new Tile[width][height];
        this.width = width;
        this.height = height;
    }

    public void set(int x, int y, Tile tile){ data[x][y] = tile; }

    public Tile get(int x, int y){ return data[x][y]; }
    public int getWidth(){ return width; }
    public int getHeight(){ return height; }

    public void fill(Tile tile){
        for(int x=0; x<width; x++)
            for(int y=0; y<height; y++)
                set(x, y, tile);
    }
}