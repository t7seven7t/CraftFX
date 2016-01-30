package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.effect.data.DurationData;
import net.t7seven7t.craftfx.item.ItemConfigurationException;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 */
public class Burn extends ExtentEffect {

    @Override
    public void initialize() throws ItemConfigurationException {
        super.initialize();
        addData(new DurationData(20));
    }

    @Override
    public boolean start(RunSpecification spec) {
        if (spec.getTargetRaw() instanceof Entity) {
            final Entity entity = (Entity) spec.getTargetRaw();
            entity.setFireTicks(getData(DurationData.class).getDuration());
            return true;
        } else if (spec.getTargetRaw() instanceof Block) {
            final Block block = (Block) spec.getTargetRaw();
            block.setType(Material.FIRE);
        }

        return false;
    }

    @Override
    public boolean end(RunSpecification spec) {
        spec.getTask().ifPresent(BukkitTask::cancel);
        return start(spec);
    }
}
