package starcraftbot.proxybot.wmes;

import java.util.ArrayList;
/**
 * Represents a starting location in StarCraft.
 * 
 * Note: x and y are in tile coordinates
 */
public class BaseLocationWME {

	private int x;
	
	private int y;
		
	/**
	 * Parses the starting locations.
	 */
	public static ArrayList<BaseLocationWME> getLocations(String locationData) {
		ArrayList<BaseLocationWME> locations = new ArrayList<BaseLocationWME>();
	
		String[] locs = locationData.split(":");
		boolean first = true;
		
		for (String location : locs) {
			if (first) {
				first = false;
				continue;
			}
			
			String[] coords = location.split(";");

			BaseLocationWME loc = new BaseLocationWME();
			loc.x = Integer.parseInt(coords[0]);
			loc.y = Integer.parseInt(coords[1]);
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
