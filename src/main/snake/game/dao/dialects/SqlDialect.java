package snake.game.dao.dialects;

import java.sql.SQLException;

public interface SqlDialect {

	String USERS_TABLE = "Users";
	String CHECK_TABLE_QUERY = "SELECT COUNT(*) AS total FROM " + USERS_TABLE;
	String SELECT_HIGHSCORE_QUERY = "SELECT MAX(score) AS highscore FROM " + USERS_TABLE;
	String SELECT_USER_QUERY = "SELECT id, score FROM " + USERS_TABLE + " WHERE name=?";
	String INSERT_USER_QUERY = "INSERT INTO " + USERS_TABLE + "(name, score, date) VALUES(?,?,?)";
	String UPDATE_USER_QUERY = "UPDATE " + USERS_TABLE + " SET score=? WHERE id=?";
	String SELECT_TOP_USERS_QUERY = "SELECT name, score FROM " + USERS_TABLE
			+ " ORDER BY score DESC, name ASC";

	default String getUsersTable() {
		return USERS_TABLE;
	}

	default String getCheckTableQuery() {
		return CHECK_TABLE_QUERY;
	}

	default String getSelectHighScoreQuery() {
		return SELECT_HIGHSCORE_QUERY;
	}

	default String getSelectUserQuery() {
		return SELECT_USER_QUERY;
	}

	default String getInsertUserQuery() {
		return INSERT_USER_QUERY;
	}

	default String getUpdateUserQuery() {
		return UPDATE_USER_QUERY;
	}

	default String getTopUsersQuery() {
		return SELECT_TOP_USERS_QUERY;
	}

	String getDbUrl();

	String getCreateTable();

	boolean isAlreadyRuning(SQLException exception);

}
