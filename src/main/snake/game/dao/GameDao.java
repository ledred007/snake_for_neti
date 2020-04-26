package snake.game.dao;

import java.util.LinkedList;

import snake.game.data.UserData;

public interface GameDao {

	boolean isReady();

	int getHighScore();

	boolean savePlayerData(UserData player);

	LinkedList<UserData> getTopPlayers();
}
