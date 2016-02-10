package net.t7seven7t.craftfx.data.trigger;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;

/**
 *
 */
public class HealthChangeData extends AbstractData {

    private final double minHealthChangeDef;
    private final double maxHealthChangeDef;
    private double minHealthChange;
    private double maxHealthChange;

    public HealthChangeData(double minHealthChangeDef, double maxHealthChangeDef) {
        this.minHealthChangeDef = minHealthChangeDef;
        this.maxHealthChangeDef = maxHealthChangeDef;
    }

    public double getMinHealthChange() {
        return minHealthChange;
    }

    public double getMaxHealthChange() {
        return maxHealthChange;
    }

    @Override
    public void onDataHolderUpdate() {
        this.minHealthChange = get("min-health-change", Double.class, minHealthChangeDef);
        this.maxHealthChange = get("max-health-change", Double.class, maxHealthChangeDef);
    }

    @Override
    public Data getCopy() {
        return new HealthChangeData(minHealthChangeDef, maxHealthChangeDef);
    }
}
