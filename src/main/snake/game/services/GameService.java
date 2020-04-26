package snake.game.services;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JLabel;

import org.apache.log4j.Logger;

import snake.game.configuration.GameParameters;
import snake.game.configuration.TextParameters;
import snake.game.dao.GameDao;
import snake.game.data.GamePanelData;
import snake.game.data.PanelData;
import snake.game.data.UserData;
import snake.game.dialogs.Dialogs;
import snake.game.fruits.Fruit;
import snake.game.fruits.FruitController;
import snake.game.panels.StatusPanel;
import snake.game.snake.SnakeBody;

public class GameService {
	final static Logger logger = Logger.getLogger(GameService.class);

	private final GameDao dbase;
	private final JLabel statusLabel;
	private final int highScore;
	private Dialogs dialogs;
	private int lifeCounter = GameParameters.PLAYER_LIFE_MAX;
	private int score = 0;
	private SnakeBody snakeBody;
	private Fruit fruit;
	private boolean isStopped;

	public GameService(GamePanelData gamePanelData, GameDao dbase, StatusPanel statusPanel) {
		this(gamePanelData, dbase, statusPanel, new Random());
	}

	public GameService(GamePanelData gamePanelData, GameDao dbase, StatusPanel statusPanel, Random random) {
		this.dbase = dbase;
		this.statusLabel = statusPanel.getStatusLabel();
		statusPanel.getJavaButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				diplayTopPlayers();
			}
		});
		this.highScore = getHighScore();
		PanelData firstCell = new PanelData(gamePanelData.getWidth(), gamePanelData.getHeight(),
				gamePanelData.getCellSize());
		FruitController.initController(firstCell);
		this.snakeBody = new SnakeBody(firstCell, random);
	}

	public void startService(Dialogs dialogs) {
		this.dialogs = dialogs;
		displayStatus();
		start();
	}

	public boolean tick(ZonedDateTime tickTime) {
		boolean isRuning = true;
		if (!isStopped) {
			if (tickTime.isAfter(snakeBody.getNextMoveTime())) {
				if (snakeBody.move()) {
					snakeBody.crawl();
					if (fruit != null && snakeBody.isHeadOnFruit(fruit)) {
						snakeBody.eatFruit();
						score++;
						FruitController.resetNextCreateTime();
						fruit = null;
						displayStatus();
					}
				} else {
					lifeCounter--;
					displayStatus();
					if (0 < lifeCounter) {
						partOver();
						logger.debug("RESET -> " + lifeCounter);
						snakeBody.reset();
						fruit = null;
					} else {
						gameOver(score);
						logger.debug("GAMEOVER - points -> " + score);
						isRuning = false;
					}
				}
			}
			if (fruit == null && tickTime.isAfter(FruitController.getNextCreateTime())) {
				fruit = FruitController.createFruit(snakeBody);
			}
		}
		return isRuning;
	}

	public void paint(Graphics graphics) {
		if (fruit != null) {
			fruit.draw(graphics);
		}
		snakeBody.draw(graphics);
	}

	public void keyEventProcess(int key) {
		snakeBody.keyEventProcess(key);
	}

	private void start() {
		isStopped = false;
	}

	private void stop() {
		isStopped = true;
	}

	private int getHighScore() {
		return dbase.getHighScore();
	}

	private void diplayTopPlayers() {
		stop();
		LinkedList<UserData> top = dbase.getTopPlayers();
		dialogs.highScoreDialog(top);
		start();
	}

	private void partOver() {
		dialogs.partOverDialog();
	}

	private void gameOver(int score) {
		String name = dialogs.gameOverDialog(score);
		UserData player = new UserData(name, score);
		dbase.savePlayerData(player);
	}

	private void displayStatus() {
		statusLabel.setText(String.format(TextParameters.STATUS_FORMAT, lifeCounter, score, highScore));
	}

}
