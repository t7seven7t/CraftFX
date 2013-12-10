/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect.loader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.InvalidDescriptionException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import com.google.common.collect.ImmutableList;

/**
 * @author t7seven7t
 */
public class EffectDescriptionFile {

	private static final Yaml yaml = new Yaml(new SafeConstructor());
	
	private String name = null;
	private String main = null;
	private String version = null;
	private List<String> authors = null;
	private List<String> depend = null;
	
	public EffectDescriptionFile(final InputStream stream) throws InvalidDescriptionException {
		loadMap((Map<?, ?>) yaml.load(stream));
	}
	
	public String getName() {
		return name;
	}
	
	String getMain() {
		return main;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getFullName() {
		return name + " v" + version;
	}
	
	public List<String> getAuthors() {
		return authors;
	}
	
	List<String> getDepend() {
		return depend;
	}
	
	private void loadMap(Map<?, ?> map) throws InvalidDescriptionException {
		try {
			name = map.get("name").toString();
			
			if (!name.matches("^[A-Za-z0-9 _.-]+$")) {
				throw new InvalidDescriptionException("name '" + name + "' contains invalid characters.");
			}
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "name is not defined");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "name is of wrong type");
		}
		
		try {
			version = map.get("version").toString();
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "version is not defined");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "version is of wrong type");
		}
		
		try {
			main = map.get("main").toString();
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "main is not defined");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "main is of wrong type");
		}
		
		if (map.get("depend") != null) {
			ImmutableList.Builder<String> dependBuilder = ImmutableList.<String>builder();
			try {
				for (Object dependency : (Iterable<?>) map.get("depend")) {
					dependBuilder.add(dependency.toString());
				}
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "depend is of wrong type");
			} catch (NullPointerException ex) {
				throw new InvalidDescriptionException(ex, "invalid dependency format");
			}
			depend = dependBuilder.build();
		}
		
		if (map.get("authors") != null) {
			ImmutableList.Builder<String> authorsBuilder = ImmutableList.<String>builder();
			if (map.get("author") != null) {
				authorsBuilder.add(map.get("author").toString());
			}
			
			try {
				for (Object o : (Iterable<?>) map.get("authors")) {
					authorsBuilder.add(o.toString());
				}
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "authors are of wrong type");
			} catch (NullPointerException ex) {
				throw new InvalidDescriptionException(ex, "authors are improperly defined");
			}
			authors = authorsBuilder.build();
		} else if (map.get("author") != null) {
			authors = ImmutableList.of(map.get("author").toString());
		} else {
			authors = ImmutableList.<String>of();
		}
	}
	
}
