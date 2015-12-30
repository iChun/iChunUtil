package me.ichun.mods.ichunutil.common.core.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IntMinMax
{
    int min() default Integer.MIN_VALUE;
    int max() default Integer.MAX_VALUE;
    int nestedMin() default Integer.MIN_VALUE;
    int nestedMax() default Integer.MAX_VALUE;
}
