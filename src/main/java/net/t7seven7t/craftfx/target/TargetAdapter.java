package net.t7seven7t.craftfx.target;

import net.t7seven7t.craftfx.Target;

import java.util.List;

/**
 *
 */
@FunctionalInterface
public interface TargetAdapter {

    List<Target> apply(TargetSelectorContext context);

}
