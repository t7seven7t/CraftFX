package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.Registry;
import net.t7seven7t.craftfx.data.ConfigData;
import net.t7seven7t.craftfx.data.effect.DurationData;
import net.t7seven7t.craftfx.data.effect.EffectTypeData;
import net.t7seven7t.craftfx.data.effect.ExplosionData;
import net.t7seven7t.craftfx.data.effect.ExtentData;
import net.t7seven7t.craftfx.data.effect.ModifyFoodData;
import net.t7seven7t.craftfx.data.effect.ModifyHealthData;
import net.t7seven7t.craftfx.data.effect.SoundData;
import net.t7seven7t.craftfx.data.effect.SpeedData;
import net.t7seven7t.craftfx.data.generic.EntityTypeData;
import net.t7seven7t.craftfx.util.MessageUtil;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.t7seven7t.craftfx.effect.ExtentState.END;
import static net.t7seven7t.craftfx.effect.ExtentState.START;

/**
 *
 */
public class EffectRegistry implements Registry<EffectSpec> {

    /**
     * List of all effect specs
     */
    private final List<EffectSpec> effectSpecList = new ArrayList<>();

    public EffectRegistry() {
        initDefaults();
    }

    @Override
    public void register(EffectSpec spec) {
        for (String alias : spec.getAliases()) {
            if (getSpec(alias).isPresent()) {
                throw new IllegalArgumentException("An EffectSpec is already registered " +
                        "with the alias " + alias);
            }
        }
        effectSpecList.add(spec);
    }

    @Override
    public Optional<EffectSpec> getSpec(String alias) {
        alias = alias.toLowerCase();
        for (EffectSpec spec : effectSpecList) {
            if (spec.getAliases().contains(alias)) return Optional.of(spec);
        }
        return Optional.empty();
    }

    private void initDefaults() {
        register(EffectSpec.builder()
                .aliases("debug")
                .effect(c -> {
                    MessageUtil.message(c.getInitiator(),
                            "Triggered %s!", c.getTriggerSpec());
                    c.forTargets(t -> MessageUtil.message(c.getInitiator(), "  - %s", t));
                })
                .build());
        register(EffectSpec.builder()
                .aliases("fly")
                .data(new SpeedData())
                .data(new ExtentData(null))
                .effect(START, c -> {
                    final SpeedData speedData = c.getData(SpeedData.class).get();
                    c.forTargets(t -> t.getPlayer().ifPresent(p -> {
                        p.setAllowFlight(true);
                        p.setVelocity(p.getVelocity().setY(1f));
                        p.setFlySpeed(speedData.getSpeed());
                        c.run(() -> {
                            if (p.getAllowFlight()) p.setFlying(true);
                        });
                    }));
                }).effect(END, c -> c.forTargets(t -> t.getPlayer().ifPresent(p -> {
                    p.setAllowFlight(false);
                    p.setFlying(false);
                }))).build());
        register(EffectSpec.builder()
                .aliases("message", "msg")
                .effect(c -> {
                    final Optional<String> message = c.getData(ConfigData.class).get()
                            .get("message", String.class);
                    message.ifPresent(m -> c.forTargets(
                            t -> t.getPlayer().ifPresent(p -> MessageUtil.message(p, m))));
                }).build());
        register(EffectSpec.builder()
                .aliases("burn")
                .data(new DurationData(20L))
                .effect(c -> {
                    final DurationData data = c.getData(DurationData.class).get();
                    c.forTargets(t -> {
                        t.getEntity().ifPresent(e -> e.setFireTicks((int) data.getDurationTicks()));
                        t.getBlock().ifPresent(b -> {
                            Block b1 = b.getRelative(BlockFace.UP);
                            if (b1.isEmpty()) b1.setType(Material.FIRE);
                        });
                        t.getLocation().ifPresent(l -> l.getWorld()
                                .createExplosion(l.getX(), l.getY(), l.getZ(), 2, true, false));
                    });
                }).build());
        register(EffectSpec.builder()
                .aliases("bound armor")
                .data(new ExtentData(null))
                        // todo add effect with data for getting armor to equip
                .build());
        register(EffectSpec.builder()
                .aliases("bukkit effect", "effect")
                .data(new EffectTypeData(0))
                .effect(c -> {
                    final EffectTypeData data = c.getData(EffectTypeData.class).get();
                    if (data.getEffect() == null) return;
                    c.forTargets(t -> t.getAsLocation().ifPresent(l -> l.getWorld()
                            .playEffect(l, data.getEffect(), data.getEffectData())));
                }).build());
        register(EffectSpec.builder()
                .aliases("disarm")
                .data(new DurationData(40L))
                .effect(c -> {
                    final DurationData data = c.getData(DurationData.class).get();
                    c.forTargets(t -> {
                        t.getPlayer().ifPresent(p -> {
                            final ItemStack item = p.getItemInHand();
                            if (item == null) return;
                            final Item i = p.getWorld()
                                    .dropItemNaturally(p.getLocation(), item);
                            p.setItemInHand(null);
                            i.setPickupDelay((int) data.getDurationTicks());
                        });
                        t.getEntity(Creature.class).ifPresent(e -> {
                            final EntityEquipment equipment = e.getEquipment();
                            final ItemStack item = equipment.getItemInHand();
                            if (item == null) return;
                            final Item i = e.getWorld()
                                    .dropItemNaturally(e.getLocation(), item);
                            equipment.setItemInHand(null);
                            i.setPickupDelay((int) data.getDurationTicks());
                        });
                    });
                }).build());
        register(EffectSpec.builder()
                .aliases("lightning")
                .effect(c -> c.forTargets(t -> t.getAsLocation().ifPresent(l ->
                        l.getWorld().strikeLightning(l)))).build());
        register(EffectSpec.builder()
                .aliases("sound")
                .data(new SoundData())
                .effect(c -> {
                    final SoundData data = c.getData(SoundData.class).get();
                    c.forTargets(t -> t.getPlayer().ifPresent(p ->
                            p.playSound(p.getLocation(), data.getSound(), data.getVolume(),
                                    data.getPitch())));
                }).build());
        register(EffectSpec.builder()
                .aliases("spawn entity")
                .data(new EntityTypeData())
                .effect(c -> {
                    final EntityTypeData data = c.getData(EntityTypeData.class).get();
                    if (!data.getEntityType().isPresent()) return;
                    c.forTargets(t -> t.getAsLocation().ifPresent(l ->
                            l.getWorld().spawnEntity(l, data.getEntityType().get())));
                }).build());
        register(EffectSpec.builder()
                .aliases("explosion")
                .data(new ExplosionData(4f, false, false))
                .effect(c -> {
                    final ExplosionData data = c.getData(ExplosionData.class).get();
                    c.forTargets(t -> t.getAsLocation().ifPresent(l -> l.getWorld()
                            .createExplosion(l.getX(), l.getY(), l.getZ(), data.getPower(),
                                    data.isSetsFire(), data.isBlockDamage())));
                }).build());
        register(EffectSpec.builder()
                .aliases("invisibility")
                .data(new ExtentData(null))
                .effect(START, c -> c.forTargets(t -> t.getPlayer().ifPresent(p -> {
                    for (Player p1 : Bukkit.getOnlinePlayers()) {
                        if (p1 == p) continue;
                        p1.hidePlayer(p);
                    }
                }))).effect(END, c -> c.forTargets(t -> t.getPlayer().ifPresent(p -> {
                    for (Player p1 : Bukkit.getOnlinePlayers()) {
                        if (p1 == p) continue;
                        p1.showPlayer(p);
                    }
                }))).build());
        register(EffectSpec.builder()
                .aliases("modify health", "change health")
                .data(new ModifyHealthData(5)) // +ve damages the entity
                .effect(c -> {
                    final ModifyHealthData data = c.getData(ModifyHealthData.class).get();
                    c.forTargets(t -> t.getEntity(LivingEntity.class).ifPresent(e -> {
                        final double newHealth = e.getHealth() - data.getHealthAmount();
                        e.setHealth(newHealth < 0 ? 0 : newHealth > e.getMaxHealth() ?
                                e.getMaxHealth() : newHealth);
                    }));
                }).build());
        register(EffectSpec.builder()
                .aliases("modify food level", "satiate")
                .data(new ModifyFoodData(20))
                .effect(c -> {
                    final ModifyFoodData data = c.getData(ModifyFoodData.class).get();
                    c.forTargets(t -> t.getPlayer().ifPresent(p -> {
                        final int newFoodLevel = p.getFoodLevel() + data.getFeedAmount();
                        final float saturation = p.getFoodLevel() > newFoodLevel ? p
                                .getSaturation() : p.getFoodLevel();
                        p.setFoodLevel(
                                newFoodLevel > 20 ? 20 : newFoodLevel < 0 ? 0 : newFoodLevel);
                        p.setSaturation(saturation);
                    }));
                }).build());
        register(EffectSpec.builder()
                .aliases("modify walk speed", "speed")
                .data(new ExtentData(null))
                .data(new SpeedData())
                .effect(START, c -> {
                    final SpeedData data = c.getData(SpeedData.class).get();
                    c.forTargets(t -> t.getPlayer().ifPresent(p ->
                            p.setWalkSpeed(data.getSpeed())));
                }).effect(END, c ->
                        c.forTargets(t -> t.getPlayer().ifPresent(p -> p.setWalkSpeed(0.2f))))
                .build());
    }

}
