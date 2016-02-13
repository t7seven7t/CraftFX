package net.t7seven7t.craftfx.command;

import com.google.common.base.Joiner;

import com.sk89q.intake.Command;
import com.sk89q.intake.Require;
import com.sk89q.intake.parametric.annotation.Optional;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.item.ItemDefinition;
import net.t7seven7t.util.intake.module.annotation.Sender;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
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
        final List<ItemDefinition> items = fx.getItemRegistry().getItemDefinitions();
        if (sender instanceof Player) {
            final List<BaseComponent> components = new ArrayList<>();
            final TextComponent c = new TextComponent("");
            components.add(c);
            final Iterator<ItemDefinition> it = items.iterator();
            while (it.hasNext()) {
                final ItemDefinition item = it.next();
                final TextComponent component = new TextComponent(item.getName());
                final HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                        new BaseComponent[]{
                                new TextComponent(fx.getNmsInterface().itemToJson(item.getItem()))
                        });
                component.setHoverEvent(hoverEvent);
                if (sender.hasPermission("craftfx.command.item")) {
                    final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                            "/fx item " + item.getName() + " ");
                    component.setClickEvent(clickEvent);
                }
                component.setColor(ChatColor.YELLOW);
                components.add(component);
                if (it.hasNext()) {
                    components.add(new TextComponent(", "));
                }
            }
            ((Player) sender).spigot().sendMessage(components.toArray(new BaseComponent[0]));
        } else {
            message(sender, "&e%s", Joiner.on(", ").join(items.stream()
                    .map(ItemDefinition::getName)
                    .collect(Collectors.toList())));
        }
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
