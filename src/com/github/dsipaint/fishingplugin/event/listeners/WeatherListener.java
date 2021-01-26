package com.github.dsipaint.fishingplugin.event.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.github.dsipaint.fishingplugin.main.Main;

public class WeatherListener implements Listener
{
	//listener for event which makes it always rain during events
	
	private Main plugin;
	
	public WeatherListener(Main plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e)
	{
		//if weather is not being changed to raining
		if(!e.toWeatherState())
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "weather rain"); //set rain again
	}
}
