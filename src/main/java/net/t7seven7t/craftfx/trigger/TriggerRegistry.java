package net.t7seven7t.craftfx.trigger;

import net.t7seven7t.craftfx.data.trigger.ChatData;
import net.t7seven7t.craftfx.data.trigger.HoldData;
import net.t7seven7t.craftfx.data.trigger.MoveData;
import net.t7seven7t.craftfx.data.trigger.SlotData;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class TriggerRegistry {

    /**
     * List of all trigger specs
     */
    private final List<TriggerSpec> triggerSpecList = new ArrayList<>();

    public TriggerRegistry() {
        initDefaults();
    }

    public void register(TriggerSpec spec) {
        for (String alias : spec.getAliases()) {
            if (getSpec(alias).isPresent()) {
                throw new IllegalArgumentException("A TriggerSpec is already registered " +
                        "with the alias " + alias);
            }
        }
        triggerSpecList.add(spec);
    }

    public Optional<TriggerSpec> getSpec(String alias) {
        alias = alias.toLowerCase();
        for (TriggerSpec spec : triggerSpecList) {
            if (spec.getAliases().contains(alias)) return Optional.of(spec);
        }
        return Optional.empty();
    }

    private void initDefaults() {
        register(TriggerSpec.builder()
                .aliases("chat")
                .data(new ChatData())
                .listener(AsyncPlayerChatEvent.class,
                        e -> new TriggerContext(e.getPlayer(), e.getMessage()))
                .filter(c -> {
                    final ChatData data = c.getData(ChatData.class).get();
                    final Optional<String> opt = data.getPattern();
                    return opt.map(p -> c.getTarget().as(String.class).get().matches(p))
                            .orElse(true);
                }).build());
        register(TriggerSpec.builder()
                .aliases("death")
                .listener(EntityDeathEvent.class, e -> e instanceof PlayerDeathEvent
                        ? new TriggerContext((Player) e.getEntity()) : null)
                .build());
        register(TriggerSpec.builder()
                .aliases("move")
                .data(new MoveData(0))
                .data(new SlotData("hand")) // might be a little intensive otherwise
                .listener(PlayerMoveEvent.class, e -> new TriggerContext(e.getPlayer(), e.getTo()))
                .filter(c -> {
                    final MoveData data = c.getData(MoveData.class).get();
                    final double dist = data.getMinMoveDist();
                    return dist <= 0 || dist < c.getInitiator().getLocation()
                            .distance(c.getTarget().getLocation().get());
                }).build());
        register(TriggerSpec.builder()
                .aliases("hold item", "hold")
                .data(new HoldData(Integer.MIN_VALUE, Integer.MAX_VALUE))
                .data(new SlotData("hand"))
                .listener(PlayerItemHeldEvent.class, e -> new TriggerContext(e.getPlayer(), e1 ->
                        e.getPlayer().getInventory().getItem(e.getNewSlot())))
                .filter(c -> {
                    final HoldData data = c.getData(HoldData.class).get();
                    final Optional<ItemStack> opt = c.getTarget().as(ItemStack.class);
                    return opt.isPresent() && data.getMinimumStackSize() < opt.get().getAmount()
                            && data.getMaximumStackSize() > opt.get().getAmount();
                }).build());
    }

}
