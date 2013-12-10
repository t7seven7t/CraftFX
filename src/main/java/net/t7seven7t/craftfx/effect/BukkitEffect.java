/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import net.t7seven7t.util.FormatUtil;

import org.bukkit.Location;

/**
 * @author t7seven7t
 */
public class BukkitEffect extends Effect {

	private static final String NAME_PATH = "name";
	
	org.bukkit.Effect effect;
	
	@Override
	public void initialize() throws Exception {
		
		if (getConfig().isString(NAME_PATH))
			effect = org.bukkit.Effect.valueOf(getConfig().getString(NAME_PATH).replaceAll("\\s+", "_").replaceAll("\\W", ""));
		else
			throw new Exception("No bukkit effect type specified.");
		
		if (effect == null)
			throw new Exception(FormatUtil.format("{0} is an invalid effect type.", getConfig().getString(NAME_PATH)));
		
	}
	
	@Override
	public void run(Location location) {
		
		location.getWorld().playEffect(location, effect, effect.getData());
		
	}
	
}
