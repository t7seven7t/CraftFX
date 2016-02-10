package net.t7seven7t.craftfx.data.effect;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;

/**
 *
 */
public class ModifyFoodData extends AbstractData {

    private final int feedAmountDef;
    private int feedAmount;

    public ModifyFoodData(int feedAmountDef) {
        this.feedAmountDef = feedAmountDef;
    }

    public int getFeedAmount() {
        return feedAmount;
    }

    @Override
    public void onDataHolderUpdate() {
        this.feedAmount = get("feed-amount", Integer.class, feedAmountDef);
    }

    @Override
    public Data getCopy() {
        return new ModifyFoodData(feedAmountDef);
    }
}
