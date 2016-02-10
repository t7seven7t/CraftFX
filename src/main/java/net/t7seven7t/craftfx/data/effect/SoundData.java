package net.t7seven7t.craftfx.data.effect;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;

/**
 *
 */
public class SoundData extends AbstractData {

    private String sound;
    private float volume;
    private float pitch;

    public String getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    @Override
    public void onDataHolderUpdate() {
        this.sound = get("sound", String.class, "");
        this.volume = get("volume", Float.class, 1f);
        this.pitch = get("pitch", Float.class, 1f);
    }

    @Override
    public Data getCopy() {
        return new SoundData();
    }
}
