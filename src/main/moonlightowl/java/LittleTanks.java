package main.moonlightowl.java;

/**
 * Little Tanks
 * April 2015 (c) MoonlightOwl
 */

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LittleTanks extends JFrame {
    public LittleTanks(){
        final Board board = new Board();
        add(board);
        setTitle("Little Tanks");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(Const.WIDTH, Const.HEIGHT);
        setIgnoreRepaint(true);
        setResizable(false);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                board.quitGame();
            }
        });
        setVisible(true);
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
