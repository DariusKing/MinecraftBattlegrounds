package net.abstractiondev.mcbg.handlers;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.bukkit.selections.Selection;

import net.abstractiondev.mcbg.BattlegroundsPlugin;
import net.abstractiondev.mcbg.data.Arena;
import net.md_5.bungee.api.ChatColor;

public class Command_BG implements CommandExecutor
{

	private BattlegroundsPlugin plugin;
	public Command_BG(BattlegroundsPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	private String buildString(String[] args, int start)
	{
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for(String sub : args)
			if(i++ >= start) builder.append(sub + " ");
		return builder.toString().trim();
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
					case "FORCE":
						if(sender.isOp() || BattlegroundsPlugin.permission.has(sender, "bg.arena.manage") || BattlegroundsPlugin.permission.has(sender, "bg.arena.*")|| BattlegroundsPlugin.permission.has(sender, "bg.*"))
						{
							if(args.length >= 2)
							{
								String identifier = buildString(args,1);
								
								identifier = identifier.toLowerCase();
								identifier = identifier.replace(' ', '_');
								//p.getInventory().addItem(plugin.creation_wand);
								
								boolean found = false;
								
								for(Arena a : plugin.arenas)
									if(a.getIdentifier().equalsIgnoreCase(identifier))
									{
										World w = Bukkit.getWorld(a.getWorld());
										Location la = new Location(w,a.getMinX(),a.getMinY(),a.getMinZ());
										Location lb = new Location(w,a.getMaxX(),a.getMaxY(),a.getMaxZ());
										
										Location r = la.add(la.subtract(lb).multiply(0.5));
										Bukkit.getWorld(a.getWorld()).getWorldBorder().setCenter(r);
										w.getWorldBorder().setSize(50.00);
										w.getWorldBorder().reset();
										
										a.setActive(true);
										
										found = true;
										break;
									}
								
								if(!found)
								{
									p.sendMessage(ChatColor.RED + "Arena not found.");
								}
							}
							else
							{
								p.sendMessage(ChatColor.RED + "Syntax: /bg create [name]");
							}
						}
						else
						{
							p.sendMessage(ChatColor.RED + "You do not have permission to manage arenas.");
						}
						break;
					case "CREATE":
						if(sender.isOp() || BattlegroundsPlugin.permission.has(sender, "bg.arena.manage") || BattlegroundsPlugin.permission.has(sender, "bg.arena.*")|| BattlegroundsPlugin.permission.has(sender, "bg.*"))
						{
							if(args.length >= 2)
							{
								String identifier = buildString(args,1);
								String friendly_name = identifier;
								
								identifier = identifier.toLowerCase();
								identifier = identifier.replace(' ', '_');
								//p.getInventory().addItem(plugin.creation_wand);
								
								boolean found = false, valid = true;
								
								for(char c : identifier.toCharArray())
									if(!Character.isAlphabetic(c) && !Character.isDigit(c) && c != '_')
									{
										p.sendMessage(ChatColor.RED + "Invalid name for arena. Names must be alphanumeric (with underscores).");
										valid = false;
										break;
									}
								
								for(Arena a : plugin.arenas)
									if(a.getIdentifier().equalsIgnoreCase(identifier))
									{
										p.sendMessage(ChatColor.RED + "An arena with this name already exists.");
										found = true;
										break;
									}
								
								if(identifier.length() < 3)
								{
									p.sendMessage(ChatColor.RED + "Invalid name for arena. Names must be at least 3 characters in length.");
									valid = false;
								}
								
								if(valid && !found)
								{
									Selection sel = BattlegroundsPlugin.worldedit.getSelection(p);
									if(sel != null)
									{
										Arena newArena = new Arena();
										
										newArena.setIdentifier(identifier);
										newArena.setRegion(sel);
										newArena.setFriendlyName(friendly_name);
										
										plugin.arenas.add(newArena);
										plugin.loader.saveArenas();
										
										p.sendMessage(ChatColor.DARK_PURPLE + "New arena '" + friendly_name + "' (" + identifier + ") has been created.");
									}
									else
									{
										p.sendMessage(ChatColor.RED + "You must first select a region using the WorldEdit selection tool.");
									}
								}
							}
							else
							{
								p.sendMessage(ChatColor.RED + "Syntax: /bg create [name]");
							}
						}
						else
						{
							p.sendMessage(ChatColor.RED + "You do not have permission to manage arenas.");
						}
						break;
					case "TEST":
						if(sender.isOp() || BattlegroundsPlugin.permission.has(sender, "bg.arena.manage") || BattlegroundsPlugin.permission.has(sender, "bg.arena.*")|| BattlegroundsPlugin.permission.has(sender, "bg.*"))
						{
							if(args.length >= 2)
							{
								String identifier = buildString(args,1);			
								identifier = identifier.toLowerCase();
								identifier.replace(' ', '_');
								
								//p.getInventory().addItem(plugin.creation_wand);
								
								boolean found = false;
								
								for(Arena a : plugin.arenas)
									if(a.getIdentifier().equalsIgnoreCase(identifier))
									{
										a.setRoundTimer(1);
										found = true;
										p.sendMessage(ChatColor.DARK_PURPLE + "Arena set to test mode.");
									}
								
								if(!found)
								{
									p.sendMessage(ChatColor.RED + "Arena not found.");
								}
							}
							else
							{
								p.sendMessage(ChatColor.RED + "Syntax: /bg test [name]");
							}
						}
						else
						{
							p.sendMessage(ChatColor.RED + "You do not have permission to manage arenas.");
						}
						break;
					case "LIST":
						if(sender.isOp() || BattlegroundsPlugin.permission.has(sender, "bg.arena.list") || BattlegroundsPlugin.permission.has(sender, "bg.arena.*")|| BattlegroundsPlugin.permission.has(sender, "bg.*"))
						{
							int count_alive = 0;
							p.sendMessage(ChatColor.DARK_PURPLE + "List of Battlegrounds Arenas (" + plugin.arenas.size() + "):");
							for(Arena a : plugin.arenas)
							{
								// Count currently-living players who are registered with this arena (they are removed upon death)
								count_alive = 0;
								for(Player player : a.getPlayers())
									count_alive += (!player.isDead()) ? 1 : 0;
								
								p.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.DARK_PURPLE + a.getFriendlyName() + ChatColor.DARK_GRAY + " [" + (a.isActive() ? ChatColor.GREEN + "Match In Progress " + ChatColor.DARK_GRAY + "(" + count_alive + " Alive)": ChatColor.YELLOW + "Waiting for Players" + ChatColor.DARK_GRAY + "]"));
							}
							
							if(plugin.arenas.size() == 0)
								p.sendMessage(ChatColor.DARK_GRAY + "No arenas loaded.");
						}
						else
						{
							p.sendMessage(ChatColor.RED + "You do not have permission to list arenas.");
						}
						break;
					case "DELETE":
						if(sender.isOp() || BattlegroundsPlugin.permission.has(sender, "bg.arena.manage") || BattlegroundsPlugin.permission.has(sender, "bg.arena.*")|| BattlegroundsPlugin.permission.has(sender, "bg.*"))
						{
							if(args.length >= 2)
							{
								String name = args[1].toLowerCase();
								
								boolean found = false;
								for(Arena a : plugin.arenas)
								{
									if(a.getIdentifier().contains(name))
									{
										found = true;
										p.sendMessage(ChatColor.DARK_PURPLE + "Removed arena '" + a.getIdentifier() + "'.");
										
										File f = new File(plugin.getDataFolder() + File.separator + "arenas" + File.separator + a.getIdentifier() + ".bga");
										plugin.arenas.remove(a);
										f.delete();
										break;
									}
								}
								
								if(!found)
									p.sendMessage(ChatColor.RED + "An arena with the supplied identifier could not be found.");
							}
							else
							{
								p.sendMessage(ChatColor.RED + "Syntax: /bg create [name]");
							}
						}
						else
						{
							p.sendMessage(ChatColor.RED + "You do not have permission to manage arenas.");
						}
						break;
					case "JOIN":
						if(args.length < 2)
						{
							if(sender.isOp() || BattlegroundsPlugin.permission.has(sender, "bg.arena.join.single") || BattlegroundsPlugin.permission.has(sender, "bg.arena.join.*")|| BattlegroundsPlugin.permission.has(sender, "bg.arena.*")|| BattlegroundsPlugin.permission.has(sender, "bg.*"))
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
								if(sender.isOp() || BattlegroundsPlugin.permission.has(sender, "bg.arena.join.single") || BattlegroundsPlugin.permission.has(sender, "bg.arena.join.*") || BattlegroundsPlugin.permission.has(sender, "bg.arena.*") || BattlegroundsPlugin.permission.has(sender, "bg.*"))
								{
									//p.sendMessage("" + (p.getInventory().getStorageContents().length));
									
									int item_count = 0;
									for(ItemStack is : p.getInventory().getContents())
									{
										try
										{
											if(is.getType() != org.bukkit.Material.AIR) item_count++;
										}
										catch(Exception e)
										{
											continue;
										}
									}
									
									if(item_count == 0)
									{
										p.sendMessage(ChatColor.DARK_PURPLE + "Joining a single match...");
										
										Arena match = null;
										
										// Gets the first arena 
										for(Arena a : plugin.arenas)
										{
											if(!a.isActive() && a.getPlayers().size() < 25)
											{
												match = a;
												break;
											}
										}
										
										if(match != null)
										{
											match.getPlayers().add(p);
											
											if(match.getPlayers().size() >= 1)
											{ // When the player count reaches this amount while waiting: Start the match
												for(Player pl : match.getPlayers())
												{
													pl.sendMessage(ChatColor.DARK_PURPLE + "Match starting...");
												}
											}
										}
									}
									else
									{
										p.sendMessage(ChatColor.RED + "You must have an empty inventory to queue for a match.");
									}
								}
								else
								{
									p.sendMessage(ChatColor.RED + "You do not have permission to join a single match.");
								}
								break;
							default:
								p.sendMessage(ChatColor.RED + "Invalid match type.");
								break;
							}
						}
						break;
					default:
						p.sendMessage(ChatColor.RED + "Invalid command. Use /bg help for assistance.");
						break;
					}
					
				}
				else
				{
					sender.sendMessage("This command may only be executed by a player in-game.");
				}
			}
			else
			{ // subcommand not defined
				if(sender instanceof Player)
				{
					Player p = (Player) sender;

					p.sendMessage(ChatColor.DARK_PURPLE + "Battlegrounds Help:");
					if(sender.isOp() || BattlegroundsPlugin.permission.has(sender, "bg.arena.manage") || BattlegroundsPlugin.permission.has(sender, "bg.arena.*") || BattlegroundsPlugin.permission.has(sender, "bg.*"))
					{
						p.sendMessage(ChatColor.DARK_PURPLE + "/bg " + ChatColor.LIGHT_PURPLE + "create [name]" + ChatColor.DARK_PURPLE + " - " + ChatColor.GRAY + "Administrative command to create a new arena.");
						p.sendMessage(ChatColor.DARK_PURPLE + "/bg " + ChatColor.LIGHT_PURPLE + "delete [name]" + ChatColor.DARK_PURPLE + " - " + ChatColor.GRAY + "Administrative command to delete an existing arena.");
						++count;
					}
					if(sender.isOp() || BattlegroundsPlugin.permission.has(sender, "bg.arena.join.single") || BattlegroundsPlugin.permission.has(sender, "bg.arena.join.*")  || BattlegroundsPlugin.permission.has(sender, "bg.arena.join.*") || BattlegroundsPlugin.permission.has(sender, "bg.arena.*") || BattlegroundsPlugin.permission.has(sender, "bg.*"))
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
					sender.sendMessage("This command may only be executed by a player in-game.");
				}
			}
			
			return true;
		}
		
		return false;
	}

}
