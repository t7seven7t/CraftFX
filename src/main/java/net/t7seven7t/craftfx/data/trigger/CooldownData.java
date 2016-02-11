package net.t7seven7t.craftfx.data.trigger;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.util.TimeUtil;

/**
 *
 */
public class CooldownData extends AbstractData {

    private long cooldownMillis;

    public long getCooldownMillis() {
        return cooldownMillis;
    }

    @Override
    public void onDataHolderUpdate() {
        this.cooldownMillis = TimeUtil.parseString(get("cooldown", String.class, "0"));
    }

    @Override
    public Data getCopy() {
        return new CooldownData();
    }
}
