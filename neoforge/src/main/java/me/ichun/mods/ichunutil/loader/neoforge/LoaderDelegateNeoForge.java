package me.ichun.mods.ichunutil.loader.neoforge;

import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.Env;
import me.ichun.mods.ichunutil.loader.LoaderDelegate;
import me.ichun.mods.ichunutil.loader.Side;
import me.ichun.mods.ichunutil.loader.neoforge.client.EventHandlerClientNeoForge;
import me.ichun.mods.ichunutil.loader.neoforge.config.ConfigHandlerNeoForge;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class LoaderDelegateNeoForge implements LoaderDelegate
{
    public LoaderDelegateNeoForge(){}

    @Override
    public Env env()
    {
        return Env.NEOFORGE;
    }

    @Override
    public void assignEventHandlerServer()
    {
        iChunUtil.eventHandlerServer = new EventHandlerServerNeoForge();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void assignEventHandlerClient()
    {
        iChunUtil.eventHandlerClient = new EventHandlerClientNeoForge();
    }

    @Override
    public Path getModsDir()
    {
        return FMLPaths.MODSDIR.get();
    }

    @Override
    public Path getConfigDir()
    {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public <T extends ConfigBase> T registerConfig(T config, Object... params)
    {
        if(params.length < 1 || !(params[0] instanceof IEventBus))
        {
            throw new IllegalArgumentException("First argument needs to be FML IEventBus!");
        }
        new ConfigHandlerNeoForge(config, (IEventBus)params[0]);
        return config;
    }

    @Override
    public Side getSide()
    {
        return FMLEnvironment.dist.isDedicatedServer() ? Side.SERVER : Side.CLIENT;
    }

    @Override
    public Side getEffectiveSide()
    {
        return EffectiveSide.get().isServer() ? Side.SERVER : Side.CLIENT;
    }

    @Override
    public MinecraftServer getServer()
    {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public boolean isModLoaded(String modId)
    {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean sendIMCMessage(String ourId, String modId, String sub, Supplier<?> thing)
    {
        return InterModComms.sendTo(ourId, modId, sub, thing);
    }

    @Override
    public void registerAddReloadListener(PreparableReloadListener reloadListener)
    {
        if(preparableReloadListeners.isEmpty())
        {
            NeoForge.EVENT_BUS.addListener(this::addReloadListenerEvent);
        }
        preparableReloadListeners.add(reloadListener);
    }

    private final Set<PreparableReloadListener> preparableReloadListeners = new HashSet<>();
    private void addReloadListenerEvent(AddReloadListenerEvent event)
    {
        for(PreparableReloadListener listener : preparableReloadListeners)
        {
            event.addListener(listener);
        }
    }
}
