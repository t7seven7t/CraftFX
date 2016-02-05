package net.t7seven7t.craftfx.data.trigger;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;

/**
 *
 */
public class MoveData extends AbstractData {

    private final double minMoveDistDef;
    private double minMoveDist;

    public MoveData(double minMoveDistDef) {
        this.minMoveDistDef = minMoveDistDef;
    }

    public double getMinMoveDist() {
        return minMoveDist;
    }

    @Override
    public void onDataHolderUpdate() {
        minMoveDist = get("min-move-distance", Double.class, minMoveDistDef);
    }

    @Override
    public Data getCopy() {
        return new MoveData(minMoveDistDef);
    }
}
