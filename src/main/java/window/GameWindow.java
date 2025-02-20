package window;

import factory.FrameFactory;
import frame.FrameApp;
import frame.GameFrame;

import javax.swing.*;

import java.awt.*;

import static frame.FrameApp.baseDisplay;

public class GameWindow extends JPanel implements Window, Runnable {

    private final GameFrame gameFrame;
    private Thread gameThread;

    public GameWindow() {
        gameFrame = FrameFactory.createFrame(baseDisplay());
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    @Override
    public void frame() {

        gameFrame.createFrame();
        startThread();
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

    }

    @Override
    public void paintComponent(final Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLUE);
        g2.fillRect(100, 100, FrameApp.createSize() / 2, FrameApp.createSize() / 2);
        g2.dispose();
    }
}
