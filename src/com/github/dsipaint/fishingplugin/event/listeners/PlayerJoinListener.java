package com.github.dsipaint.fishingplugin.event.listeners;

import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.dsipaint.fishingplugin.main.Main;

public class PlayerJoinListener implements Listener
{
	//listener for event which adds bossbar for all players who join during event
	
	private Main plugin;
	private BossBar bossbar;
	
	public PlayerJoinListener(Main plugin, BossBar bossbar)
	{
		this.plugin = plugin;
		this.bossbar = bossbar;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		bossbar.addPlayer(e.getPlayer()); //add bossbar for all new players who join
	}
}
