package net.t7seven7t.craftfx.command.provider;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.ArgumentParseException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.item.ItemDefinition;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 *
 */
public class ItemDefinitionProvider implements Provider<ItemDefinition> {
    @Override
    public boolean isProvided() {
        return false;
    }

    @Nullable
    @Override
    public ItemDefinition get(CommandArgs arguments,
                              List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
        final String argument = arguments.next();
        final Optional<ItemDefinition> opt = CraftFX.instance().getItemRegistry().matchDefinition(
                argument);
        if (!opt.isPresent()) {
            throw new ArgumentParseException(
                    String.format("No items by the name of '%s' are known.",
                            argument));
        }
        return opt.get();
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        final String lower = prefix.toLowerCase();
        return CraftFX.instance().getItemRegistry().getItemDefinitions().stream()
                .filter(i -> i.getName().startsWith(lower))
                .map(ItemDefinition::getName)
                .collect(Collectors.toList());
    }
}
