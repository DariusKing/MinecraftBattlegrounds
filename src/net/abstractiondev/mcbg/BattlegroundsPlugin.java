package net.abstractiondev.mcbg;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

/*
 * Battlegrounds Plugin  
 */

public class BattlegroundsPlugin extends JavaPlugin
{
	private Logger log;
	
	@Override
	public void onEnable()
	{
		log = Logger.getLogger("Minecraft");
		
		log.info("Initializing Minecraft Battlegrounds Plugin by JuicyKitten and GrumpyBear.");
	}
	@Override
	public void onDisable()
	{
		log.info("Disabling Minecraft Battlegrounds Plugin by JuicyKitten and GrumpyBear.");
	}
}
