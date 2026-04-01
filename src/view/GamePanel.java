package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel {

    //HẰNG SỐ CẤU HÌNH 
    private static final int TILE_W = 25;   
    private static final int TILE_H = 25;
    private static final int COLS = 10;
    private static final int ROWS = 20;
    private static final int SPEED = 400;   
    // ĐỊNH NGHĨA CÁC KHỐI GẠCH 
    private final int[][][] SHAPES = {
        {{0,0}, {1,0}, {2,0}},          // 3 ô ngang
        {{0,0}, {1,0}, {2,0}, {3,0}},   // Khối I
        {{0,0}, {0,1}, {1,1}, {2,1}},   // Khối L
        {{0,0}, {1,0}, {0,1}, {1,1}},   // Khối Vuông 
        {{1,0}, {0,1}, {1,1}, {2,1}}    // Khối chữ T
    };

    private final Color[] COLORS = {
        Color.CYAN, Color.BLUE, Color.ORANGE, Color.YELLOW, Color.MAGENTA
    };

    // TRẠNG THÁI GAME
    private int currentX, currentY;
    private int[][] currentShape;
    private Color currentColor;
    private int score = 0;
    private final List<int[]> fixedBlocks = new ArrayList<>();
    private final Random rand = new Random();
    private Timer gameTimer;

    public GamePanel() {
        setPreferredSize(new Dimension(COLS * TILE_W, ROWS * TILE_H));
        setBackground(new Color(20, 20, 20)); 
        setFocusable(true);

        initControls();
        spawnNewBlock();
        
        gameTimer = new Timer(SPEED, e -> {
            moveDown();
            repaint();
        });
        gameTimer.start();
    }
    //HÀM XOAY KHỐI GẠCH 
    private void rotateShape() {
        // Khối vuông không cần xoay để tránh lệch vị trí
        if (currentShape == SHAPES[3]) return;

        int[][] rotated = new int[currentShape.length][2];
        for (int i = 0; i < currentShape.length; i++) {
            // Công thức xoay ma trận 90 độ
            rotated[i][0] = -currentShape[i][1];
            rotated[i][1] = currentShape[i][0];
        }

        // Kiểm tra xem sau khi xoay có bị đè vào tường hoặc gạch cũ không
        if (!checkCollisionAt(currentX, currentY, rotated)) {
            currentShape = rotated;
        }
    }

    // TẠO KHỐI MỚI
    private void spawnNewBlock() {
        int type = rand.nextInt(SHAPES.length);
        currentShape = SHAPES[type];
        currentColor = COLORS[type];
        
        currentX = (COLS / 2) * TILE_W;
        currentY = 0;

        if (checkCollisionAt(currentX, currentY, currentShape)) {
            gameTimer.stop();
            JOptionPane.showMessageDialog(this, "GAME OVER! Điểm của bạn: " + score);
            resetGame();
        }
    }

    private void resetGame() {
        fixedBlocks.clear();
        score = 0;
        spawnNewBlock();
        gameTimer.start();
    }

    //ĐIỀU KHIỂN
    private void initControls() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> {
                        if (!checkCollisionAt(currentX - TILE_W, currentY, currentShape)) currentX -= TILE_W;
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (!checkCollisionAt(currentX + TILE_W, currentY, currentShape)) currentX += TILE_W;
                    }
                    case KeyEvent.VK_DOWN -> moveDown();
                    case KeyEvent.VK_UP, KeyEvent.VK_SPACE -> rotateShape(); // Xoay bằng phím lên hoặc Space
                }
                repaint();
            }
        });
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void moveDown() {
        if (!checkCollisionAt(currentX, currentY + TILE_H, currentShape)) {
            currentY += TILE_H;
        } else {
            freezeBlock();
        }
    }

    // Kiểm tra va chạm tại một vị trí cụ thể với một hình dạng cụ thể
    private boolean checkCollisionAt(int nx, int ny, int[][] shape) {
        for (int[] p : shape) {
            int tx = nx + p[0] * TILE_W;
            int ty = ny + p[1] * TILE_H;

            if (tx < 0 || tx >= COLS * TILE_W || ty >= ROWS * TILE_H) return true;
            for (int[] b : fixedBlocks) {
                if (tx == b[0] && ty == b[1]) return true;
            }
        }
        return false;
    }

    private void freezeBlock() {
        for (int[] p : currentShape) {
            fixedBlocks.add(new int[]{currentX + p[0] * TILE_W, currentY + p[1] * TILE_H, currentColor.getRGB()});
        }
        clearFullRows();
        spawnNewBlock();
    }

    private void clearFullRows() {
        for (int r = 0; r < ROWS; r++) {
            int targetY = r * TILE_H;
            int count = 0;
            for (int[] b : fixedBlocks) if (b[1] == targetY) count++;

            if (count >= COLS) {
                fixedBlocks.removeIf(b -> b[1] == targetY);
                for (int[] b : fixedBlocks) if (b[1] < targetY) b[1] += TILE_H;
                score += 100;
                r--;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Vẽ lưới nền
        g.setColor(new Color(45, 45, 45));
        for (int i = 0; i <= COLS; i++) g.drawLine(i * TILE_W, 0, i * TILE_W, ROWS * TILE_H);
        for (int i = 0; i <= ROWS; i++) g.drawLine(0, i * TILE_H, COLS * TILE_W, i * TILE_H);

        // Vẽ các khối cố định
        for (int[] b : fixedBlocks) {
            g.setColor(new Color(b[2]));
            g.fillRoundRect(b[0] + 1, b[1] + 1, TILE_W - 2, TILE_H - 2, 5, 5); 
        }

        // Vẽ khối đang rơi
        g.setColor(currentColor);
        for (int[] p : currentShape) {
            int dx = currentX + p[0] * TILE_W;
            int dy = currentY + p[1] * TILE_H;
            g.fillRoundRect(dx + 1, dy + 1, TILE_W - 2, TILE_H - 2, 5, 5);
        }

        // Vẽ UI thông tin
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("SCORE: " + score, 15, 25);
    }
}
