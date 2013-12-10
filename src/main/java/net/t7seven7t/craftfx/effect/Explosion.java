/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import org.bukkit.Location;

/**
 * @author t7seven7t
 */
public class Explosion extends Effect {

	private static final String POWER_PATH = "power";
	private static final String SET_FIRE_PATH = "set-fire";
	private static final String BREAK_BLOCKS_PATH = "break-blocks";
	
	float power = 1.0f;
	boolean setFire = false;
	boolean breakBlocks = false;
	
	@Override
	public void initialize() {
		
		if (getConfig().isDouble(POWER_PATH))
			power = (float) getConfig().getDouble(POWER_PATH);
		
		if (getConfig().isBoolean(SET_FIRE_PATH))
			setFire = getConfig().getBoolean(SET_FIRE_PATH);
		
		if (getConfig().isBoolean(BREAK_BLOCKS_PATH))
			breakBlocks = getConfig().getBoolean(BREAK_BLOCKS_PATH);

	}

	@Override
	public void run(Location location) {
		
		location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), power, setFire, breakBlocks);
		
	}
	
}
