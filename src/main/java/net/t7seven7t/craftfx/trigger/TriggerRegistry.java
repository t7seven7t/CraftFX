package net.t7seven7t.craftfx.trigger;

import net.t7seven7t.craftfx.Registry;
import net.t7seven7t.craftfx.data.trigger.ChatData;
import net.t7seven7t.craftfx.data.trigger.HealthChangeData;
import net.t7seven7t.craftfx.data.trigger.HoldData;
import net.t7seven7t.craftfx.data.trigger.MoveData;
import net.t7seven7t.craftfx.data.trigger.SlotData;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 */
public class TriggerRegistry implements Registry<TriggerSpec> {

    /**
     * List of all trigger specs
     */
    private final List<TriggerSpec> triggerSpecList = new ArrayList<>();

    public TriggerRegistry() {
        initDefaults();
    }

    @Override
    public void register(TriggerSpec spec) {
        for (String alias : spec.getAliases()) {
            if (getSpec(alias).isPresent()) {
                throw new IllegalArgumentException("A TriggerSpec is already registered " +
                        "with the alias " + alias);
            }
        }
        triggerSpecList.add(spec);
    }

    @Override
    public Optional<TriggerSpec> getSpec(String alias) {
        alias = alias.toLowerCase();
        for (TriggerSpec spec : triggerSpecList) {
            if (spec.getAliases().contains(alias)) return Optional.of(spec);
        }
        return Optional.empty();
    }

    private void initDefaults() {
        final Function<PlayerJoinEvent, TriggerContext> playerJoinFunction = e ->
                new TriggerContext(e.getPlayer());
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
                .data(new SlotData("all"))
                .listener(EntityDeathEvent.class, e -> e instanceof PlayerDeathEvent
                        ? new TriggerContext((Player) e.getEntity()) : null)
                .build());
        register(TriggerSpec.builder()
                .aliases("move")
                .data(new MoveData(0, Double.MAX_VALUE))
                .listener(PlayerMoveEvent.class, e -> new TriggerContext(e.getPlayer(), e.getTo()))
                .filter(c -> {
                    final MoveData data = c.getData(MoveData.class).get();
                    final double dist = c.getInitiator().getLocation()
                            .distance(c.getTarget().getLocation().get());
                    return dist >= data.getMinMoveDist() && dist <= data.getMaxMoveDist();
                }).build());
        final Predicate<TriggerContext> holdFilter = c -> {
            final HoldData data = c.getData(HoldData.class).get();
            final Optional<ItemStack> opt = c.getTarget().as(ItemStack.class);
            return opt.isPresent() && data.getMinimumStackSize() <= opt.get().getAmount()
                    && data.getMaximumStackSize() >= opt.get().getAmount();
        };
        final Predicate<TriggerContext> equipFilter = c -> {
            final SlotData data = c.getData(SlotData.class).get();
            final int slot = c.getTargets().get(1).as(Integer.class).get();
            if (data.isHandSlot() && slot != c.getInitiator().getInventory()
                    .getHeldItemSlot()) return false;
            for (int i : data.getSlots()) {
                if (i == slot) return true;
            }
            return false;
        };
        register(TriggerSpec.builder()
                .aliases("hold item", "hold")
                .data(new HoldData(0, 64))
                .listener(PlayerItemHeldEvent.class, e -> new TriggerContext(e.getPlayer(),
                        e.getPlayer().getInventory().getItem(e.getNewSlot())))
                .listener(InventoryClickEvent.class, e -> {
                    if (e.getWhoClicked() instanceof Player
                            && e.getSlot() == e.getWhoClicked().getInventory().getHeldItemSlot()) {
                        return new TriggerContext((Player) e.getWhoClicked(), e.getCursor());
                    }
                    return null;
                })
                .listener(PlayerJoinEvent.class, playerJoinFunction)
                .filter(holdFilter)
                .build());
        register(TriggerSpec.builder()
                .aliases("unhold item", "unhold")
                .data(new HoldData(0, 64))
                .listener(PlayerItemHeldEvent.class, e -> new TriggerContext(e.getPlayer(),
                                e.getPlayer().getInventory().getItem(e.getPreviousSlot())),
                        // execute before hold trigger on monitor priority
                        EventPriority.HIGHEST)
                .listener(InventoryClickEvent.class, e -> {
                    if (e.getWhoClicked() instanceof Player
                            && e.getSlot() == e.getWhoClicked().getInventory().getHeldItemSlot()) {
                        return new TriggerContext((Player) e.getWhoClicked(), e.getCurrentItem());
                    }
                    return null;
                })
                .listener(PlayerJoinEvent.class, playerJoinFunction)
                .filter(holdFilter)
                .build());
        register(TriggerSpec.builder()
                .aliases("equip")
                .listener(InventoryClickEvent.class, e -> {
                    if (e.getWhoClicked() instanceof Player) {
                        return new TriggerContext((Player) e.getWhoClicked(), e.getCursor(),
                                e.getSlot());
                    }
                    return null;
                }).filter(equipFilter)
                .build());
        register(TriggerSpec.builder()
                .aliases("unequip")
                .listener(InventoryClickEvent.class, e -> {
                    if (e.getWhoClicked() instanceof Player) {
                        return new TriggerContext((Player) e.getWhoClicked(), e.getCurrentItem(),
                                e.getSlot());
                    }
                    return null;
                }).filter(equipFilter)
                .build());
        register(TriggerSpec.builder()
                .aliases("hit entity")
                .data(new HealthChangeData(0, Double.MAX_VALUE))
                .listener(EntityDamageByEntityEvent.class, e -> e.getDamager() instanceof Player ?
                        new TriggerContext((Player) e.getDamager(), e.getEntity(),
                                e.getFinalDamage()) : null)
                .filter(c -> {
                    final HealthChangeData data = c.getData(HealthChangeData.class).get();
                    final double damage = c.getTargets().get(1).as(Double.class).get();
                    return damage >= data.getMinHealthChange() && damage <= data
                            .getMaxHealthChange();
                }).build());
        register(TriggerSpec.builder()
                .aliases("break item")
                .listener(PlayerItemBreakEvent.class,
                        e -> new TriggerContext(e.getPlayer(), e.getBrokenItem()))
                .build());
        register(TriggerSpec.builder()
                .aliases("consume item", "consume")
                .listener(PlayerItemConsumeEvent.class,
                        e -> new TriggerContext(e.getPlayer(), e.getItem()))
                .build());
        register(TriggerSpec.builder()
                .aliases("change health", "modify health")
                .data(new HealthChangeData(-20, 20))
                .listener(EntityDamageEvent.class, e -> e.getEntityType() == EntityType.PLAYER ?
                        new TriggerContext((Player) e.getEntity(), e.getFinalDamage()) : null)
                .filter(c -> {
                    final HealthChangeData data = c.getData(HealthChangeData.class).get();
                    final double damage = c.getTarget().as(Double.class).get();
                    return damage >= data.getMinHealthChange()
                            && damage <= data.getMaxHealthChange();
                }).build());
        register(TriggerSpec.builder()
                .aliases("teleport")
                .data(new MoveData(0, Double.MAX_VALUE))
                .listener(PlayerTeleportEvent.class,
                        e -> new TriggerContext(e.getPlayer(), e.getTo()))
                .filter(c -> {
                    final MoveData data = c.getData(MoveData.class).get();
                    final double dist = c.getTarget().getLocation().get()
                            .distance(c.getInitiator().getLocation());
                    return dist >= data.getMinMoveDist() && dist <= data.getMaxMoveDist();
                }).build());
        register(TriggerSpec.builder()
                .aliases("on kill", "kill")
                .listener(EntityDamageByEntityEvent.class, e -> {
                    if (!(e.getEntity() instanceof LivingEntity)
                            || !(e.getDamager() instanceof Player)) return null;
                    if (((LivingEntity) e.getEntity()).getHealth() - e.getFinalDamage() <= 0) {
                        return new TriggerContext((Player) e.getDamager(), e.getEntity());
                    }
                    return null;
                }).build());
        register(TriggerSpec.builder()
                .aliases("left click air")
                .listener(PlayerInteractEvent.class, e -> {
                    if (e.getAction() == Action.LEFT_CLICK_AIR) {
                        return new TriggerContext(e.getPlayer(), e.getPlayer());
                    }
                    return null;
                })
                .build());
        register(TriggerSpec.builder()
                .aliases("left click block")
                .listener(PlayerInteractEvent.class, e -> {
                    if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                        return new TriggerContext(e.getPlayer(), e.getClickedBlock());
                    }
                    return null;
                }).build());
        register(TriggerSpec.builder()
                .aliases("right click air")
                .listener(PlayerInteractEvent.class, e -> {
                    if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                        return new TriggerContext(e.getPlayer(), e.getPlayer());
                    }
                    return null;
                }).build());
        register(TriggerSpec.builder()
                .aliases("right click block")
                .listener(PlayerInteractEvent.class, e -> {
                    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        return new TriggerContext(e.getPlayer(), e.getClickedBlock());
                    }
                    return null;
                }).build());
        register(TriggerSpec.builder()
                .aliases("right click entity")
                .listener(PlayerInteractEntityEvent.class, e ->
                        new TriggerContext(e.getPlayer(), e.getRightClicked()))
                .build());
    }

}
