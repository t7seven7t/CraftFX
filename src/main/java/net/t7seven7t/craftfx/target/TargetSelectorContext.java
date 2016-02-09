package net.t7seven7t.craftfx.target;

import net.t7seven7t.craftfx.Target;
import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.craftfx.data.DataHolder;
import net.t7seven7t.craftfx.data.DataInterface;

import org.bukkit.entity.Player;

import java.util.Optional;

/**
 *
 */
public final class TargetSelectorContext implements DataInterface {

    private final Target origin;
    private final Player initiator;
    private final DataHolder holder;

    TargetSelectorContext(Target origin, Player initiator, DataHolder holder) {
        this.origin = origin;
        this.initiator = initiator;
        this.holder = holder;
    }

    public DataHolder getDataHolder() {
        return holder;
    }

    public Target getOrigin() {
        return origin;
    }

    public Player getInitiator() {
        return initiator;
    }

    @Override
    public <T extends Data> Optional<T> getData(Class<T> clazz) {
        return holder.getData(clazz);
    }
}
