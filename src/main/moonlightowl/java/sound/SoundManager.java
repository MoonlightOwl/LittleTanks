package main.moonlightowl.java.sound;

import main.moonlightowl.java.Logger;

public class SoundManager implements Runnable{
    private final Object lock;
    private Sound toPlay;
    private boolean mute;

    public SoundManager(){
        mute = false;
        lock = new Object();
        new Thread(this, "Sound Thread").start();
    }

    public void close(){
        play(null);
    }

    public void toggle(){
        mute = !mute;
    }
    public boolean muted(){ return mute; }

    public void play(Sound sound){
        if(!mute || sound == null) {
            synchronized (lock) {
                toPlay = sound;
                lock.notifyAll();
            }
        }
    }

    public void run(){
        while(true){
            synchronized(lock){
                try {
                    lock.wait();
                    if(toPlay != null)
                        toPlay.play();
                    else break;
                } catch(InterruptedException e) {
                    Logger.warning("Sound manager thread was interrupted");
                    break;
                }
            }
        }
    }
}