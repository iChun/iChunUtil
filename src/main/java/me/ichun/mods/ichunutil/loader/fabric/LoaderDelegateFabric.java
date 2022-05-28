package me.ichun.mods.ichunutil.loader.fabric;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.mojang.blaze3d.pipeline.RenderTarget;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.network.PacketChannel;
import me.ichun.mods.ichunutil.loader.LoaderDelegate;
import me.ichun.mods.ichunutil.loader.fabric.config.ConfigToToml;
import me.ichun.mods.ichunutil.loader.fabric.config.FabricConfigLoader;
import me.ichun.mods.ichunutil.loader.fabric.event.FabricClientEvents;
import me.ichun.mods.ichunutil.loader.fabric.event.FabricEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LoaderDelegateFabric
        implements LoaderDelegate
{
    public MinecraftServer serverInstance;

    public LoaderDelegateFabric()
    {
        //Register our server listeners
        ServerLifecycleEvents.SERVER_STARTING.register(server -> serverInstance = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> serverInstance = null);
    }

    @Override
    public <T extends ConfigBase> T registerConfig(T config)
    {
        return FabricConfigLoader.registerConfig(config);
    }

    @Override
    @Nullable
    public ResourceLocation getRegistryName(Object o)
    {
        if(o instanceof Block)
        {
            return Registry.BLOCK.getKey((Block)o);
        }
        else if(o instanceof Item)
        {
            return Registry.ITEM.getKey((Item)o);
        }
        else if(o instanceof EntityType<?>)
        {
            return Registry.ENTITY_TYPE.getKey((EntityType<?>)o);
        }
        else if(o instanceof SoundEvent)
        {
            return Registry.SOUND_EVENT.getKey((SoundEvent)o);
        }
        else if(o instanceof MobEffect)
        {
            return Registry.MOB_EFFECT.getKey((MobEffect)o);
        }
        else if(o instanceof Potion)
        {
            return Registry.POTION.getKey((Potion)o);
        }
        else if(o instanceof Enchantment)
        {
            return Registry.ENCHANTMENT.getKey((Enchantment)o);
        }
        throw new RuntimeException("Asking for registry type of object we have never accounted for: " + o.getClass());
    }

    @Override
    public boolean isDevEnvironment()
    {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isLogicalSideClient()
    {
        final ThreadGroup group = Thread.currentThread().getThreadGroup();
        return !group.getName().toLowerCase(Locale.ROOT).contains("server");
    }

    @Override
    public MinecraftServer getMinecraftServer()
    {
        return serverInstance;
    }

    @Override
    public boolean isOnClient()
    {
        return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT);
    }

    @SafeVarargs
    @Override
    public final PacketChannel createPacketChannel(ResourceLocation name, Class<? extends AbstractPacket>... packetTypes)
    {
        return new PacketChannelFabric(name, packetTypes);
    }

    @Override
    @Nullable
    public Block getBlockFromRegistry(ResourceLocation rl)
    {
        return Registry.BLOCK.get(rl);
    }

    @Override
    public Path getGameDir()
    {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getConfigDir()
    {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public String remapField(String fieldName)
    {
        return fieldName; //TODO update this??
    }

    @Override
    public void registerPlayerTickEndListener(Consumer<Player> consumer)
    {
        //TODO this
    }

    @Override
    public boolean isPlayerFakePlayer(ServerPlayer player)
    {
        return false; //Fabric has no fake player instance to use
    }

    @Override
    public void registerEntityTypeRegistryListener(Consumer<Object> consumer)
    {
        consumer.accept(null);
    }

    @Override
    public Packet<?> getEntitySpawnPacket(Entity e)
    {
        return new ClientboundAddEntityPacket(e);
    }

    @Override
    public boolean isEntityAddedToWorld(Entity entity)
    {
        return entity.getLevel().getEntity(entity.getId()) != entity; //null or another entity
    }

    @Override
    public void registerAddReloadListener(PreparableReloadListener reloadListener)
    {
        FabricEvents.ADD_RELOAD_LISTENER.register(list -> list.add(reloadListener));
    }

    @Override
    public void registerClientSetupListener(Consumer<Object> consumer)
    {
        FabricClientEvents.CLIENT_MOD_INIT.register(consumer::accept);
    }

    @Override
    public void registerClientLevelUnloadListener(Consumer<Level> consumer)
    {
        FabricClientEvents.CLIENT_LEVEL_UNLOAD.register(consumer::accept);
    }

    @Override
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public void checkEnableStencil(RenderTarget render)
    {
        //NOP - Fabric has no stencil support....?
    }

    @Override
    public void registerClientTickStartListener(Consumer<Minecraft> consumer)
    {
        ClientTickEvents.START_CLIENT_TICK.register(consumer::accept);
    }

    @Override
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public void registerClientTickEndListener(Consumer<Minecraft> consumer)
    {
        ClientTickEvents.END_CLIENT_TICK.register(consumer::accept);
    }

    @Override
    public void registerPostInitScreenListener(BiConsumer<Minecraft, Screen> consumer)
    {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> consumer.accept(client, screen));
    }

    @Override
    public <E extends Entity> void registerEntityRenderer(EntityType<? extends E> type, EntityRendererProvider<E> renderer)
    {
        EntityRendererRegistry.register(type, renderer);
    }
    //
    //    @Override
    //    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    //    public BakedModel getCameraTransformsModel(PoseStack matrixStackIn, BakedModel pModel, ItemTransforms.TransformType none, boolean b)
    //    {
    //        return pModel; //return the model passed in to us
    //    }
    //
    //    @Override
    //    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    //    public boolean doItemModelIsLayered(ItemRenderer itemRenderer, BakedModel pModel, ItemStack pItemStack, PoseStack matrixStackIn, MultiBufferSource buffer, int i, int noOverlay, boolean flag1)
    //    {
    //        return false;
    //    }
    //
    //    @Override
    //    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    //    public void customRendererRenderByItem(ItemStack pItemStack, ItemTransforms.TransformType type, PoseStack matrixStackIn, MultiBufferSource buffer, int light, int overlay)
    //    {
    ////        Minecraft.getInstance().getItemRenderer().blockEntityRenderer.renderByItem(itemStack, transformType, matrixStack, buffer, combinedLight, combinedOverlay);
    //    }
}
