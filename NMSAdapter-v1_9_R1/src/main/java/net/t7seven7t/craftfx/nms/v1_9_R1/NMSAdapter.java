package net.t7seven7t.craftfx.nms.v1_9_R1;

import com.google.common.collect.Multimap;

import net.minecraft.server.v1_9_R1.EnumItemSlot;
import net.minecraft.server.v1_9_R1.Item;
import net.minecraft.server.v1_9_R1.MojangsonParser;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.NBTTagList;
import net.t7seven7t.craftfx.nms.AttributeModifier;
import net.t7seven7t.craftfx.nms.NMSInterface;

import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class NMSAdapter implements NMSInterface {

    private static final String ATTRIBUTE_MODIFIER_TAG = "AttributeModifiers";

    /**
     * Checks whether an ItemStack is actually valid for the game. Bukkit ItemStacks can still use
     * Materials that are invalid as MC Items.
     *
     * @param item item to check
     * @return true if valid
     */
    @Override
    public boolean isValidItem(ItemStack item) {
        return Item.getById(item.getTypeId()) != null;
    }

    /**
     * Parses a String to NBT and applies it to a ItemStack. Returns a new ItemStack with the
     * modified NBT
     *
     * @param item       item
     * @param nbtToParse String to parse
     * @throws Exception if the NBT doesn't parse properly or another error occurs
     */
    @Override
    public ItemStack applyNBT(ItemStack item, String nbtToParse) throws Exception {
        NBTTagCompound tag = MojangsonParser.parse(nbtToParse);
        net.minecraft.server.v1_9_R1.ItemStack is = CraftItemStack.asNMSCopy(item);
        NBTTagCompound isTag = is.getTag();
        if (isTag == null) {
            is.setTag(tag);
        } else {
            if (tag.hasKeyOfType(ATTRIBUTE_MODIFIER_TAG, 9)) {
                NBTTagList tagList = tag.getList(ATTRIBUTE_MODIFIER_TAG, 10);
                if (isTag.hasKeyOfType(ATTRIBUTE_MODIFIER_TAG, 9)) {
                    NBTTagList isTagList = isTag.getList(ATTRIBUTE_MODIFIER_TAG, 10);
                    for (int i = 0; i < tagList.size(); i++) {
                        isTagList.add(tagList.get(i));
                    }
                } else {
                    isTag.set(ATTRIBUTE_MODIFIER_TAG, tagList);
                }
            }

            for (String key : tag.c()) {
                if (key.equals(ATTRIBUTE_MODIFIER_TAG)) continue;
                isTag.set(key, tag.get(key));
            }
        }
        return CraftItemStack.asCraftMirror(is);
    }

    /**
     * Gets a list of AttributeModifiers that affect an ItemStack
     *
     * @param item item
     * @return list of AttributeModifiers
     */
    @Override
    public List<AttributeModifier> getAttributeModifiers(ItemStack item) {
        List<AttributeModifier> list = new ArrayList<>();
        Multimap<String, net.minecraft.server.v1_9_R1.AttributeModifier> multimap = CraftItemStack
                .asNMSCopy(item).a(EnumItemSlot.MAINHAND);
        for (String attributeName : multimap.keySet()) {
            for (net.minecraft.server.v1_9_R1.AttributeModifier m : multimap.get(attributeName)) {
                list.add(new AttributeModifier(m.d(), attributeName, m.b(), m.c(), m.a()));
            }
        }
        return list;
    }

    /**
     * Get a json representation of an ItemStack
     *
     * @param item item
     * @return json string representation
     */
    @Override
    public String itemToJson(ItemStack item) {
        NBTTagCompound tag = new NBTTagCompound();
        CraftItemStack.asNMSCopy(item).save(tag);
        return tag.toString();
    }

    /**
     * Gets the value for the CraftFX NBT tag
     *
     * @param item item
     * @return the unique id or null
     * @throws UnsupportedOperationException if this method isn't implemented
     */
    @Override
    public String getCraftFXId(ItemStack item) throws UnsupportedOperationException {
        if (item == null || item.getTypeId() == 0) return null;
        NBTTagCompound tag = CraftItemStack.asNMSCopy(item).getTag();
        if (tag != null && tag.hasKeyOfType("craftfx", 8)) {
            return tag.getString("craftfx");
        }
        return null;
    }
}
