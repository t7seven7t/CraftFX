package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.target.Target;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;

/**
 *
 */
public class RunSpecification {

    private final Player player;
    private final Target target;
    private BukkitTask task;

    public RunSpecification(Player player, Target target) {
        this.player = player;
        this.target = target;
    }

    /**
     * Get the target for this run spec
     */
    public Target getTarget() {
        return target;
    }

    /**
     * Gets the raw target object for this run spec
     * @return
     */
    public Object getTargetRaw() {
        return target.getTarget();
    }

    /**
     * Gets the player that triggered this effect to run
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the bukkit task that calls the run method this run specification is passed to
     */
    public Optional<BukkitTask> getTask() {
        return Optional.ofNullable(task);
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public final RunSpecification copy() {
        RunSpecification result = new RunSpecification(player, target);
        result.task = task;
        return result;
    }
}
