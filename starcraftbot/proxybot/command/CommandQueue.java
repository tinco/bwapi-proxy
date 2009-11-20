package starcraftbot.proxybot.command;

import java.util.ArrayList;

import starcraftbot.proxybot.command.Command.StarCraftCommand;
/**
 * Class for queueing up commands to be sent to StarCraft. This class handles the 
 * asynchronuous communication between the agent and StarCraft.
 */
public class CommandQueue {
	
	/** queued up commands (orders) to send to StarCraft */
	private ArrayList<Command> commandQueue = new ArrayList<Command>();

	/** message number of commands to send to starcraft per response */
	private int maxCommandsPerMessage = 20;
	
	/**
	 * Gets the commands to execute in starcraft.
	 */
	public String getCommands() {
    	StringBuffer commandData = new StringBuffer("commands");

    	synchronized (commandQueue) {
    		int commandsAdded = 0;
    		
    		while (commandQueue.size() > 0 && commandsAdded < maxCommandsPerMessage) {
    			commandsAdded++;
    			Command command = commandQueue.remove(commandQueue.size() - 1);	    			
    			commandData.append(
    						   ":" + command.getCommand()
    					     + ";" + command.getUnitID()
    					     + ";" + command.getArg0()
    					     + ";" + command.getArg1()
    					     + ";" + command.getArg2());
    		}	    		
		}

    	return commandData.toString();
	}
	
	/**
	 * Adds a command to the command queue.
	 * 
	 * @param command - the command to execture, see the Orders enumeration
	 * @param unitID - the unit to control
	 * @param arg0 - the first command argument
	 * @param arg1 - the second command argument
	 * @param arg2 - the third command argument
	 */
	private void doCommand(StarCraftCommand command, int unitID, int arg0, int arg1, int arg2) {
		synchronized (commandQueue) {
			commandQueue.add(new Command(command, unitID, arg0, arg1, arg2));
		}
	}
	
    /**********************************************************
     * Commands
     *********************************************************/

	/**
	 * Tells the unit to attack move the specific location (in tile coordinates).
	 * 
	 * 	// virtual bool attackMove(Position position) = 0;
	 */
	public void attackMove(int unitID, int x, int y) { 
		doCommand(StarCraftCommand.attackMove, unitID, x, y, 0);
    }
    
	/**
	 * Tells the unit to attack another unit.
	 * 
	 * 	// virtual bool attackUnit(Unit* target) = 0;
	 */
    public void attackUnit(int unitID, int targetID) { 
		doCommand(StarCraftCommand.attackUnit, unitID, targetID, 0, 0);
    }

    /**
     * Tells the unit to right click (move) to the specified location (in tile coordinates).
     * 
     * 	// virtual bool rightClick(Position position) = 0;
     */
    public void rightClick(int unitID, int x, int y) { 
		doCommand(StarCraftCommand.rightClick, unitID, x, y, 0);
    }

    /**
     * Tells the unit to right click (move) on the specified target unit 
     * (Includes resources).
     * 
     * 	// virtual bool rightClick(Unit* target) = 0;
     */
    public void rightClick(int unitID, int targetID) { 
		doCommand(StarCraftCommand.rightClickUnit, unitID, targetID, 0, 0);
    }

    /**
     * Tells the building to train the specified unit type.
     * 
     * 	// virtual bool train(UnitType type) = 0;
     */
    public void train(int unitID, int typeID) { 
		doCommand(StarCraftCommand.train, unitID, typeID, 0, 0);
    }

    /**
     * Tells a worker unit to construct a building at the specified location.
     * 
     * 	// virtual bool build(TilePosition position, UnitType type) = 0;
     */
    public void build(int unitID, int tx, int ty, int typeID) { 
		doCommand(StarCraftCommand.build, unitID, tx, ty, typeID);
    }

    /**
     * Tells the building to build the specified add on.
     * 
     * 	// virtual bool buildAddon(UnitType type) = 0;
     */
    public void buildAddon(int unitID, int typeID) { 
		doCommand(StarCraftCommand.buildAddon, unitID, typeID, 0, 0);
    }

    /**
     * Tells the building to research the specified tech type.
     * 
     * 	// virtual bool research(TechType tech) = 0;
     */
    public void research(int unitID, int techTypeID) { 
		doCommand(StarCraftCommand.research, unitID, techTypeID, 0, 0);
    }
    
    /**
     * Tells the building to upgrade the specified upgrade type.
     * 
     * 	// virtual bool upgrade(UpgradeType upgrade) = 0;
     */
    public void upgrade(int unitID, int upgradeTypeID) { 
		doCommand(StarCraftCommand.upgrade, unitID, upgradeTypeID, 0, 0);
    }
    
    /**
     * Orders the unit to stop moving. The unit will chase enemies that enter its vision.
     * 
     * 	// virtual bool stop() = 0;
     */
    public void stop(int unitID) {
		doCommand(StarCraftCommand.stop, unitID, 0, 0, 0);
    }

    /**
     * Orders the unit to hold position. The unit will not chase enemies that enter its vision.
     * 
     * 	// virtual bool holdPosition() = 0;
     */
    public void holdPosition(int unitID) {
		doCommand(StarCraftCommand.holdPosition, unitID, 0, 0, 0);
    }

    /**
     * Orders the unit to patrol between its current location and the specified location.
     * 
     * 	// virtual bool patrol(Position position) = 0;
     */
    public void patrol(int unitID, int x, int y) { 
		doCommand(StarCraftCommand.patrol, unitID, x, y, 0);
    }
    
    /**
     * Orders a unit to follow a target unit.
     * 
     * 	// virtual bool follow(Unit* target) = 0;
     */
    public void follow(int unitID, int targetID) { 
		doCommand(StarCraftCommand.follow, unitID, targetID, 0, 0);
    }

    /**
     * Sets the rally location for a building. 
     * 
     * 	// virtual bool setRallyPosition(Position target) = 0;
     */
    public void setRallyPosition(int unitID, int x, int y) { 
		doCommand(StarCraftCommand.setRallyPosition, unitID, x, y, 0);
    }

    /**
     * Sets the rally location for a building based on the target unit's current position.
     * 
     * 	// virtual bool setRallyUnit(Unit* target) = 0;
     */
    public void setRallyUnit(int unitID, int targetID) { 
		doCommand(StarCraftCommand.setRallyUnit, unitID, targetID, 0, 0);
    }

    /**
     * Instructs an SCV to repair a target unit.
     * 
     * 	// virtual bool repair(Unit* target) = 0;
     */
    public void repair(int unitID, int targetID) {
		doCommand(StarCraftCommand.repair, unitID, targetID, 0, 0);
    }

    /**
     * Orders a zerg unit to morph to a different unit type.
     * 
     * 	// virtual bool morph(UnitType type) = 0;
     */
    public void morph(int unitID, int typeID) {
		doCommand(StarCraftCommand.morph, unitID, typeID, 0, 0);
    }

    /**
     * Tells a zerg unit to burrow. Burrow must be upgraded for non-lurker units.
     * 
     * 	// virtual bool burrow() = 0;
     */
    public void burrow(int unitID) {
		doCommand(StarCraftCommand.burrow, unitID, 0, 0, 0);
    }
    
    /**
     * Tells a burrowed unit to unburrow.
     * 
     * 	// virtual bool unburrow() = 0;
     */
    public void unburrow(int unitID) {
		doCommand(StarCraftCommand.unburrow, unitID, 0, 0, 0);
    }
    
    /**
     * Orders a siege tank to siege.
     * 
     * 	// virtual bool siege() = 0;
     */
    public void siege(int unitID) {
		doCommand(StarCraftCommand.siege, unitID, 0, 0, 0);
    }

    /** 
     * Orders a siege tank to un-siege.
     * 
     * 	// virtual bool unsiege() = 0;
     */
    public void unsiege(int unitID) {
		doCommand(StarCraftCommand.unsiege, unitID, 0, 0, 0);
    }

    /**
     * Tells a unit to cloak. Works for ghost and wraiths. 
     * 
     * 	// virtual bool cloak() = 0;
     */
    public void cloak(int unitID) {
		doCommand(StarCraftCommand.cloak, unitID, 0, 0, 0);
    }
    
    /**
     * Tells a unit to decloak, works for ghosts and wraiths.
     * 
     * 	// virtual bool decloak() = 0;
     */
    public void decloak(int unitID) {
		doCommand(StarCraftCommand.decloak, unitID, 0, 0, 0);
    }

    /** 
     * Commands a Terran building to lift off.
     * 
     * 	// virtual bool lift() = 0;
     */
    public void lift(int unitID) {
		doCommand(StarCraftCommand.lift, unitID, 0, 0, 0);
    }

    /**
     * Commands a terran building to land at the specified location.
     * 
     * 	// virtual bool land(TilePosition position) = 0;
     */
    public void land(int unitID, int tx, int ty) {
		doCommand(StarCraftCommand.land, unitID, tx, ty, 0);
    }
    
    /**
     * Orders the transport unit to load the target unit.
     * 
     * 	// virtual bool load(Unit* target) = 0;
     */
    public void load(int unitID, int targetID) { 
		doCommand(StarCraftCommand.load, unitID, targetID, 0, 0);
    }

    /**
     * Orders a transport unit to unload the target unit at the current transport location.
     * 
     * 	// virtual bool unload(Unit* target) = 0;
     */
    public void unload(int unitID, int targetID) { 
		doCommand(StarCraftCommand.unload, unitID, targetID, 0, 0);
    }

    /**
     * Orders a transport to unload all units at the current location.
     * 
     * 	// virtual bool unloadAll() = 0;
     */
    public void unloadAll(int unitID) {
		doCommand(StarCraftCommand.unloadAll, unitID, 0, 0, 0);
    }

    /**
     * Orders a unit to unload all units at the target location.
     * 
     * 	// virtual bool unloadAll(Position position) = 0;
     */
    public void unloadAll(int unitID, int x, int y) { 
		doCommand(StarCraftCommand.unloadAllPosition, unitID, x, y, 0);
    }
    
    /**
     * Orders a being to stop being constructed.
     * 
     * 	// virtual bool cancelConstruction() = 0;
     */
    public void cancelConstruction(int unitID) {
		doCommand(StarCraftCommand.cancelConstruction, unitID, 0, 0, 0);
    }

    /**
     * Tells an scv to pause construction on a building.
     * 
     *	// virtual bool haltConstruction() = 0;
     */
    public void haltConstruction(int unitID) {
		doCommand(StarCraftCommand.haltConstruction, unitID, 0, 0, 0);
    }
    
    /**
     * Orders a zerg unit to stop morphing.
     * 
     * 	// virtual bool cancelMorph() = 0;
     */
    public void cancelMorph(int unitID) {
		doCommand(StarCraftCommand.cancelMorph, unitID, 0, 0, 0);
    }
    
    /** 
     * Tells a building to remove the last unit from its training queue.
     * 
     * 	// virtual bool cancelTrain() = 0;
     */
    public void cancelTrain(int unitID) {
		doCommand(StarCraftCommand.cancelTrain, unitID, 0, 0, 0);
    }
    
    /**
     * Tells a building to remove a specific unit from its queue.
     * 
     * 	// virtual bool cancelTrain(int slot) = 0;
     */
    public void cancelTrain(int unitID, int slot) { 
		doCommand(StarCraftCommand.cancelTrainSlot, unitID, slot, 0, 0);
    }
    
    /**
     * Orders a Terran building to stop constructing an add on.
     * 
     * 	// virtual bool cancelAddon() = 0;
     */
    public void cancelAddon(int unitID) {
		doCommand(StarCraftCommand.cancelAddon, unitID, 0, 0, 0);
    }
    
    /***
     * Tells a building cancel a research in progress. 
     * 
     * 	// virtual bool cancelResearch() = 0;
     */
    public void cancelResearch(int unitID) { 
		doCommand(StarCraftCommand.cancelResearch, unitID, 0, 0, 0);
    }
    
    /***
     * Tells a building cancel an upgrade  in progress. 
     * 
     * 	// virtual bool cancelUpgrade() = 0;
     */
    public void cancelUpgrade(int unitID) { 
		doCommand(StarCraftCommand.cancelUpgrade, unitID, 0, 0, 0);
    }
    
    /**
     * Tells the unit to use the specified tech, (i.e. STEM PACKS)
     * 
     *  // virtual bool useTech(TechType tech) = 0;
     */
    public void useTech(int unitID, int techTypeID) { 
		doCommand(StarCraftCommand.useTech, unitID, techTypeID, 0, 0);
    }
    
    /**
     * Tells the unit to use tech at the target location.
     * 
     * Note: for AOE spells such as plague.
     * 
     *  // virtual bool useTech(TechType tech, Position position) = 0;
     */
    public void useTech(int unitID, int techTypeID, int x, int y) { 
		doCommand(StarCraftCommand.useTechPosition, unitID, techTypeID, x, y);
    }

    /**
     * Tells the unit to use tech on the target unit.
     * 
     * Note: for targeted spells such as irradiate.
     * 
     *  // virtual bool useTech(TechType tech, Unit* target) = 0;
     */
    public void useTech(int unitID, int techTypeID, int targetID) { 
		doCommand(StarCraftCommand.useTechTarget, unitID, techTypeID, targetID, 0);
    }	
    
    /**********************************************************
     * Utilities
     *********************************************************/

	/**
	 * Sets the game speed
	 * 
	 * 0 = 16xfastest
	 */
	public void setGameSpeed(int speed) { 
		doCommand(StarCraftCommand.gameSpeed, speed, 0, 0, 0);
    }
}
