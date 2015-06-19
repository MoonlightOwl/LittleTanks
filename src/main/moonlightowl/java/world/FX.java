package main.moonlightowl.java.world;

import main.moonlightowl.java.Assets;
import main.moonlightowl.java.math.GMath;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import java.util.Iterator;
import java.util.LinkedList;

public class FX {
    public static final int EXPLOSION = 0, SMALLEXPLOSION = 1, SMOKE = 2,
            SPARKLE = 3, SNOWFLAKE = 4;
    private LinkedList<Particle> particles;
    private Iterator<Particle> it;
    private int width, height;
    private BufferedImage imap;
    private Graphics2D g;

    public FX(int width, int height){
        this.width = width; this.height = height;
        imap = new BufferedImage(width, height,
                                 BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D)imap.getGraphics();
        particles = new LinkedList<Particle>();
    }

    public void clear(){
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, width, height);
        g.setComposite(AlphaComposite.SrcOver);
    }

    public void add(int x, int y, int type){
        BufferedImage[] image = Assets.iexplosion;
        int stage = 0, delay = 100;
        switch(type){
            case SMALLEXPLOSION: stage = 2; break;
            case SMOKE: image = Assets.ismoke; break;
            case SPARKLE: image = Assets.isparkle; break;
            case SNOWFLAKE: image = Assets.isnowflake; delay = 2000; break;
        }
        Particle particle = new Particle(x, y, type, image, stage);
        particle.setAnimaionDelay(delay);
        particles.add(particle);
    }

    public void update(){
        // update & draw existing particles
        if(!particles.isEmpty()){
            clear();
            // iterate over particles & draw new map
            it = particles.iterator();
            while(it.hasNext()){
                Particle p = it.next();
                if(p.done()) it.remove();
                else{
                    p.draw(g);
                    p.update();
                    if(p.getType() == SNOWFLAKE){
                        p.move(GMath.rand.nextBoolean() ? -1+GMath.rand.nextInt(3): 0,
                                GMath.rand.nextInt(3));
                    }
                }
            }
        }
    }

    public void draw(Graphics2D graphics, Point camera){
        if(!particles.isEmpty())
            graphics.drawImage(imap, -camera.x, -camera.y, null);
    }

    public void dispose(){
        g.dispose();
    }
}