/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.recipe;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author t7seven7t
 */
public enum RecipeType {
	SHAPED, SHAPELESS, FURNACE;
	
	private final static Map<String, RecipeType> BY_NAME = Maps.newHashMap();
	
	public static RecipeType matches(String recipeType) {			
		return BY_NAME.get(recipeType.toUpperCase());
	}
	
	static {
		for (RecipeType type : RecipeType.values())
			BY_NAME.put(type.name(), type);
	}
}
