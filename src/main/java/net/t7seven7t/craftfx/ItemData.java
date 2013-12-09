/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.t7seven7t.craftfx.effects.Effect;

/**
 * @author t7seven7t
 */
public class ItemData {

	final ItemStack item;
	
	Map<Trigger, List<Effect>> effectMap;
	long cooldown;
	boolean cooldownMessage;
	List<Recipe> recipes;
	
	ItemData(final ItemStack item) {
		this.item = item;
		
		this.effectMap = Maps.newHashMap();
		this.recipes = Lists.newArrayList();
	}
	
	public ItemStack getItem() {
		return this.item.clone();
	}
	
	public void addRecipe(Recipe recipe) {
		this.recipes.add(recipe);
	}
	
	public List<Recipe> getRecipes() {
		return Collections.unmodifiableList(this.recipes);
	}
	
	public void setCooldown(long cooldown) {
		this.cooldown = cooldown;
	}
	
	public long getCooldown() {
		return this.cooldown;
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
