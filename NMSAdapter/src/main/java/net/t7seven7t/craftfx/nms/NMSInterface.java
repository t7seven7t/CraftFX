package net.t7seven7t.craftfx.nms;

import org.bukkit.inventory.ItemStack;

/**
 *
 */
public interface NMSInterface {

    /**
     * Checks whether an ItemStack is actually valid for the game. Bukkit ItemStacks can still use
     * Materials that are invalid as MC Items.
     *
     * @param item item to check
     * @return true if valid
     */
    boolean isValidItem(ItemStack item);

}
