package net.t7seven7t.util.intake.module.annotation;

import com.sk89q.intake.parametric.annotation.Classifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Classifier
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface Ticks {
}
