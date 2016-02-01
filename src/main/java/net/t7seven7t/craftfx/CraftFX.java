package net.t7seven7t.craftfx;

import net.t7seven7t.craftfx.item.ItemLoader;
import net.t7seven7t.craftfx.item.ItemRegistry;
import net.t7seven7t.craftfx.listener.RecipeListener;
import net.t7seven7t.craftfx.nms.FallbackNMSAdapter;
import net.t7seven7t.craftfx.nms.NMSInterface;
import net.t7seven7t.craftfx.util.LogHelper;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Locale;

/**
 *
 */
public class CraftFX {

    private static CraftFX instance;
    private final LogHelper logHelper = new LogHelper();
    private final CraftFXPlugin plugin;
    private final ItemRegistry itemRegistry;
    private final NMSInterface nmsInterface;

    CraftFX(CraftFXPlugin plugin) {
        CraftFX.instance = this;
        this.plugin = plugin;
        this.nmsInterface = selectNMSVersion();

        startMetrics();
        updateConfig();

        ConfigType.values().forEach(desc -> {
            try {
                if (!desc.equals(ConfigType.DEFAULT)) desc.load();
            } catch (IOException e) {
                logHelper.severe(e.getMessage(), e);
            }
        });

        itemRegistry = new ItemRegistry();
        ItemLoader itemLoader = new ItemLoader();
        itemLoader.loadItems();

        registerListener(new RecipeListener());
    }

    public static CraftFX instance() {
        return instance;
    }

    public static boolean debug() {
        return instance().getConfig().getBoolean("debug", false);
    }

    public static LogHelper log() {
        return instance().logHelper;
    }

    public static JavaPlugin plugin() {
        return instance().plugin;
    }

    public NMSInterface getNmsInterface() {
        return nmsInterface;
    }

    private NMSInterface selectNMSVersion() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
                    .split(",")[3];
            log().info("Attempting to select NMSAdapter for version %s", version);
            Class<?> clazz = getClass().getClassLoader()
                    .loadClass("net.t7seven7t.craftfx.nms." + version + ".NMSAdapter");
            return (NMSInterface) clazz.getConstructor().newInstance();
        } catch (Exception e) {
            log().warning("NMSAdapter wasn't properly selected. " +
                    "Some things may not work right.", e);
            return new FallbackNMSAdapter();
        }
    }

    public FileConfiguration getMessages(Locale locale) {
        // todo: add locale support?
        return ConfigType.MESSAGES.get();
    }

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    public FileConfiguration getConfig() {
        return ConfigType.DEFAULT.get();
    }

    private void startMetrics() {
        try {
            Metrics metrics = new Metrics(plugin());
            metrics.start();
        } catch (IOException e) {
            // ignore
        }
    }

    private void updateConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        plugin.saveResource("config.yml", false);
        plugin.reloadConfig();
    }

    private void registerListener(Listener l) {
        Bukkit.getPluginManager().registerEvents(l, plugin);
    }

}
