package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.data.ConfigData;
import net.t7seven7t.craftfx.data.effect.ExtentData;
import net.t7seven7t.craftfx.data.effect.SpeedData;
import net.t7seven7t.craftfx.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.t7seven7t.craftfx.effect.ExtentState.END;
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
                .effect(c -> MessageUtil.message(c.getInitiator(),
                        "Triggered %s!", c.getTriggerSpec()))
                .build());
        register(EffectSpec.builder()
                .aliases("fly")
                .data(new SpeedData())
                .data(new ExtentData(null))
                .effect(START, c -> {
                    // todo: make these rely on target selection
                    c.getInitiator().setAllowFlight(true);
                    c.getInitiator().setVelocity(c.getInitiator().getVelocity().setY(1f));
                    c.getInitiator().setFlySpeed(c.getData(SpeedData.class).get().getSpeed());
                    c.run(() -> {
                        if (c.getInitiator().getAllowFlight()) c.getInitiator().setFlying(true);
                    });
                }).effect(END, c -> {
                    c.getInitiator().setAllowFlight(false);
                    c.getInitiator().setFlying(false);
                }).build());
        register(EffectSpec.builder()
                .aliases("message", "msg")
                .effect(c -> {
                    // todo: make this rely on target selection
                    Optional<String> message = c.getData(ConfigData.class).get()
                            .get("message", String.class);
                    message.ifPresent(m -> MessageUtil.message(c.getInitiator(), m));
                }).build());
    }

}
