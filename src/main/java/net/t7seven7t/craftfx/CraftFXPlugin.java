package net.t7seven7t.craftfx;

import net.t7seven7t.craftfx.command.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 *
 */
public class CraftFXPlugin extends JavaPlugin {

    private CraftFX fx;
    private Commands commands;

    @Override
    public void onEnable() {
        fx = new CraftFX(this);
        commands = new Commands();
    }

    @Override
    public void onDisable() {
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
                                      String[] args) {
        return commands.onTabComplete(sender, command, alias, args);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commands.onCommand(sender, command, label, args);
    }
}
