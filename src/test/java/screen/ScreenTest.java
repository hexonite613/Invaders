package screen;

import engine.Frame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScreenTest {
    private Screen currentScreen;
    @BeforeEach
    void setUp() {
        currentScreen = new TitleScreen(448, 520, 60);
    }

    @Test
    @DisplayName("Testing ratio calculation")
    void getRatio() {
        assertEquals(1,(int)currentScreen.getRatio());
    }
}