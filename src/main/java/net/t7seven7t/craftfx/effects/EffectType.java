/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effects;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author t7seven7t
 */
public enum EffectType {
	EXPLOSION(Explosion.class),
	FLY(Fly.class),
	POTION(Potion.class),
	UNBREAKING(Unbreaking.class);
	
	private final Class<? extends Effect> clazz;
	private EffectType(final Class<? extends Effect> clazz) {
		
		this.clazz = clazz;
		
	}
	
	public Class<? extends Effect> getEffectClass() {
		
		return clazz;
		
	}
	
	private final static Map<String, EffectType> BY_NAME = Maps.newHashMap();
	
	public static EffectType matches(String effectType) {
		
		return BY_NAME.get(effectType.toUpperCase().replaceAll("\\s+", "_").replaceAll("\\W", ""));
		
	}
	
	static {
		
		for (EffectType type : EffectType.values())
			BY_NAME.put(type.name(), type);
		
	}
	
}
