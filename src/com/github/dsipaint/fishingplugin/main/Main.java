package com.github.dsipaint.fishingplugin.main;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.github.dsipaint.fishingplugin.event.EventCommence;
import com.github.dsipaint.fishingplugin.main.fishproducts.Fish;
import com.github.dsipaint.fishingplugin.main.fishproducts.FishProduct;
import com.github.dsipaint.fishingplugin.main.fishproducts.Loot;

public class Main extends JavaPlugin
{
	/*
	 * -TODO: change default config.yml values after testing
	 */
	
	BukkitTask commenceevent;
	public static BukkitTask bossbartickevent;
	public static FileConfiguration externalconfig;
	
	static final int DEFAULT_EVENT_FREQUENCY = 20*86400,
			DEFAULT_EVENT_DURATION = 20*3600;
	
	public void onEnable()
	{		
		this.saveDefaultConfig(); //save internal config.yml, externally
		externalconfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
		
		//load in fish if any are specified
		Fish.fishandloot = new ArrayList<FishProduct>();
		if(externalconfig.getList("fish") != null)
		{
			getLogger().info("Parsing fish");
			externalconfig.getList("fish").forEach(fish ->
			{
				Map<String, ?> fishobj = (Map<String, ?>) fish;
				try
				{
					Fish currentfish = new Fish(
							fishobj.get("name").toString(),
							Integer.parseInt(fishobj.get("minlength").toString().replace("-", "")), //no negatives/wrapping
							Integer.parseInt(fishobj.get("maxlength").toString().replace("-", "")), //no negatives/wrapping
							Double.parseDouble(fishobj.get("rarity").toString()) >= 0 && Double.parseDouble(fishobj.get("rarity").toString()) <= 1 ? Double.parseDouble(fishobj.get("rarity").toString()) : 0.5, //rarity between 0 and 1, or else 0.5 used
							Material.getMaterial(fishobj.get("item").toString().toUpperCase(), false) != null ? Material.getMaterial(fishobj.get("item").toString().toUpperCase(), false) : Material.COD); //default to cod if material not found
					
					//if description exists, add description
					if(fishobj.get("description") != null)
						currentfish.appendLore(Chat.format(fishobj.get("description").toString()));
					
					getLogger().info("Parsed fish " + currentfish.getName());
					Fish.fishandloot.add(currentfish);
				}
				catch(Exception e)
				{
					getLogger().warning("Invalid fish- skipping...");
				}
				
			});
		}
		
		if(externalconfig.getList("loot") != null)
		{
			getLogger().info("Parsing loot");
			externalconfig.getList("loot").forEach(loot ->
			{
				Map<String, ?> lootobj = (Map<String, ?>) loot;
				try
				{
					Loot currentloot = new Loot(
							lootobj.get("name").toString(),
							Double.parseDouble(lootobj.get("rarity").toString()) >= 0 && Double.parseDouble(lootobj.get("rarity").toString()) <= 1 ? Double.parseDouble(lootobj.get("rarity").toString()) : 0.5, //rarity between 0 and 1, or else 0.5 used
							Material.getMaterial(lootobj.get("item").toString().toUpperCase(), false) != null ? Material.getMaterial(lootobj.get("item").toString().toUpperCase(), false) : Material.COD); //default to cod if material not found
					
					//if description exists, add description
					if(lootobj.get("description") != null)
						currentloot.appendLore(Chat.format(lootobj.get("description").toString()));
					
					getLogger().info("Parsed loot " + currentloot.getName());
					Fish.fishandloot.add(currentloot);
				}
				catch(Exception e)
				{
					getLogger().warning("Invalid loot- skipping...");
				}
				
			});
		}
		
		//set frequency of events based off config file
		getLogger().info("Setting event-frequency");
		int eventfrequency;
		String eventfrequencystring = externalconfig.getString("event-frequency");
		if(eventfrequencystring == null || !eventfrequencystring.matches("\\d+[smhd]"))
		{
			getLogger().warning("Invalid or missing event-frequency - using default value of 1 day...");
			eventfrequency = DEFAULT_EVENT_FREQUENCY;
		}
		else
			eventfrequency = toTicks(externalconfig.getString("event-frequency"));
		
		//set event duration from config file
		getLogger().info("Setting event-duration");
		int eventduration;
		String eventdurationstring = externalconfig.getString("event-duration");
		if(eventdurationstring == null || !eventdurationstring.matches("\\d+[smhd]"))
		{
			getLogger().warning("Invalid or missing event-duration - using default value of 1 hour");
			eventduration = DEFAULT_EVENT_DURATION;
		}
		else
			eventduration = toTicks(this.getConfig().getString("event-duration"));
		
		//sense-check event timings input
		if(eventduration > eventfrequency)
		{
			getLogger().warning("event-duration is larger than event-frequency - using default values instead...");
			eventfrequency = DEFAULT_EVENT_FREQUENCY;
			eventduration = DEFAULT_EVENT_DURATION;
		}
		
		//find time between now and this converted specified time
		//start time is optional
		if(externalconfig.getString("event-start-time") != null)
		{
			String[] eventtime = externalconfig.getString("event-start-time").split(":");
			int eventhour = 0, eventminute = 0;
			if(eventtime[0].matches("\\d{1,2}"))
			{
				eventhour = Integer.parseInt(eventtime[0]);
				if(eventhour > 23)
				{
					getLogger().warning("event-time hour is not valid, set event-time hour to 0...");
					eventhour = 0;
				}
			}
			else
				getLogger().warning("event-time hour is not valid, set event-time hour to 0...");
			
			if(eventtime[1].matches("\\d{1,2}"))
			{
				eventminute = Integer.parseInt(eventtime[1]);
				if(eventminute > 59)
				{
					getLogger().warning("event-time minute is not valid, set event-time minute to 0...");
					eventminute = 0;
				}
			}
			else
				getLogger().warning("event-time minute is not valid, set event-time minute to 0...");
			
			LocalDateTime utccurrenttime = LocalDateTime.now(ZoneId.of("UTC"));
			LocalDateTime eventparsetime = utccurrenttime.withHour(eventhour).withMinute(eventminute).withSecond(0);
			
			//if the time specified has already passed today, then use the same time tomorrow
			if(eventhour < utccurrenttime.getHour() || (eventhour == utccurrenttime.getHour() && eventminute < utccurrenttime.getMinute()))
				eventparsetime = eventparsetime.plusDays(1);
			
			getLogger().info("Scheduling fishing events to occur for " + eventdurationstring + ", every " + eventfrequencystring + ", with the first fishing event occurring at " 
					+ eventparsetime.getDayOfMonth() + "/"+ (eventparsetime.getMonthValue() < 10 ? "0" + eventparsetime.getMonthValue() : eventparsetime.getMonthValue()) + "/" + eventparsetime.getYear() + ", at " 
					+ (eventparsetime.getHour() < 10 ? "0" + eventparsetime.getHour() : eventparsetime.getHour()) + ":" + eventparsetime.getMinute() + " UTC");
			
			long secondsuntilfirstevent = utccurrenttime.until(eventparsetime, ChronoUnit.SECONDS);
			
			commenceevent = new EventCommence(this, eventduration).runTaskTimer(this, secondsuntilfirstevent*20, eventfrequency);
		}
		else
		{
			getLogger().info("Scheduling fishing events to occur for " + eventdurationstring + ", every " + eventfrequencystring + ", with the first fishing event occurring immediately");
			commenceevent = new EventCommence(this, eventduration).runTaskTimer(this, 0, eventfrequency); //start straight away if no time specified
		}
	}
	
	public void onDisable()
	{
		commenceevent.cancel();
	}
	
	//turns \\d+[smhd] into a number of ticks
	public int toTicks(String timearg)
	{
		int ticks;
		ticks = Integer.parseInt(timearg.split("[smhd]")[0])*20; //lowest time interval is seconds, which has 20 ticks
		switch(timearg.split("\\d+")[0])
		{
			//seconds are already taken care of
			case "s":
				break;
			
			//60 seconds in a minute
			case "m":
				ticks *= 60;
				break;
			
			//3600 seconds in an hour
			case "h":
				ticks *= 3600;
				break;
				
			//86400 seconds in a day
			case "d":
				ticks *= 86400;
				break;
		}
		
		return ticks;
	}
}
