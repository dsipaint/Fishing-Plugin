package com.github.dsipaint.fishingplugin.main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Fish
{
	public static ArrayList<Fish> fishes;
	
	private int minlength, maxlength;
	private String name;
	private double rarity;
	private ItemStack item;
	
	private static Random rfish;
	
	//TODO: add random-ish names
	//TODO: add loot/junk
	//TODO: add optional descriptions
	//TODO: round fish lengths nicer
	public Fish(String name, int minlength, int maxlength, double rarity, Material material)
	{
		rfish = new Random();
		
		this.minlength = minlength;
		this.maxlength = maxlength;
		this.name = name;
		this.rarity = rarity;
		
		item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Chat.format("&r" + name)); //make names not italic
		meta.setLore(Arrays.asList(
				Chat.format("&r" + getRandomLength() + " inches") //make lore normal
				));
		item.setItemMeta(meta);
	}
	
	public ItemStack getItem()
	{
		return this.item;
	}
	
	public int getMinlength()
	{
		return minlength;
	}

	public int getMaxlength()
	{
		return maxlength;
	}

	public String getName()
	{
		return name;
	}
	
	public double getRarity()
	{
		return this.rarity;
	}
	
	public double getProbability()
	{
		double totalrarity = 0;
		for(Fish f : fishes)
			totalrarity += f.getRarity();
		
		return rarity/totalrarity;
	}
	
	public double getRandomLength()
	{
		return minlength + (rfish.nextDouble()*(maxlength-minlength));
	}
	
	//returns null if there are no fish
	public static Fish getRandomFish()
	{	
		//keep looping through all fish until one is randomly chosen
		while(true)
		{
			for(Fish f : fishes)
			{
				if(rfish.nextDouble() <= f.getProbability())
					return f;
			}
		}
	}
}
