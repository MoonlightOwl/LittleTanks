package main.moonlightowl.java.gui.component;

/*
 * Code from LineBreak  ~  v.0.2  ~  Totoro  ~  Menu items, updating and drawing.
 */

import main.moonlightowl.java.Const;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Menu {
    public static final int NOTHING = -1, MAX_LENGTH = 20;

    private Font font;
    private FontMetrics metr;
    private Iterator it;
    private List<Item> items = new ArrayList<Item>();
    private int x, y, interval;
    private Item pointer;

    public Menu(Font font, FontMetrics metr, int x, int y, int interval){
        this.font = font;
        this.metr = metr;
        this.x = x;
        this.y = y;
        this.interval = interval;
    }

    // classes
    private class Item extends SubItem{
        List<SubItem> subItems = new ArrayList<SubItem>();
        boolean expand = false;

        Item(String name) {
            super(name);
        }
        void addSubItem(String name){
            subItems.add(new SubItem(name));
        }
        boolean isSelected(){ return selected; }
    }
    private class SubItem{
        String name;
        Rectangle rect;
        boolean selected;

        SubItem(String name){
            init(name);
            this.rect = new Rectangle(x-metr.stringWidth(name)/2,
                    y+items.size()*interval-font.getSize()+10, metr.stringWidth(name), font.getSize());
        }
        SubItem(String name, int num, int offset){
            init(name);
            this.rect = new Rectangle(x-metr.stringWidth(name)/2+offset*300-150,
                    y+num*interval-font.getSize(), metr.stringWidth(name), font.getSize());
        }

        private void init(String name){
            if(name.length() <= MAX_LENGTH)
                this.name = name;
            else
                this.name = name.substring(0, MAX_LENGTH);
            this.selected = false;
        }
    }

    // get
    public boolean isSelected(int num){
        return items.get(num).isSelected();
    }
    public int getSelected(){
        for(Item item: items){
            if(item.selected) return items.indexOf(item);
        }
        return NOTHING;
    }
    public int getSubSelected(int num){
        for(SubItem item: items.get(num).subItems){
            if(item.selected) return items.get(num).subItems.indexOf(item);
        }
        return NOTHING;
    }
    public boolean isExpand(int num){
        return items.get(num).expand;
    }
    // set
    public void setExpand(int num, boolean expand){
        items.get(num).expand = expand;
    }
    public void setName(int num, String name){
        items.get(num).name = name;
    }
    public void setSubname(int num, int offset, String name){
        items.get(num).subItems.get(offset).name = name;
    }
    public void deselectAll(){
        for(Item item: items){
            item.selected = false;
        }
    }
    // other
    public void addItem(String name){
        items.add(new Item(name));
    }
    public void addSubItem(int num, String name){
        items.get(num).subItems.add(new SubItem(name, num, items.get(num).subItems.size()));
    }

    public void draw(Graphics g){
        int i=0, j;
        g.setFont(font);
        g.setColor(Const.MENU_COLOR);
        it = items.iterator();
        while(it.hasNext()){
            pointer = (Item)it.next();
            if(pointer.expand){
                j = 0;
                for(SubItem subItem: pointer.subItems){
                    if(subItem.selected) g.setColor(Const.MENU_SELECTED_COLOR);
                    g.drawString(subItem.name,
                            x - metr.stringWidth(pointer.name)/2 - 150 + j*250, y + i*interval);
                    if(subItem.selected) g.setColor(Const.MENU_COLOR);
                    j++;
                }
            }
            else{
                g.setColor(Const.MENU_SHADOW_COLOR);
                g.drawString(pointer.name, x - metr.stringWidth(pointer.name)/2, y + i*interval + 2);
                if(pointer.selected){
                    g.setColor(Const.MENU_SELECTED_COLOR);
                    g.drawString(pointer.name, x - metr.stringWidth(pointer.name)/2, y + i*interval);
                } else {
                    g.setColor(Const.MENU_COLOR);
                    g.drawString(pointer.name, x - metr.stringWidth(pointer.name)/2, y + i*interval);
                }
            }
            i++;
        }
    }

    public void mouseMoved(MouseEvent e){
        it = items.iterator();
        while(it.hasNext()){
            pointer = (Item)it.next();
            if(pointer.expand){
                for(SubItem subItem: pointer.subItems){
                    subItem.selected = subItem.rect.contains(e.getPoint());
                }
                pointer.selected = false;
            }
            else{
                pointer.selected = pointer.rect.contains(e.getPoint());
            }
        }
    }

    public void keyPressed(KeyEvent e){
        switch(e.getKeyCode()){
            case KeyEvent.VK_UP:
                int active1 = getSelected();
                if(active1 != -1) items.get(active1).selected = false;
                items.get((active1 + items.size() - 1) % items.size()).selected = true;
                break;
            case KeyEvent.VK_DOWN:
                int active2 = getSelected();
                if(active2 != -1) items.get(active2).selected = false;
                items.get((active2 + 1) % items.size()).selected = true;
                break;
        }
    }
}