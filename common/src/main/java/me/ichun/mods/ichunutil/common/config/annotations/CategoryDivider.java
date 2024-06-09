package me.ichun.mods.ichunutil.common.config.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CategoryDivider
{
    String name() default "general"; //localization defaults to config.<modid>.cat.<name>.desc

    String comment() default "undefined"; //Unlocalized comment

    boolean showInGui() default true; //if false then the category is hidden. Used for eg CCI credentials
}
