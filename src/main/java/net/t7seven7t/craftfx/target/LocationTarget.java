package net.t7seven7t.craftfx.target;

import org.bukkit.Location;

/**
 *
 */
public class LocationTarget implements Target<Location> {

    private final Location location;

    public LocationTarget(Location location) {
        this.location = location;
    }

    @Override
    public Location getTarget() {
        return location;
    }
}
