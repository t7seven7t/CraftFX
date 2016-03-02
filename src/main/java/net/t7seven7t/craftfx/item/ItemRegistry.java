package net.t7seven7t.craftfx.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;

import net.md_5.bungee.api.ChatColor;
import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.recipe.FXRecipe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 */
public class ItemRegistry {

    /**
     * List of all item definitions
     */
    private final Set<ItemDefinition> itemDefinitionSet = Collections
            .newSetFromMap(new MapMaker().makeMap());
    private final Set<FXRecipe> recipeSet = new HashSet<>();

    /**
     * Registers an item definition into this ItemRegistry. Recipes will be registered with the
     * server
     *
     * @param item ItemDefinition to register
     */
    public void register(ItemDefinition item) {
        getDefinition(item.getName()).ifPresent(i -> {
            CraftFX.log().warning("Item with name '%s' was re-registered.", item.getName());
            itemDefinitionSet.remove(i);
        });
        itemDefinitionSet.add(item);
    }

    public void addRecipes(ItemDefinition item) {
        Iterator<Recipe> it = Bukkit.recipeIterator();
        while (it.hasNext()) {
            final Recipe r = it.next();
            if (item.isSimilar(r.getResult())) {
                it.remove();
                recipeSet.remove(r);
            }
        }
        item.getRecipes().forEach(Bukkit::addRecipe);
        recipeSet.addAll(item.getRecipes());
    }

    /**
     * Checks whether an item definition has been registered.
     *
     * @param item ItemDefinition to check
     * @return true if the definition has already been registered
     */
    public boolean contains(ItemDefinition item) {
        return itemDefinitionSet.contains(item);
    }

    /**
     * Gets a list containing all the ItemDefinitions that are registered
     *
     * @return list of all ItemDefinitions
     */
    public List<ItemDefinition> getItemDefinitions() {
        return ImmutableList.copyOf(itemDefinitionSet);
    }

    public Collection<FXRecipe> getRecipes() {
        return ImmutableSet.copyOf(recipeSet);
    }

    /**
     * Gets a list of recipes used to craft a specific ItemStack
     *
     * @param item the ItemStack
     * @return A list of recipes
     */
    public List<FXRecipe> getRecipes(ItemStack item) {
        final Optional<ItemDefinition> opt = getDefinition(item);
        return opt.map(ItemDefinition::getRecipes).orElse(ImmutableList.of());
    }

    /**
     * Gets the ItemStack from the given collection which matches the specified ItemStack by {@link
     * ItemStack#isSimilar(ItemStack)}
     *
     * @param item       the ItemStack to match
     * @param collection collection to search
     * @return An Optional containing ItemStack if the collection contains a matching ItemStack,
     * otherwise Optional.empty()
     */
    public Optional<ItemStack> getMatching(ItemStack item, Collection<ItemStack> collection) {
        for (ItemStack ingredient : collection) {
            if (isSimilar(ingredient, item)) {
                return Optional.ofNullable(ingredient);
            }
        }

        return Optional.empty();
    }

    /**
     * Searches for a registered item firstly by the unique key that it was registered with, and
     * then by its display name. Searches are not case sensitive and items will match if they start
     * with the specified String.
     *
     * @param name name the item was registered as
     * @return An Optional containing ItemDefinition if it exists, otherwise Optional.empty()
     */
    public Optional<ItemDefinition> matchDefinition(String name) {
        final Optional<ItemDefinition> exact = getDefinition(name);
        if (exact.isPresent()) return exact;
        final String lower = ChatColor.stripColor(name).toLowerCase();
        final String lowerStripped = lower.replaceAll("\\s+", "_");
        for (ItemDefinition def : itemDefinitionSet) {
            if (def.getName().startsWith(lowerStripped) || def.getName().startsWith(lower)) {
                return Optional.of(def);
            }
        }

        for (ItemDefinition def : itemDefinitionSet) {
            if (ChatColor.stripColor(def.getDisplayName()).startsWith(lower)) {
                return Optional.of(def);
            }
        }
        return Optional.empty();
    }

    /**
     * Searches for an ItemDefinition matching the specified ItemStack
     *
     * @param item the ItemStack to match
     * @return An Optional containing ItemDefinition if found, otherwise Optional.empty()
     */
    public Optional<ItemDefinition> getDefinition(ItemStack item) {
        for (ItemDefinition def : itemDefinitionSet) {
            if (def.isSimilar(item)) {
                return Optional.of(def);
            }
        }
        return Optional.empty();
    }

    /**
     * Searches for a registered item firstly by the unique key that it was registered with, and
     * then by its display name. Searches are case sensitive.
     *
     * @param name name the item was registered as
     * @return An Optional containing ItemDefinition if it exists, otherwise Optional.empty()
     */
    public Optional<ItemDefinition> getDefinition(String name) {
        final String lower = ChatColor.stripColor(name).toLowerCase();
        final String lowerStripped = lower.replaceAll("\\s+", "_");
        for (ItemDefinition def : itemDefinitionSet) {
            if (lowerStripped.equals(def.getName()) || lower.equals(def.getName())) {
                return Optional.of(def);
            }
        }

        for (ItemDefinition def : itemDefinitionSet) {
            if (lower.equalsIgnoreCase(ChatColor.stripColor(def.getDisplayName()))) {
                return Optional.of(def);
            }
        }
        return Optional.empty();
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

        try {
            final String id1 = CraftFX.instance().getNmsInterface().getCraftFXId(item1);
            final String id2 = CraftFX.instance().getNmsInterface().getCraftFXId(item2);
            if (id1 != null || id2 != null) {
                return id1 != null && id1.equals(id2);
            }
        } catch (UnsupportedOperationException e) {
            // ignore & use display name check instead
        }

        return item1.getType().equals(item2.getType())
                && item1.hasItemMeta() == item2.hasItemMeta()
                // Check durability for tools:
                && (item1.getType().getMaxDurability() <= 0 || item1.getDurability() == item2
                .getDurability())
                // Check item meta:
                && (!item1.hasItemMeta() || Bukkit.getItemFactory()
                .equals(item1.getItemMeta(), item2.getItemMeta()));
    }

    /**
     * Checks whether an Item has the same unique id as a definition. If NBT ids are unsupported
     * this method defaults to using the comparison described by {@link #isSimilar(ItemStack,
     * ItemStack)}
     *
     * @param def  item definition
     * @param item item
     * @return true if the item's NBT id matches the item definition's name (ie. they match)
     */
    public boolean matchesDefinition(ItemDefinition def, ItemStack item) {
        try {
            if (item == null || item.getTypeId() == 0) return false;
            String id = CraftFX.instance().getNmsInterface().getCraftFXId(item);
            return id != null && id.equals(def.getName());
        } catch (Exception e) {
            return isSimilar(def.getItem(), item);
        }
    }

}
