/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import org.bukkit.entity.LivingEntity;

/**
 * @author t7seven7t
 */
public class Burn extends Effect {

	private static final String BURN_TIME_PATH = "burn-time";
	
	int fireTicks = 60;
	
	@Override
	public void initialize() {
		
		if (getConfig().isDouble(BURN_TIME_PATH))
			fireTicks = (int) (getConfig().getDouble(BURN_TIME_PATH) * 20.0);
		
	}
	
	@Override
	public void run(LivingEntity entity) {
		
		entity.setFireTicks(fireTicks);
		
	}
	
}
