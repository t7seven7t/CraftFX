package net.t7seven7t.craftfx.data;

import java.util.Optional;

/**
 *
 */
public interface DataInterface {

    /**
     * Get a Data type from this DataInterface.
     *
     * @param clazz the data type's class
     * @return a data type encapsulated within an {@link Optional} or otherwise {@link
     * Optional#empty()}
     */
    <T extends Data> Optional<T> getData(Class<T> clazz);

}
