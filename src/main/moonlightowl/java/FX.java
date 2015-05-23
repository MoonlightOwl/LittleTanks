package main.moonlightowl.java;

import main.moonlightowl.java.world.entity.Particle;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import java.util.Iterator;
import java.util.LinkedList;

public class FX {
	public static final int EXPLOSION = 0, SMALLEXPLOSION = 1, SMOKE = 2, SPARKLE = 3;
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
	
	public void add(int x, int y, int type){
		BufferedImage[] image = Assets.iexplosion;
		int stage = 0;
		switch(type){
			case SMALLEXPLOSION: stage = 2; break;
			case SMOKE: image = Assets.ismoke; break;
			case SPARKLE: image = Assets.isparkle; break;
		}
		particles.add(new Particle(x, y, image, stage));
	}
	
	public void update(){
		// update & draw existing particles
		if(particles.size() > 0){
			// clear FX map
			g.setComposite(AlphaComposite.Clear);
			g.fillRect(0, 0, width, height);
			g.setComposite(AlphaComposite.SrcOver);
			// iterate over particles & draw new map
			it = particles.iterator();
			while(it.hasNext()){
				Particle p = it.next();
				if(p.done()) it.remove();
				else{
					p.draw(g);
					p.update();
				}
			}
		}
	}
	
	public void draw(Graphics2D g, Point camera){
		if(particles.size() > 0)
			g.drawImage(imap, -camera.x, -camera.y, null);
	}
	
	public void dispose(){
		g.dispose();
	}
}