package net.t7seven7t.util;

import com.google.common.collect.Lists;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.NumberConversions;

import java.util.List;

/**
 * Utility for turning Strings into PotionEffects
 */
public class PotionEffectUtil {

    public static List<PotionEffect> getPotionEffects(
            List<String> identifiers) throws IllegalArgumentException {
        List<PotionEffect> potionEffectList = Lists.newArrayList();
        for (String identifier : identifiers) {
            String[] split = identifier.replaceAll("\\s+", "_").split("\\W");
            if (split.length < 3) {
                throw new IllegalArgumentException(
                        "Missing argument: potion effect, duration or amplifier");
            }

            PotionEffectType type = PotionEffectType.getByName(EnumUtil.enumify(split[0]));
            if (type == null) {
                throw new IllegalArgumentException(
                        "Potion effect '" + split[0] + "' is not a valid type.");
            }

            int duration = (int) TimeUtil.parseString(split[1]);
            int amplifier = NumberConversions.toInt(split[2]);

            potionEffectList.add(new PotionEffect(type, duration, amplifier));
        }
        return potionEffectList;
    }

}
