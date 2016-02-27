package net.t7seven7t.craftfx.data;

import com.google.common.collect.MapMaker;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Optional;

/**
 *
 */
public class ConfigDataHolder implements DataHolder {

    private final ConfigurationSection config;
    private final Map<Class<? extends Data>, Data> dataMap = new MapMaker().makeMap();

    public ConfigDataHolder(ConfigurationSection config) {
        this.config = config;
    }

    @Override
    public void offer(Data data) {
        dataMap.put(data.getClass(), data);
        data.setHolder(this);
    }

    @Override
    public <T extends Data> Optional<T> getData(Class<T> clazz) {
        return Optional.ofNullable((T) dataMap.get(clazz));
    }

    @Override
    public <T> Optional<T> get(String propertyName, Class<T> clazz) {
        Object o = config.get(propertyName);
        return clazz.isInstance(o) ? Optional.of((T) o) : Optional.<T>empty();
    }

    /**
     * Sets a property for this DataHolder
     *
     * @param propertyName the property name
     * @param value        new value of the property
     */
    @Override
    public void set(String propertyName, Object value) {
        config.set(propertyName, value);
        dataMap.values().forEach(Data::onDataHolderUpdate);
    }

    public ConfigurationSection getConfig() {
        return config;
    }
}
