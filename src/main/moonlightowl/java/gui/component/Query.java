package main.moonlightowl.java.gui.component;

import main.moonlightowl.java.Const;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Query {
    private Label question, nickname;
    private boolean visible, active;
    private Rectangle rect;

    public Query(String question, int x, int y, Font font, FontMetrics fm, Color color){
        this.question = new Label(question, x, y, font, fm, color, true);
        nickname = new Label("", x, y+font.getSize(), font, fm, color, true);
        rect = new Rectangle(0, y-50, Const.WIDTH, font.getSize()*2+20);
        visible = true;
        active = true;
    }

    // getters & setters
    public boolean isVisible(){ return visible; }
    public boolean isActive(){ return active; }
    public String getText(){ return nickname.getText(); }
    public void setVisible(boolean visible){ this.visible = visible; }
    public void setActive(boolean active){ this.active = active; }
    public void setText(String text){ nickname.changeText(text); }

    public void keyPressed(KeyEvent e){
        switch(e.getKeyCode()){
            case KeyEvent.VK_BACK_SPACE:
                if(nickname.length()>0){
                    nickname.changeText(nickname.getText().substring(0,nickname.length()-1));
                }
                break;
            default:
                if(e.getKeyChar()>='0' && e.getKeyChar()<='z'){
                    if(nickname.length()<Const.NICKNAME_LEN){
                        nickname.changeText(nickname.getText()+e.getKeyChar());
                    }
                }
        }
    }

    public void draw(Graphics g){
        if(isVisible()) {
            g.setColor(Const.OPAQUE_DARK_COLOR);
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
            if (active && System.currentTimeMillis() % 800 < 400) {
                g.setColor(Color.GREEN);
                g.drawRect(nickname.getX() - 5, nickname.getY() - nickname.height() + 5,
                        nickname.width() + 10, nickname.height());
            }
            question.draw(g);
            nickname.draw(g);
        }
    }
}
