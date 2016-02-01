package net.t7seven7t.util.intake.module.provider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.ArgumentParseException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.argument.MissingArgumentException;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
public class PlayerArgumentProvider<T> implements Provider<T> {
    private static final List<String> TARGET_SELECTORS = ImmutableList.of("@p", "@r", "@a");
    private static final Pattern SELECTOR_PARAMS_CAPTURE = Pattern
            .compile("^@[pare](?:\\[([\\w=,!-]*)\\])?$");
    private static final Pattern SELECTOR_PARAMS_UNNAMED = Pattern
            .compile("(\\d+),(\\d+),(\\d+),(\\d+)");
    private static final Pattern SELECTOR_PARAMS_SEPARATOR = Pattern
            .compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");
    private static final Random RANDOM = new Random();

    private final PlayerListConverter<T> playerListConverter;

    public PlayerArgumentProvider(PlayerListConverter<T> playerListConverter) {
        this.playerListConverter = playerListConverter;
    }

    @Override
    public boolean isProvided() {
        return false;
    }

    @Override
    public T get(CommandArgs arguments,
                 List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
        Namespace namespace = arguments.getNamespace();

        try {
            String argument = arguments.next();
            return playerListConverter
                    .convert(getPlayerList(argument, arguments.getNamespace()));
        } catch (MissingArgumentException e) {
            if (namespace.get(CommandSender.class) instanceof Player) {
                return playerListConverter.convert(ImmutableList.of((Player)
                        namespace.get(CommandSender.class)));
            }

            throw new ArgumentParseException("You must be a player to perform this command.");
        }
    }

    private List<Player> getPlayerList(String argument,
                                       Namespace namespace) throws ArgumentException, ProvisionException {
        CommandSender commandSender = namespace.get(CommandSender.class);
        Player sender = (commandSender instanceof Player) ? (Player) commandSender : null;
        Location from = sender != null ? sender.getLocation() :
                Bukkit.getWorlds().get(0).getSpawnLocation();

        if (argument.startsWith("@p")) {
            if (sender == null) {
                throw new ArgumentParseException("You must be a player to use the @p selector.");
            }
            return ImmutableList.of(getNearestPlayer(sender, getPlayers(from, argument)));
        }

        if (argument.startsWith("@r")) {
            List<Player> players = getPlayers(from, argument);
            int position = RANDOM.nextInt(players.size());
            for (Player player : players) if (--position < 0) return ImmutableList.of(player);
            throw new ArgumentParseException("Something went wrong.");
        }

        if (argument.startsWith("@a") || argument.equals("*")) {
            return ImmutableList.copyOf(getPlayers(from, argument));
        }

        Player player = Bukkit.getPlayer(argument);
        if (player != null) return ImmutableList.of(player);

        throw new ArgumentParseException(
                String.format("No players by the name of '%s' is known.", argument));
    }

    private List<Player> getPlayers(Location from, String selector) {
        Map<String, Integer> selectorParams = getSelectorParams(selector);
        List<Player> result = Lists.newArrayList();
        Location loc = from.clone();

        Integer value;
        if ((value = selectorParams.get("x")) != null) {
            from.setX(value);
        }

        if ((value = selectorParams.get("y")) != null) {
            from.setY(value);
        }

        if ((value = selectorParams.get("z")) != null) {
            from.setZ(value);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getLocation(loc);

            // Radii
            if ((value = selectorParams.get("r")) != null) {
                if (value * value < from.distanceSquared(loc)) continue;
            }
            if ((value = selectorParams.get("rm")) != null) {
                if (value * value > from.distanceSquared(loc)) continue;
            }

            // Game mode
            if ((value = selectorParams.get("m")) != null) {
                if (value != -1 && player.getGameMode().ordinal() != value) continue;
            }

            // Player xp level
            if ((value = selectorParams.get("l")) != null) {
                if (player.getLevel() > value) continue;
            }
            if ((value = selectorParams.get("lm")) != null) {
                if (player.getLevel() < value) continue;
            }

            // Whether within distance from original location
            if ((value = selectorParams.get("dx")) != null) {
                if (!from.getWorld().equals(player.getWorld()) || loc.getX() < from.getX() || loc
                        .getX() > from.getX() + value) continue;
            }
            if ((value = selectorParams.get("dy")) != null) {
                if (!from.getWorld().equals(player.getWorld()) || loc.getY() < from.getY() || loc
                        .getY() > from.getY() + value) continue;
            }
            if ((value = selectorParams.get("dz")) != null) {
                if (!from.getWorld().equals(player.getWorld()) || loc.getZ() < from.getZ() || loc
                        .getZ() > from.getZ() + value) continue;
            }

            result.add(player);
        }
        return result;
    }

    private Map<String, Integer> getSelectorParams(String selector) {
        Matcher m = SELECTOR_PARAMS_CAPTURE.matcher(selector);
        Map<String, Integer> result = Maps.newHashMap();

        if (m.find() && m.group(1) != null) {
            String args = m.group(1);

            m = SELECTOR_PARAMS_UNNAMED.matcher(args);
            if (m.find()) {
                result.put("x", NumberConversions.toInt(m.group(1)));
                result.put("y", NumberConversions.toInt(m.group(2)));
                result.put("z", NumberConversions.toInt(m.group(3)));
                result.put("r", NumberConversions.toInt(m.group(4)));
                return result;
            }

            m = SELECTOR_PARAMS_SEPARATOR.matcher(args);
            while (m.find()) {
                result.put(m.group(1), NumberConversions.toInt(m.group(2)));
            }
        }

        return result;
    }

    private Player getNearestPlayer(Player sender, List<Player> players) {
        Player nearest = null;
        double minimum = Double.MAX_VALUE;
        double distsquared;
        for (Player player : players) {
            if (sender == player || !sender.getWorld().equals(player.getWorld())) {
                continue;
            }

            distsquared = sender.getLocation().distanceSquared(player.getLocation());
            if (distsquared < minimum) {
                minimum = distsquared;
                nearest = player;
            }
        }
        if (nearest == null) {
            nearest = sender;
        }
        return nearest;
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        if (prefix.startsWith("@")) {
            return TARGET_SELECTORS;
        }

        return Bukkit.getOnlinePlayers().stream().filter(
                player -> player.getName().startsWith(prefix)).map(Player::getName)
                .collect(Collectors.toList());
    }

    @FunctionalInterface
    public interface PlayerListConverter<T> {
        T convert(List<Player> playerList);
    }
}
