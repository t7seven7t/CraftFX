package net.t7seven7t.craftfx.effect;

import com.google.common.collect.Lists;

import net.t7seven7t.craftfx.effect.data.DelayData;
import net.t7seven7t.craftfx.effect.data.TimerData;
import net.t7seven7t.craftfx.item.ItemConfigurationException;
import net.t7seven7t.craftfx.item.ItemDefinition;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * Class encloses a process to be run when triggered.
 */
public abstract class Effect {

    /**
     * Configuration information
     */
    private ConfigurationSection config;
    /**
     * EffectType for classification of this effect
     */
    private EffectType type;
    /**
     * ItemDefinition that triggers this effect
     */
    private ItemDefinition item;
    /**
     * List of data and controls that affect how this effect functions
     */
    private final List<EffectData> effectDataList = Lists.newArrayList();

    /**
     * Empty constructor for factory
     */
    Effect() {
    }

    /**
     * Initializes the effect immediately after it is created.
     */
    final void initialize(EffectType type, ItemDefinition item,
                          ConfigurationSection config) throws ItemConfigurationException {
        this.config = config;
        this.type = type;
        this.item = item;
        // for extent effect's data created in constructor
        effectDataList.forEach(d -> d.init(this));

        // Add possible data types for all Effects:
        addData(new DelayData());
        addData(new TimerData());

        initialize();
    }

    /**
     * Initialize the effect in here when extending this class.
     *
     * @throws ItemConfigurationException if there is a problem with the configuration
     */
    public void initialize() throws ItemConfigurationException {
    }

    /**
     * Runs this run spec. Returns true IFF the effect was run on the specified parameters.
     *
     * @param runSpec RunSpecification to operate
     * @return true if effect was run, otherwise false
     */
    public abstract boolean run(RunSpecification runSpec);

    /**
     * Gets the effect data matching this class if this effect has it
     *
     * @param clazz Class of the EFfectData
     */
    public <T extends EffectData> T getData(Class<T> clazz) {
        return (T) effectDataList.stream().filter(d -> d.getClass().equals(clazz)).findAny()
                .orElse(null);
    }

    /**
     * Adds the specified EffectData to this instance. If an EffectData of the same type already
     * exists it will be removed.
     */
    public void addData(EffectData data) {
        EffectData existing = getData(data.getClass());
        if (existing != null) {
            effectDataList.remove(existing);
        }

        effectDataList.add(data);
        if (config != null) {
            data.init(this);
        }
    }

    /**
     * Get the configuration information for this effect
     */
    public ConfigurationSection getConfig() {
        return config;
    }

    /**
     * Get the EffectType
     */
    public EffectType getType() {
        return type;
    }

    /**
     * Get the item definition
     */
    public ItemDefinition getItem() {
        return item;
    }
}
