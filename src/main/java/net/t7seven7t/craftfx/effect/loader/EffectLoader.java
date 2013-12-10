/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.t7seven7t.craftfx.effect.Effect;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.UnknownDependencyException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

/**
 * @author t7seven7t
 */
public class EffectLoader {

	private final Map<String, Class<?>> classes = Maps.newHashMap();
	private final Map<String, EffectClassLoader> loaders = Maps.newHashMap();
	
	public Entry<EffectDescriptionFile, Class<? extends Effect>> loadEffectClass(File file) throws Exception {
		Validate.notNull(file, "File cannot be null");
		
		if (!file.exists()) {
			throw new FileNotFoundException(file.getPath() + " does not exist");
		}
		
		EffectDescriptionFile description;
		try {
			description = getEffectDescription(file);
		} catch (InvalidDescriptionException e) { throw new Exception(e); }
		
		List<String> depend = description.getDepend();
		if (depend == null)
			depend = ImmutableList.<String>of();
		
		for (String effect : depend) {
			if (!loaders.containsKey(effect)) {
				try {
					loadClasses(effect, new File(file.getParent(), effect + ".jar"));
				} catch (Throwable e) { throw new UnknownDependencyException(e); }
			}
		}
		
		EffectClassLoader loader = loadClasses(description.getName(), file);
		return new AbstractMap.SimpleEntry<EffectDescriptionFile, Class<? extends Effect>>
						(description, Class.forName(description.getMain(), true, loader).asSubclass(Effect.class));
		
	}
	
	private EffectClassLoader loadClasses(String key, File file) throws Exception {

		EffectClassLoader loader = null;
		
		URL[] urls = new URL[1];
		urls[0] = file.toURI().toURL();
		
		loader = new EffectClassLoader(this, urls, getClass().getClassLoader());
		
		loaders.put(key, loader);
		
		return loader;

	}
	
	public EffectDescriptionFile getEffectDescription(File file) throws InvalidDescriptionException {
		Validate.notNull(file, "File cannot be null");
		
		JarFile jar = null;
		InputStream stream = null;
		
		try {
			
			jar = new JarFile(file);
			JarEntry entry = jar.getJarEntry("plugin.yml");
			
			if (entry == null)
				throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain plugin.yml"));
			
			stream = jar.getInputStream(entry);
			
			return new EffectDescriptionFile(stream);
			
		} catch (Exception e) {
			
			throw new InvalidDescriptionException(e);
			
		} finally {
			
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) { }
			}
			
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) { }
			}
			
		}
	}
	
	Class<?> getClassByName(final String name) {
		Class<?> cachedClass = classes.get(name);
		
		if (cachedClass != null) {
			return cachedClass;
		} else {
			for (String current : loaders.keySet()) {
				EffectClassLoader loader = loaders.get(current);
				
				try {
					cachedClass = loader.findClass(name, false);
				} catch (ClassNotFoundException e) { }
				
				if (cachedClass != null)
					return cachedClass;
			}
		}
		
		return null;
	}
	
	void setClass(final String name, final Class<?> clazz) {
		if (!classes.containsKey(name)) {
			classes.put(name, clazz);
			
			if (ConfigurationSerializable.class.isAssignableFrom(clazz)) {
				Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
				ConfigurationSerialization.registerClass(serializable);
			}
		}
	}
}
