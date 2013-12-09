/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effects;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.Trigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author t7seven7t
 */
public class Unbreaking extends Effect {

	public Unbreaking(Trigger trigger, ItemStack item, ConfigurationSection config) {
		super(EffectType.UNBREAKING, trigger, item);
		
		this.affectsDamager = true;
	}
	
	@Override
	public void run(final Player player) {
		
		final Map<Integer, ItemStack> items = getSimilarItems(player.getInventory());
		new BukkitRunnable() {
			
			public void run() {
				
				for (Entry<Integer, ItemStack> entry : items.entrySet()) {

					ItemStack stack = entry.getValue();
					stack.setDurability((short) 0);
					
				}
				
				updateInventory(player);
				
			}
			
		}.runTaskLater(CraftFX.plugin, 1L);
				
	}

}
