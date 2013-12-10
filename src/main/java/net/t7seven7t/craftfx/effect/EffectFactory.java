/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.t7seven7t.craftfx.Trigger;

/**
 * @author t7seven7t
 */
public class EffectFactory {

	public static Effect newEffect(EffectType type, Trigger trigger, ItemStack item, ConfigurationSection config) throws Exception {
		
		Effect effect = type.getEffectClass().getConstructor().newInstance();
		effect.initialize(type, trigger, item, config);
		
		return effect;
		
	}
	
}
