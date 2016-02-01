package net.t7seven7t.craftfx.item;

import net.t7seven7t.craftfx.CraftFX;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
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
     * List of recipes that can create this item
     */
    final List<Recipe> recipeList;

    /**
     * Create a new ItemDefinition
     *
     * @throws Exception if an error occurred while interpreting the config
     */
    public ItemDefinition(final ItemStack item,
                          final ConfigurationSection config) throws Exception {
        this.name = config.getName(); // root key as specified in config
        this.config = config;
        this.item = item;
        this.recipeList = new ArrayList<>();
    }

    /**
     * The unique name of this ItemDefinition
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * The Bukkit ItemStack representation
     *
     * @return an ItemStack
     */
    public ItemStack getItem() {
        return item.clone();
    }

    /**
     * Configuration Information
     *
     * @return config
     */
    public ConfigurationSection getConfig() {
        return config;
    }

    public List<Recipe> getRecipes() {
        return recipeList;
    }

    public boolean isSimilar(ItemStack item) {
        return CraftFX.instance().getItemRegistry().isSimilar(this.item, item);
    }
}
