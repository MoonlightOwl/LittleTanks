package main.moonlightowl.java.sound;

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

    public void mute(){
        mute = !mute;
    }
    public boolean muted(){ return mute; }

    public void play(Sound sound){
        if(!mute || sound == null) {
            synchronized (lock) {
                toPlay = sound;
                lock.notify();
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
                } catch(InterruptedException e){ break; }
            }
        }
    }

    /*private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void play(final Sound sound) {
        executor.execute(new Runnable(){
            public void run(){
                sound.play();
            }
        });
    }

    public void close() {
        executor.shutdown();
    }*/
}