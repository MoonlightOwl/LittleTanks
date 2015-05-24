package main.moonlightowl.java.world.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Particle {
    private int ANIM_DELAY = 100;
    private int x, y, state, maxState;
    private long time;
    private BufferedImage[] image;

    public Particle(int x, int y, BufferedImage image){
        this(x, y, new BufferedImage[]{image});
    }
    public Particle(int x, int y, BufferedImage[] image, int state){
        this(x, y, image);
        this.state = state;
    }
    public Particle(int x, int y, BufferedImage[] image){
        this.x = x; this.y = y;
        this.image = image;
        state = 0; maxState = image.length - 1;
        time = System.currentTimeMillis();
    }

    public int getX(){ return x; }
    public int getY(){ return y; }
    public boolean done(){ return (state > maxState); }
    public int getAnimationDelay(){ return ANIM_DELAY; }

    public void setAnimaionDelay(int delay){
        ANIM_DELAY = delay;
    }

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