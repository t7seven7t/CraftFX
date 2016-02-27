package net.t7seven7t.craftfx.data;

import java.util.Optional;

/**
 *
 */
public interface Data {

    /**
     * Get a property for this Data
     *
     * @param propertyName the property name
     * @param clazz        class of the property type to cast to
     * @param <T>          type of the property
     * @return the property value encapsulated in an Optional or otherwise {@link Optional#empty()}
     */
    <T> Optional<T> get(String propertyName, Class<T> clazz);

    /**
     * Get a property for this Data
     *
     * @param propertyName the property name
     * @param clazz        class of the property type to cast to
     * @param def          the default value to return
     * @param <T>          type of the property
     * @return the property value encapsulated in an Optional or otherwise {@link Optional#empty()}
     */
    <T> T get(String propertyName, Class<T> clazz, T def);

    /**
     * Set the DataHolder that this Data belongs to
     *
     * @param holder data holder
     */
    void setHolder(DataHolder holder);

    /**
     * Creates a new instance of the Data object with identical internal values to the original.
     *
     * @return a copy of this Data object
     */
    Data copy();

    /**
     * Called when the setDataHolder is called with a holder that is non null so that child
     * instances have a chance to reinitialize their internal state.
     */
    void onDataHolderUpdate();
}
