package net.t7seven7t.craftfx.recipe;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 *
 */
public interface FXRecipe extends Recipe {

    /**
     * Compares an ItemMatrix with the expected input for this recipe
     */
    boolean matches(ItemStack[] matrix);

}
