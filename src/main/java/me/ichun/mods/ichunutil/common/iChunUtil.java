package me.ichun.mods.ichunutil.common;

import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import me.ichun.mods.ichunutil.client.core.ConfigClient;
import me.ichun.mods.ichunutil.client.core.EventHandlerClient;
import me.ichun.mods.ichunutil.client.core.ResourceHelper;
import me.ichun.mods.ichunutil.client.toast.ToastGui;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.EventHandlerServer;
import me.ichun.mods.ichunutil.common.entity.util.EntityHelper;
import me.ichun.mods.ichunutil.common.head.HeadHandler;
import me.ichun.mods.ichunutil.common.util.EventCalendar;
import me.ichun.mods.ichunutil.common.util.ObfHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(iChunUtil.MOD_ID)
public class iChunUtil
{
    public static final String MOD_ID = "ichunutil";
    public static final String MOD_NAME = "iChunUtil";

    public static final Logger LOGGER = LogManager.getLogger();

    private static ModLoadingStage loadingStage = ModLoadingStage.ERROR; //TODO spider fox easter egg

    public static ConfigClient configClient;

    public static EventHandlerServer eventHandlerServer;
    public static EventHandlerClient eventHandlerClient; //TODO if we have a packet channel we should only need it if a mod dep needs it.

    public iChunUtil()
    {
        loadingStage = ModLoadingStage.CONSTRUCT;
        ObfHelper.detectDevEnvironment();
        EventCalendar.checkDate();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);
        bus.addListener(this::processIMC);
        bus.addListener(this::finishLoading);

        MinecraftForge.EVENT_BUS.register(eventHandlerServer = new EventHandlerServer());

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ResourceHelper.init();
            configClient = new ConfigClient().init();
            EntityHelper.injectMinecraftPlayerGameProfile();

            bus.addListener(this::setupClient);

            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> EventHandlerClient::getConfigGui);
        });
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        loadingStage = ModLoadingStage.COMMON_SETUP;
        //TODO do I wanna check for mod updates
    }

    @OnlyIn(Dist.CLIENT)
    private void setupClient(final FMLClientSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(eventHandlerClient = new EventHandlerClient());

        if(configClient.overrideToastGui && Minecraft.getInstance().getToastGui().getClass().getName().equals("net.minecraft.client.gui.toasts.ToastGui"))
        {
            //QUICK SWITCHEROO
            Minecraft.getInstance().execute(() -> Minecraft.getInstance().toastGui = new ToastGui(Minecraft.getInstance()));
        }
    }

    private void finishLoading(FMLLoadCompleteEvent event)
    {
        loadingStage = ModLoadingStage.COMPLETE;
        ConfigBase.CONFIGS.forEach(c -> {
            if(!c.hasInit()) throw new RuntimeException("Config class created but never initialized: " + c.getConfigName());
        });
    }

    private void processIMC(InterModProcessEvent event)
    {
        event.getIMCStream(m -> m.equalsIgnoreCase("headinfo")).forEach(msg -> {
            Object o = msg.getMessageSupplier().get();
            if(o instanceof String)
            {
                String s = (String)o;
                HeadHandler.IMC_HEAD_INFO.add(s);
                iChunUtil.LOGGER.info("IMC-headinfo: Added HeadInfo json for interpretation later from: {}", msg.getSenderModId());
            }
            else if(o instanceof HeadInfo.HeadHolder)
            {
                HeadInfo.HeadHolder headInfo = (HeadInfo.HeadHolder)o;
                if(headInfo.clz == null || headInfo.info == null) //just in case
                {
                    iChunUtil.LOGGER.warn("IMC-headinfo: Custom HeadInfo has null object from mod: {}", msg.getSenderModId());
                }
                else
                {
                    HeadHandler.IMC_HEAD_INFO_OBJ.add(headInfo);
                    iChunUtil.LOGGER.info("IMC-headinfo: Caching custom HeadInfo {} from: {}", headInfo.info.getClass().getSimpleName(), msg.getSenderModId());
                }
            }
            else
            {
                iChunUtil.LOGGER.warn("IMC-headinfo: {} passed HeadInfo object is not a string: {}", msg.getSenderModId(), o);
            }
        });
    }

    public static ModLoadingStage getLoadingStage()
    {
        return loadingStage;
    }
}
