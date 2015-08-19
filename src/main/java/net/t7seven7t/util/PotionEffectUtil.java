package net.t7seven7t.util;

import com.google.common.collect.Lists;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Contains utility methods for interacting with PotionEffects
 */
public class PotionEffectUtil {

    public static List<PotionEffect> getPotionEffects(List<String> identifiers) throws Exception {
        List<PotionEffect> potionEffectList = Lists.newArrayList();
        for (String identifier : identifiers) {
            String[] split = identifier.split(",");
            if (split.length < 3) {
                throw new Exception("Missing argument: potion effect, duration or amplifier");
            }

            PotionEffectType type = EnumUtil.matchConstantValue(PotionEffectType.class, split[0]);
            if (type == null) {
                throw new Exception("Potion effect '" + split[0] + "' is invalid");
            }

            int duration = Integer.parseInt(split[1]) * 20; // * 20 ticks / sec
            int amplifier = Integer.parseInt(split[2]);

            potionEffectList.add(new PotionEffect(type, duration, amplifier));
        }
        return potionEffectList;
    }

}
