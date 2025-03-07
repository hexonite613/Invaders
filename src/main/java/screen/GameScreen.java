package screen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import engine.*;
import entity.*;

/**
 * Implements the game screen, where the action happens.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class GameScreen extends Screen {
	/** Milliseconds until the screen accepts user input. */
	private static final int INPUT_DELAY = 6000;
	/** Bonus score for each life remaining at the end of the level. */
	private static final int LIFE_SCORE = 100;
	/** Minimum time between bonus ship's appearances. */
	private static final int BONUS_SHIP_INTERVAL = 20000;
	/** Maximum variance in the time between bonus ship's appearances. */
	private static final int BONUS_SHIP_VARIANCE = 10000;
	/** Time until bonus ship explosion disappears. */
	private static final int BONUS_SHIP_EXPLOSION = 500;
	/** Time from finishing the level to screen change. */
	private static final int SCREEN_CHANGE_INTERVAL = 1500;
	/** Height of the interface separation line. */
	private static final int SEPARATION_LINE_HEIGHT = 40;

	/** Current game difficulty settings. */
	private GameSettings gameSettings;
	/** Current difficulty level number. */
	private int level;
	/** Formation of enemy ships. */
	private EnemyShipFormation enemyShipFormation;

	/** UFO 랜덤 발사 */
	private int position;

	/** Player's ship. */
	private Ship ship;
	/** Bonus enemy ship that appears sometimes. */
	private EnemyShip enemyShipSpecial;
	/** Minimum time between bonus ship appearances. */
	private Cooldown enemyShipSpecialCooldown;
	/** Time until bonus ship explosion disappears. */
	private Cooldown enemyShipSpecialExplosionCooldown;
	/** Time from finishing the level to screen change. */
	private Cooldown screenFinishedCooldown;
	/** Set of all bullets fired by on screen ships. */
	private Set<Bullet> bullets;
	/** Current score. */
	private int score;
	/** Player lives left. */
	private int lives;
	/** Total bullets shot by the player. */
	private int bulletsShot;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** Moment the game starts. */
	private long gameStartTime;
	/** Checks if the level is finished. */
	private boolean levelFinished;
	/** Checks if a bonus life is received. */
	private boolean bonusLife;

	/** Audio */
	private Audio specialAudio;
	private Audio shootAudio;
	private Audio explosionAudio;
	private Audio gameOver;
	/** Checks boss stage. */
	private boolean bossStage;

	/** 생명력 */
	private int bulletCode;
	/** pause */
	private boolean isPause;
	/** Check if the game will restart */
	private boolean isResume;
	/** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 200;

	/** Time between changes in user selection. */
	private Cooldown selectionCooldown;

	/**
	 * Constructor, establishes the properties of the screen.
	 *
	 * @param gameState
	 *            Current game state.
	 * @param gameSettings
	 *            Current game settings.
	 * @param bonusLife
	 *            Checks if a bonus life is awarded this level.
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public GameScreen(final GameState gameState,
					  final GameSettings gameSettings, final boolean bonusLife, final boolean bossStage,
					  final int width, final int height, final int fps) {
		super(width, height, fps);

		this.gameSettings = gameSettings;
		this.bonusLife = bonusLife;
		this.bossStage = bossStage;
		this.level = gameState.getLevel();
		this.score = gameState.getScore();
		this.lives = gameState.getLivesRemaining();
		if (this.bonusLife)
			this.lives++;
		this.bulletsShot = gameState.getBulletsShot();
		this.shipsDestroyed = gameState.getShipsDestroyed();
		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();
	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	public final void initialize() {
		super.initialize();

		enemyShipFormation = new EnemyShipFormation(this.gameSettings);
		enemyShipFormation.attach(this);
		this.ship = new Ship((int)(this.width / (2 * this.getRatio())), (int)((this.height - 30)/ this.getRatio()));
		// Appears each 10-30 seconds.
		this.enemyShipSpecialCooldown = Core.getVariableCooldown(
				BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
		this.enemyShipSpecialCooldown.reset();
		this.enemyShipSpecialExplosionCooldown = Core
				.getCooldown(BONUS_SHIP_EXPLOSION);
		this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.bullets = new HashSet<Bullet>();

		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();

		// Load Audio file.
		this.shootAudio = new Audio("shootAudio", false);
		this.explosionAudio = new Audio("explosionAudio", false);
		this.gameOver = new Audio("gameOver", false);
	}

	/**
	 * Starts the action.
	 *
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		this.score += LIFE_SCORE * (this.lives - 1);
		this.isPause = false;
		this.logger.info("Screen cleared with a score of " + this.score);

		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		if (this.inputDelay.checkFinished() && !this.levelFinished) {

			if (!this.ship.isDestroyed()) {
				boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT)
						|| inputManager.isKeyDown(KeyEvent.VK_D);
				boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT)
						|| inputManager.isKeyDown(KeyEvent.VK_A);

				boolean isRightBorder = this.ship.getPositionX()
						+ this.ship.getWidth() + this.ship.getSpeed() > (this.width / this.getRatio()) - 1;
				boolean isLeftBorder = this.ship.getPositionX()
						- this.ship.getSpeed() < 1;

				if (moveRight && !isRightBorder) {
					this.ship.moveRight();
				}
				if (moveLeft && !isLeftBorder) {
					this.ship.moveLeft();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_SPACE))
					if (this.ship.shoot(this.bullets)) {
						this.shootAudio.start();
						this.bulletsShot++;
					}
			}

			if (this.enemyShipSpecial != null) {
				if (!this.enemyShipSpecial.isDestroyed()) {
					this.enemyShipSpecial.move(2, 0);

					if(this.enemyShipSpecial.getPositionX() == position) {
						bullets.add(BulletPool.getBullet(enemyShipSpecial.getPositionX()+8,
								+ enemyShipSpecial.getPositionY(), 4, Color.RED));
						this.bulletCode = 1;
					}

				}
				else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
					this.enemyShipSpecial = null;
			}

			if (this.enemyShipSpecial == null
					&& this.enemyShipSpecialCooldown.checkFinished()) {
				this.specialAudio = new Audio("specialAudio", true);
				this.specialAudio.start();
				this.enemyShipSpecial = new EnemyShip();
				this.enemyShipSpecialCooldown.reset();
				// width == 448, 처음 위치 = -32
				Random rand = new Random();
				this.position = 20 + rand.nextInt((this.width-50)/2)*2;
				this.logger.info("A special ship appears");
			}
			if (this.enemyShipSpecial != null
					&& this.enemyShipSpecial.getPositionX() > this.width) {
				this.specialAudio.stop();
				this.enemyShipSpecial = null;
				this.logger.info("The special ship has escaped");
			}

			if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)
					|| inputManager.isKeyDown(KeyEvent.VK_P))
				isPause = true;
			while (isPause) {
				if (inputManager.isKeyDown(KeyEvent.VK_LEFT)
						|| inputManager.isKeyDown(KeyEvent.VK_A)) {
					previousMenuItem();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)
						|| inputManager.isKeyDown(KeyEvent.VK_D)) {
					nextMenuItem();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
					try {
						if (this.returnCode == 0) {
							this.isPause = false;
						}
						else if (this.returnCode == 1) {
							isResume = true;
							while (isResume) {
								if (inputManager.isKeyDown(KeyEvent.VK_LEFT)
										|| inputManager.isKeyDown(KeyEvent.VK_A)) {
									previousMenuItem();
								}
								if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)
										|| inputManager.isKeyDown(KeyEvent.VK_D)) {
									nextMenuItem();
								}
								if (inputManager.isKeyDown(KeyEvent.VK_ENTER)) // Checkout
									try {
										if (this.returnCode == 1) {
											this.isPause = false;
											this.isResume = false;
										} else if (this.returnCode == 0) {
											this.lives = -1;
											this.isPause = false;
											this.isResume = false;
											this.isRunning = false;
										}
										Thread.sleep(100);
									} catch (InterruptedException e) { }
								drawCheckOut(this.returnCode);
							}
						}
						Thread.sleep(100);
					} catch (InterruptedException e) { }
					this.selectionCooldown.reset();
				}
				drawPause(this.returnCode);
			}

			this.ship.update();
			this.enemyShipFormation.update();
			this.enemyShipFormation.shoot(this.bullets);
		}

		manageCollisions();
		cleanBullets();
		draw();

		if ((this.enemyShipFormation.isEmpty() || this.lives == 0)
				&& !this.levelFinished) {
			this.levelFinished = true;
			this.gameOver.start();
			this.screenFinishedCooldown.reset();
		}

		if (this.levelFinished && this.screenFinishedCooldown.checkFinished())
			this.isRunning = false;
	}
	/**
	 * Shifts the focus to the next menu item.
	 */
	private void nextMenuItem() {
		this.returnCode = 1;
	}

	/**
	 * Shifts the focus to the previous menu item.
	 */
	private void previousMenuItem() {
		this.returnCode = 0;
	}

	/** next option */

	private void drawPause(final int option) {
		drawManager.drawPause(this, INPUT_DELAY, this.isPause,
				option, this.level, this.score, this.lives);
		drawManager.drawHorizontalLine(this, this.height / 2 - this.height
				/ 4);
		drawManager.drawHorizontalLine(this, this.height / 2 + this.height
				/ 4);
		drawManager.completeDrawing(this);
	}

	private void drawCheckOut(final int option) {
		drawManager.initDrawing(this);
		drawManager.drawCheckOutScreen(this);
		drawManager.drawCheckOut(this, option);
		drawManager.completeDrawing(this);
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);
		drawManager.drawEntity(this.ship, this.ship.getPositionX(),
				this.ship.getPositionY());
		if (this.enemyShipSpecial != null)
			drawManager.drawEntity(this.enemyShipSpecial,
					this.enemyShipSpecial.getPositionX(),
					this.enemyShipSpecial.getPositionY());

		enemyShipFormation.draw();

		for (Bullet bullet : this.bullets)
			drawManager.drawEntity(bullet, bullet.getPositionX(),
					bullet.getPositionY());

		// Interface.
		drawManager.drawBulletCount(this, this.bulletsShot);
		drawManager.drawBulletCountString (this);
		drawManager.drawScoreString(this);

		drawManager.drawScore(this, this.score);
		drawManager.drawLives(this, this.lives);
		drawManager.drawHorizontalLine(this, (int) Math.round((SEPARATION_LINE_HEIGHT - 1) * (this.getHeight()/522f)));
		// Countdown to game start.
		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY
					- (System.currentTimeMillis()
					- this.gameStartTime)) / 1000);
			drawManager.drawCountDown(this, this.level, countdown,
					this.bonusLife);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height
					/ 12);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height
					/ 12);
		}

		drawManager.completeDrawing(this);
	}

	/**
	 * Cleans bullets that go off screen.
	 */
	private void cleanBullets() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets) {
			bullet.update();
			if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
					|| bullet.getPositionY() > this.height)
				recyclable.add(bullet);
		}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	/**
	 * Manages collisions between bullets and ships.
	 */
	private void manageCollisions() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets)
			if (bullet.getSpeed() > 0) {
				if (checkCollision(bullet, this.ship) && !this.levelFinished) {
					recyclable.add(bullet);
					if (!this.ship.isDestroyed()) {
						this.ship.destroy();
						this.explosionAudio.start();
						if (this.bulletCode == 1) {
							this.lives -= 2;
							this.bulletCode = 0;
						} else {
							this.lives--;
						}
						this.logger.info("Hit on player ship, " + this.lives
								+ " lives remaining.");
					}
				}
			} else {
				for (EnemyShip enemyShip : this.enemyShipFormation)
					if (!enemyShip.isDestroyed()
							&& checkCollision(bullet, enemyShip)) {
						if (enemyShip.getHp() == 1) {
							this.score += enemyShip.getPointValue();
							this.shipsDestroyed++;
							this.enemyShipFormation.destroy(enemyShip);
							recyclable.add(bullet);
						}
						else {
							this.score += enemyShip.getPointValue();
							recyclable.add(bullet);
							enemyShip.hp--;

						}
					}
				if (this.enemyShipSpecial != null
						&& !this.enemyShipSpecial.isDestroyed()
						&& checkCollision(bullet, this.enemyShipSpecial)) {
					this.score += this.enemyShipSpecial.getPointValue();
					this.shipsDestroyed++;
					this.enemyShipSpecial.destroy();
					this.enemyShipSpecialExplosionCooldown.reset();
					recyclable.add(bullet);
				}
			}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	/**
	 * Checks if two entities are colliding.
	 *
	 * @param a
	 *            First entity, the bullet.
	 * @param b
	 *            Second entity, the ship.
	 * @return Result of the collision test.
	 */
	private boolean checkCollision(final Entity a, final Entity b) {
		// Calculate center point of the entities in both axis.
		int centerAX = a.getPositionX() + a.getWidth() / 2;
		int centerAY = a.getPositionY() + a.getHeight() / 2;
		int centerBX = b.getPositionX() + b.getWidth() / 2;
		int centerBY = b.getPositionY() + b.getHeight() / 2;
		// Calculate maximum distance without collision.
		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
		// Calculates distance.
		int distanceX = Math.abs(centerAX - centerBX);
		int distanceY = Math.abs(centerAY - centerBY);

		return distanceX < maxDistanceX && distanceY < maxDistanceY;
	}

	/**
	 * Returns a GameState object representing the status of the game.
	 *
	 * @return Current game state.
	 */
	public final GameState getGameState() {
		return new GameState(this.level, this.score, this.lives,
				this.bulletsShot, this.shipsDestroyed);
	}
}