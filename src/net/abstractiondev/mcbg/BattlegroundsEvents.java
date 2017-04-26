package net.abstractiondev.mcbg;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.abstractiondev.mcbg.data.Arena;
import net.abstractiondev.mcbg.data.BattlegroundsPlayer;
import net.abstractiondev.mcbg.data.MatchType;
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
			
			BattlegroundsPlayer v = plugin.playerFiles.get(victim.getUniqueId().toString());
			
			v.Deaths[MatchType.SINGLE]++;
			v.Matches[MatchType.SINGLE]++;
		}
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerDamage(EntityDamageByEntityEvent event)
	{
		if(event.getEntity() instanceof Player)
		{
			Player victim = (Player) event.getEntity();
			
			Player damager = null;
			if(event.getDamager() instanceof Player)
			{
				damager = (Player) event.getDamager();
			}
			else if(event.getDamager() instanceof Arrow)
			{
				Arrow a = (Arrow) event.getDamager();
				
				if(a.getShooter() instanceof Player)
				{
					damager = (Player) a.getShooter();
				}
			}
			
			if(damager != null)
			{
				if(BattlegroundsPlugin.config.showDamageLog) victim.sendMessage(ChatColor.DARK_RED + "-" + ((int)event.getFinalDamage()) + " health from " + damager.getName());
				
				if(victim.isDead())
				{
					// Handle player statistics
					
					BattlegroundsPlayer k, v;
					k = plugin.playerFiles.get(damager.getUniqueId().toString());
					v = plugin.playerFiles.get(victim.getUniqueId().toString());
					
					k.Kills[MatchType.SINGLE]++;
					k.EloTotal[MatchType.SINGLE]+=v.EloTotal[MatchType.SINGLE];
					
					v.EloTotal[MatchType.SINGLE]+=k.EloTotal[MatchType.SINGLE];
					
					plugin.playerFiles.put(damager.getUniqueId().toString(), k);
					plugin.playerFiles.put(victim.getUniqueId().toString(), v);
				}
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
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDisconnect(PlayerQuitEvent event)
	{
		Player p = event.getPlayer();
		Arena aRef = null;
		for(Arena a : plugin.arenas)
		{
			for(Player pl : a.getPlayers())
			{
				if(pl.getName().equalsIgnoreCase(p.getName()))
				{
					aRef = a;
				}
			}
		}
		
		if(aRef != null)
		{
			aRef.getPlayers().remove(p);
			
			for(Player pl : aRef.getPlayers())
			{
				pl.sendMessage(ChatColor.GRAY + p.getName() + " has left the arena (" + ChatColor.RED + "disconnect" + ChatColor.GRAY + ").");
			}
		}
		
		plugin.loader.savePlayer(p);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		plugin.playerFiles.put(event.getPlayer().getUniqueId().toString(), plugin.loader.loadPlayer(event.getPlayer()));
	}
	
}
