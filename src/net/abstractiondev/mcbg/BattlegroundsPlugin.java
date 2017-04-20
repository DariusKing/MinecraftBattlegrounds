package net.abstractiondev.mcbg;

import java.util.HashSet;
import java.util.Random;
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
		
		log.info("[UNIT TEST] Creating objects.");
		Random r = new Random();
		Arena arena;
		for(int i = 0; i < 5; i++)
		{
			arena = new Arena();
			arena.identifier = "arena_new_" + r.nextInt(999);
			
			arenas.add(arena);
		}
		log.info("[UNIT TEST] Saving  objects.");
		loader.saveArenas();
		log.info("[UNIT TEST] Clearing objects.");
		arenas.clear();
		log.info("[UNIT TEST] Loading objects.");
		loader.loadArenas();
		log.info("[UNIT TEST] Loaded " + arenas.size() + " objects.");
		log.info("[UNIT TEST] Decrypted Objects:");
		for(Arena a : arenas)
		{
			log.info(a.identifier);
		}
	}
	@Override
	public void onDisable()
	{
		log.info("Disabling Minecraft Battlegrounds Plugin by JuicyKitten and GrumpyBear.");
	}
}
