package net.t7seven7t.craftfx.data.effect;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.util.EnumUtil;

import org.bukkit.entity.EntityType;

/**
 *
 */
public class TargetSelectorData extends AbstractData {

    private final String modeDef;
    private final int limitDef;
    private final double aoeRadiusDef;
    private String mode;
    private int limit;
    private EntityType entityType;
    private double aoeRadius;

    public TargetSelectorData(String modeDef, int limitDef, double aoeRadiusDef) {
        this.modeDef = modeDef;
        this.limitDef = limitDef;
        this.aoeRadiusDef = aoeRadiusDef;
    }

    public int getLimit() {
        return limit;
    }

    public double getAoeRadius() {
        return aoeRadius;
    }

    public String getMode() {
        return mode;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public void onDataHolderUpdate() {
        this.mode = get("target-mode", String.class, modeDef);
        this.limit = get("target-limit", Integer.class, limitDef);
        this.entityType = EnumUtil.matchEnumValue(EntityType.class,
                get("target-entity-type", String.class, ""));
        this.aoeRadius = get("target-aoe-radius", Double.class, aoeRadiusDef);
        if (!CraftFX.instance().getTargetSelectorRegistry().getSpec(mode).isPresent()) {
            CraftFX.log().warning("You have selected a mode that is not registered. " +
                            "Defaulting to 'self' mode. Details: effect = %s, mode = %s",
                    getHolder().get(), mode);
        }
    }

    @Override
    public Data getCopy() {
        return new TargetSelectorData(modeDef, limitDef, aoeRadiusDef);
    }
}
