package com.github.dsipaint.fishingplugin.event.listeners;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

import com.github.dsipaint.fishingplugin.main.Main;
import com.github.dsipaint.fishingplugin.main.fishproducts.Fish;
import com.github.dsipaint.fishingplugin.main.fishproducts.FishProduct;

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
		if(Fish.fishandloot.isEmpty())
			return;
		
		//if fish is caught, replace the item
		if(e.getState().equals(State.CAUGHT_FISH))
			((Item) e.getCaught()).setItemStack(FishProduct.getRandomFishProduct().getItem());
	}
}
