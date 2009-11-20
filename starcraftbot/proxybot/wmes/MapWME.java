package starcraftbot.proxybot.wmes;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
/**
 * Stores tile information about a map in StarCraft.
 * 
 * Note: internally in StarCraft, the height and walkable arrays have a higher
 *       resolution than the size tile in this class. Each tile is actually
 *       a 4x4 grid, but this has been abstracted away for simplicity for now.
 */
public class MapWME {

	/** the map name */
	private String mapName;
	
	/** number of tiles wide */
	private int mapWidth;

	/** number of tiles high */
	private int mapHeight;
	
	/** height array (valid values are 0,1,2) */
	private int[][] height;
	
	/** buildable array */
	private boolean[][] buildable;
	
	/** walkable array */
	private boolean[][] walkable;
		
//	/** 
//	 * Returns the map name.
//	 */
	public String getMapName() {
		return mapName;
	}

	/**
	 * Returns the map width (in tiles).
	 */
	public int getMapWidth() {
		return mapWidth;
	}

	/**
	 * Returns the map height (in tiles).
	 */
	public int getMapHeight() {
		return mapHeight;
	}
	
	/**
	 * Returns true if the map is walkable and the given tile coordinates.
	 */
	public boolean isWalkable(int tx, int ty) {
		return walkable[ty][tx];
	}
	
	/**
	 * Returns true if the map is buildable and the given tile coordinates.
	 */
	public boolean isBuildable(int tx, int ty) {
		return buildable[ty][tx];
	}

	public boolean isBuildable(int tx, int ty, int width, int height) {
		boolean result = true;

		if (tx < 0 || ty < 0 || (tx + width) >= mapWidth || (ty + height) >= mapHeight) {
			return false;
		}
		
		for (int h=0; h<height; h++) {			
			for (int w=0; w<width; w++) {
				result &= buildable[ty + h][tx + w];
			}
		}
		
		return result;
	}

	
	/**
	 * Returns the height of the map at the given tile coordinates.
	 */
	public int getHeight(int tx, int ty) {
		return height[ty][tx];
	}
	
	/**
	 * Creates a map based on the string recieved from the AIModule.
	 * 
	 * @param mapData - mapname:width:height:data
	 * 
	 *  Data is a character array where each tile is represented by 3 characters, 
	 *  which specific height, buildable, walkable.
	 */
	public MapWME(String mapData) {
		String[] map = mapData.split(":");
		String data = map[3];

		mapName = map[0];
		mapWidth = Integer.parseInt(map[1]);
		mapHeight = Integer.parseInt(map[2]);
		
		height = new int[mapHeight][mapWidth];
		buildable = new boolean[mapHeight][mapWidth];
		walkable = new boolean[mapHeight][mapWidth];
		
		int total = mapWidth * mapHeight;		
		for (int i=0; i<total; i++) {
			int w = i%mapWidth;
			int h = i/mapWidth;
			
			String tile = data.substring(3*i, 3*i + 3);
			
			height[h][w] = Integer.parseInt(tile.substring(0,1));
			buildable[h][w] = (1 == Integer.parseInt(tile.substring(1,2))); 
			walkable[h][w] = (1 == Integer.parseInt(tile.substring(2,3))); 
 		}		
	}
	
	/**
	 * Displays the main properties.
	 */
	public void print() {
		System.out.println("Name: " + mapName);
		System.out.println("Size: " + mapWidth + " x " + mapHeight);
		
		System.out.println("\nBuildable");
	 	  System.out.println("---------");
		for (int y=0; y<mapHeight; y++) {
			for (int x=0; x<mapWidth; x++) {
				System.out.print(buildable[y][x] ? " " : "X");
			}			
			
			System.out.println();
		}

		System.out.println("\nWalkable");
	 	  System.out.println("--------");
		for (int y=0; y<mapHeight; y++) {
			for (int x=0; x<mapWidth; x++) {
				System.out.print(walkable[y][x] ? " " : "X");
			}			
			
			System.out.println();
		}

		System.out.println("\nHeight");
	 	  System.out.println("------");
		for (int y=0; y<mapHeight; y++) {
			for (int x=0; x<mapWidth; x++) {
				switch (height[y][x]) {
					case 2:
						System.out.print(" " );
						break;
					case 1:
						System.out.print("*" );
						break;
					case 0:
						System.out.print("X" );
						break;
				}
			}			
			
			System.out.println();
		}
	}
}