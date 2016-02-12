package net.t7seven7t.craftfx.command;

import com.google.common.base.Joiner;

import com.sk89q.intake.Command;
import com.sk89q.intake.Require;
import com.sk89q.intake.parametric.annotation.Optional;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.item.ItemDefinition;
import net.t7seven7t.util.intake.module.annotation.Sender;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

import static net.t7seven7t.craftfx.util.MessageUtil.message;

/**
 *
 */
public class AdminCommands {

    @Command(
            aliases = {"give", "g"},
            desc = "command-give-desc",
            usage = "command-give-usage"
    )
    @Require("craftfx.command.give")
    public void give(List<Player> players, ItemDefinition item, @Optional("1") int amount) {
        final ItemStack i = item.getItem();
        i.setAmount(amount);
        for (Player p : players) {
            p.getInventory().addItem(i);
        }
    }

    @Command(
            aliases = {"item", "i"},
            desc = "command-item-desc",
            usage = "command-item-usage"
    )
    @Require("craftfx.command.item")
    public void item(@Sender Player sender, ItemDefinition item,
                     @Optional("1") int amount) {
        final ItemStack i = item.getItem();
        i.setAmount(amount);
        sender.getInventory().addItem(i);
    }

    @Command(
            aliases = {"list", "li"},
            desc = "command-list-desc",
            usage = "command-list-usage"
    )
    @Require("craftfx.command.list")
    public void list(CraftFX fx, CommandSender sender) {
        List<ItemDefinition> items = fx.getItemRegistry().getItemDefinitions();
        message(sender, "&e%s", Joiner.on(", ").join(items.stream()
                .map(ItemDefinition::getName).collect(Collectors.toList())));
    }

    @Command(
            aliases = {"reload"},
            desc = "command-reload-desc",
            usage = "command-reload-usage"
    )
    @Require("craftfx.command.reload")
    public void reload(CraftFX fx, CommandSender sender) {
        fx.reload();
        message(sender, "&a%s reloaded!", fx.plugin().getDescription().getFullName());
    }

}
