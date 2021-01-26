package com.github.dsipaint.fishingplugin.event.listeners;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

import com.github.dsipaint.fishingplugin.main.Fish;
import com.github.dsipaint.fishingplugin.main.Main;

public class FishListener implements Listener
{
	//listener for event which spawns custom fish when fishing
	
	private Main plugin;
	
	public FishListener(Main plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onFishCaught(PlayerFishEvent e)
	{
		//if no fish specified, can't use custom fish
		if(Fish.fishes.isEmpty())
			return;
		
		if(e.getState().equals(State.CAUGHT_FISH))
		{
			e.getCaught().remove(); //remove old fish
			e.getPlayer().getInventory().addItem(Fish.getRandomFish().getItem()); //give a random custom fish
			//TODO: make the fish spawn in the water and have the velocity of the old fish, to fly at the player like it's really been fished
		}
	}
}
