package net.t7seven7t.craftfx.item;

import net.md_5.bungee.api.ChatColor;
import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.recipe.FXRecipe;
import net.t7seven7t.craftfx.trigger.Trigger;
import net.t7seven7t.craftfx.util.MessageUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
    final List<FXRecipe> recipeList;
    /**
     * The display name of this item
     */
    final String displayName;

    /**
     * Create a new ItemDefinition
     *
     * @throws Exception if an error occurred while interpreting the config
     */
    ItemDefinition(final ItemStack item,
                   final ConfigurationSection config) throws Exception {
        // weird yaml error: Message#format will remove the double single quotes
        this.name = ChatColor.stripColor(MessageUtil.format(config.getName())).toLowerCase()
                .replaceAll("\\s+", "_");
        this.config = config;
        this.item = CraftFX.instance().getNmsInterface()
                .applyNBT(item, "{craftfx: \"" + name + "\"}");
        this.recipeList = new ArrayList<>();
        this.displayName = item.getItemMeta().getDisplayName();
    }

    ItemDefinition(final String name, final ItemStack item, final List<FXRecipe> recipes) {
        this.name = ChatColor.stripColor(MessageUtil.format(name)).toLowerCase()
                .replaceAll("\\s+", "_");
        this.config = null;
        ItemStack temp;
        try {
            temp = CraftFX.instance().getNmsInterface().applyNBT(item,
                    "{craftfx: \"" + name + "\"}");
        } catch (Exception e) {
            temp = item.clone();
        }
        this.item = temp;
        this.recipeList = new ArrayList<>(recipes);
        ItemMeta meta;
        if (item.hasItemMeta() && (meta = item.getItemMeta()).hasDisplayName()) {
            this.displayName = meta.getDisplayName();
        } else {
            // todo: what to do on occasion that display name doesn't exist?
            this.displayName = "";
        }
    }

    public static Builder builder() {
        return new Builder();
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

    public List<FXRecipe> getRecipes() {
        return recipeList;
    }

    public boolean isSimilar(ItemStack item) {
        return CraftFX.instance().getItemRegistry().matchesDefinition(this, item);
    }

    @Override
    public String toString() {
        return "ItemDefinition{" +
                "name='" + name + '\'' +
                ", item=" + item +
                '}';
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

    public static class Builder {

        private final List<FXRecipe> recipeList = new ArrayList<>();
        private final List<Trigger.Builder> triggerBuilderList = new ArrayList<>();
        private String name;
        private ItemStack item;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder item(ItemStack item) {
            this.item = item;
            return this;
        }

        public Builder recipe(FXRecipe recipe) {
            this.recipeList.add(recipe);
            return this;
        }

        public Builder trigger(Trigger.Builder trigger) {
            this.triggerBuilderList.add(trigger);
            return this;
        }

        /**
         * Builds this ItemDefinition, and also registers it and any triggers and recipes that it
         * has.
         *
         * @return the resulting ItemDefinition
         */
        public ItemDefinition build() {
            final ItemRegistry registry = CraftFX.instance().getItemRegistry();
            Validate.notNull(name, "Item name cannot be null");
            Validate.notNull(item, "ItemStack cannot be null");
            Validate.isTrue(!registry.getDefinition(name).isPresent(),
                    "Item with the name '" + name + "' already registered.");
            final ItemDefinition itemDefinition = new ItemDefinition(name, item, recipeList);
            registry.register(itemDefinition);
            registry.addRecipes(itemDefinition);
            triggerBuilderList.forEach(b -> b.item(itemDefinition).build());
            return itemDefinition;
        }
    }
}
