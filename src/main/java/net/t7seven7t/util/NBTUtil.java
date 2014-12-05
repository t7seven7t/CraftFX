package net.t7seven7t.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

@SuppressWarnings("deprecation")
public class NBTUtil {

    public static String itemToJSON(ItemStack item) {
        StringBuilder result = new StringBuilder();
        result.append("{");
        result.append("id:");
        result.append(item.getTypeId());
        result.append(",");
        result.append("tag:");
        result.append("{");
        
        List<String> tags = Lists.newArrayList();
        tags.add(itemToEnchantments(item));
        tags.add(itemToDisplay(item));
        
        // Filter out empty tags
        Iterator<String> it = Collections2.filter(tags, Predicates.not(Predicates.equalTo(""))).iterator();
        while(it.hasNext()) {
            result.append(it.next());
            if (it.hasNext()) result.append(",");
        }
        
        result.append("}");
        result.append("}");
        return result.toString();
    }
    
    static String itemToDisplay(ItemStack item) {
        StringBuilder result = new StringBuilder();
        if (item.getItemMeta().hasDisplayName() || item.getItemMeta().hasLore()) {
            result.append("display:");
            result.append("{");
            if (item.getItemMeta().hasDisplayName()) {
                result.append("Name:");
                result.append(item.getItemMeta().getDisplayName());
                if (item.getItemMeta().hasLore()) result.append(",");
            }
            if (item.getItemMeta().hasLore()) {
                result.append("Lore:");
                result.append("[");
                Iterator<String> it = item.getItemMeta().getLore().iterator();
                while (it.hasNext()) {
                    result.append("\\\"");
                    result.append(it.next());
                    result.append("\\\"");
                    if (it.hasNext()) result.append(",");
                }
                result.append("]");
            }
            result.append("}");
        }
        
        return result.toString();
    }
    
    static String itemToEnchantments(ItemStack item) {
        StringBuilder result = new StringBuilder();
        if (item.getEnchantments() != null && !item.getEnchantments().isEmpty()) {
            result.append("ench:");
            result.append("[");
            Iterator<Entry<Enchantment, Integer>> it = item.getEnchantments().entrySet().iterator();
            while (it.hasNext()) {
                Entry<Enchantment, Integer> entry = it.next();
                result.append("{");
                result.append("id:");
                result.append(entry.getKey().getId());
                result.append(",");
                result.append("lvl:");
                result.append(entry.getValue());
                result.append("}");
                if (it.hasNext()) {
                    result.append(",");
                }
            }
            result.append("]");
        }
        return result.toString();
    }
    
    public static ItemStack itemFromJson(String json) {
        return null;
    }
    
}
