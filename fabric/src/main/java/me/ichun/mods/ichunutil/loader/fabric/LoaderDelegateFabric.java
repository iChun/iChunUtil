package me.ichun.mods.ichunutil.loader.fabric;

import me.ichun.mods.ichunutil.api.fabric.event.FabricEvents;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.entity.EntityPersistentDataHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.Env;
import me.ichun.mods.ichunutil.loader.LoaderDelegate;
import me.ichun.mods.ichunutil.loader.fabric.config.ConfigHandlerFabric;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.player.Player;

import java.nio.file.Path;

public class LoaderDelegateFabric implements LoaderDelegate
{
    public LoaderDelegateFabric(){}

    @Override
    public Env env()
    {
        return Env.FABRIC;
    }

    @Override
    public Path getModsDir()
    {
        return FabricLoader.getInstance().getGameDir().resolve("mods");
    }

    @Override
    public Path getConfigDir()
    {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public <T extends ConfigBase> T registerConfig(T config, Object... params)
    {
        new ConfigHandlerFabric(config);
        return config;
    }

    @Override
    public boolean isDevEnvironment()
    {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isOnClient()
    {
        return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT);
    }

    @Override
    public MinecraftServer getServer()
    {
        return ServerListenerFabric.getServerInstance();
    }

    @Override
    public void registerAddReloadListener(PreparableReloadListener reloadListener)
    {
        FabricEvents.ADD_RELOAD_LISTENER.register(list -> list.add(reloadListener));
    }

    @Override
    public EntityPersistentDataHandler getEntityPersistedDataHandler()
    {
        if(iChunUtil.entityPersistentDataHandler == null)
        {
            iChunUtil.entityPersistentDataHandler = new EntityPersistentDataHandlerFabric();
        }
        return iChunUtil.entityPersistentDataHandler;
    }

    @Override
    public void firePlayerTickEndEvent(Player player)
    {
        FabricEvents.PLAYER_TICK_END.invoker().onPlayerTickEnd(player);
    }

    @Override
    public boolean isFakePlayer(ServerPlayer player)
    {
        return player instanceof FakePlayer || LoaderDelegate.super.isFakePlayer(player);
    }
}
