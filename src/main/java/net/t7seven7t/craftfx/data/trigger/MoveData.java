package net.t7seven7t.craftfx.data.trigger;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;

/**
 *
 */
public class MoveData extends AbstractData {

    private final double minMoveDistDef;
    private final double maxMoveDistDef;
    private double minMoveDist;
    private double maxMoveDist;

    public MoveData(double minMoveDistDef, double maxMoveDistDef) {
        this.minMoveDistDef = minMoveDistDef;
        this.maxMoveDistDef = maxMoveDistDef;
    }

    public double getMaxMoveDist() {
        return maxMoveDist;
    }

    public double getMinMoveDist() {
        return minMoveDist;
    }

    @Override
    public void onDataHolderUpdate() {
        minMoveDist = get("min-move-distance", Double.class, minMoveDistDef);
        maxMoveDist = get("max-move-distance", Double.class, maxMoveDistDef);
    }

    @Override
    public Data getCopy() {
        return new MoveData(minMoveDistDef, maxMoveDistDef);
    }
}
