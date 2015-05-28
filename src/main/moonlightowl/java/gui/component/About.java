package main.moonlightowl.java.gui.component;

import main.moonlightowl.java.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class About {
    private List<main.moonlightowl.java.gui.component.Label> text;
    private boolean visible;
    private Rectangle rect;

    public About() {
        text = new ArrayList<main.moonlightowl.java.gui.component.Label>();
        visible = false;
    }

    // getters & setters
    public void setVisible(boolean visible){ this.visible = visible; }
    public boolean isVisible(){ return visible; }

    public void addLine(String text, int x, int y, Font font, FontMetrics fm,
                        Color color, boolean centered, boolean shadow){
        main.moonlightowl.java.gui.component.Label newline = new main.moonlightowl.java.gui.component.Label(text, x, y, font, fm, color, centered);
        this.text.add(newline);
        if(shadow) newline.setShadow(true);
        rect = new Rectangle(0,this.text.get(0).getY()-36, Const.WIDTH,
                newline.getY()+60-(this.text.get(0).getY()));
    }

    // render
    public void draw(Graphics g){
        g.setColor(Const.OPAQUE_DARK_COLOR);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        for(main.moonlightowl.java.gui.component.Label line: text){
            line.draw(g);
        }
    }
}