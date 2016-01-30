package net.t7seven7t.craftfx.trigger;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.effect.Effect;
import net.t7seven7t.craftfx.effect.RunSpecification;
import net.t7seven7t.craftfx.effect.data.DelayData;
import net.t7seven7t.craftfx.effect.data.TimerData;
import net.t7seven7t.craftfx.item.ItemConfigurationException;
import net.t7seven7t.craftfx.target.Target;
import net.t7seven7t.util.EnumUtil;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Class defining how and if an effect should be triggered
 */
public class Trigger {

    /**
     * Type classification of this trigger
     */
    private final TriggerType type;
    /**
     * List of Effects fired when this trigger is triggered
     */
    List<Effect> effectList;
    /**
     * Configuration information
     */
    private ConfigurationSection config;

    /**
     * Constructor for TriggerType supplier
     */
    Trigger(TriggerType type) {
        this.type = type;
    }

    /**
     * Initializes the trigger immediately after it is created
     */
    final void initialize(ConfigurationSection config) throws Exception {
        this.config = config;
        initialize();
    }

    /**
     * Initialize the trigger in here when extending this class. Note that the paths 'effect' and
     * 'effects' are reserved for effects and should not be relied upon.
     *
     * @throws ItemConfigurationException if there is a problem with the configuration
     */
    public void initialize() throws ItemConfigurationException {
    }

    /**
     * Whilst this list is modifiable bare in mind that it may be shared by other Trigger objects
     */
    public List<Effect> getEffects() {
        return effectList;
    }

    /**
     * Runs all of this trigger's associated effects with the run parameters provided
     */
    public void runEffects(final RunSpecification spec) {
        getEffects().forEach(e -> {
            // check if timed
            // check if delayed
            final long delay = e.getData(DelayData.class).getDelayTicks();
            final CraftFX plugin = CraftFX.getInstance();
            final TimerData timerData = e.getData(TimerData.class);
            if (timerData.getIterations() > 1 || timerData.getIterations() < 0) {
                final RunSpecification copy = spec.copy();
                copy.setTask(new BukkitRunnable() {
                    private int iteration = 0;

                    @Override
                    public void run() {
                        ++iteration;
                        e.run(copy);
                        if (timerData.getIterations() > 0
                                && iteration >= timerData.getIterations()) {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, delay, timerData.getInterval()));
            } else if (delay > 0) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> e.run(spec), delay);
            } else {
                e.run(spec);
            }
        });
    }

    public Target.Type getTargetType() {
        return EnumUtil.matchEnumValue(Target.Type.class, config.getString("select", "self"));
    }

    public float getAoeRange() {
        return (float) config.getDouble("aoe-radius", 1.0d);
    }

    public boolean shouldCancelEvents() {
        return config.getBoolean("cancels-event", true);
    }

    public TriggerType getType() {
        return type;
    }
}
