package net.t7seven7t.craftfx.target;

import com.google.common.collect.ImmutableList;

import net.t7seven7t.craftfx.Target;
import net.t7seven7t.craftfx.effect.Effect;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class TargetSelectorSpec {

    private final List<String> aliases = new ArrayList<>();
    private TargetAdapter targetAdapter = c -> ImmutableList.of(new Target(c.getInitiator()));

    private TargetSelectorSpec() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<String> getAliases() {
        return ImmutableList.copyOf(aliases);
    }

    public TargetSelector newTargetSelector(Target origin, Player initiator, Effect effect) {
        if (!origin.getBlock().isPresent() && !origin.getEntity().isPresent()
                && !origin.getLocation().isPresent()) {
            origin = new Target(initiator);
        }
        return new TargetSelector(targetAdapter,
                new TargetSelectorContext(origin, initiator, effect));
    }

    public static final class Builder {
        private final TargetSelectorSpec spec;

        public Builder() {
            this.spec = new TargetSelectorSpec();
        }

        public Builder adapter(TargetAdapter targetAdapter) {
            spec.targetAdapter = targetAdapter;
            return Builder.this;
        }

        public Builder aliases(String... aliases) {
            for (String alias : aliases) {
                spec.aliases.add(alias.toLowerCase());
            }
            return Builder.this;
        }

        public TargetSelectorSpec build() {
            return spec;
        }
    }

}
