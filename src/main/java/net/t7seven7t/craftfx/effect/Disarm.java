/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author t7seven7t
 */
public class Disarm extends Effect {

	@Override
	public void run(Player player) {
		
		if (player.getItemInHand() != null) {
			
			ItemStack item = player.getItemInHand().clone();
			player.setItemInHand(null);
			
			player.getWorld().dropItemNaturally(player.getLocation(), item);
			
		}
		
	}
	
}
