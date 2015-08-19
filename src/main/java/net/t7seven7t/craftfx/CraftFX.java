package net.t7seven7t.craftfx;

import net.t7seven7t.craftfx.effect.EffectType;
import net.t7seven7t.craftfx.item.ItemLoader;
import net.t7seven7t.craftfx.item.ItemRegistry;
import net.t7seven7t.craftfx.listener.RecipeListener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

/**
 * Main access point for CraftFX
 */
public class CraftFX extends JavaPlugin {

    private ItemRegistry itemRegistry;
    private static CraftFX instance;

    /**
     * Return the currently loaded instance of CraftFX
     */
    public static CraftFX getInstance() {
        return instance;
    }

    /**
     * Gets the item registry
     */
    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEnable() {
        instance = this;
        startMetrics();
        updateConfig();

        EffectType.initialize();

        itemRegistry = new ItemRegistry();
        ItemLoader itemLoader = new ItemLoader(this);
        itemLoader.loadItems();

        registerListener(new RecipeListener(itemRegistry));
    }

    private void registerListener(Listener l) {
        Bukkit.getPluginManager().registerEvents(l, this);
    }

    /**
     * Save and reload config
     */
    private void updateConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        saveResource("config.yml", false);
        reloadConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisable() {
        instance = null;
        itemRegistry = null;
    }

    /**
     * Starts the metrics service
     */
    private void startMetrics() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            // ignore
        }
    }

}
