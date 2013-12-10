/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

/**
 * @author t7seven7t
 */
public class EffectClassLoader extends URLClassLoader {
	private final EffectLoader loader;
	private final Map<String, Class<?>> classes = Maps.newHashMap();
	
	EffectClassLoader(final EffectLoader loader, final URL[] urls, final ClassLoader parent) {
		super(urls, parent);
		this.loader = loader;		
	}
	
	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return findClass(name, true);
	}
	
	Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
		Class<?> result = classes.get(name);
		
		if (result == null) {
			if (checkGlobal) {
				result = loader.getClassByName(name);
			}
			
			if (result == null) {
				result = super.findClass(name);
			}
			
			classes.put(name, result);
		}
		
		return result;
	}
	
	Set<String> getClasses() {
		return classes.keySet();
	}
	
}
