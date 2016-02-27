package net.t7seven7t.craftfx.data;

import java.util.Optional;

/**
 *
 */
public interface DataHolder extends DataInterface {

    /**
     * Offer a Data type to this DataHolder. If a Data with the same class already exists it will be
     * replaced.
     *
     * @param data data
     */
    void offer(Data data);

    /**
     * Get a property from this DataHolder
     *
     * @param propertyName the property name
     * @param clazz        class of the property type to cast to
     * @param <T>          type of the property
     * @return the property value encapsulated in an Optional or otherwise {@link Optional#empty()}
     */
    <T> Optional<T> get(String propertyName, Class<T> clazz);

    /**
     * Sets a property for this DataHolder
     *
     * @param propertyName the property name
     * @param value        new value of the property
     */
    void set(String propertyName, Object value);

}
