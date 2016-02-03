package net.t7seven7t.craftfx.data.trigger;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;

import java.util.Optional;

/**
 *
 */
public class ChatData extends AbstractData {

    public Optional<String> getPattern() {
        return get("pattern", String.class);
    }

    @Override
    public Data getCopy() {
        return new ChatData();
    }
}
