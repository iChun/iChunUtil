package me.ichun.mods.ichunutil.common.entity.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import me.ichun.mods.ichunutil.mixin.LivingEntityInvokerMixin;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.function.Predicate;

public class EntityHelper
{
    public static final UUID UUID_EXAMPLE = UUID.fromString("DEADBEEF-DEAD-BEEF-DEAD-DEADBEEFD00D");

    //Game Profile lookup service
    public static final Map<String, GameProfile> GAME_PROFILE_CACHE = Collections.synchronizedMap(new HashMap<>());
    public static final Map<UUID, GameProfile> GAME_PROFILE_CACHE_UUID = Collections.synchronizedMap(new HashMap<>());
    private static GameProfileCache profileCache;
    private static MinecraftSessionService sessionService;

    private static final GameProfile DUMMY_PROFILE = new GameProfile(UUID_EXAMPLE, "ForgeDev");

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public static void injectMinecraftPlayerGameProfile()
    {
        if(Minecraft.getInstance() != null) // null when generating data.
        {
            GAME_PROFILE_CACHE.put(Minecraft.getInstance().getUser().getName(), Minecraft.getInstance().getUser().getGameProfile());
            GAME_PROFILE_CACHE_UUID.put(Minecraft.getInstance().getUser().getGameProfile().getId(), Minecraft.getInstance().getUser().getGameProfile());
        }
    }

    public static GameProfile getDummyGameProfile()
    {
        return DUMMY_PROFILE;
    }

    public static GameProfile getGameProfile(@Nullable UUID uuid, @Nullable String playerName) //Never have both null!
    {
        if(playerName != null && GAME_PROFILE_CACHE.containsKey(playerName))
        {
            return GAME_PROFILE_CACHE.get(playerName);
        }
        if(uuid != null && GAME_PROFILE_CACHE_UUID.containsKey(uuid))
        {
            return GAME_PROFILE_CACHE_UUID.get(uuid);
        }

        if(profileCache == null || sessionService == null)
        {
            if(LoaderHandler.d().isOnDedicatedServer())
            {
                sessionService = LoaderHandler.d().getMinecraftServer().getSessionService();
                profileCache = LoaderHandler.d().getMinecraftServer().getProfileCache();
            }
            else
            {
                setClientProfileLookupObjects();
            }
        }

        Optional<GameProfile> optional = uuid != null ? profileCache.get(uuid) : profileCache.get(playerName);
        GameProfile gameprofile = optional.orElseGet(() -> sessionService.fillProfileProperties(new GameProfile(uuid, playerName), true));

        Property property = Iterables.getFirst(gameprofile.getProperties().get("textures"), null);
        if(property == null)
        {
            gameprofile = sessionService.fillProfileProperties(gameprofile, true);
        }

        GAME_PROFILE_CACHE.put(gameprofile.getName(), gameprofile);
        GAME_PROFILE_CACHE_UUID.put(gameprofile.getId(), gameprofile);
        return gameprofile;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    private static void setClientProfileLookupObjects()
    {
        YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(Minecraft.getInstance().getProxy(), UUID.randomUUID().toString());
        sessionService = yggdrasilauthenticationservice.createMinecraftSessionService();
        GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
        profileCache = new GameProfileCache(gameprofilerepository, new File(Minecraft.getInstance().gameDirectory, MinecraftServer.USERID_CACHE_FILE.getName()));
    }

    public static Player getClientPlayer()
    {
        if(LoaderHandler.d().isOnClient())
        {
            return getMinecraftPlayer();
        }
        return null;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    private static Player getMinecraftPlayer()
    {
        return Minecraft.getInstance().player;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public static void nudgeHand(float mag)
    {
        Minecraft.getInstance().player.xBob += mag;
    }

    public static float updateRotation(float oriRot, float intendedRot, float maxChange)
    {
        float var4 = Mth.wrapDegrees(intendedRot - oriRot);

        if(var4 > maxChange)
        {
            var4 = maxChange;
        }

        if(var4 < -maxChange)
        {
            var4 = -maxChange;
        }

        return oriRot + var4;
    }

    public static float sineifyProgress(float progress) //0F - 1F; Yay math
    {
        return ((float)Math.sin(Math.toRadians(-90F + (180F * progress))) / 2F) + 0.5F;
    }

    public static int countInInventory(Container inv, Item item)
    {
        int totalCount = 0;

        for(int i = 0; i < inv.getContainerSize(); i++)
        {
            ItemStack is = inv.getItem(i);
            if(!is.isEmpty() && is.getItem() == item)
            {
                totalCount += is.getCount();
            }
        }

        return totalCount;
    }

    public static boolean consumeInventoryItem(Container inventory, Item item)
    {
        return consumeInventoryItem(inventory, item, 1);
    }

    /**
     * @param inventory inventory to access
     * @param itemIn item to check for, null for any item
     * @param removeCount amount to remove, negative numbers to remove entire stack.
     * @return if items were removed successfully (or removeCount == 0).
     */
    public static boolean consumeInventoryItem(Container inventory, Item itemIn, int removeCount)
    {
        if(removeCount > 0 && countInInventory(inventory, itemIn) < removeCount)
        {
            return false;
        }

        if (removeCount != 0)
        {
            int removed = 0;
            for(int j = inventory.getContainerSize() - 1; j >= 0; --j)
            {
                ItemStack itemstack = inventory.getItem(j);

                if(!itemstack.isEmpty() && (itemIn == null || itemstack.getItem() == itemIn))
                {
                    int removeFromStack = removeCount < 0 ? itemstack.getCount() : Math.min(removeCount - removed, itemstack.getCount());
                    removed += removeFromStack;

                    itemstack.shrink(removeFromStack);

                    if(itemstack.isEmpty())
                    {
                        inventory.setItem(j, ItemStack.EMPTY);
                        inventory.setChanged();
                    }

                    if(removeCount < 0 || removed >= removeCount)
                    {
                        return true;
                    }
                }
            }
        }

        return removeCount == 0;
    }

    public static void putEntityWithinAABB(Entity ent, AABB aabb)
    {
        double posX = ent.getX();
        double posY = ent.getY();
        double posZ = ent.getZ();
        if(ent.getBoundingBox().maxX > aabb.maxX)
        {
            posX += aabb.maxX - ent.getBoundingBox().maxX;
        }
        if(ent.getBoundingBox().minX < aabb.minX)
        {
            posX += aabb.minX - ent.getBoundingBox().minX;
        }
        if(posY + ent.getEyeHeight() > aabb.maxY)
        {
            posY += aabb.maxY - posY - ent.getEyeHeight();
        }
        if(posY < aabb.minY)
        {
            posY += aabb.minY - posY + 0.001D;
        }
        if(ent.getBoundingBox().maxZ > aabb.maxZ)
        {
            posZ += aabb.maxZ - ent.getBoundingBox().maxZ;
        }
        if(ent.getBoundingBox().minZ < aabb.minZ)
        {
            posZ += aabb.minZ - ent.getBoundingBox().minZ;
        }
        ent.setPos(posX, posY, posZ);
    }

    public static HitResult getEntityLook(Entity ent, double d)
    {
        return getEntityLook(ent, d, true);
    }

    public static HitResult getEntityLook(Entity ent, double d, boolean checkEntities)
    {
        return getEntityLook(ent, d, checkEntities, 1F);
    }

    public static HitResult getEntityLook(Entity ent, double d, boolean checkEntities, float partialTick)
    {
        return rayTrace(ent.level, ent.getEyePosition(partialTick), ent.getEyePosition(partialTick).add(ent.getViewVector(partialTick).multiply(d, d, d)), ent, checkEntities, ClipContext.Block.COLLIDER, b -> true, ClipContext.Fluid.NONE, e -> true);
    }

    public static HitResult rayTrace(Level world, Vec3 origin, Vec3 dest, @Nonnull Entity exception, boolean checkEntityCollision, ClipContext.Block blockMode, Predicate<BlockInfo> blockFilter, ClipContext.Fluid fluidMode, Predicate<Entity> filter) {
        ClipContext clipContext = new ClipContext(origin, dest, blockMode, fluidMode, exception);
        HitResult raytraceresult = BlockGetter.traverseBlocks(clipContext.getFrom(), clipContext.getTo(), clipContext, (context, pos) -> {
            BlockState blockstate = world.getBlockState(pos);
            if(blockFilter.test(new BlockInfo(world, blockstate, pos))) //Taken from BlockGetter.clip
            {
                FluidState ifluidstate = world.getFluidState(pos);
                Vec3 vec3d = context.getFrom();
                Vec3 vec3d1 = context.getTo();
                VoxelShape voxelshape = context.getBlockShape(blockstate, world, pos);
                BlockHitResult blockraytraceresult = world.clipWithInteractionOverride(vec3d, vec3d1, pos, voxelshape, blockstate);
                VoxelShape voxelshape1 = context.getFluidShape(ifluidstate, world, pos);
                BlockHitResult blockraytraceresult1 = voxelshape1.clip(vec3d, vec3d1, pos);
                double d0 = blockraytraceresult == null ? Double.MAX_VALUE : context.getFrom().distanceToSqr(blockraytraceresult.getLocation());
                double d1 = blockraytraceresult1 == null ? Double.MAX_VALUE : context.getFrom().distanceToSqr(blockraytraceresult1.getLocation());
                return d0 <= d1 ? blockraytraceresult : blockraytraceresult1;
            }
            return null;
        }, (context) -> {
            Vec3 vec3d = context.getFrom().subtract(context.getTo()); //getStartVec, getEndVec
            return BlockHitResult.miss(context.getTo(), Direction.getNearest(vec3d.x, vec3d.y, vec3d.z), new BlockPos(context.getTo()));
        });
        if (checkEntityCollision) {
            if (raytraceresult.getType() != HitResult.Type.MISS) {
                dest = raytraceresult.getLocation();
            }

            AABB aabb = new AABB(origin, dest).inflate(1F);
            HitResult raytraceresult1 = ProjectileUtil.getEntityHitResult(world, exception, origin, dest, aabb, filter);
            if (raytraceresult1 != null) {
                raytraceresult = raytraceresult1;
            }
        }

        return raytraceresult;
    }

    private static Set<Entity> getEntityAndMount(Entity rider) { //taken from ProjectileHelper
        Entity entity = rider.getVehicle();
        return entity != null ? ImmutableSet.of(rider, entity) : ImmutableSet.of(rider);
    }

    public static void faceEntity(Entity facer, Entity faced, float maxYaw, float maxPitch)
    {
        faceLocation(facer, faced.getX(), (faced instanceof LivingEntity ? (faced.getY() + faced.getEyeHeight()) : (faced.getBoundingBox().minY + faced.getBoundingBox().maxY) / 2D), faced.getZ(), maxYaw, maxPitch);
    }

    public static void faceLocation(Entity facer, double posX, double posY, double posZ, float maxYaw, float maxPitch)
    {
        double d0 = posX - facer.getX();
        double d1 = posZ - facer.getZ();
        double d2 = posY - (facer.getY() + (double)facer.getEyeHeight());

        double d3 = (double)Math.sqrt(d0 * d0 + d1 * d1);
        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));
        facer.setXRot(updateRotation(facer.getXRot(), f3, maxPitch));
        facer.setYRot(updateRotation(facer.getYRot(), f2, maxYaw));
    }

    public static Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = pitch * ((float)Math.PI / 180F);
        float f1 = -yaw * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    public static Vec3 getVectorRenderYawOffset(float renderYawOffset) {
        return getVectorForRotation(0, renderYawOffset);
    }

    public static void playSound(@Nonnull Entity ent, SoundEvent soundEvent, SoundSource soundCategory, float volume, float pitch)
    {
        ent.getCommandSenderWorld().playSound(ent.getCommandSenderWorld().isClientSide ? getClientPlayer() : null, ent.getX(), ent.getY() + ent.getEyeHeight(), ent.getZ(), soundEvent, soundCategory, volume, pitch); // sound will not play if the world is a WorldClient unless the entity == mc.player.
    }

    //TODO
    //    public static boolean destroyBlocksInAABB(Entity ent, AABB aabb, Predicate<BlockInfo> blockFilter)
    //    {
    //        int i = Mth.floor(aabb.minX);
    //        int j = Mth.floor(aabb.minY);
    //        int k = Mth.floor(aabb.minZ);
    //        int l = Mth.floor(aabb.maxX);
    //        int i1 = Mth.floor(aabb.maxY);
    //        int j1 = Mth.floor(aabb.maxZ);
    //        boolean flag = false;
    //        boolean flag1 = false;
    //
    //        for(int k1 = i; k1 <= l; ++k1)
    //        {
    //            for(int l1 = j; l1 <= i1; ++l1)
    //            {
    //                for(int i2 = k; i2 <= j1; ++i2)
    //                {
    //                    BlockPos blockpos = new BlockPos(k1, l1, i2);
    //                    BlockState blockstate = ent.level.getBlockState(blockpos);
    //
    //                    if (!blockstate.isAir(ent.level, blockpos) && blockstate.getMaterial() != Material.FIRE) {
    //                        if (ForgeEventFactory.getMobGriefingEvent(ent.level, ent) && blockstate.canEntityDestroy(ent.level, blockpos, ent) && (!(ent instanceof LivingEntity) || ForgeEventFactory.onEntityDestroyBlock((LivingEntity)ent, blockpos, blockstate)) && blockFilter.test(new BlockInfo(ent.level, blockstate, blockpos))) {
    //                            flag1 = ent.level.removeBlock(blockpos, false) || flag1;
    //                        } else {
    //                            flag = true;
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //
    //        if(flag1)
    //        {
    //            BlockPos blockpos1 = new BlockPos(i + ent.level.random.nextInt(l - i + 1), j + ent.level.random.nextInt(i1 - j + 1), k + ent.level.random.nextInt(j1 - k + 1));
    //            ent.level.levelEvent(2008, blockpos1, 0);
    //        }
    //
    //        return flag;
    //    }
    //
    //    public static CompoundTag getPlayerPersistentData(@Nonnull Player player, @Nullable String name) //if null returns the whole persisted data tag. else get a tag by the name
    //    {
    //        CompoundTag persistedTag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
    //        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persistedTag);
    //        if(name == null)
    //        {
    //            return persistedTag;
    //        }
    //        CompoundTag specificTag = persistedTag.getCompound(name);
    //        persistedTag.put(name, specificTag);
    //        return specificTag;
    //    }

    public static boolean isFakePlayer(@Nonnull ServerPlayer player) //Fake Players extend ServerPlayerEntity. Please check or cast first
    {
        return player.connection == null || LoaderHandler.d().isPlayerFakePlayer(player); // || player.getName().getUnformattedComponentText().toLowerCase().startsWith("fakeplayer") || player.getName().getUnformattedComponentText().toLowerCase().startsWith("[minecraft]");
    }

    public static boolean hasCompletedAdvancement(@Nonnull ResourceLocation rl, @Nonnull Player player)
    {
        if(!player.level.isClientSide)
        {
            ServerPlayer serverPlayer = ((ServerPlayer)player);
            Advancement advancement = serverPlayer.getServer().getAdvancements().getAdvancement(rl);
            if(advancement != null)
            {
                return serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone();
            }
            return false;
        }
        else
        {
            return hasCompletedAdvancementClient(rl, player);
        }
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    private static boolean hasCompletedAdvancementClient(@Nonnull ResourceLocation rl, @Nonnull Player player)
    {
        if(player instanceof LocalPlayer)
        {
            LocalPlayer clientPlayer = (LocalPlayer)player;
            if(clientPlayer.connection != null)
            {
                Advancement advancement = clientPlayer.connection.getAdvancements().getAdvancements().get(rl);
                if(advancement != null)
                {
                    AdvancementProgress progress = clientPlayer.connection.getAdvancements().progress.get(advancement);
                    if(progress != null && progress.isDone())
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    //REFLECTIVE methods
    public static <T extends LivingEntity> SoundEvent getHurtSound(T ent, DamageSource source)
    {
        return ((LivingEntityInvokerMixin)ent).callGetHurtSound(source);
    }

    public static <T extends LivingEntity> SoundEvent getDeathSound(T ent)
    {
        return ((LivingEntityInvokerMixin)ent).callGetDeathSound();
    }

    public static <T extends LivingEntity> float getSoundVolume(T ent)
    {
        return ((LivingEntityInvokerMixin)ent).callGetSoundVolume();
    }

    public static <T extends LivingEntity> float getVoicePitch(T ent)
    {
        return ((LivingEntityInvokerMixin)ent).callGetVoicePitch();
    }

    public static <T extends LivingEntity> void onEffectUpdated(T ent, MobEffectInstance effect, boolean reapply, Entity entity)
    {
        ((LivingEntityInvokerMixin)ent).callOnEffectUpdated(effect, reapply, entity);
    }

    public static class BlockInfo
    {
        public final LevelReader world;
        public final BlockState state;
        public final BlockPos pos;

        public BlockInfo(LevelReader world, BlockState state, BlockPos pos) {
            this.world = world;
            this.state = state;
            this.pos = pos;
        }
    }
}
