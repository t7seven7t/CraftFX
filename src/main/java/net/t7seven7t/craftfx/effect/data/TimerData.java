package net.t7seven7t.craftfx.effect.data;

import net.t7seven7t.craftfx.effect.EffectData;

/**
 * Accessor for data relevant to setting up a repeating timer for an effect
 */
public class TimerData extends EffectData {

    /**
     * Get the number of times to iterate the effect
     */
    public int getIterations() {
        return getConfig().getInt("timer-iterations", 1);
    }

    /**
     * Get the tick delay between intervals
     */
    public long getInterval() {
        return getConfig().getLong("timer-interval", 20);
    }

}
