package net.abstractiondev.mcbg;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
//import org.bukkit.permissions.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.abstractiondev.mcbg.data.Arena;
import net.abstractiondev.mcbg.data.BattlegroundsConfig;
import net.abstractiondev.mcbg.data.BattlegroundsPlayer;
import net.abstractiondev.mcbg.handlers.Command_BG;
import net.abstractiondev.mcbg.handlers.SingleMatchHandler;
import net.abstractiondev.mcbg.managers.DataLoader;

/*
 * Battlegrounds Plugin  
 */

public class BattlegroundsPlugin extends JavaPlugin
{
	public Logger log;
	public static Logger slog;
	public DataLoader loader;
	public ArrayList<Arena> arenas;
	public static BattlegroundsConfig config;
	
	public static net.milkbowl.vault.permission.Permission permission = null;
	public static WorldEditPlugin worldedit;
	
	public ItemStack creation_wand;
	public HashMap<String,Location> creation_selA, creation_selB;
	public HashMap<String,String> creation_name;
	public HashMap<String,Boolean> creation_state;
	
	public HashMap<String,BattlegroundsPlayer> playerFiles;
	
	@Override
	public void onEnable()
	{
		log = Logger.getLogger("Minecraft");
		slog = log;
		loader = new DataLoader(this);
		arenas = new ArrayList<Arena>();
		
		log.info("[Battlegrounds] Initializing Minecraft Battlegrounds Plugin by JuicyKitten and GrumpyBear.");
		
		log.info("[Battlegrounds] Initializing permissions link.");
		log.info("[Battlegrounds] " + (setupPermissions() ? "Linked to Vault permissions." : "Failed to hook into permissions."));
		
		this.getDataFolder().mkdirs();
		
		log.info("[Battlegrounds] Loading plugin data.");
		loader.loadConfiguration();
		loader.loadArenas();
		
		// Register listeners
		this.getServer().getPluginManager().registerEvents(new BattlegroundsEvents(this), this);
		
		// Commands
		this.getCommand("bg").setExecutor(new Command_BG(this));
		
		// Register WorldEdit hook
		worldedit = (WorldEditPlugin) this.getServer().getPluginManager().getPlugin("WorldEdit");
		
		// Creation
		creation_selA = new HashMap<String,Location>();
		creation_selB = new HashMap<String,Location>();
		creation_name = new HashMap<String,String>();
		creation_state = new HashMap<String,Boolean>();
		
		playerFiles = new HashMap<String,BattlegroundsPlayer>();
		
		// Load existing players
		
		File f = new File(this.getDataFolder() + File.separator + "players");
		if(!f.exists())
			log.severe("[Battlegrounds] " + (f.mkdir() ? "Created" : "Unable to create") + " directory '" + f.getAbsolutePath() + "'.");
		
		f = new File(this.getDataFolder() + File.separator + "arenas");
		if(!f.exists())
			log.severe("[Battlegrounds] " + (f.mkdir() ? "Created" : "Unable to create") + " directory '" + f.getAbsolutePath() + "'.");
		
		if(this.getServer().getOnlinePlayers().size() > 0)
		{
			log.info("[Battlegrounds] Loading data for currently online players...");
			for(Player pl : this.getServer().getOnlinePlayers())
			{
				this.playerFiles.put(pl.getUniqueId().toString(),loader.loadPlayer(pl));
			}
		}

		f = new File(this.getDataFolder() + File.separator + "arenas");
		if(!f.exists())
			log.severe("[Battlegrounds] " + (f.mkdir() ? "Created" : "Unable to create") + " directory '" + f.getAbsolutePath() + "'.");
		
		// Schedulers
		//this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new SingleMatchHandler(this), 0L, 20L);
		
		// This schedules the task asynchronously
		this.getServer().getScheduler().runTaskTimer(this, new SingleMatchHandler(this), 0L, 20L);
	}
	@Override
	public void onDisable()
	{
		log.info("Disabling Minecraft Battlegrounds Plugin by JuicyKitten and GrumpyBear.");
		
		for(Player p : this.getServer().getOnlinePlayers())
		{
			this.loader.savePlayer(p);
		}
	}
	
	private boolean setupPermissions()
	{
		RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> provider = this.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if(provider != null) permission = provider.getProvider();
		return (permission != null);
	}
}
