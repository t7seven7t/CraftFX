/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import net.t7seven7t.util.FormatUtil;

import org.bukkit.entity.Player;

/**
 * @author t7seven7t
 */
public class Sound extends Effect {

	private static final String SOUND_PATH = "sound";
	private static final String VOLUME_PATH = "volume";
	private static final String PITCH_PATH = "pitch";
	
	org.bukkit.Sound sound;
	float volume = 1.0f;
	float pitch = 1.0f;
	
	@Override
	public void initialize() throws Exception {
		
		if (getConfig().isString(SOUND_PATH))
			sound = org.bukkit.Sound.valueOf(getConfig().getString(SOUND_PATH).replaceAll("\\s+", "_").replaceAll("\\W", ""));
		else
			throw new Exception("No sound type specified.");
		
		if (sound == null)
			throw new Exception(FormatUtil.format("{0} is an invalid sound type", getConfig().getString(SOUND_PATH)));
		
		if (getConfig().isDouble(VOLUME_PATH))
			volume = (float) getConfig().getDouble(VOLUME_PATH);
		
		if (getConfig().isDouble(PITCH_PATH))
			pitch = (float) getConfig().getDouble(PITCH_PATH);
		
	}
	
	@Override
	public void run(Player player) {
		
		player.playSound(player.getLocation(), sound, volume, pitch);
		
	}
	
}
