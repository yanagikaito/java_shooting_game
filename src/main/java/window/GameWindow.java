package window;

import factory.FrameFactory;
import frame.FrameApp;
import frame.GameFrame;
import game.Bullet;
import game.Enemy;
import game.EnumShootingScreen;
import key.KeyHandler;

import javax.swing.*;
import java.awt.*;
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
    private int playerX = 100;
    private int playerY = 100;
    private int playerSpeed = 4;
    private int bulletInterval = 0;
    private int score = 0;
    private int level = 0;
    private int hitCount = 0;
    long levelTimer = 0;

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

    public void spawnEnemy() {
        int enemyX = random.nextInt(768);  // ウィンドウ幅内のランダムなx位置
        int enemyY = 0;  // ウィンドウの上部から開始
        int enemySpeed = 2;  // 敵の速度を設定
        enemies.add(new Enemy(enemyX, enemyY, enemySpeed));
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
            // 連射間隔の設定
            bulletInterval = 8;
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
            if (bullet.getY() < 0 || bullet.getX() < 0 || bullet.getX() > 768 || bullet.getY() > 576) {
                bulletsPlayer.remove(i);
                i--;
            }

            for (int j = 0; j < enemies.size(); j++) {
                Enemy enemy = enemies.get(j);
                if (bullet.getX() >= enemy.getX() && bullet.getX() <= enemy.getX() + 30 &&
                        bullet.getY() >= enemy.getY() && bullet.getY() <= enemy.getY() + 20) {
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

                g2.setColor(Color.WHITE);
                Font font = new Font("SansSerif", Font.PLAIN, 50);
                g2.setFont(font);
                FontMetrics metrics = g2.getFontMetrics(font);


            case GAME:

                g2.setColor(Color.BLUE);
                g2.fillRect(playerX + 12, playerY - 10, FrameApp.createSize() / 2, FrameApp.createSize() / 2);
                g2.fillRect(playerX + 12, playerY, FrameApp.createSize() / 2, FrameApp.createSize() / 2);
                g2.fillRect(playerX, playerY, (FrameApp.createSize() / 2) * 2, FrameApp.createSize() / 2);

                // プレイヤーの弾丸の描画
                for (Bullet bullet : bulletsPlayer) {
                    g2.fillRect(bullet.getX(), bullet.getY(), FrameApp.createSize() / 4, FrameApp.createSize() / 4);
                }

                g2.setColor(Color.RED);

                for (int i = 0; i < enemies.size(); i++) {
                    Enemy enemy = enemies.get(i);
                    g2.fillRect(enemy.getX(), enemy.getY(), (FrameApp.createSize() / 2) * 2, FrameApp.createSize() / 2);
                    g2.fillRect(enemy.getX() + (FrameApp.createSize() / 2) - 12, enemy.getY() + 10,
                            FrameApp.createSize() / 2, FrameApp.createSize() / 2);
                    double direction = Math.random() * 360;
                    bulletsEnemy.add(new Bullet(enemy.getX() + 12, enemy.getY(), (int) direction));
                    enemy.setY(enemy.getY() + 3);
                    if (enemy.getY() > 576) {
                        enemies.remove(i);
                        i--;
                    }
                    if (random.nextInt(level < 50 ? 80 - level : 30) == 1)
                        bulletsEnemy.add(new Bullet(enemy.getX() + 12, enemy.getY(), random.nextInt(360)));
                    if ((enemy.getX() >= playerX && enemy.getX() <= playerX + 30 &&
                            enemy.getY() >= playerY && enemy.getY() <= playerX + 20) ||
                            (enemy.getX() + 30 >= playerY && enemy.getX() + 30 <= playerX + 30 &&
                                    enemy.getY() + 20 >= playerY && enemy.getY() + 20 <= playerY + 20)) {
                        score += (level - 1) * 100;
                    }
                }
                if (random.nextInt(level < 10 ? 30 - level : 10) == 1)
                    enemies.add(new Enemy(random.nextInt(768), 0, 0));
                for (int i = 0; i < bulletsEnemy.size(); i++) {
                    Bullet bullet = bulletsEnemy.get(i);
                    g2.fillRect(bullet.getX(), bullet.getY(), FrameApp.createSize() / 4, FrameApp.createSize() / 4);
                    int speed = 8;
                    bullet.setX(bullet.getX() + (int) (Math.cos(Math.toRadians(bullet.getDirection())) * speed));
                    bullet.setY(bullet.getY() - (int) (Math.sin(Math.toRadians(bullet.getDirection())) * speed));
                    if (bullet.getY() < 0 || bullet.getX() < 0 || bullet.getX() > 768 || bullet.getY() > 576) {
                        bulletsEnemy.remove(i);
                        i--;
                    }
                    if (bullet.getX() >= playerX && bullet.getX() <= playerX + 30 &&
                            bullet.getY() >= playerY && bullet.getY() <= playerY + 20) {
                        if (hitCount >= 100) {
                            screen = EnumShootingScreen.GAME_OVER;
                            score += (level - 1) * 100;
                        } else {
                            hitCount++;
                            System.out.println(hitCount);
                        }
                    }
                }
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