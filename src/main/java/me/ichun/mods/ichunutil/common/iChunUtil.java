package me.ichun.mods.ichunutil.common;

import com.mojang.logging.LogUtils;
import me.ichun.mods.ichunutil.client.core.ConfigClient;
import me.ichun.mods.ichunutil.client.core.EventHandlerClient;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.ichunutil.common.core.EventHandlerServer;
import me.ichun.mods.ichunutil.common.network.PacketChannel;
import me.ichun.mods.ichunutil.common.util.EventCalendar;
import me.ichun.mods.ichunutil.common.util.ObfHelper;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.Util;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class iChunUtil
{
    public static final String MOD_ID = "ichunutil";
    public static final String MOD_NAME = "iChunUtil";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static iChunUtil INSTANCE; //set when the mod initialises our mod class

    public static ConfigClient configClient;

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public static EventHandlerClient eventHandlerClient;
    public static EventHandlerServer eventHandlerServer;

    public iChunUtil()
    {
        ObfHelper.detectDevEnvironment();
        EventCalendar.checkDate();
    }

    //TODO head info wenxin's workaround for spider, maybe also do it for wither.

    //TODO update BNS's renderTooltip for Forge
    //TODO ItemModelRenderer - Forge related method for BakedModel

    //TODO process IMC for Forge

    //TODO can I just replace this line in the source when compiling?
    //@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    //@net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
}
