package starcraftbot.proxybot.bot;

import starcraftbot.proxybot.Game;
import starcraftbot.proxybot.Constants.Order;
import starcraftbot.proxybot.Constants.Race;
import starcraftbot.proxybot.wmes.UnitTypeWME;
import starcraftbot.proxybot.wmes.UnitTypeWME.UnitType;
import starcraftbot.proxybot.wmes.unit.UnitWME;
/**
 * Example implementation of the StarCraftBot.
 * 
 * This build will tell workers to mine, build additional workers,
 * and build additional supply units.
 */
public class ExampleStarCraftBot implements StarCraftBot {

	/** specifies that the agent is running */
	boolean running = true;
	
	/**
	 * Starts the bot.
	 * 
	 * The bot is now the owner of the current thread.
	 */
	public void start(Game game) {

		// run until told to exit
		while (running) {
			try {
				Thread.sleep(1000);
			}
			catch (Exception e) {}

			// start mining
			for (UnitWME unit : game.getPlayerUnits()) {
				if (unit.getOrder() == Order.PlayerGuard.ordinal()) {

					int patchID = -1;
					double closest = Double.MAX_VALUE;
					
					for (UnitWME minerals : game.getMinerals()) {
						double dx = unit.getX() - minerals.getX();
						double dy = unit.getY() - minerals.getY();
						double dist = Math.sqrt(dx*dx + dy*dy); 
			
						if (dist < closest) {
							patchID = minerals.getID();
							closest = dist;
						}
					}					
					
					if (patchID != -1) {
						game.getCommandQueue().rightClick(unit.getID(), patchID);
					}
				}				
			}		
			
			// build more workers
			if (game.getPlayer().getMinerals() >= 50) {
				int workerType = UnitTypeWME.getWorkerType(game.getPlayerRace());

				// morph a larva into a worker
				if (game.getPlayerRace() == Race.Zerg.ordinal()) {
					for (UnitWME unit : game.getPlayerUnits()) {
						if (unit.getTypeID() == UnitType.Zerg_Larva.ordinal()) {
							game.getCommandQueue().morph(unit.getID(), workerType);
						}
					}						
				}
				// train a worker
				else {				
					int centerType = UnitTypeWME.getCenterType(game.getPlayerRace());
	
					for (UnitWME unit : game.getPlayerUnits()) {
						if (unit.getTypeID() == centerType) {
							game.getCommandQueue().train(unit.getID(), workerType);
						}
					}
				}
			}
			
			// build more supply
			if (game.getPlayer().getMinerals() >= 100 && 
					game.getPlayer().getSupplyUsed() >= (game.getPlayer().getSupplyTotal() - 2) ) {
				int supplyType = UnitTypeWME.getSupplyType(game.getPlayerRace());

				// morph a larva into a supply 
				if (game.getPlayerRace() == Race.Zerg.ordinal()) {
					for (UnitWME unit : game.getPlayerUnits()) {
						if (unit.getTypeID() == UnitType.Zerg_Larva.ordinal()) {
							game.getCommandQueue().morph(unit.getID(), supplyType);
						}
					}						
				}
				// build a farm
				else {
					int workerType = UnitTypeWME.getWorkerType(game.getPlayerRace());
					for (UnitWME unit : game.getPlayerUnits()) {
						if (unit.getTypeID() == workerType) {
							
							// pick a random spot near the worker
							game.getCommandQueue().build(unit.getID(), 
									unit.getX() + (int)(-10.0 + Math.random() * 20.0), 
									unit.getY() + (int)(-10.0 + Math.random() * 20.0), 
									supplyType);							
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Tell the main thread to quit.
	 */
	public void stop() {
		running = false;
	}
}
