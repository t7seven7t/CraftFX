/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.CraftFX;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author t7seven7t
 */
public class Fly extends Effect {

	private static final String FLY_SPEED_PATH = "fly-speed";
	
	float flySpeed = 0.2f;
	
	@Override
	public void initialize() {
		
		if (getConfig().isDouble(FLY_SPEED_PATH))
			flySpeed = (float) getConfig().getDouble(FLY_SPEED_PATH);
		
		if (Math.abs(flySpeed) > 1)
			flySpeed = 1 * Math.signum(flySpeed);

	}
	
	@Override
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
