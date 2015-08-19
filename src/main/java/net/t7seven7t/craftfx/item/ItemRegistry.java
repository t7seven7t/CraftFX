package net.t7seven7t.craftfx.item;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

import net.md_5.bungee.api.ChatColor;
import net.t7seven7t.craftfx.trigger.TriggerType;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ItemRegistry {

    /**
     * List of all item definitions
     */
    List<ItemDefinition> itemDefinitionList = Lists.newArrayList();

    /**
     * Map of trigger types to the items that own them
     */
    Map<TriggerType, List<ItemDefinition>> triggerTypeItemMap = new MapMaker().makeMap();

    /**
     * Registers an item definition with CraftFX. During this process recipes will be registered
     * with the server
     *
     * @param item Item definition to register
     */
    public void register(ItemDefinition item) {
        itemDefinitionList.add(item);
        // Register recipes
        item.getRecipes().forEach(Bukkit::addRecipe);

        // Add item definition to trigger type mapping
        item.getTriggers().forEach(t -> {
            List<ItemDefinition> itemDefinitionList = triggerTypeItemMap.get(t.getType());
            if (itemDefinitionList == null) {
                itemDefinitionList = Lists.newArrayList();
                triggerTypeItemMap.put(t.getType(), itemDefinitionList);
            }
            itemDefinitionList.add(item);
        });
    }

    /**
     * Gets a list of ItemDefinitions that have a trigger of the specified type.
     */
    public List<ItemDefinition> getTriggeredDefinitions(TriggerType type) {
        return triggerTypeItemMap.get(type);
    }

    /**
     * Gets a list of recipes registered for a specific ItemStack
     *
     * @return List of recipes or otherwise an empty list
     */
    public List<Recipe> getRecipes(ItemStack item) {
        ItemDefinition def = getDefinition(item);
        if (def == null) {
            return Collections.EMPTY_LIST;
        }

        return def.getRecipes();
    }

    /**
     * Gets the ItemStack from the collection given which matches an ItemStack by {@link
     * ItemStack#isSimilar(ItemStack)} or null
     */
    public ItemStack getMatching(ItemStack item, Collection<ItemStack> collection) {
        for (ItemStack ingredient : collection) {
            if (isSimilar(ingredient, item)) {
                return ingredient;
            }
        }

        return null;
    }

    /**
     * Gets an ItemDefinition for a specific ItemStack
     */
    public ItemDefinition getDefinition(ItemStack item) {
        for (ItemDefinition def : itemDefinitionList) {
            if (def.isSimilar(item)) {
                return def;
            }
        }

        return null;
    }

    /**
     * Searches for a registered item firstly by the unique key it was registered with and then by
     * its display name. Searches are case sensitive
     */
    public ItemDefinition getDefinition(String name) {
        name = ChatColor.stripColor(name);
        for (ItemDefinition item : itemDefinitionList) {
            if (name.equals(ChatColor.stripColor(item.getName()))) {
                return item;
            }
        }

        for (ItemDefinition item : itemDefinitionList) {
            if (name.equals(ChatColor.stripColor(item.getItem().getItemMeta().getDisplayName()))) {
                return item;
            }
        }

        return null;
    }

    /**
     * Compares two ItemStacks by their type and item meta ignoring amounts and also damage values
     * for tools
     */
    public boolean isSimilar(ItemStack item1, ItemStack item2) {
        if ((item1 == null || item1.getType().equals(Material.AIR))
                && (item2 == null || item2.getType().equals(Material.AIR))) {
            return true;
        }

        if (item1 == null || item2 == null) {
            return false;
        }

        if (item1 == item2) {
            return true;
        }

        return item1.getType().equals(item2.getType())
                && item1.hasItemMeta() == item2.hasItemMeta()
                // Check durability for tools:
                && (item1.getType().getMaxDurability() > 0 ? item1.getDurability() == item2
                .getDurability() : true)
                // Check item meta:
                && (item1.hasItemMeta() ? Bukkit.getItemFactory().equals(item1.getItemMeta(),
                item2.getItemMeta()) : true);
    }

}
