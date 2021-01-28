package com.github.dsipaint.fishingplugin.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.dsipaint.fishingplugin.main.Chat;
import com.github.dsipaint.fishingplugin.main.Main;

public class EventBossBarTick extends BukkitRunnable
{
	//called every bossbartick to update the bossbar and end the event when needed
	
	private Main plugin;
	private int progress;
	
	public static final int BOSSBAR_TICKS = 12;
	
	public EventBossBarTick(Main plugin)
	{
		this.plugin = plugin;
		progress = BOSSBAR_TICKS;
	}
	
	public void run()
	{
		EventCommence.bossbar.setProgress(((double) progress/BOSSBAR_TICKS)); //one tick is a fraction of the duration
		
		//if event duration is over, time ran out, reset for next event
		if(progress == 0)
		{
			//disable listeners used for event
			HandlerList.unregisterAll(EventCommence.fishlistener);
			HandlerList.unregisterAll(EventCommence.playerjoinlistener);
			HandlerList.unregisterAll(EventCommence.weatherlistener);
			
			if(EventCommence.bossbar != null)
			{
				EventCommence.bossbar.removeAll(); //remove all players from bossbar
				HandlerList.unregisterAll(EventCommence.playerjoinlistener);
			}
			
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "weather clear"); //change weather back
			
			//announce in chat if end message specified
			String endmsg = Main.externalconfig.getString("event-end-message");
			if(endmsg != null)
			{
				for(Player p : plugin.getServer().getOnlinePlayers())
					p.sendMessage(Chat.format(endmsg));
					
			}
			
			plugin.getLogger().info("Fishing event ended!");
			
			this.cancel(); //cancel ticking event
		}
		
		progress--; //minus the time left for next tick
	}
}
