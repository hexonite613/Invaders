package engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import screen.GameSummaryScreen;
import screen.Screen;

import static org.junit.jupiter.api.Assertions.*;

class GameSummaryTest {
    private Screen currentScreen;
    private static final int adjust=30;

    @BeforeEach
    void setUp(){
        currentScreen = new GameSummaryScreen(448, 520, 60);
    }


    @org.junit.jupiter.api.Test
    @DisplayName("Checking whether game summary screen properly works")
    void checksummaryscreen() {
        assertNotNull(currentScreen);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("checking positions of words")
    void Placecheck(){
        assertEquals(100,currentScreen.getHeight() / 4-adjust);
        assertEquals(160,currentScreen.getHeight() / 4+adjust);
        assertEquals(190,currentScreen.getHeight() / 4+adjust*2);
        assertEquals(220,currentScreen.getHeight() / 4+adjust*3);
        assertEquals(310,currentScreen.getHeight() / 4+adjust*6);
        assertEquals(340,currentScreen.getHeight() / 4+adjust*7);
        assertEquals(370,currentScreen.getHeight() / 4+adjust*8);
        assertEquals(400,currentScreen.getHeight() / 4+adjust*9);
        assertEquals(460,currentScreen.getHeight() / 4+adjust*11);
    }

}