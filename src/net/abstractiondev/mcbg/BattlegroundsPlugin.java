package net.abstractiondev.mcbg;

import java.util.HashSet;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import net.abstractiondev.mcbg.data.Arena;
import net.abstractiondev.mcbg.managers.DataLoader;

/*
 * Battlegrounds Plugin  
 */

public class BattlegroundsPlugin extends JavaPlugin
{
	public Logger log;
	public DataLoader loader;
	public HashSet<Arena> arenas;
	
	@Override
	public void onEnable()
	{
		log = Logger.getLogger("Minecraft");
		loader = new DataLoader(this);
		arenas = new HashSet<Arena>();
		
		log.info("Initializing Minecraft Battlegrounds Plugin by JuicyKitten and GrumpyBear.");
		
		this.getDataFolder().mkdirs();
		
		loader.loadArenas();
	}
	@Override
	public void onDisable()
	{
		log.info("Disabling Minecraft Battlegrounds Plugin by JuicyKitten and GrumpyBear.");
	}
}
