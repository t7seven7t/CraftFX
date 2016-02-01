package net.t7seven7t.util.intake.module.annotation;

import com.sk89q.intake.parametric.annotation.Classifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used primarily to apply a better name to lists since Intake otherwise names them unknowns
 */
@Retention(RetentionPolicy.RUNTIME)
@Classifier
@java.lang.annotation.Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface Target {
}
