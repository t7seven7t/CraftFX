/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx;

import com.google.common.collect.Lists;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Iterator;
import java.util.List;

/**
 * @author t7seven7t
 */
public class FXShapelessRecipe extends ShapelessRecipe {

	private List<ItemStack> ingredients = Lists.newArrayList();
	
	public FXShapelessRecipe(ItemStack result) {
		super(result);
	}
	
	public FXShapelessRecipe addIngredient(int count, ItemStack item) {
		Validate.isTrue(ingredients.size() + count <= 9, "Shapeless recipes cannto have more than 9 ingredients");
		
		while (count-- > 0)
			ingredients.add(item);
		
		return this;
	}
	
	public FXShapelessRecipe removeIngredient(int count, ItemStack item) {
		Iterator<ItemStack> iterator = ingredients.iterator();
		while (count > 0 && iterator.hasNext()) {
			ItemStack stack = iterator.next();
			if (CraftFX.isSimilar(stack, item)) {
				iterator.remove();
				count--;
			}
		}
		return this;
	}
	
	@Override
	public List<ItemStack> getIngredientList() {
		List<ItemStack> result = Lists.newArrayList();
		for (ItemStack ingredient : ingredients)
			result.add(ingredient.clone());
		return result;
	}

}
