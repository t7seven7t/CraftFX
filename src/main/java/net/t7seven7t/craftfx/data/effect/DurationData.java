package net.t7seven7t.craftfx.data.effect;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.util.TimeUtil;

/**
 *
 */
public class DurationData extends AbstractData {

    private final long durationTicksDef;
    private long durationTicks;

    public DurationData(long durationTicksDef) {
        this.durationTicksDef = durationTicksDef;
    }

    public long getDurationTicks() {
        return durationTicks;
    }

    @Override
    public void onDataHolderUpdate() {
        this.durationTicks = TimeUtil
                .parseString(get("duration", String.class, durationTicksDef + "t")) / 50;
    }

    @Override
    public Data getCopy() {
        return new DurationData(durationTicksDef);
    }
}
