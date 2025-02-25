package window;

import factory.FrameFactory;
import frame.FrameApp;
import frame.GameFrame;
import game.Bullet;
import game.Enemy;
import game.EnumShootingScreen;
import key.KeyHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static frame.FrameApp.baseDisplay;
import static game.EnumShootingScreen.*;

public class GameWindow extends JPanel implements Window, Runnable {

    private GameFrame gameFrame = FrameFactory.createFrame(baseDisplay());
    private EnumShootingScreen screen = START;
    private Thread gameThread;
    private KeyHandler keyHandler = new KeyHandler(this);
    private ArrayList<Bullet> bulletsPlayer = new ArrayList<>();
    private ArrayList<Bullet> bulletsEnemy = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private Random random = new Random();
    private BufferedImage image;
    private int playerX = 235;
    private int playerY = 435;
    private int playerSpeed = 4;
    private int bulletInterval = 0;
    private int score = 0;
    private int level = 0;
    private int hitCount = 0;
    long levelTimer = 0;

    public GameWindow() {
        this.setBackground(Color.LIGHT_GRAY);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
        this.startThread();
        this.loadPlayerImage();
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

    public void spawnEnemy() {
        int enemyX = random.nextInt(768);  // ウィンドウ幅内のランダムなx位置
        int enemyY = 0;  // ウィンドウの上部から開始
        int enemySpeed = 2;  // 敵の速度を設定
        enemies.add(new Enemy(enemyX, enemyY, enemySpeed));
    }

    public void loadPlayerImage() {

        try {

            image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("プレイヤー.png"));

        } catch (IOException e) {
            e.printStackTrace();
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
        if (keyHandler.getEnterPressed()) {
            screen = EnumShootingScreen.GAME;
            bulletsPlayer = new ArrayList<>();
            bulletsEnemy = new ArrayList<>();
            enemies = new ArrayList<>();
            playerX = 235;
            playerY = 430;
            score = 0;
            level = 0;
            hitCount = 0;
        }

        // スペースキーが押されたときに弾丸を生成
        if (keyHandler.getSpacePressed() && bulletInterval == 0) {
            double direction = Math.random() * 360;
            bulletsPlayer.add(new Bullet(playerX + 12, playerY, (int) direction));

            // 連射間隔の設定
            bulletInterval = 2;
        }
        if (bulletInterval > 0) bulletInterval--;

        // ランダムな間隔で敵をスポーン
        if (random.nextInt(100) < 1) {  // スポーンの頻度を調整
            spawnEnemy();
        }

        // 各敵の位置を更新
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            enemy.setY(enemy.getY() + enemy.getSpeed());  // Y座標を速度分増加させる

            // 画面外に出た敵を削除
            if (enemy.getY() > 576) {
                enemies.remove(i);
                i--;
            }
        }

        // 弾丸の位置更新
        for (int i = 0; i < bulletsPlayer.size(); i++) {
            Bullet bullet = bulletsPlayer.get(i);
            int speed = 10;
            bullet.setX(bullet.getX() + (int) (Math.cos(Math.toRadians(bullet.getDirection())) * speed));
            bullet.setY(bullet.getY() - (int) (Math.sin(Math.toRadians(bullet.getDirection())) * speed));
            if (bullet.getY() < 0 || bullet.getX() < 0
                    || bullet.getX() > 768 || bullet.getY() > 576) {
                bulletsPlayer.remove(i);
                i--;
            }

            for (int j = 0; j < enemies.size(); j++) {
                Enemy enemy = enemies.get(j);
                if (bullet.getX() >= enemy.getX() && bullet.getX() <= enemy.getX() + FrameApp.createSize() &&
                        bullet.getY() >= enemy.getY() && bullet.getY() <= enemy.getY() + FrameApp.createSize()) {
                    enemies.remove(j);
                    score += 10;
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        switch (screen) {
            case START:

                g2.setColor(Color.BLUE);
                Font font = new Font("SansSerif", Font.PLAIN, 50);
                g2.setFont(font);
                FontMetrics metrics = g2.getFontMetrics(font);
                g2.drawString("Shooting", 250 - (metrics.stringWidth("Shooting") / 2), 100);
                font = new Font("SansSerif", Font.PLAIN, 20);
                g2.setFont(font);
                metrics = g2.getFontMetrics(font);
                g2.drawString("Press ENTER to Start", 250 - (metrics.stringWidth("Press ENTER to Start") / 2), 160);

            case GAME:

                g2.drawImage(image, playerX, playerY,
                        FrameApp.createSize() * 2, FrameApp.createSize() * 2, null);

                g2.setColor(Color.BLUE);
                // プレイヤーの弾丸の描画
                for (Bullet bullet : bulletsPlayer) {
                    g2.fillRect(bullet.getX() + 15, bullet.getY() + 15,
                            FrameApp.createSize() / 4, FrameApp.createSize() / 4);
                }

                g2.setColor(Color.RED);

                for (int i = 0; i < enemies.size(); i++) {
                    Enemy enemy = enemies.get(i);

                    g2.fillRect(enemy.getX(), enemy.getY(), (FrameApp.createSize() / 2) * 2, FrameApp.createSize() / 2);
                    g2.fillRect(enemy.getX() + (FrameApp.createSize() / 2) - 12, enemy.getY() + 10,
                            FrameApp.createSize() / 2, FrameApp.createSize() / 2);

                    double direction = Math.random() * 1440;

                    bulletsEnemy.add(new Bullet(enemy.getX() + 20, enemy.getY() + 10, (int) direction));
                    enemy.setY(enemy.getY() + 3);
                    if (enemy.getY() > 576) {
                        enemies.remove(i);
                        i--;
                    }
                    if (random.nextInt(level < FrameApp.createSize() ? 68 - level : 100) == 1)
                        bulletsEnemy.add(new Bullet(enemy.getX() + 12, enemy.getY(), random.nextInt(1440)));

                    if ((enemy.getX() >= playerX && enemy.getX() <= playerX + FrameApp.createSize() &&
                            enemy.getY() >= playerY && enemy.getY() <= playerX + FrameApp.createSize()) ||
                            (enemy.getX() + FrameApp.createSize() >= playerY && enemy.getX() + FrameApp.createSize()
                                    <= playerX + FrameApp.createSize() &&
                                    enemy.getY() + FrameApp.createSize()
                                            >= playerY && enemy.getY() + FrameApp.createSize()
                                    <= playerY + FrameApp.createSize())) {
                        score += (level + 1) * 100;
                    }
                }
                if (random.nextInt(level < FrameApp.createSize() ? 68 - level : FrameApp.createSize()) == 1)
                    enemies.add(new Enemy(random.nextInt(768), 0, 0));

                for (int i = 0; i < bulletsEnemy.size(); i++) {
                    Bullet bullet = bulletsEnemy.get(i);
                    g2.fillRect(bullet.getX(), bullet.getY(), FrameApp.createSize() / 4, FrameApp.createSize() / 4);
                    int speed = 5;
                    bullet.setX(bullet.getX() + (int) (Math.cos(Math.toRadians(bullet.getDirection())) * speed));
                    bullet.setY(bullet.getY() - (int) (Math.sin(Math.toRadians(bullet.getDirection())) * speed));
                    if (bullet.getY() < FrameApp.createSize() ||
                            bullet.getX() < FrameApp.createSize() || bullet.getX() > 768 || bullet.getY() > 576) {
                        bulletsEnemy.remove(i);
                        i--;
                    }
                    if (bullet.getX() >= playerX && bullet.getX() <= playerX + FrameApp.createSize() / 2 &&
                            bullet.getY() >= playerY && bullet.getY() <= playerY + FrameApp.createSize() / 2) {
                        hitCount++;
                        score += hitCount;
                        System.out.println("hitCount: " + hitCount);
                        if (hitCount >= 1000) {
                            System.out.println(hitCount);
                            screen = EnumShootingScreen.GAME_OVER;
                        } else if (score >= 1000) {
                            level++;
                        }
                        System.out.println(level);
                    }
                }

                g2.setColor(Color.WHITE);
                font = new Font("SanSerif", Font.PLAIN, 20);
                metrics = g2.getFontMetrics(font);
                g2.setFont(font);
                g2.drawString("SCORE" + score, 470 - metrics.stringWidth("SCORE:" + score), 430);
                g2.drawString("LEVEL" + level, 470 - metrics.stringWidth("LEVEL:" + level), 450);

                break;
            case GAME_OVER:
                g2.setColor(Color.WHITE);

                font = new Font("SansSerif", Font.PLAIN, 50);
                g2.setFont(font);
                metrics = g2.getFontMetrics(font);
                g2.drawString("Game Over", 350 - (metrics.stringWidth("Game Over") / 2), 100);
        }
    }
}