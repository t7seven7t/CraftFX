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

    public Optional<DataHolder> getHolder() {
        return holder;
    }

    @Override
    public final void setHolder(DataHolder holder) {
        this.holder = Optional.ofNullable(holder);
        if (holder != null) onDataHolderUpdate();
    }

    @Override
    public Data copy() {
        Data data = getCopy();
        data.setHolder(holder.orElse(null));
        return data;
    }

    /**
     * Called when the setDataHolder is called with a holder that is non null so that child
     * instances have a chance to reinitialize their internal state.
     */
    public void onDataHolderUpdate() {
    }

    public abstract Data getCopy();
}
