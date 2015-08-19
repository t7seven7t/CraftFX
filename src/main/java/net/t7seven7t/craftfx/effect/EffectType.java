package net.t7seven7t.craftfx.effect;

import com.google.common.collect.MapMaker;

import net.t7seven7t.craftfx.CraftFX;

import java.io.File;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Represents a type of Effect. Also aids in creating new Effect instances on the fly by use of
 * factory methods.
 */
public class EffectType {

    /**
     * Map of names to their effect type for by-name searches
     */
    private static final Map<String, EffectType> effectTypes = new MapMaker().makeMap();
    /**
     * Factory method for creating new Effect instances
     */
    final Supplier<? extends Effect> supplier;
    /**
     * Effect class of this EffectType
     */
    private final Class<? extends Effect> clazz;

    /**
     * @param supplier Factory method for creating a new effect
     */
    private EffectType(Supplier<? extends Effect> supplier) {
        this.supplier = supplier;
        this.clazz = supplier.get().getClass();
    }

    /**
     * @param supplier Factory method for creating a new effect
     */
    public static void registerEffectType(Supplier<Effect> supplier) {
        registerEffectType(supplier, supplier.get().getClass().getSimpleName());
    }

    /**
     * @param supplier Factory method for creating a new effect
     * @param niceName Nice name to register the effect type by
     */
    public static void registerEffectType(Supplier<Effect> supplier, String niceName) {
        EffectType type = new EffectType(supplier);
        effectTypes.put(niceName, type);
    }

    /**
     * Gets the effect type with this name
     *
     * @param name name of effect type
     * @return EffectType if matched otherwise null
     */
    public static EffectType get(String name) {
        return effectTypes.get(name.toUpperCase().replaceAll("\\s+", "_").replaceAll("\\W", ""));
    }

    /**
     * Loads effects from a directory
     *
     * @param directory Directory to find effects in
     */
    static void loadEffectTypes(File directory) {
        // TODO: Load effects from files
    }

    /**
     * Registers default effect types that are packaged with CraftFX
     */
    static void loadDefaults() {
        // TODO: add all pre-defined effect types here
    }

    /**
     * Register default and custom Effects
     */
    public static void initialize() {
        loadDefaults();

        File directory = new File(CraftFX.getInstance().getDataFolder(), "effects");
        if (!directory.exists()) {
            directory.mkdir();
        }

        loadEffectTypes(directory);
    }

    /**
     * Get the class controlling this type of effect
     */
    @SuppressWarnings("unused")
    public Class<? extends Effect> getEffectClass() {
        return clazz;
    }

}
