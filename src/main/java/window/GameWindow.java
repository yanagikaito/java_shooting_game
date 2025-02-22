package window;

import factory.FrameFactory;
import frame.FrameApp;
import frame.GameFrame;
import game.Bullet;
import key.KeyHandler;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static frame.FrameApp.baseDisplay;

public class GameWindow extends JPanel implements Window, Runnable {

    private GameFrame gameFrame = FrameFactory.createFrame(baseDisplay());
    private Thread gameThread;
    private KeyHandler keyHandler = new KeyHandler(this);
    private ArrayList<Bullet> bulletsPlayer = new ArrayList<>();
    private Random random = new Random();
    private int playerX = 100;
    private int playerY = 100;
    private int playerSpeed = 4;
    private int bulletInterval = 0;

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
        if (keyHandler.getUpPressed()) {
            playerY -= playerSpeed;
        }
        if (keyHandler.getDownPressed()) {
            playerY += playerSpeed;
        }
        if (keyHandler.getLeftPressed()) {
            playerX -= playerSpeed;
        }
        if (keyHandler.getRightPressed()) {
            playerX += playerSpeed;
        }

        // スペースキーが押されたときに弾丸を生成
        if (keyHandler.getSpacePressed() && bulletInterval == 0) {
            double direction = Math.random() * 360;
            bulletsPlayer.add(new Bullet(playerX + 12, playerY, (int) direction));
            bulletInterval = 8; // 連射間隔の設定
        }
        if (bulletInterval > 0) bulletInterval--;

        // 弾丸の位置更新
        for (int i = 0; i < bulletsPlayer.size(); i++) {
            Bullet bullet = bulletsPlayer.get(i);
            int speed = 15;
            bullet.setX(bullet.getX() + (int) (Math.cos(Math.toRadians(bullet.getDirection())) * speed));
            bullet.setY(bullet.getY() - (int) (Math.sin(Math.toRadians(bullet.getDirection())) * speed));
            if (bullet.getY() < 0 || bullet.getX() < 0 || bullet.getX() > 768 || bullet.getY() > 576) {
                bulletsPlayer.remove(i);
                i--;
            }
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

        // 弾丸の描画
        for (Bullet bullet : bulletsPlayer) {
            g2.fillRect(bullet.getX(), bullet.getY(), FrameApp.createSize() / 4, FrameApp.createSize() / 4);
        }
    }
}