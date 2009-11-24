package bwapiproxy.bot;

import bwapiproxy.bot.impl.ExampleStarCraftBot;


public class StarCraftBotFactory {

	private static StarCraftBot instance;

	private StarCraftBotFactory() {
		//instance = new NullStarCraftBot();
		instance = new ExampleStarCraftBot();
	}

	public static StarCraftBot getBot() {
		return instance;
	}

	public static void setBot(StarCraftBot bot) {
		instance = bot;
	}
}
