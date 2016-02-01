package net.t7seven7t.craftfx.command;

import com.google.common.base.Joiner;

import com.sk89q.intake.Command;
import com.sk89q.intake.parametric.annotation.Text;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.item.ItemDefinition;
import net.t7seven7t.util.intake.module.annotation.Sender;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.t7seven7t.craftfx.util.MessageUtil.message;

/**
 *
 */
public class AdminCommands {

    @Command(
            aliases = {"give", "g", "i"},
            desc = "command-give-desc",
            usage = "command-give-usage"
    )
    public void giveItem(CraftFX fx, @Sender Player player, @Text String name) {
        Optional<ItemDefinition> opt = fx.getItemRegistry().matchDefinition(name.trim());
        if (opt.isPresent()) {
            player.getInventory().addItem(opt.get().getItem());
        } else {
            message(player, "No comprende");
        }
    }

    @Command(
            aliases = {"list", "li"},
            desc = "command-list-desc",
            usage = "command-list-usage"
    )
    public void listItems(CraftFX fx, CommandSender sender) {
        List<ItemDefinition> items = fx.getItemRegistry().getItemDefinitions();
        message(sender, Joiner.on(", ").join(items.stream()
                .map(ItemDefinition::getName).collect(Collectors.toList())));
    }

}
