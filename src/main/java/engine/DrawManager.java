package engine;

import entity.Entity;
import entity.Ship;
import screen.Screen;

import java.awt.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Manages screen drawing.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class DrawManager {

	/** Singleton instance of the class. */
	private static DrawManager instance;
	/** Current frame. */
	private static Frame frame;
	/** FileManager instance. */
	private static FileManager fileManager;
	/** Application logger. */
	private static Logger logger;
	/** Graphics context. */
	private static Graphics graphics;
	/** Buffer Graphics. */
	private static Graphics backBufferGraphics;
	/** Graphics Device. */
	private static GraphicsDevice graphicsDevice;
	/** Graphics Environment. */
	private static GraphicsEnvironment graphicsEnvironment;
	/** Buffer image. */
	private static BufferedImage backBuffer;
	/** Normal sized font. */
	private static Font fontRegular;
	/** Normal sized font properties. */
	private static FontMetrics fontRegularMetrics;
	/** Big sized font. */
	private static Font fontBig;
	/** Big sized font properties. */
	private static FontMetrics fontBigMetrics;
	/**Place adjustment*/
	private static final int adjust=30;
	/** Frame size */
	private static Dimension frame_size;

	// Add
	private Screen screen;

	/** Sprite types mapped to their images. */
	private static Map<SpriteType, boolean[][]> spriteMap;

	/** Sprite types. */
	public static enum SpriteType {
		/** Player ship. */
		Ship,
		/** Destroyed player ship. */
		ShipDestroyed,
		/** Player bullet. */
		Bullet,
		/** Enemy bullet. */
		EnemyBullet,
		/** First enemy ship - first form. */
		EnemyShipA1,
		/** First enemy ship - second form. */
		EnemyShipA2,
		/** Second enemy ship - first form. */
		EnemyShipB1,
		/** Second enemy ship - second form. */
		EnemyShipB2,
		/** Third enemy ship - first form. */
		EnemyShipC1,
		/** Third enemy ship - second form. */
		EnemyShipC2,
		/** Bonus ship. */
		EnemyShipSpecial,
		Explosion,
		/** Boss. */
		EnemyBoss
	};

	/**
	 * Private constructor.
	 */
	private DrawManager() {
		fileManager = Core.getFileManager();
		logger = Core.getLogger();
		logger.info("Started loading resources.");

		graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();

		try {
			spriteMap = new LinkedHashMap<SpriteType, boolean[][]>();

			spriteMap.put(SpriteType.Ship, new boolean[13][8]);
			spriteMap.put(SpriteType.ShipDestroyed, new boolean[13][8]);
			spriteMap.put(SpriteType.Bullet, new boolean[3][5]);
			spriteMap.put(SpriteType.EnemyBullet, new boolean[3][5]);
			spriteMap.put(SpriteType.EnemyShipA1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipA2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipB1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipB2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipC1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipC2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipSpecial, new boolean[16][7]);
			spriteMap.put(SpriteType.Explosion, new boolean[13][7]);
			spriteMap.put(SpriteType.EnemyBoss, new boolean[48][32]);

			fileManager.loadSprite(spriteMap);
			logger.info("Finished loading the sprites.");

			// Font loading.
			fontRegular = fileManager.loadFont(14f);
			fontBig = fileManager.loadFont(24f);
			logger.info("Finished loading the fonts.");

		} catch (IOException e) {
			logger.warning("Loading failed.");
		} catch (FontFormatException e) {
			logger.warning("Font formating failed.");
		}
	}

	/**
	 * Returns shared instance of DrawManager.
	 *
	 * @return Shared instance of DrawManager.
	 */
	protected static DrawManager getInstance() {
		if (instance == null)
			instance = new DrawManager();
		return instance;
	}
	/**
	 * Sets the frame to draw the image on.
	 *
	 * @param currentFrame
	 *            Frame to draw on.
	 */
	public void setFrame(final Frame currentFrame) {
		frame = currentFrame;
	}
	/**
	 * Sets the frame to draw the image on.
	 *
	 * @return Shared frame of drawManager.
	 */
	public Frame getFrame() { return frame; }
	/**
	 * Sets the frame to draw the image on.
	 *
	 * @param currentFrame
	 *            Frame to draw on.
	 */
	public void setFont(final Frame currentFrame) {
		// Font loading.
		frame = currentFrame;
		float regularFontSize = Math.round((frame.getWidth()/440f) * 14f);
		float bigFontSize = Math.round((frame.getWidth()/440f) * 24f);
		try {
			fontRegular = fileManager.loadFont(regularFontSize);
			fontBig = fileManager.loadFont(bigFontSize);
		} catch (IOException e) {
			logger.warning("Loading failed.");
		} catch (FontFormatException e) {
			logger.warning("Font formating failed.");
		}
	}

	/**
	 * Set the frame to the Full screen.
	 */
	public void setFullScreenFrame() {
		graphicsDevice.setFullScreenWindow(frame);
	}

	public void setMiniScreenFrame() {
		graphicsDevice.setFullScreenWindow(null);
	}

	/**
	 * First part of the drawing process. Initialices buffers, draws the
	 * background and prepares the images.
	 *
	 * @param screen
	 *            Screen to draw in.
	 */
	public void initDrawing(final Screen screen) {
		frame_size = getFrame().size();
		backBuffer = new BufferedImage(screen.getWidth(), screen.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		this.screen = screen;

		graphics = frame.getGraphics();
		backBufferGraphics = backBuffer.getGraphics();

		backBufferGraphics.setColor(Color.BLACK);
		backBufferGraphics.fillRect(0, 0, screen.getWidth(), screen.getHeight());

		fontRegularMetrics = backBufferGraphics.getFontMetrics(fontRegular);
		fontBigMetrics = backBufferGraphics.getFontMetrics(fontBig);

//		drawBorders(screen);
//		drawGrid(screen);
	}

	/**
	 * Draws the completed drawing on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void completeDrawing(final Screen screen) {
		frame_size = getFrame().size();
		graphics.drawImage(backBuffer, (int) Math.round(frame_size.width/2f - frame.getWidth()/2f)
				, frame.getInsets().top, frame);
	}

	/**
	 * Draws an entity, using the apropiate image.
	 *
	 * @param entity
	 *            Entity to be drawn.
	 * @param positionX
	 *            Coordinates for the left side of the image.
	 * @param positionY
	 *            Coordinates for the upper side of the image.
	 */
	public void drawEntity(final Entity entity, final int positionX,
						   final int positionY) {
		boolean[][] image = spriteMap.get(entity.getSpriteType());

		backBufferGraphics.setColor(entity.getColor());
		switch (entity.getHp()) {
			case 1:
				backBufferGraphics.setColor(Color.WHITE);
				break;
			case 2:
				backBufferGraphics.setColor(Color.orange);
				break;
			case 3:
				backBufferGraphics.setColor(Color.yellow);
				break;
			case 4:
				backBufferGraphics.setColor(Color.blue);
				break;
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				backBufferGraphics.setColor(Color.red);
				break;

		}


		for (int i = 0; i < image.length; i++)
			for (int j = 0; j < image[i].length; j++)
				if (image[i][j])
					backBufferGraphics.fillRect(screen.getPosition(positionX) + i * screen.getPosition(2),
							screen.getPosition(positionY) + j * screen.getPosition(2),
							screen.getPosition(2), screen.getPosition(2));
	}


	/**draw "BULLET: " left from bullet count*/
	public void drawBulletCountString (final Screen screen) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		String bulletCountString = "Bullet: ";
		backBufferGraphics.drawString(bulletCountString,screen.getWidth() - 270, 25);}

	/**
	 * Draws current bullet count on screen.
	 *
	 * @param screen
	 *            Screen to draw on
	 *            Current bullet count.
	 */

	public void drawBulletCount(final Screen screen, final int bulletsShot) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		String scoreString = String.format("%04d", bulletsShot);
		backBufferGraphics.drawString(scoreString,
				screen.getWidth() - 195, 25);
	}
	/**draw "SCORE: " left from score*/
	public void drawScoreString (final Screen screen) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		String bulletCountString = "Score: ";
		backBufferGraphics.drawString(bulletCountString,screen.getWidth() - 125, 25);}



	/**
	 * For debugging purpouses, draws the canvas borders.
	 *
	 * @param screen
	 *            Screen to draw in.
	 */
	@SuppressWarnings("unused")
	private void drawBorders(final Screen screen) {
		backBufferGraphics.setColor(Color.GREEN);
		backBufferGraphics.drawLine(0, 0, screen.getWidth() - 1, 0);
		backBufferGraphics.drawLine(0, 0, 0, screen.getHeight() - 1);
		backBufferGraphics.drawLine(screen.getWidth() - 1, 0,
				screen.getWidth() - 1, screen.getHeight() - 1);
		backBufferGraphics.drawLine(0, screen.getHeight() - 1,
				screen.getWidth() - 1, screen.getHeight() - 1);
	}

	/**
	 * For debugging purpouses, draws a grid over the canvas.
	 *
	 * @param screen
	 *            Screen to draw in.
	 */
	@SuppressWarnings("unused")
	private void drawGrid(final Screen screen) {
		backBufferGraphics.setColor(Color.DARK_GRAY);
		for (int i = 0; i < screen.getHeight() - 1; i += 2)
			backBufferGraphics.drawLine(0, i, screen.getWidth() - 1, i);
		for (int j = 0; j < screen.getWidth() - 1; j += 2)
			backBufferGraphics.drawLine(j, 0, j, screen.getHeight() - 1);
	}

	/**
	 * Draws current score on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param score
	 *            Current score.
	 */

	public void drawScore(final Screen screen, final int score) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		String scoreString = String.format("%04d", score);
		backBufferGraphics.drawString(scoreString,
				screen.getWidth() - screen.getPosition(60), screen.getPosition(25));
	}

	/**
	 * Draws number of remaining lives on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param lives
	 *            Current lives.
	 */
	public void drawLives(final Screen screen, final int lives) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(Integer.toString(lives), screen.getPosition(20), screen.getPosition(25));
		Ship dummyShip = new Ship(0, 0);
		for (int i = 1; i <= lives; i++)
			drawEntity(dummyShip, screen.getPosition(10)  + 35 * i, screen.getPosition(5)+5);
	}

	/**
	 * Draws a thick line from side to side of the screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param positionY
	 *            Y coordinate of the line.
	 */
	public void drawHorizontalLine(final Screen screen, final int positionY) {
		backBufferGraphics.setColor(Color.GREEN);
		backBufferGraphics.drawLine(0, positionY, screen.getWidth(), positionY);
		backBufferGraphics.drawLine(0, positionY + 1, screen.getWidth(),
				positionY + 1);
	}

	/**
	 * Draws game title.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawTitle(final Screen screen) {
//		System.out.println(fontBigMetrics.charWidth('1') + " & " + fontBigMetrics.getHeight());
//		System.out.println(fontRegularMetrics.charWidth('1') + " & " + fontRegularMetrics.getHeight());
		String titleString = "Invaders";
		String instructionsString =
				"select with w+s / arrows, confirm with space";

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 2);

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, titleString, screen.getHeight() / 3);
	}

	/**
	 * Draws main menu.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param option
	 *            Option selected.
	 */
	public void drawMenu(final Screen screen, final int option) {
		String playString = "Play";
		String settingString = "Setting";
		String highScoresString = "High scores";
		String exitString = "exit";

		if (option == 2)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, playString,
				screen.getHeight() / 3 * 2);
		if (option == 3)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, settingString,
				screen.getHeight() / 3 * 2 + fontRegularMetrics.getHeight() * 2);
		if (option == 4)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, highScoresString,
				screen.getHeight() / 3 * 2 + fontRegularMetrics.getHeight() * 4);
		if (option == 0)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, exitString, screen.getHeight() / 3
				* 2 + fontRegularMetrics.getHeight() * 6);
	}

	/**
	 * Draws setting menu.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param option
	 *            Option selected.
	 */
	public void drawSettingTitle(final Screen screen, final int option) {
		String settingString = "Setting";
		String instructionsString = "select with w+s / confirm with space";

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 2);

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, settingString, screen.getHeight() / 3);
	}

	public void drawSettingMenu(final Screen screen, final int option) {
		String windowModeString = "Window Mode";
		String difficultyString = "Difficulty";
		String exitString = "exit";

		if (option == 2)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, windowModeString,screen.getHeight() / 3 * 2);
		if (option == 3)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, difficultyString, screen.getHeight() / 3
				* 2 + fontRegularMetrics.getHeight() * 2);
//		if (option == 4)
//			backBufferGraphics.setColor(Color.GREEN);
//		else
//			backBufferGraphics.setColor(Color.WHITE);
//		drawCenteredRegularString(screen, volumeString, screen.getHeight() / 4
//				* 2 + fontRegularMetrics.getHeight() * 6);
		if (option == 0)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, exitString, screen.getHeight() / 3
				* 2 + fontRegularMetrics.getHeight() * 4);
	}



	/**
	 * Draws pause menu.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param level
	 *            Current level.
	 * @param score
	 *            Current score.
	 * @param liveRemaining
	 *            Current remaining lives.
	 */
	public void drawPause(final Screen screen, final int number, final boolean isPause,
						  final int option, final int level, final int score, final int liveRemaining) {
		int rectWidth = screen.getWidth();
		int rectHeight = screen.getHeight() / 2 ;
		String titleString = "Restart the game?";
		String stateString = "Lv : " + level + "  #  Score : " + score + "  #  Life : " + liveRemaining;
		String instructionsString = "select with a+d";
		String resumeString = "Resume";
		String restartString = "Restart";

		backBufferGraphics.setColor(Color.BLACK);
		backBufferGraphics.fillRect(0, screen.getHeight() / 2 - rectHeight / 2,
				rectWidth, rectHeight);

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, titleString,
				screen.getHeight() / 2 - fontBigMetrics.getHeight() * 2);
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredRegularString(screen, stateString,
				screen.getHeight() / 2 - fontRegularMetrics.getHeight());

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 2 + fontRegularMetrics.getHeight());

		if (option == 0)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, resumeString, 3, 1,
				screen.getHeight() / 3 * 2 );

		if (option == 1)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, restartString, 3, 2,
				screen.getHeight() / 3 * 2);
	}

	/** set CheckOut Screen */
	public void drawCheckOutScreen(final Screen screen){
		String check1String = "Are you sure";
		String check2String = "you want to return to title?";
		String guideString = "Press the Enter button";
		String warningString = "Can't save the score";

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, check1String, screen.getHeight()/3);
		drawCenteredBigString(screen, check2String, screen.getHeight()/3
				+ fontBigMetrics.getHeight() * 6 / 7);

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, guideString, screen.getHeight()/3
				+ fontBigMetrics.getHeight() * 2);
		drawCenteredRegularString(screen, warningString, screen.getHeight()/3
				+ fontBigMetrics.getHeight() * 20 / 7);
	}

	/**Check to go out to title*/
	public void drawCheckOut(final Screen screen, final int option){
		String yesString = "Yes";
		String noString = "No";

		if (option == 0)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, yesString, 3, 1,
				screen.getHeight() / 3 * 2 );

		if (option == 1)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, noString, 3, 2,
				screen.getHeight() / 3 * 2);
	}

	/**
	 * Draws pause menu.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param option
	 *            Option selected.
	 */
	public void drawPause(final Screen screen, final int option, final int level, final int score, final int lives) {
		String settingString = "Pause";
		String stateString = "Lv : " + level + " | Score : " + score + " | Life : " + lives;
		String suggestionsString = "Return to TITLE Screen?";
		String instructionsString = "select with w+s";

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, settingString, screen.getHeight() / 3 - fontBigMetrics.getHeight());
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredRegularString(screen, stateString, screen.getHeight() / 3 + fontRegularMetrics.getHeight());

		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, suggestionsString,
				screen.getHeight() / 2 - fontRegularMetrics.getHeight());
		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 2 + fontRegularMetrics.getHeight());
	}

	public void drawPauseMenu(final Screen screen, final int option) {
		String resumeString = "Resume";
		String restartString = "Restart";

		if (option == 2)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, resumeString,
				screen.getHeight() / 3 * 2);
		if (option == 3)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, restartString, screen.getHeight() / 3
				* 2 + fontRegularMetrics.getHeight() * 2);
	}

	/**
	 * Draws game results.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param score
	 *            Score obtained.
	 * @param livesRemaining
	 *            Lives remaining when finished.
	 * @param shipsDestroyed
	 *            Total ships destroyed.
	 * @param accuracy
	 *            Total accuracy.
	 * @param isNewRecord
	 *            If the score is a new high score.
	 */
	public void drawResults(final Screen screen, final int score,
							final int livesRemaining, final int shipsDestroyed,
							final float accuracy, final boolean isNewRecord) {
		String scoreString = String.format("score %04d", score);
		String livesRemainingString = "lives remaining " + livesRemaining;
		String shipsDestroyedString = "enemies destroyed " + shipsDestroyed;
		String accuracyString = String
				.format("accuracy %.2f%%", accuracy * 100);

		int height = isNewRecord ? 4 : 2;

		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, scoreString, screen.getHeight()
				/ height);
		drawCenteredRegularString(screen, livesRemainingString,
				screen.getHeight() / height + fontRegularMetrics.getHeight()
						* 2);
		drawCenteredRegularString(screen, shipsDestroyedString,
				screen.getHeight() / height + fontRegularMetrics.getHeight()
						* 4);
		drawCenteredRegularString(screen, accuracyString, screen.getHeight()
				/ height + fontRegularMetrics.getHeight() * 6);
	}

	/**
	 * Draws interactive characters for name input.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param name
	 *            Current name selected.
	 * @param nameCharSelected
	 *            Current character selected for modification.
	 */
	public void drawNameInput(final Screen screen, final char[] name,
							  final int nameCharSelected) {
		String newRecordString = "New Record!";
		String introduceNameString = "Introduce name:";

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredRegularString(screen, newRecordString, screen.getHeight()
				/ 4 + fontRegularMetrics.getHeight() * 10);
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, introduceNameString,
				screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 12);

		// 3 letters name.
		int positionX = screen.getWidth()
				/ 2
				- (fontRegularMetrics.getWidths()[name[0]]
				+ fontRegularMetrics.getWidths()[name[1]]
				+ fontRegularMetrics.getWidths()[name[2]]
				+ fontRegularMetrics.getWidths()[' ']) / 2;

		for (int i = 0; i < 3; i++) {
			if (i == nameCharSelected)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);

			positionX += fontRegularMetrics.getWidths()[name[i]] / 2;
			positionX = i == 0 ? positionX
					: positionX
					+ (fontRegularMetrics.getWidths()[name[i - 1]]
					+ fontRegularMetrics.getWidths()[' ']) / 2;

			backBufferGraphics.drawString(Character.toString(name[i]),
					positionX,
					screen.getHeight() / 4 + fontRegularMetrics.getHeight()
							* 14);
		}
	}

	/**
	 * Draws Game Summary.
	 *
	 *@paramscreen
	 *            Screen to draw on.
	 */
	public void drawSummary(final Screen screen) {
		String summarytitleString = "Game Summary";
		String summarydetails1=
				"Aliens are invading Earth";
		String summarydetails2=
				"it is your job to defend your ";
		String summarydetails3=
				"home planet from the 3 types of " ;
		String summarydetails4=
				"descending Invaders";

		String gamemanualtitleString="Game Manual";
		String gamemanualdetailString1=
				"Move: A+D or left+right key";
		String gamemanualdetailString2=
				"Shooting: Space";
		String gamemanualdetailString3=
				"Pause: ESC";

		String instructionsString = "To Continue, press Space";

/** Game summary title */
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, summarytitleString, screen.getHeight() / 4-adjust);

/** Game summary details */
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, summarydetails1, screen.getHeight() / 4);
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, summarydetails2, screen.getHeight() / 4+adjust);
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, summarydetails3, screen.getHeight() / 4+adjust*2);
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, summarydetails4, screen.getHeight() / 4+adjust*3);

/** Game manual title */
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, gamemanualtitleString, screen.getHeight() / 4+adjust*6);

/** Game manual details */
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, gamemanualdetailString1, screen.getHeight()/4+adjust*7);
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, gamemanualdetailString2, screen.getHeight()/4+adjust*8);
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, gamemanualdetailString3, screen.getHeight()/4+adjust*9);



/** How to exit */
		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight()/4+adjust*11);
	}

	/**
	 * Draws basic content of game over screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param acceptsInput
	 *            If the screen accepts input.
	 * @param isNewRecord
	 *            If the score is a new high score.
	 */
	public void drawGameOver(final Screen screen, final boolean acceptsInput,
							 final boolean isNewRecord) {
		String gameOverString = "Game Over";
		String continueOrExitString =
				"Press Space to play again, Escape to exit";

		int height = isNewRecord ? 4 : 2;


		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, gameOverString, screen.getHeight()
				/ height - fontBigMetrics.getHeight() * 2);

		if (acceptsInput)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, continueOrExitString,
				screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 10);
	}

	/**
	 * Draws high score screen title and instructions.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawHighScoreMenu(final Screen screen) {
		String highScoreString = "High Scores";
		String instructionsString = "Press Space to return";

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, highScoreString, screen.getHeight() / 8);

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 5);
	}

	/**
	 * Draws high scores.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param highScores
	 *            List of high scores.
	 */
	public void drawHighScores(final Screen screen,
							   final List<Score> highScores) {
		backBufferGraphics.setColor(Color.WHITE);
		int i = 0;
		String scoreString = "";

		for (Score score : highScores) {
			scoreString = String.format("%s        %04d", score.getName(),
					score.getScore());
			drawCenteredRegularString(screen, scoreString, screen.getHeight()
					/ 4 + fontRegularMetrics.getHeight() * (i + 1) * 2);
			i++;
		}
	}

	/**
	 * Draws a centered string on regular font.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	public void drawCenteredRegularString(final Screen screen,
										  final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}


	/**
	 * Draws a split of width string on regular font.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param split
	 * 			  Split of width
	 * @param width
	 *            width to drawing.
	 * @param height
	 *            Height of the drawing.
	 */
	public void drawCenteredRegularString(final Screen screen,
										  final String string, final int split, final int width, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() / split * width
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}

	/**
	 * Draws a centered string on big font.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	public void drawCenteredBigString(final Screen screen, final String string,
									  final int height) {
		backBufferGraphics.setFont(fontBig);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontBigMetrics.stringWidth(string) / 2, height);
	}

	/**
	 * Countdown to game start.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param level
	 *            Game difficulty level.
	 * @param number
	 *            Countdown number.
	 * @param bonusLife
	 *            Checks if a bonus life is received.
	 */
	public void drawCountDown(final Screen screen, final int level,
							  final int number, final boolean bonusLife) {
		int rectWidth = screen.getWidth();
		int rectHeight = screen.getHeight() / 6;
		backBufferGraphics.setColor(Color.BLACK);
		backBufferGraphics.fillRect(0, screen.getHeight() / 2 - rectHeight / 2,
				rectWidth, rectHeight);
		backBufferGraphics.setColor(Color.GREEN);
		if (number >= 4)
			if (!bonusLife) {
				drawCenteredBigString(screen, "Level " + level,
						screen.getHeight() / 2
								+ fontBigMetrics.getHeight() / 3);
			} else {
				drawCenteredBigString(screen, "Level " + level
								+ " - Bonus life!",
						screen.getHeight() / 2
								+ fontBigMetrics.getHeight() / 3);
			}
		else if (number != 0)
			drawCenteredBigString(screen, Integer.toString(number),
					screen.getHeight() / 2 + fontBigMetrics.getHeight() / 3);
		else
			drawCenteredBigString(screen, "GO!", screen.getHeight() / 2
					+ fontBigMetrics.getHeight() / 3);
	}

	/**
	 * Draws game title.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawDifficultTitle(final Screen screen) {
		String titleString = "Difficult Setting";
		String instructionsString =
				"select with w+s / confirm with space";

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 2);

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, titleString, screen.getHeight() / 3);
	}

	/**
	 * Draws main menu.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param option
	 *            Option selected.
	 */
	public void drawDifficultyMenu(final Screen screen, final int option) {
		String normalString = "Normal";
		String hardString = "Hard";
		String expertString = "Expert";

		if (option == 1)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, normalString,
				screen.getHeight() / 3 * 2);
		if (option == 2)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, hardString, screen.getHeight()
				/ 3 * 2 + fontRegularMetrics.getHeight() * 2);
		if (option == 3)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, expertString, screen.getHeight()
				/ 3 * 2 + fontRegularMetrics.getHeight() * 4);
	}

	/**
	 * Draws window mode setting title.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawWindowSettingTitle(final Screen screen) {
		String titleString = "Window Mode Setting";
		String instructionsString =
				"select with w+s / confirm with space";

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 2);

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, titleString, screen.getHeight() / 3);
	}

	/**
	 * Draws window mode menu.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param option
	 *            Option selected.
	 */
	public void drawWindowSettingMenu(final Screen screen, final int option) {
		String mode1String = "434 x 497";
		String mode2String = "661 x 757";
		String modeFullString = "Full Screen";

		if (option == 2)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, mode1String,
				screen.getHeight() / 3 * 2);
		if (option == 3)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, mode2String, screen.getHeight()
				/ 3 * 2 + fontRegularMetrics.getHeight() * 2);
		if (option == 4)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, modeFullString, screen.getHeight()
				/ 3 * 2 + fontRegularMetrics.getHeight() * 4);
	}
}