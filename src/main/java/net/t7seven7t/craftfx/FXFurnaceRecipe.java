/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

/**
 * @author t7seven7t
 */
public class FXFurnaceRecipe extends FurnaceRecipe {

	private ItemStack ingredient;
	
	public FXFurnaceRecipe(ItemStack result, ItemStack source) {
		super(result, source.getType());
		
		this.ingredient = source;
	}
	
	@Override
	public ItemStack getInput() {
		return this.ingredient.clone();
	}
	
	public FXFurnaceRecipe setInput(ItemStack source) {
		this.ingredient = source;
		return this;
	}

}
