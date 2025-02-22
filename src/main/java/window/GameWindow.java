package window;

import factory.FrameFactory;
import frame.FrameApp;
import frame.GameFrame;
import key.KeyHandler;

import javax.swing.*;

import java.awt.*;

import static frame.FrameApp.baseDisplay;

public class GameWindow extends JPanel implements Window, Runnable {

    private GameFrame gameFrame = FrameFactory.createFrame(baseDisplay());
    private Thread gameThread;
    private KeyHandler keyHandler = new KeyHandler(this);
    private int playerX = 100;
    private int playerY = 100;
    private int playerSpeed = 4;

    public GameWindow() {
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
        this.startThread();
    }

    @Override
    public void frame() {

        gameFrame.createFrame();
    }

    public void startThread() {

        gameThread = new Thread(this);

        gameThread.start();
    }

    @Override
    public void run() {

        int fps = 60;
        int nanosecond = 1000000000;
        double drawInterval = (double) nanosecond / fps;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= nanosecond) {
                System.out.println("FPS:" + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        if (keyHandler.getUpPressed() == true) {
            playerY -= playerSpeed;
        }
        if (keyHandler.getDownPressed() == true) {
            playerY += playerSpeed;
        }
        if (keyHandler.getLeftPressed() == true) {
            playerX -= playerSpeed;
        }
        if (keyHandler.getRightPressed() == true) {
            playerX += playerSpeed;
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLUE);
        g2.fillRect(playerX + 12, playerY - 10, FrameApp.createSize() / 2, FrameApp.createSize() / 2);
        g2.fillRect(playerX + 12, playerY, FrameApp.createSize() / 2, FrameApp.createSize() / 2);
        g2.fillRect(playerX, playerY, (FrameApp.createSize() / 2) * 2, FrameApp.createSize() / 2);
    }
}