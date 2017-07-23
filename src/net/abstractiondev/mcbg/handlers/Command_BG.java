package net.abstractiondev.mcbg.handlers;

import java.io.File;
import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.selections.Selection;

import net.abstractiondev.mcbg.BattlegroundsPlugin;
import net.abstractiondev.mcbg.data.Arena;
import net.abstractiondev.mcbg.data.BattlegroundsPlayer;
import net.abstractiondev.mcbg.data.MatchType;
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
					case "LEAVE":
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
							for(Player pl : aRef.getPlayers())
							{
								pl.sendMessage(ChatColor.GRAY + p.getName() + " has left the arena (" + ChatColor.RED + "surrender" + ChatColor.GRAY + ").");
							}
							
							p.teleport(p.getWorld().getSpawnLocation());
							aRef.getPlayers().remove(p);
						}
						else p.sendMessage(ChatColor.RED + "You are not in an arena.");
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
										
										boolean in_game = false;
										for(Arena a : plugin.arenas)
										{
											for(Player pl : a.getPlayers())
											{
												if(pl.getName().equalsIgnoreCase(p.getName()))
												{
													in_game = true;
												}
											}
										}
										
										if(!in_game)
										{
											p.sendMessage(ChatColor.GRAY + "Joining a single match...");
											Arena match = null;
											
											// Gets the first arena 
											for(Arena a : plugin.arenas)
											{
												if(!a.isActive() && a.getPlayers().size() < BattlegroundsPlugin.config.matchPlayers)
												{
													match = a;
													break;
												}
											}
											
											if(match != null)
											{
												com.sk89q.worldedit.Vector v = null;
												try {
													v = match.getRegion().getRegionSelector().getRegion().getCenter();
												} catch (IncompleteRegionException e1) {
													// TODO Auto-generated catch block
													e1.printStackTrace();
												}
												
												if(v != null)
												{
													try {
														p.teleport(new Location(Bukkit.getWorld(match.getRegion().getRegionSelector().getRegion().getWorld().getName()),v.getX(),v.getY(),v.getZ()));
													} catch (IncompleteRegionException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
													
													
													match.getPlayers().add(p);
													
													for(Player pl : match.getPlayers())
													{
														pl.sendMessage(ChatColor.GRAY + p.getName() + " has joined the match.");
														pl.sendMessage("Joined match in '" + match.getFriendlyName() + "'. The match will begin when " + (BattlegroundsPlugin.config.matchPlayers - match.getPlayers().size()) + " more players have joined!");
													}
												}
												else p.sendMessage(ChatColor.RED + "An error occurred while joining the match.");
											}
										}
										else p.sendMessage(ChatColor.RED + "You are already in a match.");
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
					case "STATS":
						DecimalFormat df = new DecimalFormat("#.##");
						
						int kills = 0, deaths = 0;
						long total = 0;
						long elo = 0;
						
						if(args.length < 2)
						{ // Show own stats
							BattlegroundsPlayer player = plugin.playerFiles.get(p.getUniqueId().toString());
							
							kills = player.Kills[MatchType.SINGLE];
							deaths = player.Deaths[MatchType.SINGLE];
							total = player.EloTotal[MatchType.SINGLE];
							
							elo = (total + ((400) * (kills - deaths))) / ((kills+deaths) > 0 ? (kills+deaths) : 1);
							
							p.sendMessage(ChatColor.GRAY + "Single Match Statistics:");
							p.sendMessage(ChatColor.GRAY + "Rating: [" + elo + "]");
							p.sendMessage(ChatColor.GRAY + "Kills: [" + player.Kills[MatchType.SINGLE] + "] Deaths: [" + player.Deaths[MatchType.SINGLE] + "] K/D Ratio: [" + df.format(((double)player.Kills[MatchType.SINGLE])/((double)(player.Deaths[MatchType.SINGLE]>0?player.Deaths[MatchType.SINGLE]:1))) + "]");
							p.sendMessage(ChatColor.GRAY + "Wins: [" + player.Wins[MatchType.SINGLE] + "] Matches: [" + player.Matches[MatchType.SINGLE] + "] W/L Ratio: [" + df.format(((double)player.Wins[MatchType.SINGLE])/((double)((player.Matches[MatchType.SINGLE]-player.Wins[MatchType.SINGLE])>0?(player.Matches[MatchType.SINGLE]-player.Wins[MatchType.SINGLE]):1))) + "]");
						}
						else
						{ // Show other players' stats
							Player pl = Bukkit.getPlayer(args[1]);
							
							if(pl != null && pl.isOnline())
							{
								BattlegroundsPlayer player = plugin.playerFiles.get(pl.getUniqueId().toString());
								
								kills = player.Kills[MatchType.SINGLE];
								deaths = player.Deaths[MatchType.SINGLE];
								total = player.EloTotal[MatchType.SINGLE];
								
								elo = (total + ((400) * (kills - deaths))) / ((kills+deaths) > 0 ? (kills+deaths) : 1);
								
								p.sendMessage(ChatColor.GRAY + "Single Match Statistics for " + pl.getName() + ":");
								p.sendMessage(ChatColor.GRAY + "Rating: [" + elo + "]");
								p.sendMessage(ChatColor.GRAY + "Kills: [" + player.Kills[MatchType.SINGLE] + "] Deaths: [" + player.Deaths[MatchType.SINGLE] + "] K/D Ratio: [" + df.format(((double)player.Kills[MatchType.SINGLE])/((double)(player.Deaths[MatchType.SINGLE]>0?player.Deaths[MatchType.SINGLE]:1))) + "]");
								p.sendMessage(ChatColor.GRAY + "Wins: [" + player.Wins[MatchType.SINGLE] + "] Matches: [" + player.Matches[MatchType.SINGLE] + "] W/L Ratio: [" + df.format(((double)player.Wins[MatchType.SINGLE])/((double)((player.Matches[MatchType.SINGLE]-player.Wins[MatchType.SINGLE])>0?(player.Matches[MatchType.SINGLE]-player.Wins[MatchType.SINGLE]):1))) + "]");
							}
							else p.sendMessage(ChatColor.RED + "Player not found.");
						}
						break;
					case "LEADERBOARD":
						if(sender.isOp() || BattlegroundsPlugin.permission.has(sender, "bg.leaderboard") || BattlegroundsPlugin.permission.has(sender, "bg.*"))
						{
							
						}
						break;
					case "CONFIG":
						if(sender.isOp() || BattlegroundsPlugin.permission.has(sender, "bg.arena.config") || BattlegroundsPlugin.permission.has(sender, "bg.arena.*")|| BattlegroundsPlugin.permission.has(sender, "bg.*"))
						{
							if(args.length >= 3)
							{
								switch(args[1].toUpperCase())
								{
								case "SHOWDAMAGELOG":
									if(args[2].equalsIgnoreCase("YES"))
									{
										BattlegroundsPlugin.config.showDamageLog = true;
										p.sendMessage(ChatColor.GRAY + "Battlegrounds configuration updated. [showdamagelog -> yes]");
										plugin.loader.saveConfiguration();
									}
									else if(args[2].equalsIgnoreCase("NO"))
									{
										BattlegroundsPlugin.config.showDamageLog = false;
										p.sendMessage(ChatColor.GRAY + "Battlegrounds configuration updated. [showdamagelog -> no]");
										plugin.loader.saveConfiguration();
									}
									else
										p.sendMessage(ChatColor.RED + "The configuration property 'showdamagelog' only accepts a YES or NO argument.");
									
									break;
								case "MATCHPLAYERS":
									try
									{
										int n = Integer.parseInt(args[2]);
										
										if(n >= 1 && n <= 100)
										{
											BattlegroundsPlugin.config.matchPlayers = n;
											p.sendMessage(ChatColor.GRAY + "Battlegrounds configuration updated. [matchplayers -> " + n + "]");
											plugin.loader.saveConfiguration();
										}
										else
										{
											p.sendMessage(ChatColor.RED + "Value for 'matchplayers' must be between 1 and 100.");
										}
									}
									catch(NumberFormatException e)
									{
										p.sendMessage(ChatColor.RED + "The configuration property 'matchplayers' only accepts a NUMERIC argument.");
									}
									
									break;
								case "MAXROUNDS":
									try
									{
										int n = Integer.parseInt(args[2]);
										
										if(n >= 1 && n <= 100)
										{
											BattlegroundsPlugin.config.maxRounds = n;
											p.sendMessage(ChatColor.GRAY + "Battlegrounds configuration updated. [maxrounds -> " + n + "]");
											plugin.loader.saveConfiguration();
											
											if(n <= 1)
												p.sendMessage(ChatColor.RED + "Warning: This configuration will cause the play area to never shrink.");
										}
										else
										{
											p.sendMessage(ChatColor.RED + "Value for 'maxrounds' must be between 1 and 100.");
										}
									}
									catch(NumberFormatException e)
									{
										p.sendMessage(ChatColor.RED + "The configuration property 'maxrounds' only accepts a NUMERIC argument.");
									}
									
								case "WINTHRESHOLD":
									try
									{
										int n = Integer.parseInt(args[2]);
										
										if(n >= 0 && n <= BattlegroundsPlugin.config.matchPlayers)
										{
											BattlegroundsPlugin.config.winThreshold = n;
											p.sendMessage(ChatColor.GRAY + "Battlegrounds configuration updated. [winthreshold -> " + n + "]");
											plugin.loader.saveConfiguration();
											
											if(n == 0)
												p.sendMessage(ChatColor.RED + "Warning: This configuration will cause the round to continue when there is only 1 player left.");
										}
										else
										{
											p.sendMessage(ChatColor.RED + "Value for 'winthreshold' must be between 1 and " + BattlegroundsPlugin.config.matchPlayers + ".");
										}
									}
									catch(NumberFormatException e)
									{
										p.sendMessage(ChatColor.RED + "The configuration property 'winthreshold' only accepts a NUMERIC argument.");
									}
									
									break;
								default:
									p.sendMessage(ChatColor.RED + "Unknown configuration property.");
									break;
								}
								
							}
							else
							{
								if(args.length >= 2 && args[1].equalsIgnoreCase("HELP"))
								{
									p.sendMessage(ChatColor.GRAY + "Configuration Properties:");
									p.sendMessage(ChatColor.GRAY + "SHOWDAMAGELOG - (Yes/No)");
									p.sendMessage(ChatColor.GRAY + "- Controls whether the damage logs should show up in chat.");
									p.sendMessage(ChatColor.GRAY + "MATCHPLAYERS - (Number)");
									p.sendMessage(ChatColor.GRAY + "- Controls how many players must be in a match to start (also the max).");
									p.sendMessage(ChatColor.GRAY + "MAXROUNDS - (Number)");
									p.sendMessage(ChatColor.GRAY + "- Controls how many rounds before sudden death rounds begin.");
								}
								else
								{
									p.sendMessage(ChatColor.RED + "Syntax: /bg config [property] [value]");
									p.sendMessage(ChatColor.RED + "Use '/bg config help' for a listing of properties.");
								}
							}
						}
						else p.sendMessage(ChatColor.RED + "You do not have permission to configure arenas.");
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
					if(sender.isOp() || BattlegroundsPlugin.permission.has(sender, "bg.arena.config") || BattlegroundsPlugin.permission.has(sender, "bg.arena.*") || BattlegroundsPlugin.permission.has(sender, "bg.*"))
					{
						p.sendMessage(ChatColor.DARK_PURPLE + "/bg " + ChatColor.LIGHT_PURPLE + "config [property] [value]" + ChatColor.DARK_PURPLE + " - " + ChatColor.GRAY + "Configure the Battlegrounds Plugin.");
						++count;
					}
					if(sender.isOp() || BattlegroundsPlugin.permission.has(sender, "bg.arena.join.single") || BattlegroundsPlugin.permission.has(sender, "bg.arena.join.*")  || BattlegroundsPlugin.permission.has(sender, "bg.arena.join.*") || BattlegroundsPlugin.permission.has(sender, "bg.arena.*") || BattlegroundsPlugin.permission.has(sender, "bg.*"))
					{
						p.sendMessage(ChatColor.DARK_PURPLE + "/bg " + ChatColor.LIGHT_PURPLE + "join single" + ChatColor.DARK_PURPLE + " - " + ChatColor.GRAY + "Start matchmaking for a new match.");
						++count;
					}
					if(sender.isOp() || BattlegroundsPlugin.permission.has(sender, "bg.arena.list") || BattlegroundsPlugin.permission.has(sender, "bg.arena.*") || BattlegroundsPlugin.permission.has(sender, "bg.*"))
					{
						p.sendMessage(ChatColor.DARK_PURPLE + "/bg " + ChatColor.LIGHT_PURPLE + "list" + ChatColor.DARK_PURPLE + " - " + ChatColor.GRAY + "List the available arenas.");
						++count;
					}
					
					p.sendMessage(ChatColor.DARK_PURPLE + "/bg " + ChatColor.LIGHT_PURPLE + "stats" + ChatColor.DARK_PURPLE + " - " + ChatColor.GRAY + "Show your own stats for Battlegrounds.");
					++count;
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
