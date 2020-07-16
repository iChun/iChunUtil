package me.ichun.mods.ichunutil.common.entity.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import cpw.mods.modlauncher.api.INameMappingService;
import me.ichun.mods.ichunutil.common.util.ObfHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

public class EntityHelper
{
    public static final UUID UUID_EXAMPLE = UUID.fromString("DEADBEEF-DEAD-BEEF-DEAD-DEADBEEFD00D");

    //Game Profile lookup service
    public static final Map<String, GameProfile> GAME_PROFILE_CACHE = Collections.synchronizedMap(new HashMap<>());
    private static PlayerProfileCache profileCache;
    private static MinecraftSessionService sessionService;

    private static final GameProfile DUMMY_PROFILE = new GameProfile(UUID_EXAMPLE, "ForgeDev");

    @OnlyIn(Dist.CLIENT)
    public static void injectMinecraftPlayerGameProfile()
    {
        GAME_PROFILE_CACHE.put(Minecraft.getInstance().getSession().getUsername(), Minecraft.getInstance().getSession().getProfile());
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

        if(profileCache == null || sessionService == null)
        {
            if(FMLEnvironment.dist.isDedicatedServer())
            {
                sessionService = ServerLifecycleHooks.getCurrentServer().getMinecraftSessionService();
                profileCache = ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache();
            }
            else
            {
                setClientProfileLookupObjects();
            }
        }

        GameProfile gameprofile = uuid != null ? profileCache.getProfileByUUID(uuid) : profileCache.getGameProfileForUsername(playerName);
        if(gameprofile == null)
        {
            gameprofile = sessionService.fillProfileProperties(new GameProfile(uuid, playerName), true);
        }

        Property property = Iterables.getFirst(gameprofile.getProperties().get("textures"), null);
        if(property == null)
        {
            gameprofile = sessionService.fillProfileProperties(gameprofile, true);
        }

        GAME_PROFILE_CACHE.put(gameprofile.getName(), gameprofile);
        return gameprofile;
    }

    @OnlyIn(Dist.CLIENT)
    private static void setClientProfileLookupObjects()
    {
        YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(Minecraft.getInstance().getProxy(), UUID.randomUUID().toString());
        sessionService = yggdrasilauthenticationservice.createMinecraftSessionService();
        GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
        profileCache = new PlayerProfileCache(gameprofilerepository, new File(Minecraft.getInstance().gameDir, MinecraftServer.USER_CACHE_FILE.getName()));
    }

    public static PlayerEntity getClientPlayer()
    {
        if(FMLEnvironment.dist.isClient())
        {
            return getMinecraftPlayer();
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    private static PlayerEntity getMinecraftPlayer()
    {
        return Minecraft.getInstance().player;
    }

    @OnlyIn(Dist.CLIENT)
    public static void nudgeHand(float mag)
    {
        Minecraft.getInstance().player.renderArmPitch += mag;
    }

    public static float updateRotation(float oriRot, float intendedRot, float maxChange)
    {
        float var4 = MathHelper.wrapDegrees(intendedRot - oriRot);

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

    public static int countInInventory(IInventory inv, Item item)
    {
        int totalCount = 0;

        for(int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack is = inv.getStackInSlot(i);
            if(!is.isEmpty() && is.getItem() == item)
            {
                totalCount += is.getCount();
            }
        }

        return totalCount;
    }

    public static boolean consumeInventoryItem(IInventory inventory, Item item)
    {
        return consumeInventoryItem(inventory, item, 1);
    }

    /**
     * @param inventory inventory to access
     * @param itemIn item to check for, null for any item
     * @param removeCount amount to remove, negative numbers to remove entire stack.
     * @return if items were removed successfully (or removeCount == 0).
     */
    public static boolean consumeInventoryItem(IInventory inventory, Item itemIn, int removeCount)
    {
        if(removeCount > 0 && countInInventory(inventory, itemIn) < removeCount)
        {
            return false;
        }

        if (removeCount != 0)
        {
            int removed = 0;
            for(int j = inventory.getSizeInventory() - 1; j >= 0; --j)
            {
                ItemStack itemstack = inventory.getStackInSlot(j);

                if(!itemstack.isEmpty() && (itemIn == null || itemstack.getItem() == itemIn))
                {
                    int removeFromStack = removeCount < 0 ? itemstack.getCount() : Math.min(removeCount - removed, itemstack.getCount());
                    removed += removeFromStack;

                    itemstack.shrink(removeFromStack);

                    if(itemstack.isEmpty())
                    {
                        inventory.setInventorySlotContents(j, ItemStack.EMPTY);
                        inventory.markDirty();
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

    public static void putEntityWithinAABB(Entity ent, AxisAlignedBB aabb)
    {
        double posX = ent.getPosX();
        double posY = ent.getPosY();
        double posZ = ent.getPosZ();
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
        ent.setPosition(posX, posY, posZ);
    }

    public static RayTraceResult getEntityLook(Entity ent, double d)
    {
        return getEntityLook(ent, d, true);
    }

    public static RayTraceResult getEntityLook(Entity ent, double d, boolean checkEntities)
    {
        return getEntityLook(ent, d, checkEntities, 1F);
    }

    public static RayTraceResult getEntityLook(Entity ent, double d, boolean checkEntities, float partialTick)
    {
        return rayTrace(ent.world, ent.getEyePosition(partialTick), ent.getEyePosition(partialTick).add(ent.getLook(partialTick).mul(d, d, d)), ent, checkEntities, RayTraceContext.BlockMode.COLLIDER, b -> true, RayTraceContext.FluidMode.NONE, e -> true);
    }

    public static RayTraceResult rayTrace(World world, Vector3d origin, Vector3d dest, @Nonnull Entity exception, boolean checkEntityCollision, RayTraceContext.BlockMode blockMode, Predicate<BlockInfo> blockFilter, RayTraceContext.FluidMode fluidMode, Predicate<Entity> filter) {
        RayTraceResult raytraceresult = IBlockReader.doRayTrace(new RayTraceContext(origin, dest, blockMode, fluidMode, exception), (context, pos) -> {
            BlockState blockstate = world.getBlockState(pos);
            if(blockFilter.test(new BlockInfo(world, blockstate, pos))) //Taken from IBlockReader.rayTraceBlocks
            {
                FluidState ifluidstate = world.getFluidState(pos);
                Vector3d vec3d = context.getStartVec();
                Vector3d vec3d1 = context.getEndVec();
                VoxelShape voxelshape = context.getBlockShape(blockstate, world, pos);
                BlockRayTraceResult blockraytraceresult = world.rayTraceBlocks(vec3d, vec3d1, pos, voxelshape, blockstate);
                VoxelShape voxelshape1 = context.getFluidShape(ifluidstate, world, pos);
                BlockRayTraceResult blockraytraceresult1 = voxelshape1.rayTrace(vec3d, vec3d1, pos);
                double d0 = blockraytraceresult == null ? Double.MAX_VALUE : context.getStartVec().squareDistanceTo(blockraytraceresult.getHitVec());
                double d1 = blockraytraceresult1 == null ? Double.MAX_VALUE : context.getStartVec().squareDistanceTo(blockraytraceresult1.getHitVec());
                return d0 <= d1 ? blockraytraceresult : blockraytraceresult1;
            }
            return null;
        }, (context) -> {
            Vector3d vec3d = context.getStartVec().subtract(context.getEndVec()); //getStartVec, getEndVec
            return BlockRayTraceResult.createMiss(context.getEndVec(), Direction.getFacingFromVector(vec3d.x, vec3d.y, vec3d.z), new BlockPos(context.getEndVec()));
        });
        if (checkEntityCollision) {
            if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
                dest = raytraceresult.getHitVec();
            }

            AxisAlignedBB aabb = new AxisAlignedBB(origin, dest).grow(1F);
            RayTraceResult raytraceresult1 = ProjectileHelper.rayTraceEntities(world, exception, origin, dest, aabb, filter);
            if (raytraceresult1 != null) {
                raytraceresult = raytraceresult1;
            }
        }

        return raytraceresult;
    }

    private static Set<Entity> getEntityAndMount(Entity rider) { //taken from ProjectileHelper
        Entity entity = rider.getRidingEntity();
        return entity != null ? ImmutableSet.of(rider, entity) : ImmutableSet.of(rider);
    }

    public static void faceEntity(Entity facer, Entity faced, float maxYaw, float maxPitch)
    {
        faceLocation(facer, faced.getPosX(), (faced instanceof LivingEntity ? (faced.getPosY() + faced.getEyeHeight()) : (faced.getBoundingBox().minY + faced.getBoundingBox().maxY) / 2D), faced.getPosZ(), maxYaw, maxPitch);
    }

    public static void faceLocation(Entity facer, double posX, double posY, double posZ, float maxYaw, float maxPitch)
    {
        double d0 = posX - facer.getPosX();
        double d1 = posZ - facer.getPosZ();
        double d2 = posY - (facer.getPosY() + (double)facer.getEyeHeight());

        double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1);
        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));
        facer.rotationPitch = updateRotation(facer.rotationPitch, f3, maxPitch);
        facer.rotationYaw = updateRotation(facer.rotationYaw, f2, maxYaw);
    }

    public static Vector3d getVectorForRotation(float pitch, float yaw) {
        float f = pitch * ((float)Math.PI / 180F);
        float f1 = -yaw * ((float)Math.PI / 180F);
        float f2 = MathHelper.cos(f1);
        float f3 = MathHelper.sin(f1);
        float f4 = MathHelper.cos(f);
        float f5 = MathHelper.sin(f);
        return new Vector3d(f3 * f4, -f5, f2 * f4);
    }

    public static Vector3d getVectorRenderYawOffset(float renderYawOffset) {
        return getVectorForRotation(0, renderYawOffset);
    }

    public static void playSound(@Nonnull Entity ent, SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch)
    {
        ent.getEntityWorld().playSound(ent.getEntityWorld().isRemote ? getClientPlayer() : null, ent.getPosX(), ent.getPosY() + ent.getEyeHeight(), ent.getPosZ(), soundEvent, soundCategory, volume, pitch); // sound will not play if the world is a WorldClient unless the entity == mc.player.
    }

    public static boolean destroyBlocksInAABB(Entity ent, AxisAlignedBB aabb, Predicate<BlockInfo> blockFilter)
    {
        int i = MathHelper.floor(aabb.minX);
        int j = MathHelper.floor(aabb.minY);
        int k = MathHelper.floor(aabb.minZ);
        int l = MathHelper.floor(aabb.maxX);
        int i1 = MathHelper.floor(aabb.maxY);
        int j1 = MathHelper.floor(aabb.maxZ);
        boolean flag = false;
        boolean flag1 = false;

        for(int k1 = i; k1 <= l; ++k1)
        {
            for(int l1 = j; l1 <= i1; ++l1)
            {
                for(int i2 = k; i2 <= j1; ++i2)
                {
                    BlockPos blockpos = new BlockPos(k1, l1, i2);
                    BlockState blockstate = ent.world.getBlockState(blockpos);

                    if (!blockstate.isAir(ent.world, blockpos) && blockstate.getMaterial() != Material.FIRE) {
                        if (ForgeEventFactory.getMobGriefingEvent(ent.world, ent) && blockstate.canEntityDestroy(ent.world, blockpos, ent) && (!(ent instanceof LivingEntity) || ForgeEventFactory.onEntityDestroyBlock((LivingEntity)ent, blockpos, blockstate)) && blockFilter.test(new BlockInfo(ent.world, blockstate, blockpos))) {
                            flag1 = ent.world.removeBlock(blockpos, false) || flag1;
                        } else {
                            flag = true;
                        }
                    }
                }
            }
        }

        if(flag1)
        {
            BlockPos blockpos1 = new BlockPos(i + ent.world.rand.nextInt(l - i + 1), j + ent.world.rand.nextInt(i1 - j + 1), k + ent.world.rand.nextInt(j1 - k + 1));
            ent.world.playEvent(2008, blockpos1, 0);
        }

        return flag;
    }

    public static CompoundNBT getPlayerPersistentData(@Nonnull PlayerEntity player, @Nullable String name) //if null returns the whole persisted data tag. else get a tag by the name
    {
        CompoundNBT persistedTag = player.getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
        player.getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, persistedTag);
        if(name == null)
        {
            return persistedTag;
        }
        CompoundNBT specificTag = persistedTag.getCompound(name);
        persistedTag.put(name, specificTag);
        return specificTag;
    }

    public static boolean isFakePlayer(@Nonnull ServerPlayerEntity player) //Fake Players extend ServerPlayerEntity. Please check or cast first
    {
        return player instanceof FakePlayer || player.connection == null; // || player.getName().getUnformattedComponentText().toLowerCase().startsWith("fakeplayer") || player.getName().getUnformattedComponentText().toLowerCase().startsWith("[minecraft]");
    }

    //REFLECTIVE methods
    public static <T extends LivingEntity> SoundEvent getHurtSound(T ent, DamageSource source)
    {
        return getHurtSound(ent, ent.getClass(), source);
    }

    public static <T extends LivingEntity> SoundEvent getDeathSound(T ent)
    {
        return getDeathSound(ent, ent.getClass());
    }

    public static <T extends LivingEntity> float getSoundVolume(T ent)
    {
        return getSoundVolume(ent, ent.getClass());
    }

    public static <T extends LivingEntity> float getSoundPitch(T ent)
    {
        return getSoundPitch(ent, ent.getClass());
    }

    public static <T extends LivingEntity> void onChangedPotionEffect(T ent, EffectInstance effect, boolean reapply)
    {
        onChangedPotionEffect(ent, ent.getClass(), effect, reapply);
    }

    private static <T extends LivingEntity> SoundEvent getHurtSound(T ent, Class<?> clz, DamageSource source)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, ObfHelper.getHurtSound), DamageSource.class);
            m.setAccessible(true);
            return (SoundEvent)m.invoke(ent, source);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != LivingEntity.class)
            {
                return getHurtSound(ent, clz.getSuperclass(), source);
            }
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
        return SoundEvents.ENTITY_GENERIC_HURT;
    }

    private static <T extends LivingEntity> SoundEvent getDeathSound(T ent, Class<?> clz)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, ObfHelper.getDeathSound));
            m.setAccessible(true);
            return (SoundEvent)m.invoke(ent);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != LivingEntity.class)
            {
                return getDeathSound(ent, clz.getSuperclass());
            }
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
        return SoundEvents.ENTITY_GENERIC_DEATH;
    }

    private static <T extends LivingEntity> float getSoundVolume(T ent, Class<?> clz)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, ObfHelper.getSoundVolume));
            m.setAccessible(true);
            return (float)m.invoke(ent);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != LivingEntity.class)
            {
                return getSoundVolume(ent, clz.getSuperclass());
            }
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
        return 1F;
    }

    private static <T extends LivingEntity> float getSoundPitch(T ent, Class<?> clz)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, ObfHelper.getSoundPitch));
            m.setAccessible(true);
            return (float)m.invoke(ent);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != LivingEntity.class)
            {
                return getSoundPitch(ent, clz.getSuperclass());
            }
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
        return ent.isChild() ? (ent.getRNG().nextFloat() - ent.getRNG().nextFloat()) * 0.2F + 1.5F : (ent.getRNG().nextFloat() - ent.getRNG().nextFloat()) * 0.2F + 1.0F;
    }

    private static <T extends LivingEntity> void onChangedPotionEffect(T ent, Class<?> clz, EffectInstance effect, boolean reapply)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, ObfHelper.onChangedPotionEffect), EffectInstance.class, boolean.class);
            m.setAccessible(true);
            m.invoke(ent, effect, reapply);
            return;
        }
        catch(NoSuchMethodException e)
        {
            if(clz != LivingEntity.class)
            {
                onChangedPotionEffect(ent, clz.getSuperclass(), effect, reapply);
            }
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static class BlockInfo
    {
        public final IWorldReader world;
        public final BlockState state;
        public final BlockPos pos;

        public BlockInfo(IWorldReader world, BlockState state, BlockPos pos) {
            this.world = world;
            this.state = state;
            this.pos = pos;
        }
    }
}
