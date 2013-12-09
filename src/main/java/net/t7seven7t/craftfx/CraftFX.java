/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx;

import com.google.common.collect.Lists;
import net.t7seven7t.craftfx.effects.Effect;
import net.t7seven7t.craftfx.listeners.CraftFXListener;
import net.t7seven7t.util.LogHandler;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * @author t7seven7t
 */
public class CraftFX extends JavaPlugin {
	
	public static CraftFX plugin;
	
	private LogHandler logHandler;

	private List<ItemData> itemData;
	
	public void onEnable() {

		CraftFX.plugin = this;
		
		logHandler = new LogHandler(this);
		
		itemData = Lists.newArrayList();
				
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		
		saveDefaultConfig();
		reloadConfig();
				
		/**
		 * ItemLoader loads all custom items and their effects in the config
		 */
		ItemLoader loader = new ItemLoader(this);
		loader.loadItems();
		
		CraftFXListener listener = new CraftFXListener(this);
		registerListener(listener);
				
	}

	public void onDisable() {
		
	}

	public void registerListener(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}
	
	public ItemData newItem(ItemStack item) {
		
		ItemData data = new ItemData(item);
		this.itemData.add(data);
		
		return data;
		
	}
	
	public List<Effect> getEffects(ItemStack item, Trigger trigger) {
		
		ItemData data = getItemData(item);
		
		if (data == null)
			return null;
		
		return data.getEffects(trigger);
		
	}
	
	public ItemData getItemData(ItemStack item) {
		
		for (ItemData data : itemData) {
			
			if (CraftFX.isSimilar(data.getItem(), item))
				return data;
			
		}
		
		return null;
		
	}
	
	public List<Recipe> getRecipes(ItemStack item) {
		
		ItemData data = getItemData(item);
		
		if (data == null)
			return null;
		
		return data.getRecipes().isEmpty() ? null : data.getRecipes();
		
	}
	
	public LogHandler getLogHandler() {
		
		return logHandler;
		
	}
	
	public static boolean isSimilar(ItemStack item1, ItemStack item2) {
		
		if (item1 != null && item1.getType() == Material.AIR)
			item1 = null;
		
		if (item2 != null && item2.getType() == Material.AIR)
			item2 = null;
		
		if (item1 == null && item2 == null)
			return true;
		
		if (item1 == null || item2 == null)
			return false;
				
		if (item1 == item2)
			return true;
						
		return item1.getType() == item2.getType()
				&& item1.hasItemMeta() == item2.hasItemMeta()
				&& (item1.hasItemMeta() ? Bukkit.getItemFactory().equals(
						item1.getItemMeta(), item2.getItemMeta()) : true);
		
	}

}
