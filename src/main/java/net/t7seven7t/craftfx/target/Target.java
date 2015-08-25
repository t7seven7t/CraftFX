package net.t7seven7t.craftfx.target;

/**
 * The target of an effect
 */
public interface Target<T> {

    T getTarget();

    enum Type {
        BLOCK, ENTITY, LOCATION, SELF
    }

}
