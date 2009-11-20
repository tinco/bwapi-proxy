package starcraftbot.proxybot.wmes;

import java.util.ArrayList;
/**
 * Represents a starting location in StarCraft.
 * 
 * Note: x and y are in tile coordinates
 */
public class ChokePointWME {

	private int x;
	
	private int y;
	
	private int width;
		
	/**
	 * Parses the starting locations.
	 */
	public static ArrayList<ChokePointWME> getLocations(String locationData) {
		ArrayList<ChokePointWME> locations = new ArrayList<ChokePointWME>();
	
		String[] locs = locationData.split(":");
		boolean first = true;
		
		for (String location : locs) {
			if (first) {
				first = false;
				continue;
			}
			
			String[] coords = location.split(";");

			ChokePointWME loc = new ChokePointWME();
			loc.x = Integer.parseInt(coords[0])/32;
			loc.y = Integer.parseInt(coords[1])/32;
			loc.width = Integer.parseInt(coords[2])/32;
			
			if (loc.width < 15) {
				locations.add(loc);
			}
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

	public int getWidth() {
		return width;
	}
	
	public String toString() {
		return x + "," + y;
	}
}
