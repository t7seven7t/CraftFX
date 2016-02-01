package net.t7seven7t.util;

import org.bukkit.CoalType;
import org.bukkit.DyeColor;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.SandstoneType;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.EntityType;
import org.bukkit.material.Coal;
import org.bukkit.material.Leaves;
import org.bukkit.material.LongGrass;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Sandstone;
import org.bukkit.material.SpawnEgg;
import org.bukkit.material.Tree;
import org.bukkit.material.WoodenStep;
import org.bukkit.material.Wool;

import java.util.function.Function;

import static org.bukkit.Material.matchMaterial;

/**
 * Utility class for converting Strings to MaterialData objects
 */
public class MaterialDataUtil {

    public static MaterialData getMaterialData(String identifier) {
        String[] split = identifier.replaceAll("\\s+", "_").split("\\W");
        // TODO: Add additional material/name database like essentials/worldedit have
        Material material = matchMaterial(split[0]);

        if (material == null) {
            return null;
        }

        if (split.length == 1) {
            return new MaterialData(material);
        }

        try {
            byte rawData = Byte.parseByte(split[1]);
            return new MaterialData(material, rawData);
        } catch (NumberFormatException e) {
            // ignore
        }

        switch (material) {
            case LEAVES:
                return getMaterialData(material, Leaves::new, TreeSpecies.class, split[1]);
            case COAL:
                return getMaterialData(material, Coal::new, CoalType.class, split[1]);
            case LONG_GRASS:
                return getMaterialData(material, LongGrass::new, GrassSpecies.class, split[1]);
            case SANDSTONE:
                return getMaterialData(material, Sandstone::new, SandstoneType.class, split[1]);
            case MONSTER_EGG:
                return getMaterialData(material, SpawnEgg::new, EntityType.class, split[1]);
            case LOG:
                return getMaterialData(material, Tree::new, TreeSpecies.class, split[1]);
            case WOOD_STEP:
                return getMaterialData(material, WoodenStep::new, TreeSpecies.class, split[1]);
            case WOOL:
                return getMaterialData(material, Wool::new, DyeColor.class, split[1]);
            // TODO: Add Dye here when Spigot finally accepts my PR to match other MaterialData types
            default:
                // Couldn't find additional data for this material
                return new MaterialData(material);
        }
    }

    private static <V extends Enum<V>> MaterialData getMaterialData(Material material,
                                                                    Function<V, MaterialData> factory,
                                                                    Class<V> enumClass, String id) {
        V result = EnumUtil.matchEnumValue(enumClass, id);
        return result == null ? new MaterialData(material) : factory.apply(result);
    }

}
