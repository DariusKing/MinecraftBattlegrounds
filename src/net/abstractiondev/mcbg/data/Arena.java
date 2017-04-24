package net.abstractiondev.mcbg.data;

import java.io.Serializable;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.Region;

public class Arena implements Serializable
{
	private static final long serialVersionUID = -8630123407400929986L;

	private String identifier;
	private String friendly_name;
	
	private String world;
	
	private double max_x, max_y, max_z, min_x, min_y, min_z;
	
	private boolean active;
	private HashSet<Player> players;
	
	private int round = 1;
	private int round_timer = 300;
	
	private Region playArea;
	
	public void setPlayArea(Region r)
	{
		this.playArea = r;
	}
	public Region getPlayArea()
	{
		return playArea;
	}
	public int getRoundTimer()
	{
		return round_timer;
	}
	public int getRound()
	{
		return round;
	}
	public void setRoundTimer(int seconds)
	{
		this.round_timer = seconds;
	}
	public void setRound(int round)
	{
		this.round = round;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getFriendlyName()
	{
		return friendly_name;
	}
	
	public void setFriendlyName(String s)
	{
		this.friendly_name = s;
	}
	
	public void setRegion(Selection region)
	{ // Takes the WE Selection and serializes the block locations
		world = region.getWorld().getName();
		max_x = region.getMaximumPoint().getX();
		max_y = region.getMaximumPoint().getY();
		max_z = region.getMaximumPoint().getZ();
		
		min_x = region.getMinimumPoint().getX();
		min_y = region.getMinimumPoint().getY();
		min_z = region.getMinimumPoint().getZ();
	}
	public CuboidSelection getRegion()
	{
		return new CuboidSelection(Bukkit.getWorld(this.getWorld()), new Location(Bukkit.getWorld(this.getWorld()),min_x,min_y,min_z), new Location(Bukkit.getWorld(this.getWorld()),max_x,max_y,max_z));
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public HashSet<Player> getPlayers() {
		return players;
	}

	public void setPlayers(HashSet<Player> players) {
		this.players = players;
	}

	public static long getSerialVersionUid() {
		return serialVersionUID;
	}
	
	public Arena()
	{
		long num = (System.currentTimeMillis()%9999);
		identifier = "arena_" + num;
		active = false;
		players = new HashSet<Player>();
		friendly_name = "Arena " + num;
		world = "";
		
		max_x = 0.00;
		max_y = 0.00;
		max_z = 0.00;
		min_x = 0.00;
		min_y = 0.00;
		min_z = 0.00;
		
		playArea = null;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public double getMaxX() {
		return max_x;
	}

	public void setMaxX(double max_x) {
		this.max_x = max_x;
	}

	public double getMaxY() {
		return max_y;
	}

	public void setMaxY(double max_y) {
		this.max_y = max_y;
	}

	public double getMaxZ() {
		return max_z;
	}

	public void setMaxZ(double max_z) {
		this.max_z = max_z;
	}

	public double getMinX() {
		return min_x;
	}

	public void setMinX(double min_x) {
		this.min_x = min_x;
	}

	public double getMinY() {
		return min_y;
	}

	public void setMinY(double min_y) {
		this.min_y = min_y;
	}

	public double getMinZ() {
		return min_z;
	}

	public void setMinZ(double min_z) {
		this.min_z = min_z;
	}
}
