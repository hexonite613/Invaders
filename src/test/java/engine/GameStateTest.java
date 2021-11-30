package engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameStateTest {
    GameState gameState;

    @BeforeEach
    void setUp() {
        List<GameSettings> gameSettings_Default;
        gameSettings_Default = new ArrayList<GameSettings>();
        gameSettings_Default.add(new GameSettings(5, 4, 60, 2000));
        gameState = new GameState(1, 0, 3, 0, 0);
    }


    @Test
    @DisplayName("Getting level")
    void getLevel() {
        assertEquals(1,gameState.getLevel(),"Getting level correct");
    }
}