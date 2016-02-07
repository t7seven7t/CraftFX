package net.t7seven7t.craftfx.target;

import net.t7seven7t.craftfx.Target;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class TargetSelector {

    private final TargetAdapter targetAdapter;
    private final TargetSelectorContext context;
    private final List<Target> targets = new ArrayList<>();

    TargetSelector(TargetAdapter targetAdapter,
                   TargetSelectorContext context) {
        this.targetAdapter = targetAdapter;
        this.context = context;
        recalculate();
    }

    public void recalculate() {
        targets.clear();
        targets.addAll(targetAdapter.apply(context));
    }

    public List<Target> getTargets() {
        return targets;
    }

}
