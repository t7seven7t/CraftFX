package net.t7seven7t.craftfx.recipe;

import org.bukkit.inventory.ItemStack;

/**
 *
 */
class Ingredient {

    final ItemStack item;
    final int amount;
    final char key;

    public Ingredient(ItemStack item, int amount, char key) {
        this.item = item;
        this.amount = amount;
        this.key = key;
    }

}
