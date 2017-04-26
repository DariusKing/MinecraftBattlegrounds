package net.abstractiondev.mcbg.data;

import java.io.Serializable;

import org.bukkit.entity.Player;

public class BattlegroundsPlayer implements Serializable
{
	private static final long serialVersionUID = 2120087017031470758L;
	
	public String UUID = "";
	public int[] Kills = {0, 0, 0};
	public int[] Assists = {0, 0, 0};
	public int[] Deaths = {0, 0, 0};
	
	public long[] EloTotal = {1000, 1000, 1000};
	public int[] Matches = {0, 0, 0};
	
	
	// TODO: Track favorite weapons used in battle
	
	public BattlegroundsPlayer(Player p)
	{
		UUID = p.getUniqueId().toString();
		Kills[MatchType.SINGLE] = 0;
		Assists[MatchType.SINGLE] = 0;
		Deaths[MatchType.SINGLE] = 0;
		EloTotal[MatchType.SINGLE] = 1000;
		
		Kills[MatchType.DOUBLES] = 0;
		Assists[MatchType.DOUBLES] = 0;
		Deaths[MatchType.DOUBLES] = 0;
		EloTotal[MatchType.DOUBLES] = 1000;
		
		Kills[MatchType.SQUAD] = 0;
		Assists[MatchType.SQUAD] = 0;
		Deaths[MatchType.SQUAD] = 0;
		EloTotal[MatchType.SQUAD] = 1000;
	}
}

