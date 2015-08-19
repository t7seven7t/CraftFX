package net.t7seven7t.craftfx.recipe;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

/**
 * Wrapper for Bukkit's FurnaceRecipe that allows setting an ingredient with ItemStack data
 */
public class FXFurnaceRecipe extends FurnaceRecipe {

    private ItemStack ingredient;

    public FXFurnaceRecipe(ItemStack result, ItemStack source) {
        super(result, source.getData());
        this.ingredient = source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ItemStack getInput() {
        return ingredient;
    }

    /**
     * Sets the input of this recipe to the specified ItemStack
     */
    public FXFurnaceRecipe setInput(ItemStack input) {
        this.ingredient = input;
        return this;
    }
}
