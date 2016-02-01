package net.t7seven7t.craftfx.util;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.util.EnumUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.ChatColor.BOLD;
import static net.md_5.bungee.api.ChatColor.ITALIC;
import static net.md_5.bungee.api.ChatColor.MAGIC;
import static net.md_5.bungee.api.ChatColor.RESET;
import static net.md_5.bungee.api.ChatColor.STRIKETHROUGH;
import static net.md_5.bungee.api.ChatColor.UNDERLINE;
import static net.md_5.bungee.api.ChatColor.getByChar;
import static net.md_5.bungee.api.ChatColor.translateAlternateColorCodes;

/**
 * Utility for sending and formatting of messages
 */
public class MessageUtil {

    private static final Pattern ACTION_SELECTOR_PATTERN = Pattern
            .compile("(?:\\[([^\\[\\]]+)\\])");
    private static final Pattern SPLIT_ACTION_PATTERN = Pattern.compile("([^|]+)");
    private static final Pattern SPLIT_FORMAT_PATTERN = Pattern.compile("([^&\\u00A7]+)");
    private static final Pattern REPLACER_PATTERN = Pattern.compile("([^%]*)%([\\w]+)%");

    public static void message(CommandSender sender, String message, Object... args) {
        ConfigurationSection messages = CraftFX.instance().getMessages(Locale.ENGLISH);
        if (messages.contains(message)) {
            String key = message;
            message = messages.getString(key);
            if (!message.equalsIgnoreCase(key)) {
                message(sender, message, args);
                return;
            }
        }

        message = replace(message, sender);

        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(translateText(String.format(message, args)));
        } else {
            sender.sendMessage(color(String.format(message, args)));
        }
    }

    public static String color(String message) {
        return translateAlternateColorCodes('&', message);
    }

    public static BaseComponent translateText(String message) {
        List<BaseComponent[]> hoverText = null;
        List<BaseComponent[]> clickText = null;

        Matcher m = ACTION_SELECTOR_PATTERN.matcher(message);
        while (m.find()) {
            Matcher m1 = SPLIT_ACTION_PATTERN.matcher(m.group(1));
            List<String> split = Lists.newArrayList();
            while (m1.find()) split.add(m1.group(1));
            if (split.size() != 4) continue;
            if (split.get(0).equalsIgnoreCase("hover")) {
                HoverEvent.Action action = EnumUtil.matchEnumValue(HoverEvent.Action.class,
                        split.get(1));
                BaseComponent[] components = TextComponent.fromLegacyText(color(split.get(2)));
                fixLegacyFormat(components);
                HoverEvent event = new HoverEvent(action, components);
                components = TextComponent.fromLegacyText(color(split.get(3)));
                for (BaseComponent c : components) c.setHoverEvent(event);
                if (hoverText == null) hoverText = Lists.newArrayList();
                hoverText.add(components);
                message = message.replace(m.group(0), "%hover%");
            } else if (split.get(0).equalsIgnoreCase("click")) {
                ClickEvent.Action action = EnumUtil
                        .matchEnumValue(ClickEvent.Action.class, split.get(1));
                if (clickText == null) clickText = Lists.newArrayList();
                ClickEvent event = new ClickEvent(action, split.get(2));
                BaseComponent[] components = TextComponent.fromLegacyText(color(split.get(3)));
                for (BaseComponent c : components) c.setClickEvent(event);
                clickText.add(components);
                message = message.replace(m.group(0), "%click%");
            }
        }

        List<BaseComponent> components = Lists.newArrayList();
        m = SPLIT_FORMAT_PATTERN.matcher(message);
        BaseComponent c;
        components.add(new TextComponent(""));
        List<ChatColor> formatting = new ArrayList<>();
        while (m.find()) {
            String s = m.group(1);
            ChatColor color = null;
            if (s.length() > 0) {
                char format = s.charAt(0);
                color = getByChar(format);
                if (color != null) {
                    s = s.substring(1);
                    addFormat(formatting, color);
                }
            }

            int end = 0;
            Matcher m1 = REPLACER_PATTERN.matcher(s);
            while (m1.find()) {
                c = new TextComponent(m1.group(1));
                if (color != null) format(c, formatting);
                last(components).addExtra(c);
                components.add(c);

                if (m1.group(2).equals("hover") && hoverText != null && !hoverText.isEmpty()) {
                    for (BaseComponent b : hoverText.get(0)) {
                        if (color != null) format(b, formatting);
                        last(components).addExtra(b);
                        components.add(b);
                    }
                    hoverText.remove(0);
                } else if (m1.group(2).equals("click") && clickText != null && !clickText
                        .isEmpty()) {
                    for (BaseComponent b : clickText.get(0)) {
                        if (color != null) format(b, formatting);
                        last(components).addExtra(b);
                        components.add(b);
                    }
                    clickText.remove(0);
                }
                end = m1.end();
            }

            if (m1.groupCount() > 1) {
                s = s.substring(Math.min(s.length(), end));
                if (s.length() > 0) {
                    c = new TextComponent(s);
                    if (color != null) format(c, formatting);
                    last(components).addExtra(c);
                    components.add(c);
                    c.setHoverEvent(null);
                    c.setClickEvent(null);
                }
            }
        }
        return components.get(0);
    }

    private static void addFormat(List<ChatColor> formatting, ChatColor format) {
        boolean contained = formatting.remove(format);
        if (formatting.stream().filter(MessageUtil::isColor).findAny().isPresent()
                || (!isColor(format) && contained)) {
            formatting.clear();
        }
        formatting.add(format);
    }

    private static boolean isColor(ChatColor format) {
        switch (format) {
            case BOLD:
            case STRIKETHROUGH:
            case ITALIC:
            case MAGIC:
            case RESET:
            case UNDERLINE:
                return false;
            default:
                return true;
        }
    }

    public static String translate(CommandSender recipient, String key) {
        if (key == null) return "undefined";
        ConfigurationSection messages = CraftFX.instance().getMessages(Locale.ENGLISH);
        // todo: Could add support for multiple languages based on sender's geolocation/language prefs
        // todo: support for null recipient ^
        if (messages.contains(key)) {
            return messages.getString(key);
        }
        return key;
    }

    public static String format(String pattern, Object... arguments) {
        return color(MessageFormat.format(pattern, arguments));
    }

    private static void format(BaseComponent component, List<ChatColor> formatting) {
        for (ChatColor format : formatting) {
            if (isColor(format) || format.equals(RESET)) {
                component.setColor(format);
            }
        }
        component.setBold(formatting.contains(BOLD));
        component.setItalic(formatting.contains(ITALIC));
        component.setObfuscated(formatting.contains(MAGIC));
        component.setStrikethrough(formatting.contains(STRIKETHROUGH));
        component.setUnderlined(formatting.contains(UNDERLINE));
    }

    private static <T> T last(List<T> list) {
        return list.get(list.size() - 1);
    }

    private static void fixLegacyFormat(BaseComponent[] components) {
        Arrays.stream(components).forEach(c -> {
            Optional.of(c.isBoldRaw() != null).ifPresent(c::setBold);
            Optional.of(c.isItalicRaw() != null).ifPresent(c::setItalic);
            Optional.of(c.isObfuscatedRaw() != null).ifPresent(c::setObfuscated);
            Optional.of(c.isStrikethroughRaw() != null).ifPresent(c::setStrikethrough);
            Optional.of(c.isUnderlinedRaw() != null).ifPresent(c::setUnderlined);
        });
    }

    public static String replace(String message, CommandSender sender) {
        Matcher m = REPLACER_PATTERN.matcher(message);
        String result = "";
        int end = 0;
        while (m.find()) {
            result += m.group(1);
            if (m.group(2).equalsIgnoreCase("name")) result += sender.getName();
            end = m.end();
        }
        return result + message.substring(end);
    }

}
