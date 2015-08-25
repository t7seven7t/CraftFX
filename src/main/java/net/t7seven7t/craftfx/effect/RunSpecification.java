package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.target.Target;

import org.bukkit.entity.Player;

/**
 *
 */
public class RunSpecification {

    private final Player player;
    private final Target target;

    /**
     * Get the target for this run spec
     */
    public Target getTarget() {
        return target;
    }

    public RunSpecification(Player player, Target target) {
        this.player = player;
        this.target = target;
    }

    /**
     * Gets the player that triggered this effect to run
     */
    public Player getPlayer() {
        return player;
    }
}
