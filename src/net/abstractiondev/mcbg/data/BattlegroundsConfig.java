package net.abstractiondev.mcbg.data;

import java.io.Serializable;

public class BattlegroundsConfig implements Serializable
{
	private static final long serialVersionUID = 5140851676490640667L;

	// Internal configuration variables
	public int winThreshold = 0;
	
	// Normal configuration variables
	public boolean showDamageLog = false;
	public int matchPlayers = 1;
	public int maxRounds = 20;
}
