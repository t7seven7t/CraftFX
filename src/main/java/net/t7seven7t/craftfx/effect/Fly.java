/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.Trigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author t7seven7t
 */
public class Fly extends Effect {

	float flySpeed = 1.0f;
	
	public Fly(Trigger trigger, ItemStack item, ConfigurationSection config) {
		super(trigger, item);
		
		if (config.contains("fly-speed"))
			flySpeed = (float) config.getDouble("fly-speed");
		
		if (flySpeed > 1)
			flySpeed = 1;
		
		if (flySpeed < -1)
			flySpeed = -1;
	}
	
	public void begin(final Player player) {
		
		player.setAllowFlight(true);
		player.setVelocity(player.getVelocity().setY(1f));
		player.setFlySpeed(flySpeed);
		
		new BukkitRunnable() {
			
			public void run() {
				player.setFlying(true);
			}
			
		}.runTask(CraftFX.plugin);
		
	}
	
	public void end(Player player) {

		player.setFlying(false);
		player.setAllowFlight(false);
		
	}

}
