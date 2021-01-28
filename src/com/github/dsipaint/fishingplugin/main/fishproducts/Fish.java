package com.github.dsipaint.fishingplugin.main.fishproducts;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.dsipaint.fishingplugin.main.Chat;

public class Fish extends FishProduct
{
	
	private int minlength, maxlength;
	
	public Fish(String name, int minlength, int maxlength, double rarity, Material material)
	{
		super(name, rarity, material);
		
		this.minlength = minlength;
		this.maxlength = maxlength;
		
		
		ItemMeta meta = item.getItemMeta();
		
		String randlength = Double.toString(getRandomLength());
		meta.setLore(Arrays.asList(
				Chat.format("&r" + randlength.substring(0, randlength.indexOf('.') + 3) + " inches") //make lore normal
				));
		
		item.setItemMeta(meta);
	}
	
	public int getMinlength()
	{
		return minlength;
	}

	public int getMaxlength()
	{
		return maxlength;
	}
	
	public double getRandomLength()
	{
		return minlength + (FishProduct.rfish.nextDouble()*(maxlength-minlength));
	}
}
