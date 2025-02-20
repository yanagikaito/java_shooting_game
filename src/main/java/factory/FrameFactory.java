package factory;

import frame.FrameSize;
import frame.GameFrame;
import window.GameWindow;

import javax.swing.*;

public class FrameFactory {

    static GameWindow gameWindow = new GameWindow();

    public static GameFrame createFrame(FrameSize size) {
        return () -> {
            JFrame frame = new JFrame();
            frame.setResizable(false);
            frame.setSize(size.width(), size.height());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle("Shooting");
            frame.add(gameWindow);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        };
    }
}
