/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import com.google.common.collect.Maps;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.effect.loader.EffectDescriptionFile;
import net.t7seven7t.craftfx.effect.loader.EffectLoader;

import org.apache.commons.lang.Validate;

/**
 * @author t7seven7t
 */
public class EffectType {
	
	public static void loadEffectTypes(File directory) {
		newEffectType(Explosion.class);
		newEffectType(Fly.class);
		newEffectType(Potion.class);
		newEffectType(Unbreaking.class);

		Validate.notNull(directory, "Directory cannot be null");
		Validate.isTrue(directory.isDirectory(), "Directory must be a directory");
		
		Pattern filter = Pattern.compile("\\.jar$");
		EffectLoader loader = new EffectLoader();
		
		int customEffectCount = 0;
		
		for (File file : directory.listFiles()) {
			Matcher match = filter.matcher(file.getName());
			if (!match.find())
				continue;
			
			try {
				newEffectType(loader.loadEffectClass(file));
				customEffectCount++;
			} catch (Exception e) {
				CraftFX.plugin.getLogHandler().log(Level.SEVERE, "Effect {0} failed to load: {1}", file.getName(), e.getMessage());
				
				if (CraftFX.plugin.getConfig().getBoolean("debug"))
					e.printStackTrace();
			}
			
		}
		
		CraftFX.plugin.getLogHandler().log("{0} custom effects loaded.", customEffectCount);
		
	}
	
	private final static Map<String, EffectType> effectTypes = Maps.newHashMap();
	
	public static void newEffectType(Class<? extends Effect> clazz) {
		
		newEffectType(clazz.getSimpleName(), clazz);
		
	}
	
	public static void newEffectType(Entry<EffectDescriptionFile, Class<? extends Effect>> entry) {
		
		newEffectType(entry.getKey().getName(), entry.getValue());
		
	}
	
	public static void newEffectType(String name, Class<? extends Effect> clazz) {
				
		effectTypes.put(name.toUpperCase().replaceAll("\\s+", "_").replaceAll("\\W", ""), new EffectType(clazz));
		
	}
	
	private final Class<? extends Effect> clazz;
	private EffectType(final Class<? extends Effect> clazz) {
		
		this.clazz = clazz;
		
	}
	
	public Class<? extends Effect> getEffectClass() {
		
		return clazz;
		
	}
	
	public static EffectType get(String effectType) {
		
		return effectTypes.get(effectType.toUpperCase().replaceAll("\\s+", "_").replaceAll("\\W", ""));
		
	}
	
}
