package net.t7seven7t.util.intake.module.provider;

import com.google.common.collect.ImmutableList;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;

import net.t7seven7t.util.TimeUtil;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 *
 */
public class TimeProvider implements Provider<Long> {
    private final MillisecondConverter converter;

    public TimeProvider(MillisecondConverter converter) {
        this.converter = converter;
    }

    @Override
    public boolean isProvided() {
        return false;
    }

    @Override
    public Long get(CommandArgs arguments,
                    List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
        return converter.convert(TimeUtil.parseString(arguments.next()));
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        return ImmutableList.of();
    }

    @FunctionalInterface
    public interface MillisecondConverter {
        Long convert(long millis);
    }
}
