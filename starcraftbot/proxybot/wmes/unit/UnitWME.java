package starcraftbot.proxybot.wmes.unit;

import java.util.ArrayList;
import java.util.HashMap;

import starcraftbot.proxybot.wmes.PlayerWME;
import starcraftbot.proxybot.wmes.UnitTypeWME;
import starcraftbot.proxybot.wmes.UnitTypeWME.UnitType;
/**
 * Represents a unit in StarCraft.
 */
public class UnitWME {
	
	/** a unique identifier for referencing the unit */
	protected int ID = -1;
	
	/** the player the unit belongs too */
	private int playerID;

	/** the unit type */
	private UnitTypeWME type;
	
	/** x tile position */
	private int x;
	
	/** y tile position */
	private int y;
	
	/** unit hit points */
	private int hitPoints;
	
	/** unit shields */
	private int shields;
	
	/** unit energy */
	private int energy;
	
	/** an internal timer used in StarCraft */
	private int orderTimer;

	/** for vultures only */
	protected int mineCount = 0;
	
	private int buildTimer;
	private int trainTimer;
	private int researchTimer;
	private int upgradeTimer;

	/**
	 * Order type currently being executed by the unit.
	 * @See the Order enum in Constants.java
	 */
	private int order;

	/** resources remaining, mineral count for patches, and gas for geysers */ 
	private int resources;
	
	private int addonID;

	/**
	 * Parses the unit data.
	 */
	public static ArrayList<UnitWME> getUnits(String unitData, HashMap<Integer, UnitTypeWME> types, 
			int playerID, PlayerWME[] players) {
		
		ArrayList<UnitWME> units = new ArrayList<UnitWME>();		
		String[] unitDatas = unitData.split(":");
		boolean first = true;
		
		for (String data : unitDatas) {
			if (first) {
				first = false;
				continue;
			}
			
			String[] attributes = data.split(";");
			UnitWME unit = new UnitWME();
			int pID = Integer.parseInt(attributes[1]);
			int type = Integer.parseInt(attributes[2]);

			if (pID == playerID) {
				unit = new PlayerUnitWME();
			}
			else if (type == UnitType.Resource_Mineral_Field.ordinal()) {
				unit = new MineralWME();
			}
			else if (type == UnitType.Resource_Vespene_Geyser.ordinal()) {
				unit = new GeyserWME();
			}
			else if(pID != playerID && pID != 11 && !players[pID].isAlly()) {
				unit = new EnemyUnitWME();
			}
			else if(pID != playerID && pID != 11 && players[pID].isAlly()) {
				unit = new AllyUnitWME();
			}

			unit.ID = Integer.parseInt(attributes[0]);
			unit.playerID = pID;
			unit.type = types.get(type);
			unit.x = Integer.parseInt(attributes[3]);
			unit.y = Integer.parseInt(attributes[4]);
			unit.hitPoints = Integer.parseInt(attributes[5]);
			unit.shields = Integer.parseInt(attributes[6]);
			unit.energy = Integer.parseInt(attributes[7]);
			unit.buildTimer = Integer.parseInt(attributes[8]);				
			unit.trainTimer = Integer.parseInt(attributes[9]);				
			unit.researchTimer = Integer.parseInt(attributes[10]);				
			unit.upgradeTimer = Integer.parseInt(attributes[11]);				
			unit.orderTimer = Integer.parseInt(attributes[12]);				
			unit.order = Integer.parseInt(attributes[13]);
			unit.resources = Integer.parseInt(attributes[14]);			
			unit.addonID = Integer.parseInt(attributes[15]);			
			unit.mineCount = Integer.parseInt(attributes[16]);			
			units.add(unit);
		}		

		return units;
	}
		
	public int getMineCount() {
		return mineCount;
	}

	public int getAddonID() {
		return addonID;
	}
	
	public boolean getIsBuilt() {
		return buildTimer == 0;
	}
	
	public double distance(double x, double y) {
		return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));
	}
	
	/**
	 * Returns the Euclidian distance to the specified unit.
	 */
	public double distance(UnitWME unit) {
		double dx = unit.x - x;
		double dy = unit.y - y;
		
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	/**
	 * Returns the unit ID.
	 */
	public int getID() {
		return ID;
	}
	
	/**
	 * Returns if the unit is a worker type.
	 */
	public boolean getIsWorker() {
		return type.isWorker();
	}
	
	/**
	 * Returns whether the unit is a center type.
	 */
	public boolean getIsCenter() {
		return type.isCenter();
	}

	/**
	 * Returns the id of the player controlling the unit.
	 * @return
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * Returns the unit type.
	 */
	public UnitTypeWME getType() {
		return type;
	}

	/**
	 * Returns the type ID.
	 */
	public int getTypeID() {
		return type.getId();
	}

	/**
	 * Returns the units x position (tile coordinates)
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Returns the units y position (tile coordinates)
	 */	
	public int getY() {
		return y;
	}
	
	/**
	 * Returns the units health
	 */
	public int getHitPoints() {
		return hitPoints;
	}

	/**
	 * Returns the units shield energy.
	 */
	public int getShields() {
		return shields;
	}

	/**
	 * Returns the amount of energy (mana) the unit has.
	 */
	public int getEnergy() {
		return energy;
	}

	/**
	 * Returns the ID of the order the unit is currently executing.
	 */
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public int getOrderTimer() {
		return orderTimer;
	}

	public int getBuildTimer() {
		return buildTimer;	
	}
	public int getTrainTimer() {
		return trainTimer;
	}
	
	public int getResearchTimer() {
		return researchTimer;
	}
	
	public int getUpgradeTimer() {
		return upgradeTimer;
	}
	
	/**
	 * Specifies the amount of resources remaining (for mineral patches and geysers)
	 */
	public int getResources() {
		return resources;
	}
	
	public String toString() {
		return 
			"ID:" + ID +
			" player:" + playerID +
			" type:" + type.getName() +
			" x:" + x +
			" y:" + y +
			" hitPoints:" + hitPoints +
			" shields:" + shields +
			" enemy:" + energy +
			" orderTimer:" + orderTimer +
			" order:" + order + 
			" resource:" + resources;
	}
}
