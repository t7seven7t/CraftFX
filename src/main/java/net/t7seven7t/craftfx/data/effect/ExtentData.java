package net.t7seven7t.craftfx.data.effect;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.craftfx.effect.ExtentState;
import net.t7seven7t.util.EnumUtil;

import java.util.Optional;

/**
 *
 */
public class ExtentData extends AbstractData {

    private Optional<ExtentState> disabled;
    private ExtentState disabledDef;
    private boolean inverted;
    private boolean invertedDef;

    public ExtentData() {
        disabledDef = ExtentState.END;
    }

    public ExtentData(ExtentState disabledDef) {
        this.disabledDef = disabledDef;
    }

    public ExtentData(ExtentState disabledDef, boolean invertedDef) {
        this.disabledDef = disabledDef;
        this.invertedDef = invertedDef;
    }

    public Optional<ExtentState> getDisabledExtent() {
        return disabled;
    }

    public boolean isExtentDisabled(ExtentState state) {
        return disabled.orElse(null) == state;
    }

    public boolean isInverted() {
        return inverted;
    }

    @Override
    public void onDataHolderUpdate() {
        disabled = Optional.ofNullable(EnumUtil.matchEnumValue(ExtentState.class,
                get("disable-extent", String.class, disabledDef.name())));
        inverted = get("invert-extents", Boolean.class, invertedDef);
    }

    @Override
    public Data getCopy() {
        return new ExtentData();
    }
}
