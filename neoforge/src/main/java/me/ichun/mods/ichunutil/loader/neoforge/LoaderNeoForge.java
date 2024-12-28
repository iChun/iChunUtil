package me.ichun.mods.ichunutil.loader.neoforge;

import me.ichun.mods.ichunutil.client.core.ConfigClient;
import me.ichun.mods.ichunutil.client.core.ResourceHelper;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.function.Supplier;

@Mod(iChunUtil.MOD_ID)
public class LoaderNeoForge extends iChunUtil
{
    public LoaderNeoForge(IEventBus modEventBus, ModContainer container)
    {
        modProxy = this;

        if(FMLEnvironment.dist.isClient())
        {
            initClient(modEventBus, container);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void initClient(IEventBus modEventBus, ModContainer container)
    {
        ResourceHelper.init();

        configClient = d().registerConfig(new ConfigClient(), modEventBus); // configs cannot be initialised in setup stage.

        container.registerExtensionPoint(IConfigScreenFactory.class, (Supplier<IConfigScreenFactory>)() -> (modContainer, screen) -> new WorkspaceConfigs(screen, MOD_ID));
    }
}
