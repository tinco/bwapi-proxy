package starcraftbot.proxybot;

import java.util.ArrayList;
import java.util.HashMap;

import starcraftbot.proxybot.Constants.Race;
import starcraftbot.proxybot.command.CommandQueue;
import starcraftbot.proxybot.wmes.BaseLocationWME;
import starcraftbot.proxybot.wmes.ChokePointWME;
import starcraftbot.proxybot.wmes.MapWME;
import starcraftbot.proxybot.wmes.PlayerWME;
import starcraftbot.proxybot.wmes.StartingLocationWME;
import starcraftbot.proxybot.wmes.TechTypeWME;
import starcraftbot.proxybot.wmes.UnitTypeWME;
import starcraftbot.proxybot.wmes.UpgradeTypeWME;
import starcraftbot.proxybot.wmes.unit.AllyUnitWME;
import starcraftbot.proxybot.wmes.unit.EnemyUnitWME;
import starcraftbot.proxybot.wmes.unit.GeyserWME;
import starcraftbot.proxybot.wmes.unit.MineralWME;
import starcraftbot.proxybot.wmes.unit.PlayerUnitWME;
import starcraftbot.proxybot.wmes.unit.UnitWME;
/**
 * StarCraft AI Interface.
 * 
 * Maintains StarCraft state and provides hooks for StarCraft commands.
 * 
 * Note: all coordinates are specified in tile coordinates.
 */
public class Game {

	/** the bots player ID */
	private int playerID;

	/** the bots race */
	private int playerRace;
	
	/** player information */
	private PlayerWME player;

	/** all players */
	private ArrayList<PlayerWME> players;
	private PlayerWME[] playerArray = new PlayerWME[12];
	
	/** map information */
	private MapWME map;
	
	/** a list of the starting locations */
	private ArrayList<StartingLocationWME> startingLocations;

	/** a list of the units */
	private ArrayList<UnitWME> units;

	/** StarCraft unit types */
	private HashMap<Integer, UnitTypeWME> unitTypes = UnitTypeWME.getUnitTypeMap();

	/** list of tech types */
	private ArrayList<TechTypeWME> techTypes = TechTypeWME.getTechTypes();

	/** list of upgrade types */
	private ArrayList<UpgradeTypeWME> upgradeTypes = UpgradeTypeWME.getUpgradeTypes();

	/** queue of commands to execute */
	private CommandQueue commandQueue = new CommandQueue();
	
	/** timestamp of when the game state was last changed */
	private long lastGameUpdate = 0;

	private ArrayList<BaseLocationWME> baseLocations;
	
	private ArrayList<ChokePointWME> chokePoints;
	
	int frame = 0;
	
	/**
	 * Constructs a game object from the initial information sent from StarCraft.
	 * 
	 * The game object will not have units until update is called.
	 */
	public Game(String playerData, String locationData, String mapData, String chokesData, String basesData) {
    	String[] playerDatas = playerData.split(":");    	
    	playerID = Integer.parseInt(playerDatas[0].split(";")[1]);		
		players = PlayerWME.getPlayers(playerData);		
		
		for (PlayerWME p : players) {
			if (playerID == p.getPlayerID()) {
				player = p;
		    	playerRace = Race.valueOf(p.getRace()).ordinal();
			}
			
			playerArray[p.getPlayerID()] = p;
		}
		
		map = new MapWME(mapData);
		startingLocations = StartingLocationWME.getLocations(locationData);		
		baseLocations = BaseLocationWME.getLocations(basesData);		
		chokePoints = ChokePointWME.getLocations(chokesData);		
	}

	/**
	 * Returns the command queue.
	 */
	public CommandQueue getCommandQueue() {
		return commandQueue;
	}
	
	public int getGameFrame() {
		return frame;
	}

	/**
	 * Updates the state of the game.
	 */
	public void update(String updateData) {
		frame++;
		player.update(updateData);
		units = UnitWME.getUnits(updateData, unitTypes, playerID, playerArray);
		lastGameUpdate = System.currentTimeMillis();
	}
	
	/**
	 * Returns the time when the game state was last updated.
	 */
	public long getLastGameUpdate() {
		return lastGameUpdate;
	}
	
	/**
	 * Returns a player object for the bot.
	 */
	public PlayerWME getPlayer() {
		return player;
	}
	
	/**
	 * Returns the bots race.
	 */
	public int getPlayerRace() {
		return playerRace;
	}
	
	/**
	 * Returns the Map data.
	 */
	public MapWME getMap() {
		return map;
	}

	/**
	 * Returns a map of the tech types indexed by ID.
	 */
	public HashMap<Integer, UnitTypeWME> getUnitTypes() {
		return unitTypes;
	}

	/**
	 * Returns the starting locations.
	 */
	public ArrayList<StartingLocationWME> getStartingLocations() {
		return startingLocations;
	}

	/**
	 * Gets all units
	 */
	public ArrayList<UnitWME> getUnits() {
		return units;
	}
	
	public ArrayList<ChokePointWME> getChokePoints() {
		return chokePoints;
	}
	
	public ArrayList<BaseLocationWME> getBaseLocations() {
		return baseLocations;
	}

	/**
	 * Returns a list of the bots units.
	 */
	public ArrayList<PlayerUnitWME> getPlayerUnits() {
		ArrayList<PlayerUnitWME> playerUnits = new ArrayList<PlayerUnitWME>();
		for (UnitWME unit : units) {
			if (unit instanceof PlayerUnitWME) {
				playerUnits.add((PlayerUnitWME)unit);
			}
		}
		
		return playerUnits;
	}

	/**
	 * Returns a list of enemy units.
	 */
	public ArrayList<EnemyUnitWME> getEnemyUnits() {
		ArrayList<EnemyUnitWME> enemyUnits = new ArrayList<EnemyUnitWME>();
		for (UnitWME unit : units) {
			if (unit instanceof EnemyUnitWME) {
				enemyUnits.add((EnemyUnitWME)unit);
			}
		}
		
		return enemyUnits;
	}
	
	/**
	 * Returns a list of allied units.
	 */
	public ArrayList<AllyUnitWME> getAllyUnits() {
		ArrayList<AllyUnitWME> allyUnits = new ArrayList<AllyUnitWME>();
		for (UnitWME unit : units) {
			if (unit instanceof AllyUnitWME) {
				allyUnits.add((AllyUnitWME)unit);
			}
		}
		
		return allyUnits;
	}

	/**
	 * Returns the mineral patches.
	 */
	public ArrayList<MineralWME> getMinerals() {
		ArrayList<MineralWME> minerals= new ArrayList<MineralWME>();
		for (UnitWME unit : units) {
			if (unit instanceof MineralWME) {
				minerals.add((MineralWME)unit);
			}
		}
		
		return minerals;
	}

	/**
	 * Returns the list of geysers.
	 */
	public ArrayList<GeyserWME> getGeysers() {
		ArrayList<GeyserWME> gas = new ArrayList<GeyserWME>();
		for (UnitWME unit : units) {
			if (unit instanceof GeyserWME) {
				gas.add((GeyserWME)unit);
			}
		}
		
		return gas;
	}
	
	/**
	 * Returns the tech types.
	 */
	public ArrayList<TechTypeWME> getTechTypes() {
		return techTypes;
	}

	/**
	 * Returns the upgrade types.
	 */
	public ArrayList<UpgradeTypeWME> getUpgradeTypes() {
		return upgradeTypes;
	}
}
