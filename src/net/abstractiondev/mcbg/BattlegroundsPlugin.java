package net.abstractiondev.mcbg;

import java.util.HashSet;
import java.util.logging.Logger;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.abstractiondev.mcbg.data.Arena;
import net.abstractiondev.mcbg.data.BattlegroundsConfig;
import net.abstractiondev.mcbg.managers.DataLoader;
import net.milkbowl.vault.permission.Permission;

/*
 * Battlegrounds Plugin  
 */

public class BattlegroundsPlugin extends JavaPlugin
{
	public Logger log;
	public DataLoader loader;
	public HashSet<Arena> arenas;
	public BattlegroundsConfig config;
	
	public static Permission permission = null;
	
	@Override
	public void onEnable()
	{
		log = Logger.getLogger("Minecraft");
		loader = new DataLoader(this);
		arenas = new HashSet<Arena>();
		
		log.info("[Battlegrounds] Initializing Minecraft Battlegrounds Plugin by JuicyKitten and GrumpyBear.");
		
		log.info("[Battlegrounds] Initializing permissions link.");
		log.info("[Battlegrounds] " + (setupPermissions() ? "Linked to Vault permissions." : "Failed to hook into permissions."));
		
		this.getDataFolder().mkdirs();
		
		log.info("[Battlegrounds] Loading plugin data.");
		loader.loadConfiguration();
		loader.loadArenas();
		
	}
	@Override
	public void onDisable()
	{
		log.info("Disabling Minecraft Battlegrounds Plugin by JuicyKitten and GrumpyBear.");
	}
	
	private boolean setupPermissions()
	{
		RegisteredServiceProvider<Permission> provider = this.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if(provider != null) permission = provider.getProvider();
		return (permission != null);
	}
}
