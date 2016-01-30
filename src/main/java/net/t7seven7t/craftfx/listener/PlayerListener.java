package net.t7seven7t.craftfx.listener;

import com.google.common.collect.Lists;

import net.t7seven7t.craftfx.effect.RunSpecification;
import net.t7seven7t.craftfx.item.ItemDefinition;
import net.t7seven7t.craftfx.item.ItemRegistry;
import net.t7seven7t.craftfx.target.Targets;
import net.t7seven7t.craftfx.trigger.Trigger;
import net.t7seven7t.craftfx.trigger.TriggerType;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

/**
 *
 */
public class PlayerListener implements Listener {

    private final ItemRegistry registry;

    public PlayerListener(ItemRegistry registry) {
        this.registry = registry;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        List<ItemDefinition> triggeredDefinitions = registry.getTriggeredDefinitions(
                TriggerType.TELEPORT);
        List<Trigger> triggers = Lists.newArrayList();
        event.getPlayer().getInventory().forEach(
                // check if any items are the same as possible triggers
                i1 -> triggeredDefinitions.stream().filter(i2 -> i2.isSimilar(i1)).findAny()
                        // if so add all of their triggers to execution list
                        .ifPresent(i2 -> triggers.addAll(i2.getTriggers(TriggerType.TELEPORT))));

        triggers.forEach(t -> t.runEffects(new RunSpecification(event.getPlayer(),
                // self, location. NO entity support. Block is called thru location
                Targets.from(t, event, event.getTo()))));
    }

}
