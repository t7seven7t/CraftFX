/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import org.bukkit.entity.LivingEntity;

/**
 * @author t7seven7t
 */
public class ModifyHealth extends Effect {

	private static final String AMOUNT_PATH = "amount";
	
	double amount = -1.0f;
	
	@Override
	public void initialize() {
		
		if (getConfig().isDouble(AMOUNT_PATH))
			amount = getConfig().getDouble(AMOUNT_PATH);
		
	}
	
	@Override
	public void run(LivingEntity entity) {
		
		entity.damage(amount);
		
	}
	
}
