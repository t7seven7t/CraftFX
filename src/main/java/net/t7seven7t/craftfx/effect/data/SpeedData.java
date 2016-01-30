package net.t7seven7t.craftfx.effect.data;

import net.t7seven7t.craftfx.effect.EffectData;

import java.util.Optional;

/**
 * Accessor for effects that need to get a player speed.
 */
public class SpeedData extends EffectData {
    private final float def;

    public SpeedData(float def) {
        this.def = def;
    }

    public float getSpeed() {
        return Optional.of((float) getConfig().getDouble("speed", def))
                // If abs value is greater than 1 restrict to 1/-1
                .map(f -> Math.abs(f) > 1 ? 1 * Math.signum(f) : f).get();
    }

}
