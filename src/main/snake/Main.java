package snake;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import snake.game.configuration.GameParameters;
import snake.game.configuration.PanelParameters;
import snake.game.configuration.TextParameters;
import snake.game.dao.GameDao;
import snake.game.dao.GameDaoSqlImpl;
import snake.game.dao.dialects.SqlDialect;
import snake.game.dao.dialects.SqlDialectDerbyImpl;
import snake.game.data.GamePanelData;
import snake.game.panels.GamePanel;
import snake.game.panels.PlayGroundPanel;
import snake.game.panels.StatusPanel;
import snake.game.services.GameService;
import snake.game.utils.Conversion;

public class Main {
	final static Logger logger = Logger.getLogger(Main.class);

	public static JFrame frame;

	public Main(int width, int height, GameDao dbase) {
		frame = new JFrame();

		JPanel playGroundPanel = new PlayGroundPanel();
		StatusPanel statusPanel = new StatusPanel();

		GamePanelData gamePanelData = new GamePanelData(width, height, GameParameters.CELL_SIZE,
				GameParameters.GAME_PANEL_SHOW_GRID);
		GameService service = new GameService(gamePanelData, dbase, statusPanel);
		JPanel gamePanel = new GamePanel(gamePanelData, service);

		playGroundPanel.add(gamePanel);

		frame.setLayout(PanelParameters.FRAME_LAYOUT);
		frame.add(playGroundPanel, BorderLayout.NORTH);
		frame.add(statusPanel, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle(TextParameters.GAME_TITLE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		if (2 <= args.length) {
			int width = Conversion.getPositiveInteger(args[0]);
			int height = Conversion.getPositiveInteger(args[1]);
			LinkedList<String> errors = checkInputData(width, height);
			if (errors.size() == 0) {
				String alreadyRunungMessage = checkAlreadyRuning();
				if (alreadyRunungMessage == null) {
					SqlDialect dialect = new SqlDialectDerbyImpl();
					GameDao dbase = new GameDaoSqlImpl(dialect);
					if (dbase.isReady()) {
						logger.info("Start-> " + TextParameters.GAME_TITLE);
						new Main(width, height, dbase);
					} else {
						logger.error("Database Open error!");
						System.exit(0);
					}
				} else {
					logger.error(alreadyRunungMessage);
					System.exit(0);
				}
			} else {
				for (String error : errors) {
					logger.error(error);
				}
				System.exit(0);
			}
		} else {
			logger.info("Proper Usage is: java sneak <width> <height>");
			System.exit(0);
		}
	}

	private static LinkedList<String> checkInputData(int width, int height) {
		LinkedList<String> result = new LinkedList<String>();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		if (width < GameParameters.PANEL_MIN_WIDTH | height < GameParameters.PANEL_MIN_HEIGHT) {
			result.add("Proper Usage is: java sneak <width> <height>");
			result.add("- <width> and <height> are positive integer values");
			result.add("- <width> at least " + GameParameters.PANEL_MIN_WIDTH);
			result.add("- <height> at least " + GameParameters.PANEL_MIN_HEIGHT);
		} else if (screenSize.getWidth() < (width * GameParameters.CELL_SIZE) + GameParameters.WIDTH_GAP ||
				screenSize.getHeight() < (height * GameParameters.CELL_SIZE) + GameParameters.HEIGHT_GAP) {
			int maxWidth = (int) (screenSize.getWidth() - GameParameters.WIDTH_GAP) / GameParameters.CELL_SIZE;
			int maxHeight = (int) (screenSize.getHeight() - GameParameters.HEIGHT_GAP) / GameParameters.CELL_SIZE;
			result.add("The <width> or the <height> parameter too big for the display.");
			result.add("The possible value of the maximum <width> is " + maxWidth);
			result.add("The possible value of the maximum <height> is " + maxHeight);
		}
		return result;
	}

	private static String checkAlreadyRuning() {
		String message = null;
		SqlDialect dialect = new SqlDialectDerbyImpl();
		try (Connection checkConnection = DriverManager.getConnection(dialect.getDbUrl())) {
			checkConnection.close();
		} catch (SQLException e) {
			if (dialect.isAlreadyRuning(e)) {
				message = "Another instance of the program is already runing!";
			}
		}
		return message;
	}

}
