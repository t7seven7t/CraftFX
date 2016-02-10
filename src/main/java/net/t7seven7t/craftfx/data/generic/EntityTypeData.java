package net.t7seven7t.craftfx.data.generic;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.util.EnumUtil;

import org.bukkit.entity.EntityType;

import java.util.Optional;

/**
 *
 */
public class EntityTypeData extends AbstractData {

    private EntityType entityType;

    public Optional<EntityType> getEntityType() {
        return Optional.ofNullable(entityType);
    }

    @Override
    public void onDataHolderUpdate() {
        this.entityType = EnumUtil.matchEnumValue(EntityType.class,
                get("entity-type", String.class, ""));
    }

    @Override
    public Data getCopy() {
        return new EntityTypeData();
    }
}
