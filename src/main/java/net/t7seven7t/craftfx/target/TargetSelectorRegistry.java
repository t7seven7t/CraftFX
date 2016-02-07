package net.t7seven7t.craftfx.target;

import com.google.common.collect.ImmutableList;

import net.t7seven7t.craftfx.Registry;
import net.t7seven7t.craftfx.Target;
import net.t7seven7t.craftfx.data.effect.TargetSelectorData;

import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            if (!optEntity.isPresent()) {
                return ImmutableList.of();
            } else if (data.getEntityType() == null) {
                return ImmutableList.of(c.getOrigin());
            } else {
                return optEntity.filter(e -> e.getType() == data.getEntityType())
                        .map(Target::new).map(ImmutableList::of).orElse(ImmutableList.of());
            }
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
    }
}
