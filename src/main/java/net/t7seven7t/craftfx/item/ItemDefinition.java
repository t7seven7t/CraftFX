package net.t7seven7t.craftfx.item;

import net.md_5.bungee.api.ChatColor;
import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.util.MessageUtil;

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
     * The unique name of this item definition
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
     * The display name of this item
     */
    final String displayName;

    /**
     * Create a new ItemDefinition
     *
     * @throws Exception if an error occurred while interpreting the config
     */
    public ItemDefinition(final ItemStack item,
                          final ConfigurationSection config) throws Exception {
        // weird yaml error: Message#format will remove the double single quotes
        this.name = ChatColor.stripColor(MessageUtil.format(config.getName())).toLowerCase();
        this.config = config;
        this.item = CraftFX.instance().getNmsInterface()
                .applyNBT(item, "{craftfx: \"" + name + "\"}");
        this.recipeList = new ArrayList<>();
        this.displayName = item.getItemMeta().getDisplayName();
    }

    /**
     * The display name of the Item defined by this ItemDefinition
     *
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
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
        return CraftFX.instance().getItemRegistry().matchesDefinition(this, item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDefinition that = (ItemDefinition) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
