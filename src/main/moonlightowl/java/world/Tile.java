package main.moonlightowl.java.world;

// Simple tiles for battle field

public class Tile {
    public static final int GRASS = 0, WALL = 1, BOX = 2,
        HOLE = 3, SANDSTONE = 4, METAL = 5, GRID = 6, BARREL = 7,
        CONCRETE = 8, SAND = 9, SPAWN = 10, BUSH = 11, DOOR = 12,
        SAFE = 13, PLATE = 14, TURRET = 15, FLOORTILES = 16,
        COBBLESTONE = 17, SNOW = 18, RUSTBLOCK = 19;

    private int type;
    private boolean passable = false, flyable = false, castshadow = false;
    private int stage = 0;

    public Tile(Tile old){
        copyFrom(old);
    }
    public Tile(){ create(GRASS); }
    public Tile(int type){
        create(type);
    }
    public Tile(char ch){
        switch(ch){
            case '.': create(GRASS);break;
            case 'B': create(BOX); break;
            case 'b': create(BARREL); break;
            case '#': create(WALL); break;
            case '+': create(SANDSTONE); break;
            case 'O': create(HOLE); break;
            case 'M': create(METAL); break;
            case 'X': create(GRID); break;
            case 'C': create(CONCRETE); break;
            case ',': create(SAND); break;
            case '*': create(BUSH); break;
            case 'D': create(DOOR); break;
            case 'S': create(SAFE); break;
            case '=': create(PLATE); break;
            case 'T': create(TURRET); break;
            case '_': create(FLOORTILES); break;
            case '~': create(COBBLESTONE); break;
            case '-': create(SNOW); break;
            case 'R': create(RUSTBLOCK); break;
            case '@': case '&': case '%': case '$': case '?': create(SPAWN); break;
            default: create(GRASS);
        }
    }
    private void create(int type){
        this.type = type;
        switch(type){
            case BOX: castshadow = true; break;
            case BARREL: break;
            case WALL: castshadow = true; break;
            case SANDSTONE: castshadow = true; stage = 5; break;
            case METAL: castshadow = true; break;
            case HOLE: flyable = true; break;
            case GRID: flyable = true; break;
            case CONCRETE: castshadow = true; break;
            case BUSH: passable = true; break;
            case DOOR: castshadow = true; stage = 5; break;
            case SAFE: castshadow = true; stage = 1; break;
            case PLATE: flyable = true; passable = true; stage = 0; break;
            case TURRET: flyable = true; break;
            case RUSTBLOCK: castshadow = true; break;
            default: flyable = true; passable = true;
        }
    }
    public void copyFrom(Tile tile){
        this.type = tile.type;
        this.passable = tile.passable;
        this.flyable = tile.flyable;
        this.castshadow = tile.castshadow;
        this.stage = tile.stage;
    }

    public void set(int type){ this.type = type; }
    public void setStage(int stage){ this.stage = stage; }
    public void setCastShadow(boolean cast){ this.castshadow = cast; }
    public void setPassable(boolean passable){ this.passable = passable; }
    public void setFlyable(boolean flyable){ this.flyable = flyable; }

    public int get(){ return type; }
    public boolean isPassable(){ return passable; }
    public boolean isFlyable(){ return flyable; }
    public boolean castShadow(){ return castshadow; }
    public int getStage(){ return stage; }
}