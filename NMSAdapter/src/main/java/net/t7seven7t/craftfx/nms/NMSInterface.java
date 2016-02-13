package net.t7seven7t.craftfx.nms;

import org.bukkit.inventory.ItemStack;

import java.util.List;

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

    /**
     * Parses a String to NBT and applies it to a ItemStack. Returns a new ItemStack with the
     * modified NBT
     *
     * @param item       item
     * @param nbtToParse String to parse
     * @throws Exception if the NBT doesn't parse properly or another error occurs
     */
    ItemStack applyNBT(ItemStack item, String nbtToParse) throws Exception;

    /**
     * Gets a list of AttributeModifiers that affect an ItemStack
     *
     * @param item item
     * @return list of AttributeModifiers
     */
    List<AttributeModifier> getAttributeModifiers(ItemStack item);

    /**
     * Get a json representation of an ItemStack
     *
     * @param item item
     * @return json string representation
     */
    String itemToJson(ItemStack item);

    /**
     * Gets the value for the CraftFX NBT tag
     *
     * @param item item
     * @return the unique id or null
     * @throws UnsupportedOperationException if this method isn't implemented
     */
    String getCraftFXId(ItemStack item) throws UnsupportedOperationException;

}
