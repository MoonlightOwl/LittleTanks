package main.moonlightowl.java.world;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Particle {
    private int ANIM_DELAY = 100;
    private int x, y, state, maxState, type;
    private long time;
    private BufferedImage[] image;

    public Particle(int x, int y, int type, BufferedImage image){
        this(x, y, 0, new BufferedImage[]{image});
    }
    public Particle(int x, int y, int type, BufferedImage[] image, int state){
        this(x, y, type, image);
        this.state = state;
    }
    public Particle(int x, int y, int type, BufferedImage[] image){
        this.x = x; this.y = y; this.type = type;
        this.image = image;
        state = 0; maxState = image.length - 1;
        time = System.currentTimeMillis();
    }

    public int getX(){ return x; }
    public int getY(){ return y; }
    public int getType(){ return type; }
    public boolean done(){ return (state > maxState); }
    public int getAnimationDelay(){ return ANIM_DELAY; }

    public void setAnimaionDelay(int delay){
        ANIM_DELAY = delay;
    }
    public void move(int dx, int dy){ this.x += dx; this.y += dy; }

    public void update(){
        if(state <= maxState)
            if((System.currentTimeMillis() - time) > ANIM_DELAY){
                state++;
                time = System.currentTimeMillis();
            }
    }
    public void draw(Graphics2D g){
        if(state <= maxState){
            g.drawImage(image[state], x, y, null);
        }
    }
}