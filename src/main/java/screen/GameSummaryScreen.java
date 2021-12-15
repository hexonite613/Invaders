package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

import engine.Core;
import engine.Score;

/**
 * Implements the high scores screen, it shows player records.
 *
 *@author<a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class GameSummaryScreen extends Screen {

    /** List of past high scores. */
    private List<Score> highScores;

    /**
     * Constructor, establishes the properties of the screen.
     *
     *@paramwidth
     *            Screen width.
     *@paramheight
     *            Screen height.
     *@paramfps
     *            Frames per second, frame rate at which the game is run.
     */
    public GameSummaryScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        this.returnCode = 2;

        try {
            this.highScores = Core.getFileManager().loadHighScores();
        } catch (NumberFormatException | IOException e) {
            logger.warning("Couldn't load high scores!");
        }
    }

    /**
     * Starts the action.
     *
     *@returnNext screen code.
     */
    public final int run() {
        super.run();

        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        draw();
        if (inputManager.isKeyDown(KeyEvent.VK_SPACE)
                && this.inputDelay.checkFinished())
            this.isRunning = false;
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawSummary(this);


        drawManager.completeDrawing(this);
    }
}
