package com.github.dsipaint.fishingplugin.main;
import org.bukkit.ChatColor;

public class Chat
{
	public static String format(String text)
	{
		return ChatColor.translateAlternateColorCodes('&', text);
	}
}
