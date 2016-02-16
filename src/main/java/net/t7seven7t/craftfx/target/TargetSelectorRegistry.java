package net.t7seven7t.craftfx.target;

import com.google.common.collect.ImmutableList;

import net.t7seven7t.craftfx.Registry;
import net.t7seven7t.craftfx.Target;
import net.t7seven7t.craftfx.data.effect.TargetSelectorData;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
public class TargetSelectorRegistry implements Registry<TargetSelectorSpec> {

    private final List<TargetSelectorSpec> targetSelectorList = new ArrayList<>();
    private final TargetSelectorSpec defaultSpec = TargetSelectorSpec.builder().aliases("self")
            .build();

    public TargetSelectorRegistry() {
        initDefaults();
    }

    @Override
    public void register(TargetSelectorSpec spec) {
        for (String alias : spec.getAliases()) {
            if (getSpecOrEmpty(alias).isPresent()) {
                throw new IllegalArgumentException("A TargetSelectorSpec is already registered " +
                        "with the alias " + alias);
            }
        }
        targetSelectorList.add(spec);
    }

    @Override
    public Optional<TargetSelectorSpec> getSpec(String alias) {
        return Optional.of(getSpecOrEmpty(alias).orElse(defaultSpec));
    }

    private Optional<TargetSelectorSpec> getSpecOrEmpty(String alias) {
        alias = alias.toLowerCase();
        for (TargetSelectorSpec spec : targetSelectorList) {
            if (spec.getAliases().contains(alias)) {
                return Optional.of(spec);
            }
        }
        return Optional.empty();
    }

    private void initDefaults() {
        final TargetAdapter toPlayer = c -> c.getOrigin().getPlayer()
                .filter(p -> !p.equals(c.getInitiator()))
                .map(Target::new)
                .map(ImmutableList::of)
                .orElse(ImmutableList.of());
        final TargetAdapter toEntity = c -> {
            final TargetSelectorData data = c.getData(TargetSelectorData.class).get();
            Optional<Entity> optEntity = c.getOrigin().getEntity();
            if (!optEntity.isPresent() || optEntity.get() == c.getInitiator()) {
                return ImmutableList.of();
            } else if (data.getEntityType() == null) {
                return ImmutableList.of(c.getOrigin());
            } else {
                return optEntity.filter(e -> e.getType() == data.getEntityType())
                        .map(Target::new).map(ImmutableList::of).orElse(ImmutableList.of());
            }
        };
        final TargetAdapter toBlock = c -> {
            final Optional<Block> optBlock = c.getOrigin().getAsBlock();
            if (optBlock.isPresent()) return ImmutableList.of(c.getOrigin());
            return ImmutableList.of();
        };
        register(defaultSpec);
        register(TargetSelectorSpec.builder()
                .aliases("player")
                .adapter(toPlayer)
                .build());
        register(TargetSelectorSpec.builder()
                .aliases("entity")
                .adapter(toEntity)
                .build());
        register(TargetSelectorSpec.builder()
                .aliases("block")
                .adapter(toBlock)
                .build());
        register(TargetSelectorSpec.builder()
                .aliases("aoe entity")
                .adapter(c -> {
                    // get entities around the origin
                    final Location origin = c.getOrigin().getAsLocation().orElse(null);
                    if (origin == null) return ImmutableList.of();
                    final TargetSelectorData data = c.getData(TargetSelectorData.class).get();
                    final double radius = data.getAoeRadius();
                    return origin.getWorld().getNearbyEntities(origin, radius, radius, radius)
                            .stream()
                            .sorted(Comparator.comparingDouble(e ->
                                    e.getLocation().distance(origin)))
                            .map(e -> toEntity.apply(new TargetSelectorContext(new Target(e),
                                    c.getInitiator(), c.getDataHolder())))
                            .filter(e -> !e.isEmpty())
                            .limit(data.getLimit())
                            .map(e -> e.get(0))
                            .collect(Collectors.toList());
                }).build());
        register(TargetSelectorSpec.builder()
                .aliases("aoe player")
                .adapter(c -> {
                    final Location origin = c.getOrigin().getAsLocation().orElse(null);
                    if (origin == null) return ImmutableList.of();
                    final TargetSelectorData data = c.getData(TargetSelectorData.class).get();
                    final double radius = data.getAoeRadius();
                    return origin.getWorld().getNearbyEntities(origin, radius, radius, radius)
                            .stream()
                            .filter(e -> e instanceof Player)
                            .sorted(Comparator.comparingDouble(p ->
                                    p.getLocation().distance(origin)))
                            .map(p -> toPlayer.apply(new TargetSelectorContext(new Target(p),
                                    c.getInitiator(), c.getDataHolder())))
                            .filter(p -> !p.isEmpty())
                            .limit(data.getLimit())
                            .map(l -> l.get(0))
                            .collect(Collectors.toList());
                }).build());
    }
}
