/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.item;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.t7seven7t.craftfx.Trigger;
import net.t7seven7t.craftfx.effect.Effect;

/**
 * @author t7seven7t
 */
public class ItemData {

	final String name;
	final ItemStack item;
	final ConfigurationSection config;	
	
	Map<Trigger, List<Effect>> effectMap;
	long cooldownLower;
	long cooldownUpper;
	boolean cooldownMessage;
	List<Recipe> recipes;
	
	public ItemData(final String name, final ItemStack item, final ConfigurationSection config) {
		this.name = name;
		this.item = item;
		this.config = config;
		
		this.effectMap = Maps.newHashMap();
		this.recipes = Lists.newArrayList();
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack getItem() {
		return this.item.clone();
	}
	
	public ConfigurationSection getConfig() {
		return config;
	}
	
	public void addRecipe(Recipe recipe) {
		this.recipes.add(recipe);
	}
	
	public List<Recipe> getRecipes() {
		return Collections.unmodifiableList(this.recipes);
	}
	
	public int getTriggerCount() {
		return effectMap.size();
	}
	
	public int getEffectCount() {
		int effectCount = 0;
		for (List<Effect> l : effectMap.values())
			effectCount += l.size();
		return effectCount;
	}
	
	public void setCooldown(long cooldownLower, long cooldownUpper) {
		this.cooldownLower = cooldownLower;
		this.cooldownUpper = cooldownUpper;
	}
	
	public void setCooldown(long cooldown) {
		setCooldown(cooldown, cooldown);
	}
	
	public long getCooldown() {

		return (long) (Math.random() * (cooldownUpper - cooldownLower) + cooldownLower);
		
	}
	
	public void displayCooldownMessage() {
		this.cooldownMessage = true;
	}
	
	public boolean isCooldownMessage() {
		return this.cooldownMessage;
	}
	
	public List<Effect> getEffects(Trigger trigger) {
		return effectMap.get(trigger);
	}
	
	public void addTriggerEffects(Trigger trigger, List<Effect> effects) {
		
		List<Effect> effectsList;
		
		if ((effectsList = effectMap.get(trigger)) == null)
			effectsList = Lists.newArrayList();
					
		effectsList.addAll(effects);
				
		effectMap.put(trigger, effectsList);			
		
	}
	
	public void addTriggerEffect(Trigger trigger, Effect effect) {
		addTriggerEffects(trigger, Lists.newArrayList(effect));
	}
	
}
