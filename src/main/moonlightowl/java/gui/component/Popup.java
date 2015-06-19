package main.moonlightowl.java.gui.component;

import main.moonlightowl.java.Const;

import java.awt.*;

/**
 * LittleTanks.Popup
 * Created by MoonlightOwl on 6/19/15.
 * ---
 * Ingame popup message box
 */

public class Popup {
    private Label message;
    private Rectangle rectangle;
    private Stroke stroke = new BasicStroke(2);
    private boolean visible = false;

    public Popup(String message, int x, int y, Font font, FontMetrics fontMetrics){
        this.message = new Label(message, x, y, font, fontMetrics, Color.WHITE, true);
        this.message.setShadow(true);
        rectangle = new Rectangle();
        setMessage(message);
    }

    public boolean isVisible(){ return visible; }

    public void setMessage(String message){
        this.message.changeText(message);
        rectangle.width = this.message.width() + 10;
        rectangle.height = this.message.height() + 6;
        rectangle.y = this.message.getY() - rectangle.height + 8;
        rectangle.x = this.message.getX() - 6;
    }
    public void setVisible(boolean visible){ this.visible = visible; }

    public void draw(Graphics2D g){
        if(visible) {
            g.setColor(Const.OPAQUE_COLOR);
            g.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            message.draw(g);
            g.setColor(Color.DARK_GRAY);
            g.setStroke(stroke);
            g.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
    }
}
