package snake.game.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.event.KeyEvent;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.junit.Test;

import snake.game.configuration.GameParameters;
import snake.game.dao.GameDao;
import snake.game.data.GamePanelData;
import snake.game.data.UserData;
import snake.game.dialogs.Dialogs;
import snake.game.panels.StatusPanel;

public class GameServiceTest {

	private static final ZoneId zone = ZoneId.systemDefault();
	private static long sneakMoveTimeInNanos = GameParameters.SNEAK_MOVE_TIME_IN_NANOS + 10;

	class MockGameDao implements GameDao {

		public boolean isReady() {
			return true;
		}

		public int getHighScore() {
			return 123;
		}

		public boolean savePlayerData(UserData player) {
			return true;
		}

		public LinkedList<UserData> getTopPlayers() {
			return new LinkedList<UserData>();
		}

	}

	class MockStatusLabel extends JLabel {
		private static final long serialVersionUID = 12340071L;

	}

	class MockJavaButton extends JButton {
		private static final long serialVersionUID = 23450071L;

	}

	class MockDialogs extends Dialogs {

		public MockDialogs(JPanel parent) {
			super(parent);
		}

		@Override
		public String gameOverDialog(int score) {
			return "Test User";
		}

		@Override
		public void partOverDialog() {
		}

	}

	class MockRandom extends Random {
		private static final long serialVersionUID = 9007L;

		public int nextInt(int bound) {
			int result = 0;
			switch (bound) {
			case 25:
				result = 25;
				break;
			case 20:
				result = 20;
				break;
			}
			return result;
		}

	}

	private final GameDao dbase = new MockGameDao();
	private final JLabel statusLabel = new MockStatusLabel();
	private final JButton javaButton = new MockJavaButton();
	private final StatusPanel statusPanel = new StatusPanel(statusLabel, javaButton);
	private final JPanel parent = new JPanel();
	private final Dialogs dialogs = new MockDialogs(parent);
	private final Random random = new MockRandom();

	@Test(expected = NullPointerException.class)
	public void constructorWithNull() {
		new GameService(null, null, null, null);
	}

	@Test
	public void constructorWithMock() {
		GamePanelData gamePanelData = new GamePanelData(50, 50, 10, false);
		GameService gameService = new GameService(gamePanelData, dbase, statusPanel, random);
		gameService.startService(dialogs);
	}

	@Test
	public void moveOutOfBoundaryOnLeftToGameOver() {
		GamePanelData gamePanelData = new GamePanelData(50, 40, 10, false);
		GameService gameService = new GameService(gamePanelData, dbase, statusPanel, random);
		gameService.startService(dialogs);
		for (int idx = 0; idx < 25 * GameParameters.PLAYER_LIFE_MAX - 1; idx++) {
			if (idx % 25 == 0) {
				gameService.keyEventProcess(KeyEvent.VK_RIGHT);
			}
			assertTrue(gameService.tick(ZonedDateTime.now(zone).plusNanos(sneakMoveTimeInNanos)));
		}
		assertFalse(gameService.tick(ZonedDateTime.now(zone).plusNanos(sneakMoveTimeInNanos)));
	}

	@Test
	public void moveOutOfBoundaryOnRightToGameOver() {
		GamePanelData gamePanelData = new GamePanelData(50, 40, 10, false);
		GameService gameService = new GameService(gamePanelData, dbase, statusPanel, random);
		gameService.startService(dialogs);
		for (int idx = 0; idx < 26 * GameParameters.PLAYER_LIFE_MAX - 1; idx++) {
			if (idx % 26 == 0) {
				gameService.keyEventProcess(KeyEvent.VK_LEFT);
			}
			assertTrue(gameService.tick(ZonedDateTime.now(zone).plusNanos(sneakMoveTimeInNanos)));
		}
		assertFalse(gameService.tick(ZonedDateTime.now(zone).plusNanos(sneakMoveTimeInNanos)));
	}

	@Test
	public void moveOutOfBoundaryOnBottomToGameOver() {
		GamePanelData gamePanelData = new GamePanelData(50, 40, 10, false);
		GameService gameService = new GameService(gamePanelData, dbase, statusPanel, random);
		gameService.startService(dialogs);
		for (int idx = 0; idx < 20 * GameParameters.PLAYER_LIFE_MAX - 1; idx++) {
			if (idx % 20 == 0) {
				gameService.keyEventProcess(KeyEvent.VK_DOWN);
			}
			assertTrue(gameService.tick(ZonedDateTime.now(zone).plusNanos(sneakMoveTimeInNanos)));
		}
		assertFalse(gameService.tick(ZonedDateTime.now(zone).plusNanos(sneakMoveTimeInNanos)));
	}

	@Test
	public void moveOutOfBoundaryOnTopToGameOver() {
		GamePanelData gamePanelData = new GamePanelData(50, 40, 10, false);
		GameService gameService = new GameService(gamePanelData, dbase, statusPanel, random);
		gameService.startService(dialogs);
		for (int idx = 0; idx < 21 * GameParameters.PLAYER_LIFE_MAX - 1; idx++) {
			if (idx % 21 == 0) {
				gameService.keyEventProcess(KeyEvent.VK_UP);
			}
			assertTrue(gameService.tick(ZonedDateTime.now(zone).plusNanos(sneakMoveTimeInNanos)));
		}
		assertFalse(gameService.tick(ZonedDateTime.now(zone).plusNanos(sneakMoveTimeInNanos)));
	}

	// TODO: Test for SnakeBody.crashedIntoItself

}
