package net.t7seven7t.craftfx.target;

import net.t7seven7t.craftfx.trigger.Trigger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class Targets {

    public static Target from(Trigger trigger, PlayerEvent event, Object... objects) {
        return from(trigger, event.getPlayer(), objects);
    }

    public static Target from(Trigger trigger, Player self, Object... objects) {
        List<Object> params = Arrays.asList(objects);
        switch (trigger.getTargetType()) {
            case SELF:
                return Target.create(self);
            case BLOCK:
                return Optional.of(filter(params, Block.class))
                        // or else try to get block from location
                        .orElse(Optional.of(getLocationTarget(self, params))
                                // map location to block
                                .map(t -> Target.create(t.getTarget().getBlock())).orElse(null));
            case ENTITY:
                return filter(params, Entity.class);
            case LOCATION:
                return getLocationTarget(self, params);
            case AOE_BLOCK:
                throw new AbstractMethodError(); // TODO
            case AOE_ENTITY:
                throw new AbstractMethodError(); // TODO
            default:
                return null;
        }
    }

    private static Target<Location> getLocationTarget(Player self, List<Object> objects) {
        return Optional.of(filter(objects, Location.class)).orElse(
                Target.create(self.getLocation()));
    }

    private static <T> Target<T> filter(List<Object> objects, Class<T> clazz) {
        return objects.stream().filter(o -> clazz.isInstance(o)).findAny().map(Target::create)
                .orElse(null);
    }

}
