package main.moonlightowl.java;

/*
 * LineBreak  ~  v.0.2  ~  NightOwl  ~  Scoreboard code.
 */

import java.awt.*;
import java.util.*;
import java.util.List;

public class Scores {
	public static final String key = "verysecretkey";
	
    private Iterator it;
    private List<Item> table;
    private Item pointer;
    private boolean visible = false;
    private Font font;
    private FontMetrics fm;
    private Rectangle rect;
	private String filename;
	private Item active;

    public Scores(String filename, Font font, FontMetrics fm){
        this.font = font; this.fm = fm;
        loadScores(filename);
        rect = new Rectangle(0, 220, Const.WIDTH, Const.SCOREBOARD_SIZE*font.getSize()+20);
    }
	private void initTable(){
		table = new ArrayList<Item>();
        while(table.size()<Const.SCOREBOARD_SIZE){
            table.add(new Item("John Doe", 0));
        }
	}

    public void addRecord(String name, int score){
        String nickname = name.length() == 0 ? "Anonymouse" : name;

        it = table.iterator();
        while(it.hasNext()){
            pointer = (Item)it.next();
            if(pointer.score<score){
				Item item = new Item(nickname, score);
                table.add(table.indexOf(pointer), item);
				active = item;
                break;
            }
        }
        if(table.size()>Const.SCOREBOARD_SIZE){
            table.remove(table.size()-1);
        }
    }

    // getters & setters
    public void setVisible(boolean visible){
		this.visible = visible; 
		if(!visible) active = null;
	}
    public boolean isVisible(){ return visible; }
    public int worst(){ return table.get(table.size()-1).score; }

    // classes
    private class Item{
        public String name;
        public int score;
        private String text;

        public Item(String name, int score){
            this.name = name;
            this.score = score;
            text = name+"  "+Integer.toString(score);
        }
    }

    // other
    public void draw(Graphics g){
        g.setColor(Const.OPAQUE_DARK_COLOR);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        it = table.iterator();
        while(it.hasNext()){
            pointer = (Item)it.next();
            g.setFont(font);
            if(pointer == active) 
				g.setColor(Color.ORANGE);
			else
				g.setColor(Color.WHITE);
            g.drawString(pointer.text, Const.WIDTH/2-fm.stringWidth(pointer.text)/2, 260+font.getSize()*table.indexOf(pointer));
        }
    }

    public void loadScores(String filename){
		this.filename = filename;
		initTable();
		
		byte[] array = Crypter.decrypt(BinaryIO.read(filename), key);
		String[] tokens = new String(array).split("\n");
		if(tokens.length >= 2){
			for(int c=0; c<tokens.length; c+=2){
				String name = tokens[c];
				int score = Integer.parseInt(tokens[c+1]);
	            addRecord(name, score);
			}
		}
		active = null;
    }

    public void saveScores(){
		String data = "";
		it = table.iterator();
        while(it.hasNext()){
            pointer = (Item)it.next();
			data = data + pointer.name + "\n" + pointer.score + "\n";
        }
		BinaryIO.write(Crypter.encrypt(data, key), filename);
    }
}