package snake.game.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import snake.game.configuration.GameParameters;
import snake.game.dao.dialects.SqlDialect;
import snake.game.data.UserData;

public class GameDaoSqlImpl implements GameDao {
	final static Logger logger = Logger.getLogger(GameDaoSqlImpl.class);

	private static final ZoneId zone = ZoneId.systemDefault();

	private Connection connection;
	private final SqlDialect dialect;
	private boolean ready;

	public GameDaoSqlImpl(SqlDialect dialect) {
		this.dialect = dialect;
		try {
			connection = DriverManager.getConnection(dialect.getDbUrl());
			ready = isUsersTableExists();
			if (!ready) {
				ready = createUsersTable();
			}
		} catch (SQLException e) {
			connection = null;
			logger.error("DataBase Create Connection Error -> ", e);
		}
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	public int getHighScore() {
		int highScore = 0;
		try (Statement stmt = connection.createStatement()) {
			ResultSet rs = stmt.executeQuery(dialect.getSelectHighScoreQuery());
			if (rs != null) {
				if (rs.next()) {
					highScore = rs.getInt("highscore");
					logger.debug("DataBase Users HighScore -> " + highScore);
				}
				rs.close();
			}
		} catch (SQLException e) {
			logger.error("DataBase Users Users HighScore Error -> ", e);
		}
		return highScore;
	}

	@Override
	public boolean savePlayerData(UserData player) {
		boolean result = false;
		Entry<Integer, Integer> entry = selectUserData(player);
		if (entry.getKey() == 0) {
			result = insertUserData(player);
		} else if (entry.getValue() < player.getScore()) {
			result = updateUserData(entry.getKey(), player.getScore());
		}
		return result;
	}

	@Override
	public LinkedList<UserData> getTopPlayers() {
		LinkedList<UserData> players = new LinkedList<UserData>();
		try (Statement stmt = connection.createStatement()) {
			stmt.setMaxRows(GameParameters.TOP_PLAYERS);
			ResultSet rs = stmt.executeQuery(dialect.getTopUsersQuery());
			while (rs != null && rs.next()) {
				players.add(new UserData(rs.getString("name"), rs.getInt("score")));
			}
			rs.close();
		} catch (SQLException e) {
			logger.error("DataBase Users Users HighScore Error -> ", e);
		}

		return players;
	}

	private boolean isUsersTableExists() {
		boolean result = false;
		try (Statement stmt = connection.createStatement()) {
			ResultSet rs = stmt.executeQuery(dialect.getCheckTableQuery());
			if (rs != null && rs.next()) {
				int counter = rs.getInt("total");
				logger.debug("DataBase Users table counter -> " + counter);
				result = 0 <= counter;
				rs.close();
			}
		} catch (SQLException e) {
			logger.debug("DataBase Users table counter error -> ", e);
			logger.warn("DataBase table didn't exist! - Table creation required!");
		}
		return result;
	}

	private boolean createUsersTable() {
		boolean result = false;
		try (Statement stmt = connection.createStatement()) {
			logger.debug("DataBase Users table create SQL -> " + dialect.getCreateTable());
			stmt.execute(dialect.getCreateTable());
			connection.commit();
			result = true;
			logger.debug("DataBase Users table created!");
		} catch (SQLException e) {
			logger.error("DataBase table create failed: ", e);
		}
		return result;
	}

	private Entry<Integer, Integer> selectUserData(UserData player) {
		int id = 0;
		int score = 0;
		try (PreparedStatement ps = connection.prepareStatement(dialect.getSelectUserQuery())) {
			ps.setString(1, player.getName());
			logger.debug("savePlayerData.selectUserData -> " + player.toString());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs != null && rs.next()) {
					id = rs.getInt("id");
					score = rs.getInt("score");
					logger.debug("savePlayerData.executeQuery -> " + id + " : " + score);
				}
			} catch (SQLException e) {
				logger.error("DataBase Users table select error -> ", e);
			}
		} catch (SQLException e) {
			logger.error("DataBase Users table select prepare error -> ", e);
		}
		return new SimpleImmutableEntry<Integer, Integer>(id, score);
	}

	private boolean insertUserData(UserData player) {
		boolean result = false;
		try (PreparedStatement ps = connection.prepareStatement(dialect.getInsertUserQuery())) {
			ps.setString(1, player.getName());
			ps.setInt(2, player.getScore());
			ps.setString(3, ZonedDateTime.now(zone).toString());
			logger.debug("insertUserData.insert -> " + player.getName() + " : " + player.getScore());
			try {
				boolean execresult = ps.execute();
				logger.debug("insertUserData.execresult -> " + execresult);
				result = true;
				connection.commit();
			} catch (SQLException e) {
				logger.error("DataBase Users table insert error -> ", e);
			}
		} catch (SQLException e) {
			logger.error("DataBase Users table insert prepare error -> ", e);
		}
		return result;
	}

	private boolean updateUserData(int id, int score) {
		boolean result = false;
		try (PreparedStatement ps = connection.prepareStatement(dialect.getUpdateUserQuery())) {
			ps.setInt(1, score);
			ps.setInt(2, id);
			logger.debug("updateUserData.update -> " + id + " : " + score);
			try {
				boolean execresult = ps.execute();
				logger.debug("updateUserData.execresult -> " + execresult);
				result = true;
				connection.commit();
			} catch (SQLException e) {
				logger.error("DataBase Users table update error -> ", e);
			}
		} catch (SQLException e) {
			logger.error("DataBase Users table insert update prepare error -> ", e);
		}
		return result;
	}

}
