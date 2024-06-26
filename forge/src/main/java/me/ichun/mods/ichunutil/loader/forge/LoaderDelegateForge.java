package me.ichun.mods.ichunutil.loader.forge;

import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.Env;
import me.ichun.mods.ichunutil.loader.LoaderDelegate;
import me.ichun.mods.ichunutil.loader.Side;
import me.ichun.mods.ichunutil.loader.forge.client.EventHandlerClientForge;
import me.ichun.mods.ichunutil.loader.forge.config.ConfigHandlerForge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class LoaderDelegateForge implements LoaderDelegate
{
    public LoaderDelegateForge(){}

    @Override
    public Env env()
    {
        return Env.FORGE;
    }

    @Override
    public void assignEventHandlerServer()
    {
        iChunUtil.eventHandlerServer = new EventHandlerServerForge();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void assignEventHandlerClient()
    {
        iChunUtil.eventHandlerClient = new EventHandlerClientForge();
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
        new ConfigHandlerForge(config);
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
    public Block registryBlock(ResourceLocation rl)
    {
        return ForgeRegistries.BLOCKS.getValue(rl);
    }

    @Override
    public SoundEvent registrySoundEvents(ResourceLocation rl)
    {
        return ForgeRegistries.SOUND_EVENTS.getValue(rl);
    }

    @Override
    public void registerAddReloadListener(PreparableReloadListener reloadListener)
    {
        if(preparableReloadListeners.isEmpty())
        {
            MinecraftForge.EVENT_BUS.addListener(this::addReloadListenerEvent);
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
