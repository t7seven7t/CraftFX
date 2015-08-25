package net.t7seven7t.craftfx.target;

import org.bukkit.entity.Entity;

/**
 *
 */
public class EntityTarget implements Target<Entity> {

    private final Entity entity;

    public EntityTarget(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Entity getTarget() {
        return entity;
    }
}
