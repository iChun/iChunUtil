package me.ichun.mods.ichunutil.common.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Prop
{
    boolean needsRestart() default false;

    double min() default Double.MIN_VALUE;

    double max() default Double.MAX_VALUE;

    String[] values() default "";

    String validator() default "undefined"; //points to method in within class that takes an Object as first argument and returns a boolean

    boolean hidden() default false;

    String comment() default "undefined"; //unlocalized comment

    String guiElementOverride() default "";
}
