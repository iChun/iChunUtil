package me.ichun.mods.ichunutil.loader.forge.forge;

import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.entity.EntityPersistentDataHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.Env;
import me.ichun.mods.ichunutil.loader.LoaderDelegate;
import me.ichun.mods.ichunutil.loader.forge.forge.config.ConfigHandlerForge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class LoaderDelegateForge implements LoaderDelegate
{
    @Override
    public Env env()
    {
        return Env.FORGE;
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
    public boolean isOnClient()
    {
        return FMLEnvironment.dist.isClient();
    }

    @Override
    public MinecraftServer getServer()
    {
        return ServerLifecycleHooks.getCurrentServer();
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

    @Override
    public Block getBlockFromRegistry(ResourceLocation rl)
    {
        return ForgeRegistries.BLOCKS.getValue(rl);
    }

    @Override
    public EntityPersistentDataHandler getEntityPersistedDataHandler()
    {
        if(iChunUtil.entityPersistentDataHandler == null)
        {
            iChunUtil.entityPersistentDataHandler = new EntityPersistentDataHandlerForge();
        }
        return iChunUtil.entityPersistentDataHandler;
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
