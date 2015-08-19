package net.t7seven7t.craftfx.item;

import com.google.common.collect.Lists;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.trigger.Trigger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

/**
 *
 */
public class ItemDefinition {

    /**
     * The name of this item definition
     */
    final String name;
    /**
     * Bukkit ItemStack
     */
    final ItemStack item;
    /**
     * Configuration information
     */
    final ConfigurationSection config;
    /**
     * List of triggers that this item watches
     */
    final List<Trigger> triggerList;
    /**
     * List of recipes that can create this item
     */
    final List<Recipe> recipeList;

    /**
     * Create a new ItemDefinition
     *
     * @throws Exception if an error occurred while interpreting the config
     */
    public ItemDefinition(final ItemStack item, final ConfigurationSection config) throws Exception {
        this.name = config.getName(); // root key as specified in config
        this.config = config;
        this.item = item;
        this.triggerList = Lists.newArrayList();
        this.recipeList = Lists.newArrayList();
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public ConfigurationSection getConfig() {
        return config;
    }

    public List<Trigger> getTriggers() {
        return triggerList;
    }

    public List<Recipe> getRecipes() {
        return recipeList;
    }

    public boolean isSimilar(ItemStack item) {
        return CraftFX.getInstance().getItemRegistry().isSimilar(this.item, item);
    }

}
