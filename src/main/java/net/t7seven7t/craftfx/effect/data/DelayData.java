package net.t7seven7t.craftfx.effect.data;

import net.t7seven7t.craftfx.effect.EffectData;

import java.time.Instant;
import java.time.temporal.TemporalUnit;

/**
 * Accessor for getting the delay of running an effect
 */
public class DelayData extends EffectData {

    /**
     * Retrieves the delay in terms of the TemporalUnit specified. Beware that this method will
     * round down to the nearest whole unit and can be imprecise depending on the unit type.
     */
    public long getDelay(TemporalUnit unit) {
        return unit.between(Instant.EPOCH, Instant.ofEpochMilli((long) (getDelay() * 1000)));
    }

    /**
     * Retrieves the delay in seconds
     */
    public double getDelay() {
        return Math.max(getConfig().getDouble("delay", 0), 0);
    }

    /**
     * Retrieves the delay in ticks
     */
    public long getDelayTicks() {
        return (int) (getDelay() * 20);
    }

}
