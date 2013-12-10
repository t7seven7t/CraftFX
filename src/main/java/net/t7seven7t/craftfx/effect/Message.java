/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import org.bukkit.entity.Player;

/**
 * @author t7seven7t
 */
public class Message extends Effect {

	private static final String MESSAGE_PATH = "message";
	
	String message = "";
	
	@Override
	public void initialize() {
		
		if (getConfig().isString(MESSAGE_PATH))
			message = getConfig().getString(MESSAGE_PATH);
		
	}
	
	@Override
	public void run(Player player) {
		
		player.sendMessage(message);
		
	}
	
}
