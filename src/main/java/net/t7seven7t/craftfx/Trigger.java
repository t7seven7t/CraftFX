/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx;

import com.google.common.collect.Maps;
import org.bukkit.event.block.Action;

import java.util.Map;

/**
 * @author t7seven7t
 */
public enum Trigger {
	CHAT, //
	DEATH,
	EMPTY_BUCKET,
	EQUIPPED, 
	HIT_ENTITY,
	ITEM_BREAK,
	ITEM_CONSUME,
	ITEM_HELD, 
	LEFT_CLICK_AIR,
	LEFT_CLICK_BLOCK,
	LOSE_HEALTH,
	MOVE, 
	PROJECTILE_HIT,
	PROJECTILE_LAUNCH,
	REGAIN_HEALTH,
	RIGHT_CLICK_AIR,
	RIGHT_CLICK_BLOCK,
	RIGHT_CLICK_ENTITY,
	TELEPORT;
	
	private final static Map<String, Trigger> BY_NAME = Maps.newHashMap();
	
	public static Trigger matches(String actionType) {
		
		return BY_NAME.get(actionType.toUpperCase().replaceAll("\\s+", "_").replaceAll("\\W", ""));
		
	}
	
	public static Trigger getMatchingTrigger(Action action) {
		
		return matches(action.name());
		
	}
	
	static {
		
		for (Trigger type : Trigger.values())
			BY_NAME.put(type.name(), type);
		
	}
	
	public boolean isPotion() {
		
		switch (this) {
		
			case EQUIPPED:
			case HIT_ENTITY:
			case ITEM_CONSUME:
			case ITEM_HELD:
			case LEFT_CLICK_AIR:
			case LEFT_CLICK_BLOCK:
			case LOSE_HEALTH:
			case PROJECTILE_HIT:
			case PROJECTILE_LAUNCH:
			case REGAIN_HEALTH:
			case RIGHT_CLICK_AIR:
			case RIGHT_CLICK_BLOCK:
			case RIGHT_CLICK_ENTITY:
			case TELEPORT:
				return true;
				
			default:
				return false;
				
		}
		
	}
	
}
