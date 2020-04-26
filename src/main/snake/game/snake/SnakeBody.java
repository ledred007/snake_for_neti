package snake.game.snake;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.Random;

import org.apache.log4j.Logger;

import snake.game.configuration.GameParameters;
import snake.game.data.CellData;
import snake.game.data.PanelData;
import snake.game.fruits.Fruit;

public class SnakeBody {
	final static Logger logger = Logger.getLogger(SnakeBody.class);

	private static final ZoneId zone = ZoneId.systemDefault();

	private final PanelData panelData;
	private final Random random;
	private LinkedList<SnakeBodyPart> snakeBodyList;
	private int snakeX, snakeY, snakeSize;
	private boolean right;
	private boolean left;
	private boolean up;
	private boolean down;
	private ZonedDateTime lastSneakTime;
	private long sneakMoveTimeInNanos;

	public SnakeBody(PanelData panelData, Random random) {
		snakeBodyList = new LinkedList<SnakeBodyPart>();
		this.random = random;
		this.panelData = panelData;
		reset();
	}

	public void reset() {
		right = false;
		left = false;
		up = false;
		down = false;
		snakeX = random.nextInt(panelData.getWidth() / 2);
		snakeY = random.nextInt(panelData.getHeight() / 2);
		snakeBodyList.clear();
		SnakeBodyPart body = new SnakeBodyPart(
				new CellData(snakeX, snakeY, panelData.getCellSize(), panelData.getCellSize()));
		snakeBodyList.add(body);
		snakeSize = snakeBodyList.size();
		lastSneakTime = ZonedDateTime.now(zone);
		sneakMoveTimeInNanos = GameParameters.SNEAK_MOVE_TIME_IN_NANOS;
	}

	public ZonedDateTime getNextMoveTime() {
		return lastSneakTime.plusNanos(sneakMoveTimeInNanos);
	}

	public boolean move() {
		if (right)
			snakeX++;
		if (left)
			snakeX--;
		if (up)
			snakeY--;
		if (down)
			snakeY++;
		return !outOfBoundary() && !crashedIntoItself();
	}

	public int getSize() {
		return snakeSize;
	}

	public boolean isHeadOnFruit(Fruit fruit) {
		return getHeadX() == fruit.getX() && getHeadY() == fruit.getY();
	}

	public void eatFruit() {
		snakeSize++;
		logger.debug("snake.eatFruit: " + getHeadX() + " : " + getHeadY() + " -> " + snakeSize);
	}

	public void crawl() {
		if (isMoving()) {
			SnakeBodyPart body = new SnakeBodyPart(
					new CellData(snakeX, snakeY, panelData.getCellSize(), panelData.getCellSize()));
			snakeBodyList.add(body);
			if (snakeSize < snakeBodyList.size()) {
				snakeBodyList.remove(0);
			}
			logger.debug("snake.crawl: " + getHeadX() + " : " + getHeadY());
		}
		lastSneakTime = ZonedDateTime.now(zone);
	}

	public void draw(Graphics graphics) {
		for (SnakeBodyPart body : snakeBodyList) {
			body.draw(graphics);
		}
	}

	public int getPositionForCell(int x, int y) {
		int snakeIdx = -1;
		float centerX = x * panelData.getCellSize() + ((float) panelData.getCellSize()) / 2;
		float centerY = y * panelData.getCellSize() + ((float) panelData.getCellSize()) / 2;
		for (int i = 0; snakeIdx < 0 && i < snakeBodyList.size(); i++) {
			if (snakeBodyList.get(i).getCenterX() == centerX && snakeBodyList.get(i).getCenterY() == centerY) {
				snakeIdx = i;
			}
		}
		logger.debug("snake.isOutOfSnake:" + centerX + " : " + centerY + " -> " + snakeIdx);
		return snakeIdx;
	}

	public void keyEventProcess(int key) {
		logger.debug("snake.keyEventProcess -> " + key);
		if (key == KeyEvent.VK_RIGHT && !left) {
			moveRight();
		}
		if (key == KeyEvent.VK_LEFT && !right) {
			moveLeft();
		}
		if (key == KeyEvent.VK_UP && !down) {
			moveUp();
		}
		if (key == KeyEvent.VK_DOWN && !up) {
			moveDown();
		}
	}

	public PanelData getPanelData() {
		return panelData;
	}

	private boolean isMoving() {
		return right || left || up || down;
	}

	private int getHeadX() {
		return snakeBodyList.get(snakeBodyList.size() - 1).getX();
	}

	private int getHeadY() {
		return snakeBodyList.get(snakeBodyList.size() - 1).getY();
	}

	private boolean outOfBoundary() {
		return snakeX < 0 || snakeY < 0 || panelData.getWidth() - 1 < snakeX || panelData.getHeight() - 1 < snakeY;
	}

	private boolean crashedIntoItself() {
		return 0 < getPositionForCell(snakeX, snakeY);
	}

	private void moveRight() {
		right = true;
		up = false;
		down = false;
	}

	private void moveLeft() {
		left = true;
		up = false;
		down = false;
	}

	private void moveUp() {
		up = true;
		left = false;
		right = false;
	}

	private void moveDown() {
		down = true;
		left = false;
		right = false;
	}

}
