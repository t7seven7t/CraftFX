/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.item.ItemLoader;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import java.util.List;

/**
 * @author t7seven7t
 */
public class Potion extends Effect {

	public static final String POTION_EFFECTS_PATH = "potion-effects";
	
	List<PotionEffect> potionEffects;
	
	@Override
	public void initialize() throws Exception {
		
		this.potionEffects = ItemLoader.getPotionEffects(getConfig().getStringList(POTION_EFFECTS_PATH));
		
	}
	
	@Override
	public void run(LivingEntity entity) {

		for (PotionEffect potionEffect : potionEffects) {
			
			if (entity.hasPotionEffect(potionEffect.getType()))
				entity.removePotionEffect(potionEffect.getType());
			
			potionEffect.apply(entity);
			
		}
		
	}
		
}
