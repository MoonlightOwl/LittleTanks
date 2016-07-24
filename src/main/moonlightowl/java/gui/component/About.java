package main.moonlightowl.java.gui.component;

import main.moonlightowl.java.Const;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class About {
    private List<Label> text;
    private Rectangle rect;

    public About() {
        text = new ArrayList<Label>();
    }

    public void addLine(String text, int x, int y, Font font, FontMetrics fm,
                        Color color, boolean centered, boolean shadow){
        Label newline = new Label(text, x, y, font, fm, color, centered);
        this.text.add(newline);
        if(shadow) newline.setShadow(true);
        rect = new Rectangle(0,this.text.get(0).getY()-36, Const.WIDTH,
                newline.getY()+60-(this.text.get(0).getY()));
    }

    // render
    public void draw(Graphics g){
        g.setColor(Const.OPAQUE_DARK_COLOR);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        for(Label line: text){
            line.draw(g);
        }
    }
}