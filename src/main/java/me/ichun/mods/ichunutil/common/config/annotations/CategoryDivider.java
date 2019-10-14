package me.ichun.mods.ichunutil.common.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CategoryDivider
{
    String name() default "general"; //localization defaults to config.<modid>.cat.<name>.desc

    String comment() default "undefined"; //Unlocalized comment
}
