package net.t7seven7t.craftfx.data;

import java.util.Optional;

/**
 *
 */
public abstract class AbstractData implements Data {
    private Optional<DataHolder> holder = Optional.empty();

    @Override
    public <T> Optional<T> get(String propertyName, Class<T> clazz) {
        return holder.map(h -> h.get(propertyName, clazz)).orElse(Optional.empty());
    }

    @Override
    public <T> T get(String propertyName, Class<T> clazz, T def) {
        return get(propertyName, clazz).orElse(def);
    }

    @Override
    public void setHolder(DataHolder holder) {
        this.holder = Optional.ofNullable(holder);
    }

    @Override
    public Data copy() {
        Data data = getCopy();
        data.setHolder(holder.orElse(null));
        return data;
    }

    public abstract Data getCopy();
}
