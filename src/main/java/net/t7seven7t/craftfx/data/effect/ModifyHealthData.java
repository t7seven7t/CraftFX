package net.t7seven7t.craftfx.data.effect;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;

/**
 *
 */
public class ModifyHealthData extends AbstractData {

    private final double healthAmountDef;
    private double healthAmount;

    public ModifyHealthData(double healthAmountDef) {
        this.healthAmountDef = healthAmountDef;
    }

    @Override
    public void onDataHolderUpdate() {
        this.healthAmount = get("health-amount", Double.class, healthAmountDef);
    }

    public double getHealthAmount() {
        return healthAmount;
    }

    @Override
    public Data getCopy() {
        return new ModifyHealthData(healthAmountDef);
    }
}
