package me.ichun.mods.ichunutil.common;

import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(iChunUtil.MOD_ID)
public class iChunUtil
{
    public static final String MOD_ID = "ichunutil";
    public static final String MOD_NAME = "iChunUtil";

    public static final Logger LOGGER = LogManager.getLogger(); //TODO should we add Markers?

    private static ModLoadingStage loadingStage = ModLoadingStage.ERROR;

    public iChunUtil()
    {
        //HMM since everything is loaded concurrently, is it safe to do long thread/io reads in mod constructor?
        loadingStage = ModLoadingStage.CONSTRUCT;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::finishLoading);
    }

    private void init(final FMLCommonSetupEvent event)
    {
        loadingStage = ModLoadingStage.COMMON_SETUP;
    }

    private void finishLoading(FMLLoadCompleteEvent event)
    {
        loadingStage = ModLoadingStage.COMPLETE;
    }

    public static ModLoadingStage getLoadingStage()
    {
        return loadingStage;
    }
}
