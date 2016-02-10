package net.t7seven7t.craftfx.data.effect;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.util.EnumUtil;

import org.bukkit.Effect;

/**
 *
 */
public class EffectTypeData extends AbstractData {

    private final int effectDataDef;
    private Effect effect;
    private int effectData;

    public EffectTypeData(int effectDataDef) {
        this.effectDataDef = effectDataDef;
    }

    @Override
    public void onDataHolderUpdate() {
        this.effect = EnumUtil.matchEnumValue(Effect.class, get("effect-type", String.class, ""));
        this.effectData = get("effect-data", Integer.class, effectDataDef);
    }

    public int getEffectData() {
        return effectData;
    }

    public Effect getEffect() {
        return effect;
    }

    @Override
    public Data getCopy() {
        return new EffectTypeData(effectDataDef);
    }
}
