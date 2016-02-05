package net.t7seven7t.craftfx.data.trigger;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;

import java.util.Optional;

/**
 *
 */
public class ChatData extends AbstractData {

    private Optional<String> pattern;

    public Optional<String> getPattern() {
        return pattern;
    }

    @Override
    public void onDataHolderUpdate() {
        this.pattern = get("pattern", String.class);
    }

    @Override
    public Data getCopy() {
        return new ChatData();
    }
}
