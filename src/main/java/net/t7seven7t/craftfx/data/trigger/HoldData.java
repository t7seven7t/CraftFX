package net.t7seven7t.craftfx.data.trigger;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;

/**
 *
 */
public class HoldData extends AbstractData {

    private final int minimumStackSize;
    private final int maximumStackSize;

    public HoldData(int minimumStackSize, int maximumStackSize) {
        this.minimumStackSize = minimumStackSize;
        this.maximumStackSize = maximumStackSize;
    }

    public int getMinimumStackSize() {
        return minimumStackSize;
    }

    public int getMaximumStackSize() {
        return maximumStackSize;
    }

    @Override
    public Data getCopy() {
        return new HoldData(minimumStackSize, maximumStackSize);
    }
}
