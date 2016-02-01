package net.t7seven7t.craftfx.nms.v1_8_R3;

import com.google.common.collect.Multimap;

import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.t7seven7t.craftfx.nms.AttributeModifier;
import net.t7seven7t.craftfx.nms.NMSInterface;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NMSAdapter implements NMSInterface {

    private static final String ATTRIBUTE_MODIFIER_TAG = "AttributeModifiers";

    @Override
    public boolean isValidItem(ItemStack item) {
        return Item.getById(item.getTypeId()) != null;
    }

    @Override
    public ItemStack applyNBT(ItemStack item, String nbtToParse) throws Exception {
        NBTTagCompound tag = MojangsonParser.parse(nbtToParse);
        net.minecraft.server.v1_8_R3.ItemStack is = CraftItemStack.asNMSCopy(item);
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

    @Override
    public List<AttributeModifier> getAttributeModifiers(ItemStack item) {
        List<AttributeModifier> list = new ArrayList<>();
        Multimap<String, net.minecraft.server.v1_8_R3.AttributeModifier> multimap = CraftItemStack
                .asNMSCopy(item).B();
        for (String attributeName : multimap.keySet()) {
            for (net.minecraft.server.v1_8_R3.AttributeModifier m : multimap.get(attributeName)) {
                list.add(new AttributeModifier(m.d(), attributeName, m.b(), m.c(), m.a()));
            }
        }
        return list;
    }

}
