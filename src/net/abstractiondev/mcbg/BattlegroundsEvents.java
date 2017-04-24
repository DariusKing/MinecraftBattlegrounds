package net.abstractiondev.mcbg;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.abstractiondev.mcbg.data.Arena;
import net.md_5.bungee.api.ChatColor;

public class BattlegroundsEvents implements Listener {

	BattlegroundsPlugin plugin;
	public BattlegroundsEvents(BattlegroundsPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerMatchDeath(PlayerDeathEvent event)
	{
		Player victim = event.getEntity();
		
		// Detect if they are in a match
		Arena match = null;
		for(Arena a : plugin.arenas)
		{
			if(a.getPlayers().contains(victim))
				match = a;
		}
		
		if(match != null)
		{
			// Death announcement
			victim.sendMessage(ChatColor.GRAY + "You have been eliminated in Round " + match.getRound() + ".");
			for(Player p : match.getPlayers())
			{
				p.sendMessage(ChatColor.DARK_GRAY + victim.getName() + " has been " + ChatColor.DARK_RED + "eliminated" + ChatColor.DARK_GRAY + " - " + (match.getPlayers().size() <= 10 ? ChatColor.YELLOW : ChatColor.DARK_GRAY) + (match.getPlayers().size()-1) + " remaining.");
				match.getPlayers().remove(victim);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerMatchBreakBlock(BlockBreakEvent event)
	{
		Player victim = event.getPlayer();
		
		Arena match = null;
		for(Arena a : plugin.arenas)
		{
			if(a.getPlayers().contains(victim))
				match = a;
		}
		
		if(match != null)
		{
			victim.sendMessage(ChatColor.RED + "You cannot break blocks whilst in a match.");
			event.setCancelled(true);
		}
	}
}
