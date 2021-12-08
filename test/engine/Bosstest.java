package engine;

import entity.Bullet;
import entity.BulletPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class Bosstest {
    DrawManager.SpriteType shooter;
    Bullet bullet = BulletPool.getBullet(1, 1, 10, Color.red);
    GameSettings gameSettings_Default;
    int BOSS_TYPE_POINTS=300;
    int pointValue;

    @BeforeEach
    void setUp() {
        shooter = DrawManager.SpriteType.EnemyBoss;
        gameSettings_Default=new GameSettings(1, 1, 50, 500,true);
        pointValue=BOSS_TYPE_POINTS;
    }

    @Test
    @DisplayName("Testing Boss sprite")
    void BossSpritecheck() {
        assertEquals(shooter.toString(),"EnemyBoss");
    }

    @Test
    @DisplayName("Boss shooting frequency")
    void BossShooting(){
        assertEquals(500, gameSettings_Default.getShootingFrecuency());
    }

    @Test
    @DisplayName("Testing Bullet speed")
    void BossBullet() {
        assertEquals(bullet.getSpeed(),10);
        assertEquals(bullet.getColor(),Color.red);
    }

    @Test
    @DisplayName("Boss score")
    void BossScore(){
        assertEquals(300,pointValue);
    }

    @Test
    @DisplayName("Testing check Boss")
    void IsBossCheck(){
        assertEquals(true, gameSettings_Default.getIsBoss());
    }

}