package net.t7seven7t.craftfx.recipe;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.item.ItemRegistry;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Wrapper for Bukkit's ShapelessRecipe that allows setting ingredients with ItemStack data
 */
public class FXShapelessRecipe extends ShapelessRecipe {

    private final List<ItemStack> ingredients = new ArrayList<>();
    private Map<ItemStack, Integer> itemCounts;

    /**
     * {@inheritDoc}
     */
    public FXShapelessRecipe(final ItemStack result) {
        super(result);
    }

    /**
     * Adds a number of ingredients to the recipe
     */
    public FXShapelessRecipe addIngredient(int count, final ItemStack item) {
        Validate.isTrue(ingredients.size() + count <= 9,
                "Shapeless recipes cannot have more than 9 ingredients.");
        while (count-- > 0) {
            ingredients.add(item);
        }
        return this;
    }

    FXShapelessRecipe addIngredient(Ingredient ingredient) {
        return addIngredient(ingredient.amount, ingredient.item);
    }

    /**
     * Removes a number of ingredients from the recipe
     */
    public FXShapelessRecipe removeIngredient(int count, final ItemStack item) {
        final Iterator<ItemStack> it = ingredients.iterator();
        final ItemRegistry registry = CraftFX.instance().getItemRegistry();
        while (count > 0 && it.hasNext()) {
            ItemStack i = it.next();
            if (registry.isSimilar(i, item)) {
                it.remove();
                count--;
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ItemStack> getIngredientList() {
        return ingredients.stream().map(ItemStack::clone).collect(Collectors.toList());
    }

    /**
     * Compares an ItemMatrix with the expected input for this recipe
     */
    public boolean matches(ItemStack[] matrix) {
        ItemRegistry registry = CraftFX.instance().getItemRegistry();
        Map<ItemStack, Integer> matrixCount = getItemCount(matrix, registry);
        if (itemCounts == null) {
            itemCounts = getItemCount(ingredients.toArray(new ItemStack[0]), registry);
        }
        for (Map.Entry<ItemStack, Integer> entry : itemCounts.entrySet()) {
            Optional<ItemStack> opt = registry.getMatching(entry.getKey(), matrixCount.keySet());
            if (opt.isPresent()) {
                if (!entry.getValue().equals(matrixCount.get(opt.get()))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gets a map of item counts
     */
    private Map<ItemStack, Integer> getItemCount(ItemStack[] items, ItemRegistry registry) {
        Map<ItemStack, Integer> result = new HashMap<>();
        for (ItemStack item : items) {
            boolean counted = false;
            for (Map.Entry<ItemStack, Integer> entry : result.entrySet()) {
                if (registry.isSimilar(entry.getKey(), item)) {
                    entry.setValue(entry.getValue() + 1);
                    counted = true;
                    break;
                }
            }
            if (!counted) {
                result.put(item, 1);
            }
        }
        return result;
    }
}
