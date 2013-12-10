/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import org.bukkit.entity.Player;

/**
 * @author t7seven7t
 */
public class ModifyWalkSpeed extends Effect {

	private static final String WALK_SPEED_PATH = "walk-speed";
	
	float walkSpeed = 0.4f;
	
	@Override
	public void initialize() {
		
		if (getConfig().isDouble(WALK_SPEED_PATH))
			walkSpeed = (float) getConfig().getDouble(WALK_SPEED_PATH);
		
		if (Math.abs(walkSpeed) > 1)
			walkSpeed = 1 * Math.signum(walkSpeed);
		
	}
	
	@Override
	public void begin(Player player) {
		
		player.setWalkSpeed(walkSpeed);
		
	}
	
	@Override
	public void end(Player player) {
		
		player.setWalkSpeed(0.2f);
		
	}
	
}
