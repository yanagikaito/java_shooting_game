package factory;

import frame.FrameSize;
import frame.GameFrame;
import panel.GamePanel;

import javax.swing.*;


public class FrameFactory {

    static GamePanel gamePanel = PanelFactory.createPanel();

    public static GameFrame createFrame(FrameSize size) {
        return () -> {
            JFrame frame = new JFrame();
            frame.setResizable(false);
            frame.setSize(size.width(), size.height());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle("Shooting");
            frame.add(gamePanel.createPanel());

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        };
    }
}
