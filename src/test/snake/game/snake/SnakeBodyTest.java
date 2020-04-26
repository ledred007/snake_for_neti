package snake.game.snake;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.event.KeyEvent;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

import org.junit.Test;

import snake.game.data.CellData;
import snake.game.data.PanelData;
import snake.game.fruits.Apple;

public class SnakeBodyTest {

	private static final ZoneId zone = ZoneId.systemDefault();

	class MockRandom extends Random {
		private static final long serialVersionUID = 9007L;

		public int nextInt(int bound) {
			int result = 0;
			switch (bound) {
			case 5:
				result = 2;
				break;
			case 6:
				result = 1;
				break;
			}
			return result;
		}

	}

	private final Random random = new MockRandom();

	@Test(expected = NullPointerException.class)
	public void constructorWithNull() {
		new SnakeBody(null, new Random());
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void constructorWithZerosPanel() {
		PanelData panelData = new PanelData(0, 0, 0);
		new SnakeBody(panelData, new Random());
	}

	@Test
	public void constructorWithValidPanelCheckData() {
		PanelData panelData = new PanelData(10, 12, 10);
		SnakeBody snakeBody = new SnakeBody(panelData, random);
		assertTrue(ZonedDateTime.now(zone).isBefore(snakeBody.getNextMoveTime()));
		assertEquals(1, snakeBody.getSize());
		assertEquals(-1, snakeBody.getPositionForCell(0, 0));
		assertEquals(-1, snakeBody.getPositionForCell(0, 1));
		assertEquals(-1, snakeBody.getPositionForCell(0, 2));
		assertEquals(-1, snakeBody.getPositionForCell(0, 3));
		assertEquals(-1, snakeBody.getPositionForCell(1, 0));
		assertEquals(-1, snakeBody.getPositionForCell(1, 1));
		assertEquals(-1, snakeBody.getPositionForCell(1, 2));
		assertEquals(-1, snakeBody.getPositionForCell(1, 3));
		assertEquals(-1, snakeBody.getPositionForCell(2, 0));
		assertEquals(0, snakeBody.getPositionForCell(2, 1));
		assertEquals(-1, snakeBody.getPositionForCell(2, 2));
		assertEquals(-1, snakeBody.getPositionForCell(2, 3));
		assertEquals(-1, snakeBody.getPositionForCell(3, 0));
		assertEquals(-1, snakeBody.getPositionForCell(3, 1));
		assertEquals(-1, snakeBody.getPositionForCell(3, 2));
		assertEquals(-1, snakeBody.getPositionForCell(3, 3));
	}

	@Test
	public void moveAndEat() {
		PanelData panelData = new PanelData(10, 12, 10);
		SnakeBody snakeBody = new SnakeBody(panelData, random); // Snake head on [2,1]
		assertEquals(1, snakeBody.getSize());
		CellData cellData = new CellData(4, 1, 10, 10);
		Apple apple = new Apple(cellData); // Aple on [4,1]
		assertEquals(false, snakeBody.isHeadOnFruit(apple));
		snakeBody.keyEventProcess(KeyEvent.VK_RIGHT);
		assertEquals(true, snakeBody.move()); // Snake move right [2,1] -> [3,1] 
		snakeBody.crawl(); // Snake head on [3,1]
		assertEquals(false, snakeBody.isHeadOnFruit(apple)); // Not on Apple
		assertEquals(true, snakeBody.move()); // Snake move right [3,1] -> [4,1]
		snakeBody.crawl(); // Snake head on [4,1]
		assertEquals(true, snakeBody.isHeadOnFruit(apple)); // On Apple
		snakeBody.eatFruit();
		assertEquals(2, snakeBody.getSize()); // Snake grown
	}

	@Test
	public void moveOutOfBoundaryOnTop() {
		PanelData panelData = new PanelData(10, 12, 10);
		SnakeBody snakeBody = new SnakeBody(panelData, random); // Snake head on [2,1]
		snakeBody.keyEventProcess(KeyEvent.VK_UP);
		assertEquals(true, snakeBody.move()); // Snake move up [2,1] -> [2,0]
		snakeBody.crawl(); // Snake head on [2,0]
		assertEquals(false, snakeBody.move()); // Snake move up [2,0] -> [2,-1] - Out of Boundary
	}

	@Test
	public void moveOutOfBoundaryOnLeft() {
		PanelData panelData = new PanelData(10, 12, 10);
		SnakeBody snakeBody = new SnakeBody(panelData, random); // Snake head on [2,1]
		snakeBody.keyEventProcess(KeyEvent.VK_LEFT);
		assertEquals(true, snakeBody.move()); // Snake move left [2,1] -> [1,1]
		snakeBody.crawl(); // Snake head on [1,1]
		assertEquals(true, snakeBody.move()); // Snake move left [1,1] -> [0,1]
		snakeBody.crawl(); // Snake head on [0,1]
		assertEquals(false, snakeBody.move()); // Snake move left [0,1] -> [-1,1]
	}

	@Test
	public void moveOutOfBoundaryOnBottom() {
		PanelData panelData = new PanelData(10, 12, 10);
		SnakeBody snakeBody = new SnakeBody(panelData, random); // Snake head on [2,1]
		snakeBody.keyEventProcess(KeyEvent.VK_DOWN);
		assertEquals(true, snakeBody.move()); // Snake move down [2,1] -> [2,2]
		snakeBody.crawl(); // Snake head on [2,2]
		assertEquals(true, snakeBody.move()); // Snake move down [2,2] -> [2,3]
		snakeBody.crawl(); // Snake head on [2,3]
		assertEquals(true, snakeBody.move()); // Snake move down [2,3] -> [2,4]
		snakeBody.crawl(); // Snake head on [2,4]
		assertEquals(true, snakeBody.move()); // Snake move down [2,4] -> [2,5]
		snakeBody.crawl(); // Snake head on [2,5]
		assertEquals(true, snakeBody.move()); // Snake move down [2,5] -> [2,6]
		snakeBody.crawl(); // Snake head on [2,6]
		assertEquals(true, snakeBody.move()); // Snake move down [2,6] -> [2,7]
		snakeBody.crawl(); // Snake head on [2,7]
		assertEquals(true, snakeBody.move()); // Snake move down [2,7] -> [2,8]
		snakeBody.crawl(); // Snake head on [2,8]
		assertEquals(true, snakeBody.move()); // Snake move down [2,8] -> [2,9]
		snakeBody.crawl(); // Snake head on [2,9]
		assertEquals(true, snakeBody.move()); // Snake move down [2,9] -> [2,10]
		snakeBody.crawl(); // Snake head on [2,10]
		assertEquals(true, snakeBody.move()); // Snake move down [2,10] -> [2,11]
		snakeBody.crawl(); // Snake head on [2,11]
		assertEquals(false, snakeBody.move()); // Snake move down [2,11] -> [2,12] - Out of Boundary
	}

	@Test
	public void moveOutOfBoundaryOnRight() {
		PanelData panelData = new PanelData(10, 12, 10);
		SnakeBody snakeBody = new SnakeBody(panelData, random); // Snake head on [2,1]
		snakeBody.keyEventProcess(KeyEvent.VK_RIGHT);
		assertEquals(true, snakeBody.move()); // Snake move right [2,1] -> [2,3]
		snakeBody.crawl(); // Snake head on [2,3]
		assertEquals(true, snakeBody.move()); // Snake move right [2,3] -> [2,4]
		snakeBody.crawl(); // Snake head on [2,4]
		assertEquals(true, snakeBody.move()); // Snake move right [2,4] -> [2,5]
		snakeBody.crawl(); // Snake head on [2,5]
		assertEquals(true, snakeBody.move()); // Snake move right [2,5] -> [2,6]
		snakeBody.crawl(); // Snake head on [2,6]
		assertEquals(true, snakeBody.move()); // Snake move right [2,6] -> [2,7]
		snakeBody.crawl(); // Snake head on [2,7]
		assertEquals(true, snakeBody.move()); // Snake move right [2,7] -> [2,8]
		snakeBody.crawl(); // Snake head on [2,8]
		assertEquals(true, snakeBody.move()); // Snake move right [2,8] -> [2,9]
		snakeBody.crawl(); // Snake head on [2,9]
		assertEquals(false, snakeBody.move()); // Snake move right [2,9] -> [2,10] - Out of Boundary
	}

}
