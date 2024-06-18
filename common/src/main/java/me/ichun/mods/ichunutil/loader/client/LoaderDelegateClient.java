package me.ichun.mods.ichunutil.loader.client;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public interface LoaderDelegateClient
{
    public static void assignLoaderDelegateClient()
    {
        Class<?> clz;
        try
        {
            clz = Class.forName("me.ichun.mods.ichunutil.loader.fabric.client.LoaderDelegateClientFabric");
        }
        catch(ClassNotFoundException ignored)
        {
            try
            {
                clz = Class.forName("me.ichun.mods.ichunutil.loader.forge.client.LoaderDelegateClientForge");
            }
            catch(ClassNotFoundException ignored2)
            {
                try
                {
                    clz = Class.forName("me.ichun.mods.ichunutil.loader.neoforge.client.LoaderDelegateClientNeoForge");
                }
                catch(ClassNotFoundException ignored3)
                {
                    clz = null;
                }
            }
        }

        if(clz == null)
        {
            throw new RuntimeException("Unable to create determine Loader Delegate Client type!");
        }

        try
        {
            iChunUtil.loaderDelegateClient = (LoaderDelegateClient)clz.getDeclaredConstructor().newInstance();
        }
        catch(InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e)
        {
            throw new RuntimeException("Unable to create Loader Delegate Client of type " + clz.getName() + "!", e);
        }
    }

    void registerKeyMapping(KeyMapping key, String...conflictContext);

    void registerClientTickStartListener(Consumer<Minecraft> consumer);

    void registerClientTickEndListener(Consumer<Minecraft> consumer);

    default void fireLivingRenderPreEvent(LivingEntity entity, LivingEntityRenderer livingEntityRenderer, float partialTicks){}

    default void fireClientLevelLoad(ClientLevel level){};
}
