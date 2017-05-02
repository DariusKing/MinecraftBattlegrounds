package net.abstractiondev.mcbg.data;

import java.io.Serializable;

import org.bukkit.entity.Player;

public class BattlegroundsPlayer implements Serializable
{
	private static final long serialVersionUID = 2120087017031470758L;
	
	public String UUID = "";
	public String Name = "";
	public int[] Kills = {0, 0, 0};
	public int[] Assists = {0, 0, 0};
	public int[] Deaths = {0, 0, 0};
	
	public long[] EloTotal = {0, 0, 0};
	public int[] Wins = {0, 0, 0};
	public int[] Matches = {0, 0, 0};
	
	// TODO: Track favorite weapons used in battle
	
	public BattlegroundsPlayer(Player p)
	{
		UUID = p.getUniqueId().toString();
		Name = p.getName();
		Kills[MatchType.SINGLE] = 0;
		Assists[MatchType.SINGLE] = 0;
		Deaths[MatchType.SINGLE] = 0;
		EloTotal[MatchType.SINGLE] = 0;
		Wins[MatchType.SINGLE] = 0;
		Matches[MatchType.SINGLE] = 0;
		
		Kills[MatchType.DOUBLES] = 0;
		Assists[MatchType.DOUBLES] = 0;
		Deaths[MatchType.DOUBLES] = 0;
		EloTotal[MatchType.DOUBLES] = 0;
		Wins[MatchType.DOUBLES] = 0;
		Matches[MatchType.DOUBLES] = 0;
		
		Kills[MatchType.SQUAD] = 0;
		Assists[MatchType.SQUAD] = 0;
		Deaths[MatchType.SQUAD] = 0;
		EloTotal[MatchType.SQUAD] = 0;
		Wins[MatchType.SQUAD] = 0;
		Matches[MatchType.SQUAD] = 0;
	}
}

