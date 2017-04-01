package me.ichun.mods.ichunutil.common.core.config.annotations;

import net.minecraftforge.fml.relauncher.Side;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigProp
{
    boolean changeable() default true;

    boolean useSession() default false;

    boolean hidden() default false; //Hidden properties are never read

    String category() default "general";

    String comment() default "undefined";

    String nameOverride() default "";

    String module() default "";

    Side side() default Side.SERVER;
}
