package me.ichun.mods.ichunutil.client.core;

import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;

public class ConfigClient extends ConfigBase
{
    @CategoryDivider(name = "clientOnly")
    @Prop(comment = "Enables (most) Client-Side Easter Eggs for iChun's Mods")
    public boolean easterEgg = true;

    @Prop(comment = "Renders iChunUtil's GUIs in a Minecraft Style instead")
    public boolean guiStyleMinecraft = false;

    @Prop(comment = "Number of ticks before showing a tooltip", min = 0)
    public int tooltipCooldown = 10;

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
        return ModConfig.Type.CLIENT;
    }
}
