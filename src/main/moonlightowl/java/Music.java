package main.moonlightowl.java;

import javazoom.jl.player.advanced.*;
import javazoom.jl.decoder.JavaLayerException;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;

import java.util.Random;

public class Music {
	private File[] filelist;
	private Layer player;
    private Random rand = new Random(System.currentTimeMillis());
	
	public Music(String dir) {
		FileFilter filter = new FileFilter(){
			public boolean accept(File file){
				return file.getName().endsWith(".mp3");
			}
		};
		filelist = new File(dir).listFiles(filter);
		player = new Layer(filelist[rand.nextInt(filelist.length)]);
	}
	
	public void play(){
		player = new Layer(filelist[rand.nextInt(filelist.length)]);
		player.play();
	}
	public void stop(){
		if(player != null) player.stop();
	}
	public void next(){
		stop();
		play();
	}
	
	private class Layer extends PlaybackListener implements Runnable {
		private File file;
		private AdvancedPlayer player;
		private Thread playerThread;
		private boolean playing = false;
		
		public Layer(File file) {
			this.file = file;
		}
		
		public void play() {
			try {
				player = new AdvancedPlayer(new FileInputStream(file));
				player.setPlayBackListener(this);
				playerThread = new Thread(this);
				this.playerThread.start();
			}
			catch(Exception e){ e.printStackTrace(); }
		}
		
		public void stop(){
			if(playing){
				playing = false;
				player.stop();
				//playerThread.stop();
				playerThread = null;
			}
		}
		
		public void playbackStarted(PlaybackEvent e){
			
		}
		public void playbackFinished(PlaybackEvent e){
			playing = false;
		}
		
		public void run(){
			playing = true;
			try{
				player.play();
			} catch(JavaLayerException e){ e.printStackTrace(); }
		}
	}
}