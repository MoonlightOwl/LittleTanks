package main.moonlightowl.java.gui.component;

import main.moonlightowl.java.Const;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * LittleTanks.Selector
 * Created by MoonlightOwl on 6/4/15.
 * ---
 * Let you switch between many variants.
 */

public class Selector {
    private int x, y, ax, ay, columns, rows, page, totalpages, pagesize,
        gap, offx, itemwidth, itemheight;
    private int width = Const.WIDTH, height = 200;
    private boolean active;
    private Font font;
    private FontMetrics fm;
    private Color colorSelected;
    private Polygon leftArrow, rightArrow;
    private String[] items;

    public Selector(int x, int y, int columns, int rows, Font font, FontMetrics fm, Color selected){
        this.x = x; this.y = y; this.columns = columns; this.rows = rows;
        ax = 0; ay = 0; page = 0; totalpages = 0; pagesize = columns*rows;
        this.font = font; this.fm = fm;
        this.colorSelected = selected;

        int centery = y + height/2 - 30;
        int[] xs = {20+x, 60+x, 60+x};
        int[] ys = {30+centery, 60+centery, centery};
        leftArrow = new Polygon(xs, ys, xs.length);

        xs[0] = x+width-60; xs[1] = x+width-60; xs[2] = x+width-20;
        ys[0] = centery; ys[1] = 60+centery; ys[2] = 30+centery;
        rightArrow = new Polygon(xs, ys, xs.length);

        offx = 100; gap = 20;
        itemwidth = (width - offx*2 - gap*(columns-1)) / columns;
        itemheight = (height - gap*(rows+1))/rows;

        items = new String[0];

        active = true;
    }


    public boolean isActive(){ return active; }
    public String currentItem(){
        return items[ay*columns + ax];
    }
    public int size(){ return items.length; }

    public void setActive(boolean active){ this.active = active; }
    public void setItems(String[] items){
        this.items = items;
        page = 0; ax = 0; ay = 0;
        totalpages = (int)Math.floor(items.length / pagesize);
    }
    public boolean lowerBoundaryReached(){
        return (ay == (rows-1)) || (!isValidItem(ax, ay+1));
    }


    private boolean isValidItem(int ix, int iy){
        int number = iy*columns + ix + page*pagesize;
        return number < items.length;
    }
    public void keyPressed(KeyEvent e){
        switch(e.getKeyCode()){
            case KeyEvent.VK_LEFT:
                if(ax > 0) ax--;
                else if(page > 0){
                    page--;
                    ax = columns-1;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if(ax < (columns-1)) ax++;
                else if(page < totalpages){
                    page++;
                    ax = 0;
                }
                break;
            case KeyEvent.VK_UP:
                if(ay > 0) ay--;
                break;
            case KeyEvent.VK_DOWN:
                if(ay < (rows-1)) ay++;
                break;
        }
        // correct for partially filled pages
        while(!isValidItem(ax, ay)){
            if(ay > 0) ay--;
            else if(ax > 0) ax--;
            else break;
        }
    }


    public void draw(Graphics2D g){
        //g.setColor(Const.OPAQUE_COLOR);
        //g.drawRect(x, y, width, height);

        g.setColor(Const.OPAQUE_DARK_COLOR);
        if(page > 0) g.fill(leftArrow);
        if(page < totalpages) g.fill(rightArrow);

        g.setFont(font);

        if(size() > 0) {
            int ix = 0, iy = 0;
            for (int i = page * pagesize; i < (page + 1) * pagesize; i++) {
                if (i >= items.length) break;
                int px = x + offx + ix * (itemwidth + gap), py = y + gap + iy * (itemheight + gap);

                g.setColor(Const.OPAQUE_COLOR);
                g.fillRect(px, py, itemwidth, itemheight);
                if (active && ix == ax && iy == ay) {
                    g.fillRect(px, py, itemwidth, itemheight);
                    if (System.currentTimeMillis() % 800 < 400) {
                        g.setColor(Color.GREEN);
                        g.drawRect(px, py, itemwidth, itemheight);
                    }
                    g.setColor(colorSelected);
                } else g.setColor(Color.WHITE);
                g.drawString(items[i], px + 10, py + itemheight - 5);

                ix++;
                if (ix >= columns) {
                    ix = 0;
                    iy++;
                    if (iy >= rows) break;
                }
            }
        } else {
            int pwidth = fm.stringWidth("... nothing found ..."),
                px = x + width/2 - pwidth/2,
                py = y + height/2 - 20;
            g.setColor(Const.OPAQUE_COLOR);
            g.fillRect(px, py-40, pwidth, 58);
            g.setColor(Color.WHITE);
            g.drawString("... nothing found ...", px, py);
        }
    }
}
