package com.github.dsipaint.fishingplugin.main.fishproducts;

import org.bukkit.Material;

import com.github.dsipaint.fishingplugin.main.Chat;

public class Loot extends FishProduct
{
	//TODO: add support for enchantments and other nbt tags?
	public Loot(String name, double rarity, Material material)
	{
		super(name, rarity, material);
		this.getItem().getItemMeta().setDisplayName(Chat.format("&r" + name)); //make names not italic
	}
}
