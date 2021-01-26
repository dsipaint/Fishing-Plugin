package com.github.dsipaint.fishingplugin.event;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.dsipaint.fishingplugin.event.listeners.FishListener;
import com.github.dsipaint.fishingplugin.event.listeners.PlayerJoinListener;
import com.github.dsipaint.fishingplugin.event.listeners.WeatherListener;
import com.github.dsipaint.fishingplugin.main.Chat;
import com.github.dsipaint.fishingplugin.main.Fish;
import com.github.dsipaint.fishingplugin.main.Main;

public class EventCommence extends BukkitRunnable
{
	//listeners and bossbar are enabled in the EventCommence task, called when an event is scheduled
	
	private Main plugin;
	
	public static FishListener fishlistener;
	public static WeatherListener weatherlistener;
	
	public static PlayerJoinListener playerjoinlistener;
	public static BossBar bossbar;
	
	private int eventduration;
	
	public EventCommence(Main plugin, int eventduration)
	{
		this.eventduration = eventduration;
		this.plugin = plugin;
		fishlistener = new FishListener(plugin);
		weatherlistener = new WeatherListener(plugin);
		playerjoinlistener = new PlayerJoinListener(plugin, bossbar);
		
		//if a title is specified, set a bossbar, otherwise don't
		bossbar = null;
		if(Main.externalconfig.getString("event-title-name") != null)
		{
			String barcolour = Main.externalconfig.getString("event-title-colour");
			if(barcolour == null || BarColor.valueOf(barcolour.toUpperCase()) == null)
				EventCommence.bossbar = Bukkit.createBossBar(Main.externalconfig.getString("event-title-name"), BarColor.PURPLE, BarStyle.SEGMENTED_12);
			else
				EventCommence.bossbar = Bukkit.createBossBar(Main.externalconfig.getString("event-title-name"), BarColor.valueOf(Main.externalconfig.getString("event-title-colour").toUpperCase()), BarStyle.SEGMENTED_12);
		}
	}
	
	public void run()
	{
		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "weather rain"); //set raining
		plugin.getServer().getPluginManager().registerEvents(weatherlistener, plugin); //change weather back if it stops raining
		if(!Fish.fishes.isEmpty())
			plugin.getServer().getPluginManager().registerEvents(fishlistener, plugin); //add custom fishing if there are custom fish
		
		//set bossbar on top
		for(Player p : Bukkit.getOnlinePlayers())
			bossbar.addPlayer(p);
		
		//announce in chat if start message specified
		String startmsg = Main.externalconfig.getString("event-start-message");
		if(startmsg != null)
		{
			for(Player p : Bukkit.getOnlinePlayers())
					p.sendMessage(Chat.format(startmsg));
		}
		plugin.getServer().getPluginManager().registerEvents(playerjoinlistener, plugin); //add listener for players who join during event
		
		//schedule another event, define a "tick" based on event duration, and schedule event every tick, to tick boss bar/end event
		int bossbartickfrequency = eventduration/EventBossBarTick.BOSSBAR_TICKS;
		Main.bossbartickevent = new EventBossBarTick(plugin).runTaskTimer(plugin, 0, bossbartickfrequency); //TODO: tick event starts and ends correctly, but bar ticks down way too fast
	}
}
