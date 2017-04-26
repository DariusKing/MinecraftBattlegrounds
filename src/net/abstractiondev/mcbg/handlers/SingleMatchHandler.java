package net.abstractiondev.mcbg.handlers;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.Region;

import net.abstractiondev.mcbg.BattlegroundsPlugin;
import net.abstractiondev.mcbg.data.Arena;
import net.md_5.bungee.api.ChatColor;

public class SingleMatchHandler implements Runnable
{
	private BattlegroundsPlugin plugin;
	public SingleMatchHandler(BattlegroundsPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		World w;
		
		// World Borders
		Region outer = null;
		//Vector centerPoint = null;
		Vector perfectCenter = null;
		for(Arena a : plugin.arenas)
		{
			// Remove inactive players
			for(Player pl : a.getPlayers())
				if(pl != null && !pl.isOnline())
				{
					a.getPlayers().remove(pl);
					
					for(Player player : a.getPlayers())
					{
						player.sendMessage(ChatColor.DARK_PURPLE + pl.getName() + " has been removed from the arena.");
					}
				}
			
			// Check match condition
			if(a.getPlayers().size() == 0) // TODO: Change to more players
			{
				for(Player pl : a .getPlayers()) Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + pl.getName() + " has won a match in arena '" + a.getFriendlyName() + "'!");
				
				a.setActive(false);
				a.setRound(1);
				a.setRoundTimer(300);
				a.setPlayers(new HashSet<Player>());
			}
			
			if(!a.isActive())
			{ // Check if it is time to start the match
				if(a.getPlayers().size() >= 1)
				{
					Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "A new battlegrounds match is starting in arena '" + a.getFriendlyName() + "'!");
					for(Player pl : a.getPlayers())
					{
						for(int i = 0; i <= 50; i++) pl.sendMessage(" "); // Clear chat
						pl.sendMessage(ChatColor.DARK_PURPLE + "Welcome to Battlegrounds!");
						pl.sendMessage(ChatColor.GRAY + "Your goal is to " + ChatColor.GRAY + "survive by killing other players in the arena" + ChatColor.GRAY + ".");
						pl.sendMessage(ChatColor.GRAY + "Collect " + ChatColor.GRAY + "weapons, armor, and power-ups " + ChatColor.GRAY + "found in crates.");
						pl.sendMessage(ChatColor.GRAY + "The world border shrinks as you play! " + ChatColor.GRAY + "Stay away from the burning ring or you will lose health" + ChatColor.GRAY + ".");
						pl.sendMessage(ChatColor.GRAY + "This game is a sudden death style game! That means " + ChatColor.GRAY + "you only have a single life" + ChatColor.GRAY + ".");
					}
					
					try {
						a.setPlayArea(a.getRegion().getRegionSelector().getRegion());
					} catch (IncompleteRegionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					a.setActive(true);
					a.setRound(1);
					a.setRoundTimer(300);
					
					for(Player p : a.getPlayers())
					{
						p.sendMessage(ChatColor.GRAY + "Round " + a.getRound() + " - " + ChatColor.YELLOW + a.getPlayers().size() + " players remaining.");
					}
				}
				
				continue;
			}
			
			a.setRoundTimer(a.getRoundTimer()-1);
			//Bukkit.broadcastMessage("Handling World: " + a.getFriendlyName() + " (Round " + a.getRound() + ", " + a.getRoundTimer() + " seconds remaining) :: A = " + a.getRegion().getArea());

			w = Bukkit.getWorld(a.getWorld());

			perfectCenter = null;
			try {
				perfectCenter = a.getRegion().getRegionSelector().getRegion().getCenter();
			} catch (IncompleteRegionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
			if(perfectCenter != null)
			{
				if(a.getRoundTimer() <= 0)
				{
					// At the end of round 9, notify players of sudden death
					if(a.getRound() == 9)
					{
						for(Player p : a.getPlayers())
						{
							p.sendMessage(ChatColor.DARK_RED + "SUDDEN DEATH! " + ChatColor.GRAY + "The player with the highest health will win at the end of this round.");
						}
					}
					
					a.setRound(a.getRound()+1);
					switch(a.getRound())
					{
					case 1:
					case 2: // normal circle
						//centerPoint = perfectCenter;
						a.setRoundTimer(180);
						
						break;
					case 3:
					case 4: // 2/3 size circle
						//centerPoint = perfectCenter;
						a.setRoundTimer(120);
						break;
					case 5:
					case 6: // 1/3 size circle
						//centerPoint = perfectCenter;
						a.setRoundTimer(90);
						break;
					default:
						//centerPoint = perfectCenter;
						a.setRoundTimer(60);
						break;
					}
					
					for(Player p : a.getPlayers())
					{
						p.sendMessage(ChatColor.GRAY + "Round " + a.getRound() + " - " + ChatColor.YELLOW + a.getPlayers().size() + " players remaining.");
					}
				}
				
				// Handle victory condition for sudden death
				if(a.getRound() >= 10)
				{ // end of round 10, determine the winner
					double highest = 0.00;
					for(Player p : a.getPlayers())
					{
						if(p.getHealth() < highest)
						{
							p.sendMessage(ChatColor.GRAY + "You have been eliminated in Sudden Death.");
							p.damage(100.0);
						}
						else
							highest = p.getHealth();
					}
				}
				 
				// Warnings
				if(a.getRound() % 2 == 0)
				{
					if(a.getRoundTimer() % 15 == 0)
					{
						for(Player p : a.getPlayers())
						{
							p.sendMessage(ChatColor.GRAY + "The play area is shrinking! Stay inside the circle or you will lose health.");
						}
					}
				}
				else
				{
					if(a.getRoundTimer() % 60 == 0 || (a.getRoundTimer() < 60 && a.getRoundTimer() % 15 == 0))
					{
						for(Player p : a.getPlayers())
						{
							p.sendMessage(ChatColor.GRAY + "The play area will begin shrinking in " + a.getRoundTimer() + " seconds.");
						}
					}
				}
				
				outer = a.getPlayArea();
				
				// Debug Message
				CylinderRegion r = new CylinderRegion(perfectCenter,new Vector2D((outer.getWidth()/2),(outer.getWidth()/2)),outer.getMinimumPoint().getBlockY(),outer.getMaximumPoint().getBlockY());
				if((outer.getWidth()-a.getRound()) >= a.getRegion().getWidth()/20 && (outer.getWidth()-a.getRound()) >= 10)
				{
					r = new CylinderRegion(perfectCenter,new Vector2D((outer.getWidth()/2)-(a.getRound()),(outer.getWidth()/2)-(a.getRound())),outer.getMinimumPoint().getBlockY(),outer.getMaximumPoint().getBlockY());
					
					if(a.getRound() % 2 == 0 && a.getRoundTimer() % 5 == 0)
					{
						r.getRadius().subtract(new Vector2D(1.00,1.00));
						//Bukkit.broadcastMessage("(" + r.getRadius().getX() + "," + r.getRadius().getZ() + ")"); // Debug for radius calculation
						
						a.setPlayArea(r);
						outer = r;
					}
				}
				
				CylinderRegion ri = new CylinderRegion(perfectCenter,new Vector2D((outer.getWidth()/2)-1,(outer.getWidth()/2)-1),outer.getMinimumPoint().getBlockY(),outer.getMaximumPoint().getBlockY());
				CylinderRegion ro = new CylinderRegion(perfectCenter,new Vector2D((outer.getWidth()/2)+1,(outer.getWidth()/2)+1),outer.getMinimumPoint().getBlockY(),outer.getMaximumPoint().getBlockY());
				
				//Block centerBlock = w.getBlockAt(new Location(w,perfectCenter.getBlockX(),perfectCenter.getBlockY(),perfectCenter.getBlockZ()));
				//w.spawnParticle(Particle.DRAGON_BREATH,new Location(w,centerBlock.getX(),centerBlock.getY(),centerBlock.getZ()),3);
				
				// Draw particle circle
				Chunk c;
				int x, y, z;
				Block b;
				Location b_loc;
				
				Particle particle = Particle.FLAME;
				for(Vector2D v : r.getChunks())
				{
					c = w.getChunkAt(v.getBlockX(),v.getBlockZ());
					
					for(x = 0; x < 16; x++)
					{
						for(z = 0; z < 16; z++)
						{
							for(y = 0; y < 255; y++)
							{
								b = c.getBlock(x, y, z);
								b_loc = b.getLocation();
								if(ro.contains(new Vector(b_loc.getX(),b_loc.getY(),b_loc.getZ())) && !ri.contains(new Vector(b_loc.getX(),b_loc.getY(),b_loc.getZ())) && b.getType() == Material.AIR && b.getRelative(BlockFace.DOWN).getType().isSolid())
								{
									// Spawn the wall of flames
									w.spawnParticle(Particle.LAVA, b_loc, 0, 0, 0.00, 0, 0);
									for(int i = 0; i < 4; i++)
										w.spawnParticle(particle, b_loc, 0, 0, 5.00, 0, (4-i));
								}
							}
						}
					}
				}
				
				//Bukkit.broadcastMessage("Detected " + blockCount + " eligible particle blocks.");
				
				// Apply the "poison gas" effect to those who are outside of the outer circle
				for(Player p : a.getPlayers())
				{
					Location l = p.getLocation();
					
					// Test for the arena play area (soft border)
					if(!outer.contains(new Vector(l.getX(),l.getY(),l.getZ())))
					{
						// Notify the player every 5 seconds
						if(a.getRoundTimer() % 5 == 0)
						{
							if(BattlegroundsPlugin.config.showDamageLog) p.sendMessage(ChatColor.RED + "You have been exposed to poison gas! Return to the play area to recover.");
							p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,100,2));
							p.spawnParticle(Particle.CLOUD, p.getLocation(),0,0,5.00,0,0);
						}
						
						// Apply these every second
						p.damage(0.5*a.getRound());
					}
					
					// Test for the arena border (hard border)
					if(!a.getRegion().contains(l))
					{
						p.damage(5.0);
						if(BattlegroundsPlugin.config.showDamageLog) p.sendMessage(ChatColor.RED + "You have taken 5.00 damage for being outside of the arena."); // TODO: Remove this and make it an instant death if they are outside of the main play area.
					}
				}
				
				//if(a.getRoundTimer() % 5 == 0) Bukkit.broadcastMessage("Arena: " + a.getFriendlyName() + ", A = " + outer.getArea() + ", " + a.getPlayers().size() + " alive, Round " + a.getRound() + " (" + a.getRoundTimer() + "), W = " + outer.getWidth() + ", R = [" + r.getRadius().getX() + "," + r.getRadius().getZ() + "]");
			}
		}
	}

}
