package main.moonlightowl.java;

/**
 * Little Tanks
 * April 2015 (c) MoonlightOwl
 */

import javax.swing.*;

public class LittleTanks extends JFrame {
    public LittleTanks(){
        add(new Board());
        setTitle("Little Tanks");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(Const.WIDTH,Const.HEIGHT);
        setIgnoreRepaint(true);
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LittleTanks();
            }
        });
    }
}
