package com.github.dsipaint.fishingplugin.main.fishproducts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.dsipaint.fishingplugin.main.Chat;

public abstract class FishProduct
{
	public static ArrayList<FishProduct> fishandloot;
	
	protected String name;
	protected double rarity;
	protected ItemStack item;
	
	protected static Random rfish = new Random();
	
	public FishProduct(String name, double rarity, Material material)
	{
		this.name = name;
		this.rarity = rarity;
		item = new ItemStack(material);
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Chat.format("&r" + name)); //make names not italic
		item.setItemMeta(meta);
	}
	
	public ItemStack getItem()
	{
		return this.item;
	}

	public String getName()
	{
		return name;
	}
	
	public double getRarity()
	{
		return this.rarity;
	}
	
	public void appendLore(String description)
	{
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		
		if(lore == null)
			lore = Arrays.asList(description);
		else
			lore.add(description);
		
		meta.setLore(lore);
		item.setItemMeta(meta);
	}
	
	public double getProbability()
	{
		double totalrarity = 0;
		for(FishProduct f : fishandloot)
			totalrarity += f.getRarity();
		
		return rarity/totalrarity;
	}
	
	//returns null if there are no fish
	public static FishProduct getRandomFishProduct()
	{
		//keep looping through all fish until one is randomly chosen
		while(true)
		{
			for(FishProduct f : FishProduct.fishandloot)
			{
				if(rfish.nextDouble() <= f.getProbability())
					return f;
			}
		}
	}
}
