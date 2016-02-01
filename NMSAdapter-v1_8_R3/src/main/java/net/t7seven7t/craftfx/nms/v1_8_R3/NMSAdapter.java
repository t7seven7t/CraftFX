package net.t7seven7t.craftfx.nms.v1_8_R3;

import net.minecraft.server.v1_8_R3.Item;
import net.t7seven7t.craftfx.nms.NMSInterface;

import org.bukkit.inventory.ItemStack;

public class NMSAdapter implements NMSInterface {

    @Override
    public boolean isValidItem(ItemStack item) {
        return Item.getById(item.getTypeId()) != null;
    }

}
