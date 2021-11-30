package engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameSettingsTest {
    GameSettings gameSettings_Default;

    @BeforeEach
    void setUp() {
        gameSettings_Default=new GameSettings(5, 4, 60, 2000);
    }


    @Test
    @DisplayName("Getting formation width")
    void getFormationWidth() {
        assertEquals(5, gameSettings_Default.getFormationWidth(),"Formation width correct");
    }

    @Test
    @DisplayName("Getting formation height")
    void getFormationHeight() {
        assertEquals(4, gameSettings_Default.getFormationHeight(),"Formation height correct");
    }

    @Test
    @DisplayName("Getting enemies speed")
    void getBaseSpeed() {
        assertEquals(60, gameSettings_Default.getBaseSpeed(),"Enemies speed correct");
    }

    @Test
    @DisplayName("Getting shooting frequency rate")
    void getShootingFrecuency() {
        assertEquals(2000, gameSettings_Default.getShootingFrecuency(),"Shooting frequency correct");
    }
}