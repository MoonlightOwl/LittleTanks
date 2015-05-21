package main.moonlightowl.java;

// Just joking  =)  about OOP

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Color;

public class Label{
    private String name;
    private int x,y,cx;
    private Font font;
    private FontMetrics fm;
    private Color color, shadowColor = Color.BLACK;
    boolean shadow = false, centered = false;

    Label(String text, int x, int y, Font font, FontMetrics fm, Color color){
        name = text; this.x = x; this.y = y;
        this.font = font; this.fm = fm;
        this.color = color;
    }
	Label(String text, int x, int y, Font font, FontMetrics fm, Color color, boolean centered, Color shadowColor){
		this(text, x, y, font, fm, color, centered);
		this.shadowColor = shadowColor;
	}
    Label(String text, int x, int y, Font font, FontMetrics fm, Color color, boolean centered){
        name = text; this.y = y;
        this.font = font; this.fm = fm; this.cx = x;
        if(centered) this.x = x-fm.stringWidth(text)/2;
        else this.x = x;
        this.color = color;
        this.centered = centered;
    }

    // set
    public void setShadow(boolean flag){ shadow = flag; }
    public void setPosition(int x, int y){
        if(centered) this.x = x-fm.stringWidth(name)/2;
        else this.x = x;
        this.y = y;
    }
    public void setX(int x){
        if(centered) this.x = x-fm.stringWidth(name)/2;
        else this.x = x;
    }
    public void setY(int y){ this.y = y; }
    public void changeText(String text){
        name = text;
        if(centered) this.x = cx-fm.stringWidth(text)/2;
    }
    // get
    public int getX(){ return x; }
    public int getY(){ return y; }
    public String getText(){ return name; }
    public int lenght(){ return name.length(); }
    public int width(){ return fm.stringWidth(name); }
    public int height(){ return font.getSize(); }

    public void draw(Graphics g){
        g.setFont(font);
        if(shadow){
            g.setColor(shadowColor);
            g.drawString(name, x, y+3);
        }
        g.setColor(color);
        g.drawString(name, x, y);
    }
}