package starcraftbot.proxybot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import starcraftbot.proxybot.wmes.BaseLocationWME;
import starcraftbot.proxybot.wmes.ChokePointWME;
import starcraftbot.proxybot.wmes.MapWME;
import starcraftbot.proxybot.wmes.StartingLocationWME;
import starcraftbot.proxybot.wmes.unit.AllyUnitWME;
import starcraftbot.proxybot.wmes.unit.EnemyUnitWME;
import starcraftbot.proxybot.wmes.unit.GeyserWME;
import starcraftbot.proxybot.wmes.unit.MineralWME;
import starcraftbot.proxybot.wmes.unit.PlayerUnitWME;
import starcraftbot.proxybot.wmes.unit.UnitWME;
/**
 * GUI for showing the ProxyBot's view of the game state.
 */
public class StarCraftFrame extends JPanel implements KeyListener {

	/** draw object IDs */
	private static boolean drawIDs = true;

	/** draw the starting locations */
	private static boolean drawStartSpots = true;
	
	/** draw mineral and gas locations */
	private static boolean drawResources = true;

	/** reference to the proxy bot */
	private Game game;
	
	/** pixels per map tile, StarCraft is 32 */
	private int tileSize = 4;
	
	/** height of the resource panel */
	private int panelHeight = 30;

	/** font size for unit ids */
	private int textSize = 8;

	/** the parent of this component */
	private JFrame frame;
	
	/** draw the influence map? */
	private boolean influenceMap = false;
	
	/**
	 * Constructs a JFrame and draws the ProxyBot's state.
	 */
	public StarCraftFrame(Game game) {
		this.game = game;
		
		int width = game.getMap().getMapWidth();
		int height = game.getMap().getMapHeight();
		setPreferredSize(new Dimension(tileSize*width, tileSize*height + panelHeight));

		frame = new JFrame("Proxy Bot");
		frame.add(this);
		frame.pack();
		frame.setLocation(640, 0);
		frame.addKeyListener(this);
		frame.setVisible(true);		
	}
	
	/**
	 * Shuts down the GUI.
	 */
	public void stop() {
		frame.setVisible(false);
	}
	
	/**
	 * Draws the agent's view of the game state.
	 */
	public void paint(Graphics g) {

		// regular panel
		if (!influenceMap) {
			
			// tile set
			MapWME map = game.getMap();
			for (int y=0; y<map.getMapHeight(); y++) {
				for (int x=0; x<map.getMapWidth(); x++) {
					int walkable = map.isWalkable(x, y) ? 1 : 0;
					int buildable = map.isBuildable(x, y) ? 1 : 0;
					int height = map.getHeight(x, y);
					
					int c = (70*walkable + 60*buildable + 50*height);
					g.setColor(new Color(c,c,(int)(c*3/4)));
					g.fillRect(x*tileSize, panelHeight  + y*tileSize, tileSize, tileSize);
				}
			}
	
			// Starting locations
			if (drawStartSpots) {
				g.setColor(Color.ORANGE);
				for (StartingLocationWME location : game.getStartingLocations()) {
					g.fillRect(location.getX()*tileSize, panelHeight  + location.getY()*tileSize, 4*tileSize, 3*tileSize);				
				}
			}
			
			// minerals
			if (drawResources) {
				g.setColor(new Color(0,255,255));
				for (MineralWME unit : game.getMinerals()) {
						g.fillRect(unit.getX()*tileSize, panelHeight  + unit.getY()*tileSize, tileSize, tileSize);				
				}
			}
			
			// gas
			if (drawResources) {
				g.setColor(new Color(0,128,0));
				for (GeyserWME unit : game.getGeysers()) {
					g.fillRect(unit.getX()*tileSize, panelHeight  + unit.getY()*tileSize, 
							unit.getType().getTileWidth()*tileSize, unit.getType().getTileHeight()*tileSize);				
				}
			}
			
			// enemy units
			g.setColor(new Color(255,0,0));
			for (EnemyUnitWME unit : game.getEnemyUnits()) {
				g.fillRect(unit.getX()*tileSize, panelHeight  + unit.getY()*tileSize, 
						unit.getType().getTileWidth()*tileSize, unit.getType().getTileHeight()*tileSize);				
			}		
			
			// ally units
			g.setColor(Color.YELLOW);
			for (AllyUnitWME unit : game.getAllyUnits()) {
				g.fillRect(unit.getX()*tileSize, panelHeight  + unit.getY()*tileSize, 
						unit.getType().getTileWidth()*tileSize, unit.getType().getTileHeight()*tileSize);				
			}	
			
			// player units
			g.setColor(new Color(0,255,0));
			for (PlayerUnitWME unit : game.getPlayerUnits()) {
				g.fillRect(unit.getX()*tileSize, panelHeight  + unit.getY()*tileSize, 
						unit.getType().getTileWidth()*tileSize, unit.getType().getTileHeight()*tileSize);				
			}
			
			// unit IDs
			if (drawIDs) {
				g.setColor(new Color(255,255,255));
				g.setFont(new Font("ariel", 0, textSize));
				for (UnitWME unit : game.getUnits()) {
					g.drawString("" + unit.getID(), unit.getX()*tileSize, panelHeight  + unit.getY()*tileSize + textSize - 2);
				}		
			}

/*			 
			// Base Starting locations
			g.setColor(Color.CYAN);
			for (BaseLocationWME location : game.getBaseLocations()) {
				g.fillRect(location.getX()*tileSize, panelHeight  + location.getY()*tileSize, 4*tileSize, 3*tileSize);				
			}
			
			// choke points
			g.setColor(Color.PINK);
			for (ChokePointWME location : game.getChokePoints()) {
				int size = location.getWidth()/32;
				System.out.println(size);
				g.fillRect((location.getX() - size/2)*tileSize, 
						panelHeight  + (location.getY()-size/2)*tileSize, size*tileSize, size*tileSize);				
			}
*/
			
		}
		else {
			int w = game.getMap().getMapWidth();
			int h = game.getMap().getMapHeight();
			HashMap<Integer, Double> playerInfluence = new HashMap<Integer, Double>();
			HashMap<Integer, Double> enemyInfluence = new HashMap<Integer, Double>();
			HashMap<Integer, Double> allyInfluence = new HashMap<Integer, Double>();
					
			for (UnitWME unit : game.getPlayerUnits()) {
				for (int dy=-5; dy<=5; dy++) {
					for (int dx=-5; dx<=5; dx++) {
						double dist = Math.sqrt(dx*dx + dy*dy);
						int x = unit.getX() + dx;
						int y = unit.getY() + dy;
						
						Double influence = playerInfluence.get(y*w + x);
						influence = influence != null ? influence : 0;
						
						if (dist == 0) {
							influence += 0.5;							
						}
						else if (dist > 0 && dist < 5) {
							influence += 0.5/dist;
						}
						
						influence = Math.min(1, influence);
						playerInfluence.put(y*w + x, influence);
					}
				}					
			}					

			for (UnitWME unit : game.getEnemyUnits()) {
				for (int dy=-5; dy<=5; dy++) {
					for (int dx=-5; dx<=5; dx++) {
						double dist = Math.sqrt(dx*dx + dy*dy);
						int x = unit.getX() + dx;
						int y = unit.getY() + dy;
						
						Double influence = enemyInfluence.get(y*w + x);
						influence = influence != null ? influence : 0;
						
						if (dist == 0) {
							influence += 0.5;							
						}
						else if (dist > 0 && dist < 5) {
							influence += 0.5/dist;
						}
						
						influence = Math.min(1, influence);
						enemyInfluence.put(y*w + x, influence);
					}
				}					
			}					

			for (UnitWME unit : game.getAllyUnits()) {
				for (int dy=-5; dy<=5; dy++) {
					for (int dx=-5; dx<=5; dx++) {
						double dist = Math.sqrt(dx*dx + dy*dy);
						int x = unit.getX() + dx;
						int y = unit.getY() + dy;
						
						Double influence = allyInfluence.get(y*w + x);
						influence = influence != null ? influence : 0;
						
						if (dist == 0) {
							influence += 0.5;							
						}
						else if (dist > 0 && dist < 5) {
							influence += 0.5/dist;
						}
						
						influence = Math.min(1, influence);
						allyInfluence.put(y*w + x, influence);
					}
				}					
			}					
			
			for (int y=0; y<h; y++) {
				for (int x=0; x<w; x++) {
					int position = y*w + x;
					Double pInfluence = playerInfluence.get(position);
					Double eInfluence = enemyInfluence.get(position);
					Double aInfluence = allyInfluence.get(position);
					pInfluence = (pInfluence != null) ? pInfluence : 0;
					eInfluence = (eInfluence != null) ? eInfluence : 0;
					aInfluence = (aInfluence != null) ? aInfluence : 0;
					
					g.setColor(new Color((float)(double)eInfluence, (float)(double)pInfluence, (float)(double)aInfluence, 0.2f));
					g.fillRect(x*tileSize, panelHeight  + y*tileSize, tileSize, tileSize);									
				}
			}
		}
		
		// status panel
		g.setColor(new Color(255,255,255));
		g.setFont(new Font("ariel", 0, 12));
		g.fillRect(0, 0, getWidth(), panelHeight); 
		
		// minerals
		g.setColor(new Color(0,0,255));
		g.fillRect(5, 10, 10, 10);
		g.setColor(new Color(0,0,0));
		g.drawRect(5, 10, 10, 10);
		g.setColor(new Color(0,0,0));
		g.drawString("" + game.getPlayer().getMinerals(), 25, 20);

		// gas
		g.setColor(new Color(0,255,0));
		g.fillRect(105, 10, 10, 10);
		g.setColor(new Color(0,0,0));
		g.drawRect(105, 10, 10, 10);
		g.setColor(new Color(0,0,0));
		g.drawString("" + game.getPlayer().getGas(), 125, 20);
		
		// supply
		g.setColor(new Color(0,0,0));
		g.drawString((game.getPlayer().getSupplyUsed()/2) + "/" 
  				   + (game.getPlayer().getSupplyTotal()/2), 200, 20);		
	}

	/**
	 * Enable/disable the influence map.
	 */
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_I) {
			influenceMap = !influenceMap;
		}
	}

	public void keyReleased(KeyEvent e) {}

	public void keyTyped(KeyEvent e) {}
}
