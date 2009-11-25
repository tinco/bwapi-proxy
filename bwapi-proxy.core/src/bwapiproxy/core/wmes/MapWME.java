package bwapiproxy.core.wmes;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
/**
 * Stores tile information about a map in StarCraft.
 *
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
	 * Returns true if the map is walkable at the given walk tile coordinates.
	 */
	public boolean isWalkable(int wx, int wy) {
		return walkable[wy][wx];
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
	 * Returns the height of the map at the given walk tile coordinates.
	 */
	public int getHeight(int wx, int wy) {
		return height[wy][wx];
	}
	
	/**
	 * Creates a map based on the string recieved from the AIModule.
	 * 
	 * @param mapData - mapname:width:height:builddata:heightdata:walkdata
	 * 
	 *  The data fields are character arrays in which each character is a tile,
	 *  the characters specify height, buildable and walkable.
	 */
	public MapWME(String mapData) {
		String[] map = mapData.split(":");
		String buildData = map[3];
		String heightData = map[4];
		String walkData = map[5];

		mapName = map[0];
		mapWidth = Integer.parseInt(map[1]);
		mapHeight = Integer.parseInt(map[2]);

		height = new int[mapHeight][mapWidth];
		buildable = new boolean[mapHeight * 4][mapWidth * 4];
		walkable = new boolean[mapHeight * 4][mapWidth * 4];

		int total = mapWidth * mapHeight;		
		for (int i=0; i<total; i++) {
			int w = i%mapWidth;
			int h = i/mapWidth;

			buildable[h][w] = ("1" == buildData[i]);
		}		

		int total = mapWidth * 4 * mapHeight * 4;
		for (int i=0; i < total;i++) {
			int w = i%mapWidth;
			int h = i/mapWidth;

			walkable[h][w] = ("1" == walkData[i]);
			height[h][w] = Integer.parseInt(heightData[i]);
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
		for (int y=0; y<mapHeight*4; y++) {
			for (int x=0; x<mapWidth*4; x++) {
				System.out.print(walkable[y][x] ? " " : "X");
			}			
			
			System.out.println();
		}

		System.out.println("\nHeight");
	 	  System.out.println("------");
		for (int y=0; y<mapHeight*4; y++) {
			for (int x=0; x<mapWidth*4; x++) {
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
