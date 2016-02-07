package net.t7seven7t.craftfx.effect;

import net.t7seven7t.craftfx.Registry;
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
    }

}
