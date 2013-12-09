/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effects;

import net.t7seven7t.craftfx.ItemLoader;
import net.t7seven7t.craftfx.Trigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

/**
 * @author t7seven7t
 */
public class Potion extends Effect {

	List<PotionEffect> potionEffects;
	
	public Potion(Trigger trigger, ItemStack item, ConfigurationSection config) throws Exception {
		super(EffectType.POTION, trigger, item);
		
		this.potionEffects = ItemLoader.getPotionEffects(config.getStringList("potion-effects"));
	}
	
	public Potion(Trigger trigger, ItemStack item, List<PotionEffect> potionEffects) {
		super(EffectType.POTION, trigger, item);
		
		this.cancelsAction = true;
		this.potionEffects = potionEffects;
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
