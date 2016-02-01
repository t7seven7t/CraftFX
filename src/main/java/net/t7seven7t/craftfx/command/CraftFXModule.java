package net.t7seven7t.craftfx.command;

import com.sk89q.intake.parametric.AbstractModule;

import net.t7seven7t.craftfx.CraftFX;

/**
 *
 */
public class CraftFXModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CraftFX.class).toInstance(CraftFX.instance());
    }
}
