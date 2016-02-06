package net.t7seven7t.craftfx.data.effect;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.util.TimeUtil;

import java.util.Optional;

/**
 *
 */
public class DelayData extends AbstractData {

    private final long delayDef;
    private long delay;

    public DelayData(long delayDef) {
        this.delayDef = delayDef;
    }

    public long getDelayTicks() {
        return delay;
    }

    @Override
    public void onDataHolderUpdate() {
        Optional<String> optTime = get("delay", String.class);
        this.delay = optTime.map(TimeUtil::parseString).map(d -> d / 50).orElse(delayDef);
    }

    @Override
    public Data getCopy() {
        return new DelayData(0);
    }
}
