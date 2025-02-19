package factory;

import panel.GamePanel;

import javax.swing.*;
import java.awt.*;

public class PanelFactory {
    public static GamePanel createPanel() {
        return () -> {
            JPanel panel = new JPanel();
            panel.setBackground(Color.BLACK);
            panel.setDoubleBuffered(true);
            panel.setFocusable(true);
            return panel;
        };
    }
}
