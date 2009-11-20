package starcraftbot.proxybot.wmes;

import java.util.ArrayList;
/**
 * Represents a starting location in StarCraft.
 * 
 * Note: x and y are in tile coordinates
 */
public class StartingLocationWME {

	private int x;
	
	private int y;
		
	/**
	 * Parses the starting locations.
	 */
	public static ArrayList<StartingLocationWME> getLocations(String locationData) {
		ArrayList<StartingLocationWME> locations = new ArrayList<StartingLocationWME>();
	
		String[] locs = locationData.split(":");
		boolean first = true;
		
		for (String location : locs) {
			if (first) {
				first = false;
				continue;
			}
			
			String[] coords = location.split(";");

			StartingLocationWME loc = new StartingLocationWME();
			loc.x = Integer.parseInt(coords[0]) + 2;
			loc.y = Integer.parseInt(coords[1]) + 1;
			locations.add(loc);
		}
		
		return locations;		
	}
	
	/**
	 * Returns the x coordinate of the starting location (tile coordinates).
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the y coordinate of the starting location (tile coordinates).
	 */
	public int getY() {
		return y;
	}
	
	public String toString() {
		return x + "," + y;
	}
}
