package net.t7seven7t.craftfx;

import java.util.Optional;

/**
 *
 */
public interface Registry<T> {
    void register(T spec);

    Optional<T> getSpec(String alias);
}
