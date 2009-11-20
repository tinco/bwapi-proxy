package starcraftbot.proxybot.bot;

import starcraftbot.proxybot.Game;
/**
 * Interface for a Java-based StarCraft agent. The game object projects game state queues.
 * Orders can be issues through game.getCommandQueue()
 *
 * Note: The start method is called in a new thread. The method should not return until the stop
 *       method is invoked.
 */
public interface StarCraftBot {

	/**
	 * Tells the bot to start executing. The caller assumes that the bot will hold onto
	 * this thread until stop is called.
	 */
	public void start(Game game);

	/**
	 * Notifies the agent that is should stop running.
	 * 
	 * The start method must terminate shortly after this method is invoked.
	 */
	public void stop();
}
