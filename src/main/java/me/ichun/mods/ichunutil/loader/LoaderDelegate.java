package me.ichun.mods.ichunutil.loader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.network.PacketChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface LoaderDelegate
{
    <T extends ConfigBase> T registerConfig(T config);

    @Nullable
    ResourceLocation getRegistryName(Object o);

    boolean isDevEnvironment(); //TODO FMLLoader.getNaming().equals("mcp") ???? Does this still work

    boolean isOnClient(); //TODO FMLEnvironment.dist.isClient()

    default boolean isOnDedicatedServer()
    {
        return !isOnClient();
    }

    boolean isLogicalSideClient();

    MinecraftServer getMinecraftServer();

    PacketChannel createPacketChannel(ResourceLocation name, Class<? extends AbstractPacket>[] packetTypes);

    @Nullable
    Block getBlockFromRegistry(ResourceLocation rl);

    Path getGameDir();

    Path getConfigDir(); //TODO FMLPaths.CONFIGDIR.get()

    String remapField(String fieldName); //TODO ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, fieldName)

    void registerPlayerTickEndListener(Consumer<Player> consumer);

    boolean isPlayerFakePlayer(ServerPlayer player); //TODO player instanceof FakePlayer

    void registerEntityTypeRegistryListener(Consumer<Object> consumer); //TODO             bus.addGenericListener(EntityType.class, ClientEntityTracker.EntityTypes::onEntityTypeRegistry);

    Packet<?> getEntitySpawnPacket(Entity e); //TODO NetworkHooks.getEntitySpawningPacket(this);

    boolean isEntityAddedToWorld(Entity entity); //TODO entity.isAddedToWorld()

    void registerAddReloadListener(PreparableReloadListener reloadListener); //TODO AddReloadListenerEvent, event.addListener(this);

    //Client stuff

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    void registerClientSetupListener(Consumer<Object> consumer); //TODO bus.addListener(ClientEntityTracker::onClientSetup); //cast to the event object?

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    void registerClientLevelUnloadListener(Consumer<Level> consumer);

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
        //TODO
    /*
            if(mc.getMainRenderTarget().isStencilEnabled()) //if the main framebuffer is using a stencil, we might as well, too.
        {
            render.enableStencil();
        }
     */
    void checkEnableStencil(RenderTarget render);

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    void registerClientTickStartListener(Consumer<Minecraft> consumer);

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    void registerClientTickEndListener(Consumer<Minecraft> consumer);

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    void registerPostInitScreenListener(BiConsumer<Minecraft, Screen> consumer); //TODO     public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event)

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    <E extends Entity> void registerEntityRenderer(EntityType<? extends E> type, EntityRendererProvider<E> renderer); //TODO forge is in EntityRenderersEvent now. Needs to be listened to.

    //    //TODO reevaluate these
    //
    //    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    //        //TODO net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStackIn, pModel, ItemTransforms.TransformType.NONE, false);
    //    BakedModel getCameraTransformsModel(PoseStack matrixStackIn, BakedModel pModel, ItemTransforms.TransformType none, boolean b);
    //
    //    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    //    //TODO if (modelIn.isLayered()) { net.minecraftforge.client.ForgeHooksClient.drawItemLayered(this, modelIn, itemStackIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, flag1); }
    //    boolean doItemModelIsLayered(ItemRenderer itemRenderer, BakedModel pModel, ItemStack pItemStack, PoseStack matrixStackIn, MultiBufferSource buffer, int i, int noOverlay, boolean flag1);
    //
    //
    //    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    //    //TODO                 net.minecraftforge.client.RenderProperties.get(pItemStack).getItemStackRenderer().renderByItem(pItemStack, ItemTransforms.TransformType.NONE, matrixStackIn, buffer, 15728880, OverlayTexture.NO_OVERLAY);
    //    void customRendererRenderByItem(ItemStack pItemStack, ItemTransforms.TransformType type, PoseStack matrixStackIn, MultiBufferSource buffer, int light, int overlay);
}
