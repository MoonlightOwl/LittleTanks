package main.moonlightowl.java.sound;

import main.moonlightowl.java.Logger;

import javax.sound.sampled.*;
import java.io.File;

public enum Sound {
    EXPLODE("resources/sounds/explosion.wav"),
    SHOOT("resources/sounds/railgun.wav"),
    LAUNCH("resources/sounds/launcher.wav"),
    LASER("resources/sounds/laser.wav"),
    NOAMMO("resources/sounds/noammo.wav"),
    HIT("resources/sounds/hit.wav"),
    BEEP("resources/sounds/beep.wav"),
    PICKUP("resources/sounds/bonus.wav"),
    LOCK("resources/sounds/lock.wav"),
    SHIELD("resources/sounds/robot-shield.wav"),
    FREEZE("resources/sounds/robot-slowdown.wav"),
    WINNER("resources/sounds/robot-winner.wav"),
    GAMEOVER("resources/sounds/robot-gameover.wav");

    public enum Volume {
        MUTE, LOW, MEDIUM, HIGH
    }

    public static Volume volume = Volume.LOW;

    private Clip clip;

    Sound(String filename){
        try{
            //URL url = this.getClass().getClassLoader().getResource(filename);
            File url = new File(filename);
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            AudioFormat format = ais.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip)AudioSystem.getLine(info);
            clip.open(ais);
        }
        catch(Exception e){     // pokemon exception handling, yeah, yeah, i know =)
            Logger.trace(e);
        }
    }

    public void play(){
        if(volume != Volume.MUTE){
            stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }
    public void stop(){
        if(clip.isRunning()){ clip.stop(); }
    }

    public boolean isPlaying(){ return clip.isRunning(); }

    public static void init(){
        values();
    }
}