package me.ichun.mods.ichunutil.common.config.annotations;

import me.ichun.mods.ichunutil.loader.Env;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Prop
{
    boolean needsRestart() default false;

    boolean intBool() default false;

    double min() default -Double.MAX_VALUE; // Double.MIN_VALUE is the smallest POSITIVE non-zero value that a double can hold

    double max() default Double.MAX_VALUE;

    String[] values() default ""; //Only used for Strings

    String validator() default "undefined"; //points to method in within class that takes an Object as first argument and returns a boolean

    String comment() default "undefined"; //comment override

    String guiElementOverride() default "";

    @NotNull
    Env[] env() default Env.ALL; //if not ALL, only loader specific

    boolean skip() default false; //true if config handling is done somewhere else
}
