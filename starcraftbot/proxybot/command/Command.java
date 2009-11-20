package starcraftbot.proxybot.command;
/**
 * Representation of a command (Order) in StarCraft. The list of commands is enumerated here:
 * http://code.google.com/p/bwapi/wiki/Orders
 * 
 * The actual function definitions are provided in Unit.h on the AIModule side:
 *    virtual bool attackMove(Position position) = 0;
 *    virtual bool attackUnit(Unit* target) = 0;
 *    virtual bool rightClick(Position position) = 0;
 *    virtual bool rightClick(Unit* target) = 0;
 *    virtual bool train(UnitType type) = 0;
 *    virtual bool build(TilePosition position, UnitType type) = 0;
 *    virtual bool buildAddon(UnitType type) = 0;
 *    virtual bool research(TechType tech) = 0;
 *    virtual bool upgrade(UpgradeType upgrade) = 0;
 *    virtual bool stop() = 0;
 *    virtual bool holdPosition() = 0;
 *    virtual bool patrol(Position position) = 0;
 *    virtual bool follow(Unit* target) = 0;
 *    virtual bool setRallyPosition(Position target) = 0;
 *    virtual bool setRallyUnit(Unit* target) = 0;
 *    virtual bool repair(Unit* target) = 0;
 *    virtual bool morph(UnitType type) = 0;
 *    virtual bool burrow() = 0;
 *    virtual bool unburrow() = 0;
 *    virtual bool siege() = 0;
 *    virtual bool unsiege() = 0;
 *    virtual bool cloak() = 0;
 *    virtual bool decloak() = 0;
 *    virtual bool lift() = 0;
 *    virtual bool land(TilePosition position) = 0;
 *    virtual bool load(Unit* target) = 0;
 *    virtual bool unload(Unit* target) = 0;
 *    virtual bool unloadAll() = 0;
 *    virtual bool unloadAll(Position position) = 0;
 *    virtual bool cancelConstruction() = 0;
 *    virtual bool haltConstruction() = 0;
 *    virtual bool cancelMorph() = 0;
 *    virtual bool cancelTrain() = 0;
 *    virtual bool cancelTrain(int slot) = 0;
 *    virtual bool cancelAddon() = 0;
 *    virtual bool cancelResearch() = 0;
 *    virtual bool cancelUpgrade() = 0;
 *    virtual bool useTech(TechType tech) = 0;
 *    virtual bool useTech(TechType tech, Position position) = 0;
 *    virtual bool useTech(TechType tech, Unit* target) = 0;
 * 
 * Utilities:
 *    setLocalSpeed
 * 
 * On the java side, the command function definitions are provided in ProxyBot.
 * 
 * In StarCraft, commands take up to 3 arguments. 
 */
public class Command {

	/** the command to execute, as defined by StarCraftCommand */
	private int command;

	/** the unit to execute the command */
	private int unitID;
	
	/** the first argument */
	private int arg0;
	
	/** the second argument */
	private int arg1;
	
	/** the third argument */
	private int arg2;

	/**
	 * Creates a command
	 * 
	 * @param command
	 * @param unitID
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public Command(StarCraftCommand command, int unitID, int arg0, int arg1, int arg2) {
		this.command = command.ordinal();
		this.unitID = unitID;
		this.arg0 = arg0;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	/**
	 * Commands (Orders) in StarCraft.
	 */
	public enum StarCraftCommand {
	   none,
	   attackMove,
	   attackUnit,
	   rightClick,
	   rightClickUnit,
	   train,
	   build,
	   buildAddon,
	   research,
	   upgrade, 
	   stop, 
	   holdPosition, 
	   patrol,
	   follow, 
	   setRallyPosition, 
	   setRallyUnit, 
	   repair, 
	   morph, 
	   burrow, 
	   unburrow, 
	   siege, 
	   unsiege, 
	   cloak, 
	   decloak, 
	   lift, 
	   land,
	   load,
	   unload,
	   unloadAll, 
	   unloadAllPosition, 
	   cancelConstruction, 
	   haltConstruction, 
	   cancelMorph, 
	   cancelTrain, 
	   cancelTrainSlot,
	   cancelAddon, 
	   cancelResearch, 
	   cancelUpgrade, 
	   useTech, 
	   useTechPosition, 
	   useTechTarget, 		
	   gameSpeed, 		
	}	
	
	/**
	 * Returns the command ID.
	 */
	public int getCommand() {
		return command;
	}

	/**
	 * Returns the ID of the unit to execute the command.
	 */
	public int getUnitID() {
		return unitID;
	}

	/**
	 * Returns the first command argument.
	 */
	public int getArg0() {
		return arg0;
	}

	/**
	 * Returns the second command argument.
	 */
	public int getArg1() {
		return arg1;
	}

	/**
	 * Returns the third command argument.
	 */
	public int getArg2() {
		return arg2;
	}	
}
