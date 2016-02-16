package net.t7seven7t.craftfx;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 *
 */
public class Target {
    private final Object target;

    public Target(Object target) {
        if (target instanceof Target) {
            this.target = ((Target) target).target;
        } else {
            this.target = target;
        }
    }

    @Override
    public String toString() {
        return "Target{" +
                "target=" + target +
                '}';
    }

    public Optional<Player> getPlayer() {
        return as(Player.class);
    }

    public Optional<Block> getBlock() {
        return as(Block.class);
    }

    public Optional<Entity> getEntity() {
        return as(Entity.class);
    }

    public <T extends Entity> Optional<T> getEntity(Class<T> clazz) {
        return as(clazz);
    }

    public Optional<Location> getLocation() {
        return as(Location.class);
    }

    public Optional<Location> getAsLocation() {
        return Optional.ofNullable(getBlock().map(Block::getLocation)
                .orElse(getEntity().map(Entity::getLocation)
                        .orElse(getLocation().orElse(null))));
    }

    public Optional<Block> getAsBlock() {
        final Optional<Block> optBlock = getBlock();
        if (optBlock.isPresent()) return optBlock;
        return Optional.ofNullable(getAsLocation().map(l -> l.getBlock()).orElse(null));
    }

    public <T> Optional<T> as(Class<T> clazz) {
        return clazz.isInstance(target) ? of(clazz.cast(target)) : empty();
    }

}
