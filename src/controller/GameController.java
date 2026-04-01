package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameController extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            System.out.println("LEFT");
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            System.out.println("RIGHT");
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            System.out.println("DOWN");
        }
    }
}