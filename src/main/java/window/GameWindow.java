package window;

import factory.FrameFactory;
import frame.GameFrame;

import static frame.FrameApp.baseDisplay;


public class GameWindow implements Window {

    private final GameFrame gameFrame = FrameFactory.createFrame(baseDisplay());

    @Override
    public void frame() {

        gameFrame.createFrame();
    }
}
