package main.moonlightowl.java;

import java.awt.*;
import java.util.*;
import java.util.List;

public class About {
    List<Label> text = new ArrayList<Label>();
    Iterator it;
    Label line; // pointer for iterator
    int x, y, interval;
    private boolean visible = false;
    Rectangle rect;

    About(){
    }

    // getters & setters
    public void setVisible(boolean visible){ this.visible = visible; }
    public boolean isVisible(){ return visible; }

    public void addLine(String text, int x, int y, Font font, FontMetrics fm, Color color, boolean centered, boolean shadow){
        Label newline = new Label(text, x, y, font, fm, color, centered);
        this.text.add(newline);
        if(shadow) newline.setShadow(true);
        rect = new Rectangle(0,this.text.get(0).getY()-36, Const.WIDTH, newline.getY()+60-(this.text.get(0).getY()));
    }

    public void draw(Graphics g){
        g.setColor(Const.OPAQUE_DARK_COLOR);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        it = text.iterator();
        while(it.hasNext()){
            line = (Label)it.next();
            line.draw(g);
        }
    }
}
