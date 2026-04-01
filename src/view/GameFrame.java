package view;

import javax.swing.*;

public class GameFrame extends JFrame {

    public GameFrame() {

        setTitle("Tetris Game");

        GamePanel panel = new GamePanel();
        add(panel);

        pack(); // rất quan trọng

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setVisible(true);

        panel.requestFocusInWindow(); // để nhận phím
    }
}