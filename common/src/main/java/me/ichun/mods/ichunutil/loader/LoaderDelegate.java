package me.ichun.mods.ichunutil.loader;

import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.entity.EntityPersistentDataHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.client.LoaderDelegateClient;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Locale;

public interface LoaderDelegate
{
    public static void assignLoaderDelegate()
    {
        Class<?> clz;
        try
        {
            clz = Class.forName("me.ichun.mods.ichunutil.loader.fabric.LoaderDelegateFabric");
        }
        catch(ClassNotFoundException ignored)
        {
            try
            {
                clz = Class.forName("me.ichun.mods.ichunutil.loader.forge.LoaderDelegateForge");
            }
            catch(ClassNotFoundException ignored2)
            {
                try
                {
                    clz = Class.forName("me.ichun.mods.ichunutil.loader.neoforge.LoaderDelegateNeoForge");
                }
                catch(ClassNotFoundException ignored3)
                {
                    clz = null;
                }
            }
        }

        if(clz == null)
        {
            throw new RuntimeException("Unable to create determine Loader Delegate type!");
        }

        try
        {
            iChunUtil.loaderDelegate = (LoaderDelegate)clz.getDeclaredConstructor().newInstance();
        }
        catch(InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e)
        {
            throw new RuntimeException("Unable to create Loader Delegate of type " + clz.getName() + "!", e);
        }

        if(iChunUtil.d().isOnClient())
        {
            LoaderDelegateClient.assignLoaderDelegateClient();
        }
    }

    Env env();

    default boolean isDevEnvironment() //Fabric has a flag that defines dev env
    {
        return SharedConstants.IS_RUNNING_IN_IDE;
    }

    Path getModsDir(); //TODO is modlloaded

    Path getConfigDir();

    <T extends ConfigBase> T registerConfig(T config, Object...params);

    boolean isOnClient();

    default boolean isOnDedicatedServer()
    {
        return !isOnClient();
    }

    MinecraftServer getServer();

    void registerAddReloadListener(PreparableReloadListener reloadListener);

    default Block getBlockFromRegistry(ResourceLocation rl) //Forge uses its own registries, else uses the built in ones
    {
        return BuiltInRegistries.BLOCK.get(rl);
    }

    EntityPersistentDataHandler getEntityPersistedDataHandler();

    default void firePlayerTickEndEvent(Player player){}

    default boolean isFakePlayer(ServerPlayer player)
    {
        return player.connection == null || player.getClass().getSimpleName().toLowerCase(Locale.ROOT).contains("fakeplayer");
    }
}
