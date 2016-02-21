package net.t7seven7t.craftfx.recipe;

import com.google.common.base.Joiner;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.item.ItemRegistry;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Wrapper for Bukkit's ShapelessRecipe that allows setting ingredients with ItemStack data
 */
public class FXShapelessRecipe extends ShapelessRecipe implements FXRecipe {

    private final List<ItemStack> ingredients = new ArrayList<>();

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

    @Override
    public String toString() {
        final Map<ItemStack, Integer> amounts = new HashMap<>();
        for (ItemStack is : ingredients) {
            if (!amounts.containsKey(is)) amounts.put(is, 0);
            amounts.put(is, amounts.get(is) + 1);
        }
        final String ingredients = Joiner.on(", ").join(amounts.entrySet().stream().map(e -> {
            if (e.getKey().hasItemMeta() && e.getKey().getItemMeta().hasDisplayName())
                return e.getKey().getItemMeta().getDisplayName() + " x" + e.getValue();
            return e.getKey().getType() + " x" + e.getValue();
        }).collect(Collectors.toList()));
        return "FXShapelessRecipe{" + "result=" + getResult().getItemMeta().getDisplayName() +
                ", ingredients=" + ingredients +
                '}';
    }

    /**
     * Compares an ItemMatrix with the expected input for this recipe
     */
    public boolean matches(ItemStack[] matrix) {
        final ItemRegistry registry = CraftFX.instance().getItemRegistry();
        final List<ItemStack> ingredients = new ArrayList<>(this.ingredients);
        for (ItemStack item : matrix) {
            if (item != null && item.getType() != Material.AIR) {
                final Iterator<ItemStack> it = ingredients.iterator();
                boolean exit = true;
                while (it.hasNext()) {
                    if (registry.isSimilar(it.next(), item)) {
                        it.remove();
                        exit = false;
                        break;
                    }
                }
                if (exit) return false;
            }
        }
        return ingredients.isEmpty();
    }
}
