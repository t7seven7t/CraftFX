package net.t7seven7t.craftfx.data.effect;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.craftfx.effect.ExtentState;
import net.t7seven7t.util.EnumUtil;

/**
 *
 */
public class ExtentData extends AbstractData {

    private final ExtentState disabledDef;
    private final boolean invertedDef;
    private ExtentState disabled;
    private boolean inverted;

    public ExtentData() {
        this(ExtentState.END);
    }

    public ExtentData(ExtentState disabledDef) {
        this(disabledDef, false);
    }

    public ExtentData(ExtentState disabledDef, boolean invertedDef) {
        this.disabledDef = disabledDef;
        this.invertedDef = invertedDef;
    }

    public boolean isExtentDisabled(ExtentState state) {
        return disabled != null && disabled == state;
    }

    public boolean isInverted() {
        return inverted;
    }

    @Override
    public void onDataHolderUpdate() {
        disabled = EnumUtil.matchEnumValue(ExtentState.class,
                get("disable-extent", String.class, ""));
        if (disabled == null) disabled = disabledDef;
        inverted = get("invert-extents", Boolean.class, invertedDef);
    }

    @Override
    public Data getCopy() {
        return new ExtentData(disabledDef, invertedDef);
    }
}
