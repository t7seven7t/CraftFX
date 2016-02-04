package net.t7seven7t.craftfx.command;

import com.sk89q.intake.parametric.AbstractModule;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.command.provider.ItemDefinitionProvider;
import net.t7seven7t.craftfx.item.ItemDefinition;

/**
 *
 */
public class CraftFXModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CraftFX.class).toInstance(CraftFX.instance());
        bind(ItemDefinition.class).toProvider(new ItemDefinitionProvider());
    }
}
