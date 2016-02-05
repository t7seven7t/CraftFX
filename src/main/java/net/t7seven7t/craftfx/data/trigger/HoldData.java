package net.t7seven7t.craftfx.data.trigger;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;

/**
 *
 */
public class HoldData extends AbstractData {

    private final int minimumStackSizeDef;
    private final int maximumStackSizeDef;
    private int minimumStackSize;
    private int maximumStackSize;

    public HoldData(int minimumStackSizeDef, int maximumStackSizeDef) {
        this.minimumStackSizeDef = minimumStackSizeDef;
        this.maximumStackSizeDef = maximumStackSizeDef;
    }

    public int getMinimumStackSize() {
        return minimumStackSize;
    }

    public int getMaximumStackSize() {
        return maximumStackSize;
    }

    @Override
    public void onDataHolderUpdate() {
        this.minimumStackSize = get("min-stack-size", Integer.class, minimumStackSizeDef);
        this.maximumStackSize = get("max-stack-size", Integer.class, maximumStackSizeDef);
    }

    @Override
    public Data getCopy() {
        return new HoldData(minimumStackSizeDef, maximumStackSizeDef);
    }
}
