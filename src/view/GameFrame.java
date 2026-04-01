package view;

import javax.swing.*;

public class GameFrame extends JFrame {

    public GameFrame() {

        setTitle("Tetris Game");

        GamePanel panel = new GamePanel();
        add(panel);

        pack(); 

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setVisible(true);

        panel.requestFocusInWindow(); //nhan phim
    }
}
