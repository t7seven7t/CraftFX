/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

/**
 * @author t7seven7t
 * 
 * Wrapper for bukkit's ShapedRecipe that allows setting ingredients with item stack info
 */
public class FXShapedRecipe extends ShapedRecipe {
	
	private Map<Character, ItemStack> ingredients = new HashMap<Character, ItemStack>();
	private ItemStack[] items;
	private int width, height;
	
	public FXShapedRecipe(ItemStack result) {
		super(result);
	}
	
	public FXShapedRecipe setIngredient(char key, ItemStack item) {
		Validate.isTrue(super.getIngredientMap().containsKey(key), "Symbol does not appear in the shape:", key);
		
		ingredients.put(key, item);		
		return this;
	}
	
	@Override
	public ShapedRecipe shape(final String... shape) {
		
		width = 0;
		height = 0;
		
		for (String row : shape) {
			height++;
			
			int w = row.toCharArray().length;
			
			if (w > width)
				width = w;

		}
		
		return super.shape(shape);
		
	}
	
	@Override
	public Map<Character, ItemStack> getIngredientMap() {
		HashMap<Character, ItemStack> result = new HashMap<Character, ItemStack>();
		for (Map.Entry<Character, ItemStack> ingredient : ingredients.entrySet()) {
			if (ingredient.getValue() == null) {
				result.put(ingredient.getKey(), null);
			} else {
				result.put(ingredient.getKey(), ingredient.getValue().clone());
			}
		}
		return result;
	}
	
	public boolean matches(ItemStack[] matrix) {		
		String shape = "";
		
		if (items == null) {
			items = new ItemStack[width * height];
			
			for (int j = 0; j < height; j++) {
				
				shape = shape + getShape()[j];			
				
			}
			
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					
					items[i+j] = ingredients.get(shape.toCharArray()[i+j]);
					
				}
			}
		}
		
		for (int i = 0; i <= 3 - this.width; i++) {
			for (int j = 0; j <= 3 - this.height; j++) {
				
				if (matches(matrix, i, j, true))
					return true;
				
				if (matches(matrix, i, j, false))
					return true;
				
			}
		}
		
		return false;
		
	}
	
	private boolean matches(ItemStack[] matrix, int iOff, int jOff, boolean reverse) {
		
		for (int i = 0; i < 3; i++) {
			
			for (int j = 0; j < 3; j++) {
				
				int i1 = i - iOff;
				int j1 = j - jOff;
				
				ItemStack item = null;
				
				if (i1 >= 0 && j1 >= 0 && i1 < this.width && j1 < this.height) {
					if (reverse) {
						item = items[width - i1 - 1 + j1 * width];
					} else {
						item = items[i1 + j1 * width];
					}
				}
				
				int rowlen = matrix.length == 5 ? 2 : 3;
				ItemStack item1 = matrix[i + j * rowlen];
												
				if (!CraftFX.isSimilar(item1, item))
					return false;
				
			}
			
		}
		
		return true;
	}
	
}
