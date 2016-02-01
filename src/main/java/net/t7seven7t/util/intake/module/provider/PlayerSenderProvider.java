package net.t7seven7t.util.intake.module.provider;

import com.google.common.collect.ImmutableList;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.ArgumentParseException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 *
 */
public class PlayerSenderProvider implements Provider<Player> {

    @Override
    public boolean isProvided() {
        return true;
    }

    @Override
    public Player get(CommandArgs arguments,
                      List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
        if (Player.class.isInstance(arguments.getNamespace().get(CommandSender.class))) {
            return (Player) arguments.getNamespace().get(CommandSender.class);
        }

        throw new ArgumentParseException("You must be a player to perform this command.");
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        return ImmutableList.of();
    }
}
