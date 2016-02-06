package net.t7seven7t.craftfx.data.effect;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.util.TimeUtil;

import java.util.Optional;

/**
 *
 */
public class TimerData extends AbstractData {

    private final int iterationsDef;
    private final long intervalDef;
    private int iterations;
    private long interval;

    public TimerData(int iterationsDef, long intervalDef) {
        this.iterationsDef = iterationsDef;
        this.intervalDef = intervalDef;
    }

    public TimerData() {
        this(1, 20);
    }

    public int getIterations() {
        return iterations;
    }

    public long getInterval() {
        return interval;
    }

    @Override
    public void onDataHolderUpdate() {
        this.iterations = Math.max(get("timer-iterations", Integer.class, iterationsDef), 1);
        Optional<String> optInterval = get("timer-interval", String.class);
        this.interval = Math.max(optInterval.map(TimeUtil::parseString).map(i -> i / 50)
                .orElse(intervalDef), 1);
    }

    @Override
    public Data getCopy() {
        return new TimerData(iterationsDef, intervalDef);
    }
}
