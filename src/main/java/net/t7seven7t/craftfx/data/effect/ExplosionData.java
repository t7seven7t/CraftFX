package net.t7seven7t.craftfx.data.effect;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;

/**
 *
 */
public class ExplosionData extends AbstractData {

    private final float powerDef;
    private final boolean setsFireDef;
    private final boolean blockDamageDef;
    private float power;
    private boolean setsFire;
    private boolean blockDamage;

    public ExplosionData(float powerDef, boolean setsFireDef, boolean blockDamageDef) {
        this.powerDef = powerDef;
        this.setsFireDef = setsFireDef;
        this.blockDamageDef = blockDamageDef;
    }

    public float getPower() {
        return power;
    }

    public boolean isSetsFire() {
        return setsFire;
    }

    public boolean isBlockDamage() {
        return blockDamage;
    }

    @Override
    public void onDataHolderUpdate() {
        this.setsFire = get("sets-fire", Boolean.class, setsFireDef);
        this.power = get("power", Float.class, powerDef);
        this.blockDamage = get("block-damage", Boolean.class, blockDamageDef);
    }

    @Override
    public Data getCopy() {
        return new ExplosionData(powerDef, setsFireDef, blockDamageDef);
    }
}
