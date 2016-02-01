package net.t7seven7t.util;

import com.google.common.collect.MapMaker;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class Configs {

    private static Map<ConfigDescription, File> configFiles;
    private static Map<ConfigDescription, FileConfiguration> configs;

    private static String DEFAULT_NAME = "config";

    static {
        MapMaker mm = new MapMaker();
        configFiles = mm.makeMap();
        configs = mm.makeMap();
    }

    public static FileConfiguration loadConfig(ConfigDescription desc) throws IOException {
        if (desc.name.equals(DEFAULT_NAME)) {
            desc.plugin.saveDefaultConfig();
            desc.plugin.reloadConfig();
            desc.plugin.getConfig().options().copyDefaults(true);
            desc.plugin.saveConfig();
            return desc.plugin.getConfig();
        }

        try {
            File f = new File(desc.plugin.getDataFolder(), desc.name + ".yml");
            if (!f.exists()) {
                try {
                    desc.plugin.saveResource(f.getName(), false);
                } catch (IllegalArgumentException e) {
                    f.createNewFile();
                }
            }

            FileConfiguration c = YamlConfiguration.loadConfiguration(f);
            if (desc.plugin.getResource(desc.name + ".yml") != null) {
                FileConfiguration def = YamlConfiguration
                        .loadConfiguration(desc.plugin.getResource(desc.name + ".yml"));
                c.setDefaults(def);
                c.options().copyDefaults(true);
                c.save(f);
            }
            configs.put(desc, c);
            configFiles.put(desc, f);
            return c;
        } catch (IOException e) {
            throw new IOException(String.format(
                    "An IO error occurred while attempting to load a config file. Details are: %s",
                    desc), e);
        }
    }

    public static void saveConfig(ConfigDescription desc) throws IOException {
        if (desc.name.equals(DEFAULT_NAME)) {
            desc.plugin.saveConfig();
        }

        if (configs.get(desc) == null || configFiles.get(desc) == null) {
            return;
        }

        try {
            configs.get(desc).save(configFiles.get(desc));
        } catch (IOException e) {
            throw new IOException(String.format(
                    "An IO error occurred while attempting to save a config file. Details are: %s",
                    desc), e);
        }
    }

    /**
     * Gets the config described by the given ConfigDescription. If the config has not yet been
     * loaded then this method will return null.
     *
     * @param desc Config description
     * @return FileConfiguration or null if not yet loaded
     */
    public static FileConfiguration getConfig(ConfigDescription desc) {
        return desc.name.equals(DEFAULT_NAME) ? desc.plugin.getConfig() : configs.get(desc);
    }

    public static final class ConfigDescription {
        private final Plugin plugin;
        private final String name;

        public ConfigDescription(Plugin plugin, String name) {
            this.plugin = plugin;
            this.name = name.toLowerCase().replaceAll(".yml", "");
        }

        public FileConfiguration get() {
            return Configs.getConfig(this);
        }

        public void load() throws IOException {
            loadConfig(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ConfigDescription that = (ConfigDescription) o;

            return plugin.equals(that.plugin) && name.equals(that.name);
        }

        @Override
        public int hashCode() {
            int result = plugin.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }

        public void save() throws IOException {
            saveConfig(this);
        }

        @Override
        public String toString() {
            return "Plugin: " + plugin + "; Config name: " + name + ".yml";
        }
    }
}
