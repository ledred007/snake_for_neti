package snake.game.dao.dialects;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import snake.game.configuration.GameParameters;

public final class SqlDialectDerbyImpl implements SqlDialect {
	final static Logger logger = Logger.getLogger(SqlDialectDerbyImpl.class);

	private static final String DB_URL = "jdbc:derby:" + GameParameters.DATABASE_NAME + ";create=true";
	private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + USERS_TABLE + " ("
			+ "id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
			+ "name VARCHAR(255) NOT NULL DEFAULT '', "
			+ "score INT NOT NULL DEFAULT 0, "
			+ "date VARCHAR(255) NOT NULL DEFAULT '')";
	private static final String ALREADY_BOOTED_MESSAGE = "Another instance of Derby may have already booted the database";

	@Override
	public String getDbUrl() {
		return DB_URL;
	}

	@Override
	public String getCreateTable() {
		return CREATE_TABLE_QUERY;
	}

	@Override
	public boolean isAlreadyRuning(SQLException exception) {
		boolean result = false;
		SQLException nextException = exception.getNextException();
		if (nextException != null) {
			String message = nextException.getMessage();
			logger.debug("Database Open failed.nextException -> " + message);
			result = message.contains(ALREADY_BOOTED_MESSAGE);
		}
		return result;
	}

}
