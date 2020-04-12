package me.ichun.mods.ichunutil.client.core;

import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ConfigTest extends ConfigBase
{
    @CategoryDivider(name = "clientOnly")
    @Prop(comment = "Enables (most) Client-Side Easter Eggs for iChun's Mods")
    public int numberTest = 1;

    @Prop(comment = "Enables (most) Client-Side Easter Eggs for iChun's Mods")
    public double doubleTest = 2D;

    @Prop
    public boolean booleanTest = true;

    @Prop
    public String stringTest = "A string";

    @Prop
    public Constraint.Property.Type enumTest = Constraint.Property.Type.TOP;

    @Prop
    public List<String> listTest = new ArrayList() {{
        add("Damage");
        add("Charged");
        add("ChargedProjectiles");
    }};

    @Prop
    public List<Integer> integerlistInteger = new ArrayList() {{
        add(1);
        add(123);
        add(72);
    }};

    @Prop
    public List<Double> doublelistDouble = new ArrayList() {{
        add(42D);
        add(420D);
        add(69420D);
    }};

    @CategoryDivider(name = "scrollTest")
    @Prop
    public int numberTestA = 1;

    @Prop
    public int numberTestA1 = 1;

    @Prop
    public int numberTestA2 = 1;

    @Prop
    public int numberTestA3 = 1;

    @Prop
    public int numberTestA4 = 1;

    @Prop
    public int numberTestA5 = 1;

    @Prop
    public int numberTestA6 = 1;

    @Prop
    public int numberTestA7 = 1;

    @Prop
    public int numberTestA8 = 1;

    @Prop
    public int numberTestA9 = 1;

    @Prop
    public int numberTestA0 = 1;

    @Prop
    public int numberTestA12 = 1;

    @Prop
    public int numberTestA22 = 1;

    @Prop
    public int numberTestA11 = 1;

    @Prop
    public int numberTestA24 = 1;

    @Prop
    public int numberTestA34 = 1;

    @Prop
    public int numberTestA44 = 1;

    @Prop
    public int numberTestA54 = 1;

    @Prop
    public int numberTestA64 = 1;

    @Prop
    public int numberTestA74 = 1;

    @Prop
    public int numberTestA84 = 1;

    @Prop
    public int numberTestA94 = 1;

    @Prop
    public int numberTestA04 = 1;

    public ConfigTest(String s)
    {
        super(s);
    }

    @Nonnull
    @Override
    public String getModId()
    {
        return iChunUtil.MOD_ID;
    }

    @Nonnull
    @Override
    public String getConfigName()
    {
        return iChunUtil.MOD_NAME;
    }

    @Nonnull
    @Override
    public ModConfig.Type getConfigType()
    {
        return ModConfig.Type.COMMON;
    }
}
