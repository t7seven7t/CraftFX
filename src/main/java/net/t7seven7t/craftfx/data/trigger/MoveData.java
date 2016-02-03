package net.t7seven7t.craftfx.data.trigger;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;

/**
 *
 */
public class MoveData extends AbstractData {

    private final double minMoveDist;

    public MoveData(double minMoveDist) {
        this.minMoveDist = minMoveDist;
    }

    public double getMinMoveDist() {
        return get("min-move-distance", Double.class, minMoveDist);
    }

    @Override
    public Data getCopy() {
        return new MoveData(minMoveDist);
    }
}
