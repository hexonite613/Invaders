package engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;import java.awt.Insets;

class FrameTest {
    private static Frame frame;
    protected Insets insets;

    @BeforeEach
    void setUp() {
        int WIDTH=100;
        int HEIGHT=100;
        frame = new Frame(WIDTH, HEIGHT);
    }

    @Test
    @DisplayName("Testing getting Width")
    void getWidth() {
        insets= frame.getInsets();
        assertEquals(100,frame.getWidth()+ insets.left+ insets.right);
    }


    @Test
    @DisplayName("Testing getting height")
    void getHeight() {
        insets= frame.getInsets();
        assertEquals(100,frame.getHeight()+ insets.top- insets.bottom);
    }

    @Test
    @DisplayName("Testing resizing screen")
    void resizingScreen() {
        frame.setSize(200, 200);
        frame.resizingScreen();
        insets=frame.getInsets();

        assertEquals(200, frame.getHeight()+ insets.top- insets.bottom);
//      계산되는 방식이 다름(resizing screen 메소드 참고)
        assertEquals(173, frame.getWidth()+ insets.left+ insets.right);
    }
}