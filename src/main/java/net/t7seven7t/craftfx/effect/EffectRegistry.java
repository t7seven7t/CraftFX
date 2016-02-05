package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.t7seven7t.craftfx.effect.ExtentState.START;

/**
 *
 */
public class EffectRegistry {

    /**
     * List of all effect specs
     */
    private final List<EffectSpec> effectSpecList = new ArrayList<>();

    public EffectRegistry() {
        initDefaults();
    }

    public void register(EffectSpec spec) {
        for (String alias : spec.getAliases()) {
            if (getSpec(alias).isPresent()) {
                throw new IllegalArgumentException("An EffectSpec is already registered " +
                        "with the alias " + alias);
            }
        }
        effectSpecList.add(spec);
    }

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
                .effect(START, c -> MessageUtil.message(c.getInitiator(),
                        "Triggered %s!", c.getTriggerSpec()))
                .build());
    }

}
