/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx;

import org.bukkit.inventory.ItemStack;

/**
 * @author t7seven7t
 */
public class Ingredient {
	
	ItemStack item;
	int amount;
	char key;
	
	public Ingredient(ItemStack item, int amount, char key) {
		this.item = item;
		this.amount = amount;
		this.key = key;
	}
	
}
