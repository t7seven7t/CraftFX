package net.t7seven7t.craftfx.nms;

import com.google.common.collect.ImmutableList;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FallbackNMSAdapter implements NMSInterface {
    @Override
    public boolean isValidItem(ItemStack item) {
        return true;
    }

    @Override
    public ItemStack applyNBT(ItemStack item, String nbtToParse) throws Exception {
        return item;
    }

    @Override
    public List<AttributeModifier> getAttributeModifiers(ItemStack item) {
        return ImmutableList.of();
    }
}
