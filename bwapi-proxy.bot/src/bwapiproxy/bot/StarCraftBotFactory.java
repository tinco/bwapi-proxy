package bwapiproxy.bot;

import bwapiproxy.bot.impl.ExampleStarCraftBot;

/**
 * Factory for the Bot
 */
public class StarCraftBotFactory {

	/** Your bot instance */
	// TODO: use IoC to setup the bot
	private static StarCraftBot instance = new ExampleStarCraftBot();
	// private static StarCraftBot instance = new NullStarCraftBot();

	private StarCraftBotFactory() {

	}

	public static StarCraftBot getBot() {
		if (instance == null) {
			throw new IllegalStateException("Did you forget to call setBot() ?");
		}
		return instance;
	}

	public static void setBot(StarCraftBot bot) {
		instance = bot;
	}
}
