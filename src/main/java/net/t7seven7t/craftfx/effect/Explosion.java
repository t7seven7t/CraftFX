/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.Trigger;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 * @author t7seven7t
 */
public class Explosion extends Effect {

	float power = 1.0f;
	boolean setFire = false;
	boolean breakBlocks = false;
	
	public Explosion(Trigger trigger, ItemStack item, ConfigurationSection config) {
		super(trigger, item);
		
		if (config.contains("power"))
			power = (float) config.getDouble("power");
		
		if (config.contains("set-fire"))
			setFire = config.getBoolean("set-fire");
		
		if (config.contains("break-blocks"))
			breakBlocks = config.getBoolean("break-blocks");
		
	}

	@Override
	public void run(Location location) {
		
		location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), power, setFire, breakBlocks);
		
	}
	
}
