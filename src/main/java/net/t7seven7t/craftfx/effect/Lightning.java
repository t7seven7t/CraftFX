/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import org.bukkit.Location;

/**
 * @author t7seven7t
 */
public class Lightning extends Effect {

	private static final String DISABLE_DAMAGE_PATH = "disable-damage";
	
	boolean disableDamage = false;
	
	@Override
	public void initialize() {
		
		if (getConfig().isBoolean(DISABLE_DAMAGE_PATH))
			disableDamage = getConfig().getBoolean(DISABLE_DAMAGE_PATH);
		
	}
	
	@Override
	public void run(Location location) {
		
		if (disableDamage)
			location.getWorld().strikeLightningEffect(location);
		else
			location.getWorld().strikeLightning(location);
		
	}
	
}
