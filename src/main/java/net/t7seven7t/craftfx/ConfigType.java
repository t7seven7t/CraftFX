package net.t7seven7t.craftfx;

import com.google.common.collect.ImmutableList;

import net.t7seven7t.util.Configs;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ConfigType {

    private static final List<Configs.ConfigDescription> CONFIGS = new ArrayList<>();
    public static final Configs.ConfigDescription DEFAULT = config("config");
    public static final Configs.ConfigDescription MESSAGES = config("messages");

    private static Configs.ConfigDescription config(String name) {
        Configs.ConfigDescription desc = new Configs.ConfigDescription(CraftFX.plugin(), name);
        CONFIGS.add(desc);
        return desc;
    }

    public static List<Configs.ConfigDescription> values() {
        return ImmutableList.copyOf(CONFIGS);
    }

}
