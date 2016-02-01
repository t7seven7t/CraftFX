package net.t7seven7t.craftfx.nms;

import org.bukkit.inventory.ItemStack;

public class FallbackNMSAdapter implements NMSInterface {
    @Override
    public boolean isValidItem(ItemStack item) {
        return true;
    }
}
