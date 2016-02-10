package net.t7seven7t.craftfx.data.effect;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;

/**
 *
 */
public class SpeedData extends AbstractData {

    private final float speedDef;
    private float speed;

    public SpeedData(float speedDef) {
        this.speedDef = speedDef;
    }

    public SpeedData() {
        this(0.1f);
    }

    public float getSpeed() {
        return speed;
    }

    @Override

    public void onDataHolderUpdate() {
        this.speed = get("speed", Double.class)
                .map(f -> Math.abs(f) > 1 ? 1 * Math.signum(f) : f)
                .orElse((double) speedDef).floatValue();
    }

    @Override
    public Data getCopy() {
        return new SpeedData();
    }
}
