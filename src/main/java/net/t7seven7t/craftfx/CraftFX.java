/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.t7seven7t.craftfx.effect.Effect;
import net.t7seven7t.craftfx.effect.EffectType;
import net.t7seven7t.craftfx.item.ItemData;
import net.t7seven7t.craftfx.item.ItemLoader;
import net.t7seven7t.craftfx.listener.CraftFXListener;
import net.t7seven7t.util.LogHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author t7seven7t
 */
public class CraftFX extends JavaPlugin {
	
	public static CraftFX plugin;
	
	private LogHandler logHandler;

	private List<ItemData> itemData;
	private static List<Map<Player, ?>> playerMaps; // Change to a map of players and list values instead - more efficient. Also describe this better, wtf does it do
	
	public void onEnable() {

		long start = System.currentTimeMillis();
		
		CraftFX.plugin = this;
		
		logHandler = new LogHandler(this);
		
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) { }
		
		itemData = Lists.newArrayList();
		playerMaps = Lists.newArrayList();
				
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		
		saveDefaultConfig();
		reloadConfig();
		
		/**
		 * Load custom written effects
		 */
		File effectsDirectory = new File(getDataFolder(), "effects");
		if (!effectsDirectory.exists())
			effectsDirectory.mkdir();
		
		EffectType.loadEffectTypes(effectsDirectory);
		
		/**
		 * ItemLoader loads all custom items and their effects in the config
		 */
		ItemLoader loader = new ItemLoader(this);
		loader.loadItems();
		
		CraftFXListener listener = new CraftFXListener(this);
		registerListener(listener);
		
		logHandler.log("Enabled! [{0}ms]", System.currentTimeMillis() - start);
				
	}

	public void onDisable() {
		
	}

	public static void registerListener(Listener listener) {
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}
	
	public void addItem(ItemData data) {
		
		this.itemData.add(data);
		
	}
	
	public List<ItemData> getItemDataList() {
		
		return Collections.unmodifiableList(itemData);
		
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
	
	public List<Map<Player, ?>> getPlayerMaps() {
		
		return Collections.unmodifiableList(playerMaps);
		
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
	
	/**
	 * Searches for an item registered in CraftFX firstly by unique name (the key it was registered with)
	 * and then by display name. Note that multiple items can share the same display name.
	 * @param name
	 * @return an ItemStack clone of the original
	 */
	public static ItemStack getCustomItem(String name) {
		
	    for (ItemData data : plugin.getItemDataList()) {
	        
	        if (ChatColor.stripColor(data.getName()).equals(ChatColor.stripColor(name)))
	            return data.getItem();
	        
	    }
	    
		for (ItemData data : plugin.getItemDataList()) {
			
			ItemStack item = data.getItem();
			
			if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals(ChatColor.stripColor(name)))
				return item;
			
		}
		
		return null;
		
	}
	
	public static <T> Map<Player, T> newPlayerMap() {
		
		Map<Player, T> map = Maps.newHashMap();
		
		playerMaps.add(map);
		return map;
		
	}

}
