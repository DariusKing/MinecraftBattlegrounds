package net.abstractiondev.mcbg.handlers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.abstractiondev.mcbg.BattlegroundsPlugin;
import net.md_5.bungee.api.ChatColor;

public class Command_BG implements CommandExecutor
{

	private BattlegroundsPlugin plugin;
	public Command_BG(BattlegroundsPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		switch(cmd.getName().toUpperCase())
		{
		case "BG":
			int count = 0;
			if(args.length >= 1)
			{ // subcommand defined
				if(sender instanceof Player)
				{
					Player p = (Player) sender;
					
					switch(args[0].toUpperCase())
					{
					case "CREATE":
						break;
					case "JOIN":
						if(args.length < 2)
						{
							if(BattlegroundsPlugin.permission.has(sender, "bg.arena.join.single") || BattlegroundsPlugin.permission.has(sender, "bg.arena.join.*")|| BattlegroundsPlugin.permission.has(sender, "bg.arena.*")|| BattlegroundsPlugin.permission.has(sender, "bg.*"))
							{
								p.sendMessage(ChatColor.DARK_PURPLE + "/bg " + ChatColor.LIGHT_PURPLE + "join single" + ChatColor.DARK_PURPLE + " - " + ChatColor.GRAY + "Start matchmaking for a new match.");
								++count;
							}
							
							if(count == 0)
							{
								p.sendMessage(ChatColor.GRAY + "No commands available.");
							}
						}
						else
						{
							switch(args[1].toUpperCase())
							{
							case "SINGLE":
								// Join a single match
								if(BattlegroundsPlugin.permission.has(sender, "bg.arena.join.single") || BattlegroundsPlugin.permission.has(sender, "bg.arena.join.*") || BattlegroundsPlugin.permission.has(sender, "bg.arena.*") || BattlegroundsPlugin.permission.has(sender, "bg.*"))
								{
									p.sendMessage(ChatColor.DARK_PURPLE + "Joining a single match...");
								}
								else
								{
									p.sendMessage(ChatColor.RED + "[Battlegrounds] You do not have permission to join a single match.");
								}
								break;
							default:
								p.sendMessage(ChatColor.RED + "[Battlegrounds] Invalid match type.");
								break;
							}
						}
						break;
					default:
						p.sendMessage(ChatColor.RED + "[Battlegrounds] Invalid command. Use /bg help for assistance.");
						break;
					}
					
				}
				else
				{
					sender.sendMessage("[Battlegrounds] This command may only be executed by a player in-game.");
				}
			}
			else
			{ // subcommand not defined
				if(sender instanceof Player)
				{
					Player p = (Player) sender;

					p.sendMessage(ChatColor.DARK_PURPLE + "Battlegrounds Help:");
					if(BattlegroundsPlugin.permission.has(sender, "bg.arena.manage") || BattlegroundsPlugin.permission.has(sender, "bg.arena.*") || BattlegroundsPlugin.permission.has(sender, "bg.*"))
					{
						p.sendMessage(ChatColor.DARK_PURPLE + "/bg " + ChatColor.LIGHT_PURPLE + "create" + ChatColor.DARK_PURPLE + " - " + ChatColor.GRAY + "Administrative command to create a new arena.");
						++count;
					}
					if(BattlegroundsPlugin.permission.has(sender, "bg.arena.join.single") || BattlegroundsPlugin.permission.has(sender, "bg.arena.join.*")  || BattlegroundsPlugin.permission.has(sender, "bg.arena.join.*") || BattlegroundsPlugin.permission.has(sender, "bg.arena.*") || BattlegroundsPlugin.permission.has(sender, "bg.*"))
					{
						p.sendMessage(ChatColor.DARK_PURPLE + "/bg " + ChatColor.LIGHT_PURPLE + "join single" + ChatColor.DARK_PURPLE + " - " + ChatColor.GRAY + "Start matchmaking for a new match.");
						++count;
					}
					
					if(count == 0)
					{
						p.sendMessage(ChatColor.GRAY + "No commands available.");
					}
				}
				else
				{
					sender.sendMessage("[Battlegrounds] This command may only be executed by a player in-game.");
				}
			}
			
			return true;
		}
		
		return false;
	}

}
