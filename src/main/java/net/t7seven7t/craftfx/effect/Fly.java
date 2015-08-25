package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.effect.data.SpeedData;
import net.t7seven7t.craftfx.item.ItemConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 */
public class Fly extends ExtentEffect {
    @Override
    public void initialize() throws ItemConfigurationException {
        super.initialize();
        addData(new SpeedData(0.05f));
    }

    @Override
    public boolean start(RunSpecification spec) {
        if (spec.getTarget().getTarget() instanceof Player) {
            final Player target = (Player) spec.getTarget().getTarget();
            target.setAllowFlight(true);
            target.setVelocity(target.getVelocity().setY(1f));
            target.setFlySpeed(getData(SpeedData.class).getSpeed());
            // 1 tick delay setFlying
            Bukkit.getScheduler().runTask(CraftFX.getInstance(), () -> target.setFlying(true));
            return true;
        }

        return false;
    }

    @Override
    public boolean end(RunSpecification spec) {
        if (spec.getTarget().getTarget() instanceof Player) {
            final Player target = (Player) spec.getTarget().getTarget();
            target.setFlying(false);
            target.setAllowFlight(false);
            return true;
        }

        return false;
    }
}
