package net.t7seven7t.util.intake.module;

import com.google.common.reflect.TypeToken;

import com.sk89q.intake.parametric.AbstractModule;
import com.sk89q.intake.parametric.Key;

import net.t7seven7t.util.intake.module.annotation.Milliseconds;
import net.t7seven7t.util.intake.module.annotation.Sender;
import net.t7seven7t.util.intake.module.annotation.Ticks;
import net.t7seven7t.util.intake.module.provider.CommandSenderProvider;
import net.t7seven7t.util.intake.module.provider.PlayerArgumentProvider;
import net.t7seven7t.util.intake.module.provider.PlayerSenderProvider;
import net.t7seven7t.util.intake.module.provider.TimeProvider;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 *
 */
public class BukkitModule extends AbstractModule {
    private static final Key<List<Player>> PLAYER_KEY = Key.get(new TypeToken<List<Player>>() {
    }.getType());

    @Override
    public void configure() {
        bind(PLAYER_KEY).toProvider(new PlayerArgumentProvider<>(list -> list));
        // list size always >= 1; see PlayerArgumentProvider spec
        bind(Player.class).toProvider(new PlayerArgumentProvider<>(list -> list.get(0)));
        bind(Player.class).annotatedWith(Sender.class).toProvider(new PlayerSenderProvider());
        bind(CommandSender.class).toProvider(new CommandSenderProvider());
        bind(Long.class).annotatedWith(Ticks.class)
                .toProvider(new TimeProvider(millis -> millis / 50L));
        bind(Long.class).annotatedWith(Milliseconds.class)
                .toProvider(new TimeProvider(millis -> millis));
    }
}
