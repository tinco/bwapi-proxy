package starcraftbot.proxybot.wmes;

import java.util.ArrayList;

import starcraftbot.proxybot.Constants.Race;
import starcraftbot.proxybot.wmes.TechTypeWME.TechType;

/**
 * Stores information about a player
 * 
 * Note: the supply used and supply total variables are double what you would expect, because
 *       small units are represented as 1 supply in StarCraft.
 */
public class PlayerWME {

	/** the player identifier */
	private int playerID;

	/** current mineral supply */
	private int minerals;
	
	/** current gas supply */
	private int gas;
	
	/** amount of supply used by the player */
	private int supplyUsed;

	/** amount of supply provided by the player */
	private int supplyTotal;
	
	/** the players name */
	private String name;
	
	/** the players race */
	private String race;

	private int raceID;
	
	/** the players type, see http://code.google.com/p/bwapi/wiki/PlayerTypes */
	private int type;

	/** specifies if the player is an ally */
	private boolean ally;

	private int[] researchProgress = new int[47];
	private int[] upgradeProgress = new int[63];

	/**
	 * Parses the player data.
	 */
	public static ArrayList<PlayerWME> getPlayers(String playerData) {
		ArrayList<PlayerWME> players = new ArrayList<PlayerWME>();
		
		String[] playerDatas = playerData.split(":");
		boolean first = true;
		
		for (String data : playerDatas) {
			if (first) {
				first = false;
				continue;
			}

			String[] attributes = data.split(";");
			PlayerWME player = new PlayerWME();
			player.playerID = Integer.parseInt(attributes[0]);			
			player.race = attributes[1];		
			player.raceID = Race.valueOf(player.race).ordinal();
			player.name= attributes[2];			
			player.type = Integer.parseInt(attributes[3]);			
			player.ally = attributes[4].equals("1");						
			players.add(player);
		}		

		return players;
	}

	/**
	 * Updates the players attributes given the command data.
	 * 
	 * Expects a message of the form "status;minerals;gas;supplyUsed;SupplyTotal:..."
	 */
	public void update(String playerData) {		
		String[] attributes = playerData.split(":")[0].split(";");
		
		minerals = Integer.parseInt(attributes[1]);
		gas = Integer.parseInt(attributes[2]);
		supplyUsed = Integer.parseInt(attributes[3]);
		supplyTotal = Integer.parseInt(attributes[4]);
		String researchUpdate = attributes[5];
		String upgradeUpdate = attributes[6];

		for (int i=0; i<researchProgress.length; i++) {
			researchProgress[i] = Integer.parseInt("" + researchUpdate.charAt(i));
		}
		
		for (int i=0; i<upgradeProgress.length; i++) {
			upgradeProgress[i] = Integer.parseInt("" + upgradeUpdate.charAt(i));
		}
	}
	
	public boolean getResearchedSiege() {
		return researchProgress[TechType.Tank_Siege_Mode.ordinal()] == 4;
	}

	public boolean getResearchedMines() {
		return researchProgress[TechType.Spider_Mines.ordinal()] == 4;
	}
	
	/**
	 * Returns if the player is an ally.
	 */
	public boolean isAlly() {
		return ally;
	}
	
	/**
	 * Returns the player's mineral count, only accurate for the bot player.
	 */
	public int getMinerals() {
		return minerals;
	}

	/**
	 * Returns the player's gas count, only accurate for the bot player.
	 */
	public int getGas() {
		return gas;
	}

	/**
	 * Gets the current supply used. (Its double the expected value)
	 */
	public int getSupplyUsed() {
		return supplyUsed;
	}

	/**
	 * Gets the current supply provided . (Its double the expected value)
	 */
	public int getSupplyTotal() {
		return supplyTotal;
	}

	/** 
	 * Returns a unique id for the player.
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * Returns the players race.
	 */
	public String getRace() {
		return race;
	}
	
	/**
	 * Returns the players race ID.
	 */
	public int getRaceID() {
		return raceID;
	}

	public String toString() {
		return 
			"mins:" + minerals +
			" gas:" + gas +
			" supplyUsed:" + supplyUsed +
			" supplyTotal:" + supplyTotal;
	}
}