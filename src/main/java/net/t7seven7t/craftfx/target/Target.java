package net.t7seven7t.craftfx.target;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

/**
 * The target of an effect
 */
public interface Target<T> {

    T getTarget();

    enum Type {
        BLOCK, ENTITY, LOCATION, SELF, AOE_BLOCK, AOE_ENTITY
    }

    static Target create(Object o) {
        if (o instanceof Entity) {
            return new EntityTarget((Entity) o);
        } else if (o instanceof Block) {
            return new BlockTarget((Block) o);
        } else if (o instanceof Location) {
            return new LocationTarget((Location) o);
        }

        return null;
    }

}
