package net.t7seven7t.craftfx.effect.data;

import net.t7seven7t.craftfx.effect.EffectData;

/**
 *
 */
public class DurationData extends EffectData {
    private final int def;

    public DurationData(int def) {
        this.def = def;
    }

    /**
     * Gets the duration in ticks
     */
    public int getDuration() {
        return getConfig().getInt("duration", def);
    }

}
