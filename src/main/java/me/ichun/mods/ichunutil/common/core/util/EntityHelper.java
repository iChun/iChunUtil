package me.ichun.mods.ichunutil.common.core.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.BossInfoLerping;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Method;
import java.util.*;

public class EntityHelper
{
    public static final UUID uuidExample = UUID.fromString("DEADBEEF-DEAD-BEEF-DEAD-DEADBEEFD00D");

    //Game Profile lookup service
    public static HashMap<String, GameProfile> gameProfileCache = new HashMap<>();
    public static PlayerProfileCache profileCache;
    public static MinecraftSessionService sessionService;

    private static GameProfile dummyProfile = new GameProfile(uuidExample, "ForgeDev");

    @SideOnly(Side.CLIENT)
    public static void injectMinecraftPlayerGameProfile()
    {
        gameProfileCache.put(Minecraft.getMinecraft().getSession().getUsername(), Minecraft.getMinecraft().getSession().getProfile());
    }

    public static GameProfile getDummyGameProfile()
    {
        return dummyProfile;
    }

    public static GameProfile getGameProfile(String playerName)
    {
        if(gameProfileCache.containsKey(playerName))
        {
            return gameProfileCache.get(playerName);
        }

        if(profileCache == null || sessionService == null)
        {
            iChunUtil.proxy.setGameProfileLookupService();
        }

        GameProfile gameprofile = profileCache.getGameProfileForUsername(playerName);

        if(gameprofile == null)
        {
            return new GameProfile(null, playerName);
        }
        else
        {
            Property property = Iterables.getFirst(gameprofile.getProperties().get("textures"), null);

            if(property == null)
            {
                gameprofile = sessionService.fillProfileProperties(gameprofile, true);
                gameProfileCache.put(playerName, gameprofile);
            }

            return gameprofile;
        }
    }

    public static final Map<UUID, BossInfoLerping> BOSS_INFO_STORE = Maps.newLinkedHashMap(); //These aren't even really necessary anymore, Clientside boss data doesn't store boss info and stuff like that, server does tracking.

    @SideOnly(Side.CLIENT)
    public static void storeBossStatus()
    {
        BOSS_INFO_STORE.clear();
        Iterator<Map.Entry<UUID, BossInfoLerping>> ite = Minecraft.getMinecraft().ingameGUI.getBossOverlay().mapBossInfos.entrySet().iterator();
        while(ite.hasNext())
        {
            Map.Entry<UUID, BossInfoLerping> e = ite.next();
            BOSS_INFO_STORE.put(e.getKey(), e.getValue());
            ite.remove();
        }
    }

    @SideOnly(Side.CLIENT)
    public static void restoreBossStatus()
    {
        Map<UUID, BossInfoLerping> bossMap = Minecraft.getMinecraft().ingameGUI.getBossOverlay().mapBossInfos;
        Iterator<Map.Entry<UUID, BossInfoLerping>> ite = BOSS_INFO_STORE.entrySet().iterator();
        while(ite.hasNext())
        {
            Map.Entry<UUID, BossInfoLerping> e = ite.next();
            bossMap.put(e.getKey(), e.getValue());
            ite.remove();
        }
        BOSS_INFO_STORE.clear();
    }

    public static boolean consumeInventoryItem(InventoryPlayer inventory, Item item)
    {
        if(inventory.offHandInventory[0] != null && inventory.offHandInventory[0].getItem() == item)
        {
            if(--inventory.offHandInventory[0].stackSize <= 0)
            {
                inventory.offHandInventory[0] = null;
            }

            inventory.markDirty();
            return true;
        }

        for(int i = 0; i < inventory.mainInventory.length; ++i)
        {
            if(inventory.mainInventory[i] != null && inventory.mainInventory[i].getItem() == item)
            {
                if(--inventory.mainInventory[i].stackSize <= 0)
                {
                    inventory.mainInventory[i] = null;
                }

                inventory.markDirty();
                return true;
            }
        }

        return false;
    }

    public static boolean consumeInventoryItem(InventoryPlayer inventory, Item item, int damage, int amount)
    {
        boolean offhand = false;
        int found = 0;
        if(inventory.offHandInventory[0] != null && inventory.offHandInventory[0].getItem() == item && (inventory.offHandInventory[0].getItemDamage() == damage || inventory.offHandInventory[0].getItemDamage() == Short.MAX_VALUE))
        {
            found += inventory.offHandInventory[0].stackSize;
            offhand = true;
        }
        ItemStack[] stacks = Arrays.stream(inventory.mainInventory).filter(is -> is != null && is.getItem() == item && (is.getItemDamage() == damage || is.getItemDamage() == Short.MAX_VALUE)).toArray(ItemStack[]::new);
        for(ItemStack is : stacks)
        {
            found += is.stackSize;
        }
        if(found < amount)
        {
            return false;
        }
        if(offhand)
        {
            while(amount > 0 && inventory.offHandInventory[0].stackSize > 0)
            {
                amount--;
                if(--inventory.offHandInventory[0].stackSize <= 0)
                {
                    inventory.offHandInventory[0] = null;
                }
            }
            for(int i = 0; i < inventory.mainInventory.length; i++)
            {
                ItemStack is = inventory.mainInventory[i];
                if(is != null && is.getItem() == item && (is.getItemDamage() == damage || is.getItemDamage() == Short.MAX_VALUE))
                {
                    while(amount > 0 && is.stackSize > 0)
                    {
                        amount--;
                        if(--is.stackSize <= 0)
                        {
                            inventory.mainInventory[i] = null;
                        }
                    }
                }
                if(amount <= 0)
                {
                    break;
                }
            }
        }


        return true;
    }

    public static void playSoundAtEntity(Entity ent, SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch)
    {
        ent.worldObj.playSound(ent.worldObj.isRemote ? iChunUtil.proxy.getMcPlayer() : null, ent.posX, ent.posY + ent.getEyeHeight(), ent.posZ, soundEvent, soundCategory, volume, pitch); // sound will not play if the world is a WorldClient unless the entity == mc.thePlayer.
    }

    public static <T extends EntityLivingBase> SoundEvent getHurtSound(T ent, Class clz)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfHelper.obfuscated() ? ObfHelper.getHurtSoundObf : ObfHelper.getHurtSoundDeobf);
            m.setAccessible(true);
            return (SoundEvent)m.invoke(ent);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != EntityLivingBase.class)
            {
                return getHurtSound(ent, clz.getSuperclass());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return SoundEvents.ENTITY_GENERIC_HURT;
    }

    public static <T extends EntityLivingBase> SoundEvent getDeathSound(T ent, Class clz)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfHelper.obfuscated() ? ObfHelper.getDeathSoundObf : ObfHelper.getDeathSoundDeobf);
            m.setAccessible(true);
            return (SoundEvent)m.invoke(ent);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != EntityLivingBase.class)
            {
                return getDeathSound(ent, clz.getSuperclass());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return SoundEvents.ENTITY_GENERIC_DEATH;
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

    public static void faceEntity(Entity facer, Entity faced, float maxYaw, float maxPitch)
    {
        faceLocation(facer, faced.posX, (faced instanceof EntityLivingBase ? (faced.posY + faced.getEyeHeight()) : (faced.getEntityBoundingBox().minY + faced.getEntityBoundingBox().maxY) / 2D), faced.posZ, maxYaw, maxPitch);
    }

    public static void faceLocation(Entity facer, double posX, double posY, double posZ, float maxYaw, float maxPitch)
    {
        double d0 = posX - facer.posX;
        double d1 = posZ - facer.posZ;
        double d2 = posY - (facer.posY + (double)facer.getEyeHeight());

        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));
        facer.rotationPitch = updateRotation(facer.rotationPitch, f3, maxPitch);
        facer.rotationYaw = updateRotation(facer.rotationYaw, f2, maxYaw);
    }

    public static Vec3d getEntityPositionEyes(Entity ent, float partialTicks)
    {
        if(partialTicks == 1.0F)
        {
            return new Vec3d(ent.posX, ent.posY + (double)ent.getEyeHeight(), ent.posZ);
        }
        else
        {
            double d0 = ent.prevPosX + (ent.posX - ent.prevPosX) * (double)partialTicks;
            double d1 = ent.prevPosY + (ent.posY - ent.prevPosY) * (double)partialTicks + (double)ent.getEyeHeight();
            double d2 = ent.prevPosZ + (ent.posZ - ent.prevPosZ) * (double)partialTicks;
            return new Vec3d(d0, d1, d2);
        }
    }

    public static float interpolateRotation(float prevRotation, float nextRotation, float partialTick)
    {
        float f3;

        for(f3 = nextRotation - prevRotation; f3 < -180.0F; f3 += 360.0F)
        {
            ;
        }

        while(f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return prevRotation + partialTick * f3;
    }

    public static float interpolateValues(float prevVal, float nextVal, float partialTick)
    {
        return prevVal + partialTick * (nextVal - prevVal);
    }

    public static void setVelocity(Entity entity, double d, double d1, double d2)
    {
        entity.motionX = d;
        entity.motionY = d1;
        entity.motionZ = d2;
    }

    public static RayTraceResult getEntityLook(Entity ent, double d)
    {
        return getEntityLook(ent, d, false);
    }

    public static RayTraceResult getEntityLook(Entity ent, double d, boolean ignoreEntities) //goes through liquid
    {
        return getEntityLook(ent, d, ignoreEntities, false, true, 1.0F);
    }

    public static RayTraceResult getEntityLook(Entity ent, double d, boolean ignoreEntities, boolean ignoreTransparentBlocks, boolean ignoreLiquid, float renderTick)
    {
        Vec3d vec3 = getEntityPositionEyes(ent, renderTick);
        Vec3d vec31 = ent.getLook(renderTick);
        Vec3d vec32 = vec3.addVector(vec31.xCoord * d, vec31.yCoord * d, vec31.zCoord * d);

        RayTraceResult rayTrace = rayTraceBlocks(ent.worldObj, d, vec3, vec32, !ignoreLiquid, ignoreTransparentBlocks, false, true);

        if(!ignoreEntities)
        {
            double dist = d;
            if(rayTrace != null)
            {
                dist = rayTrace.hitVec.distanceTo(vec3);
            }

            Entity entityTrace = null;
            Vec3d vec33 = null;
            float f = 1.0F;
            List<Entity> list = ent.worldObj.getEntitiesInAABBexcluding(ent, ent.getEntityBoundingBox().addCoord(vec31.xCoord * dist, vec31.yCoord * dist, vec31.zCoord * dist).expand((double)f, (double)f, (double)f), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
            {
                public boolean apply(Entity p_apply_1_)
                {
                    return p_apply_1_.canBeCollidedWith();
                }
            }));
            double d2 = dist;

            for(int j = 0; j < list.size(); ++j)
            {
                Entity entity1 = (Entity)list.get(j);
                float f1 = entity1.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double)f1, (double)f1, (double)f1);
                RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if(axisalignedbb.isVecInside(vec3))
                {
                    if(d2 >= 0.0D)
                    {
                        entityTrace = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                }
                else if(movingobjectposition != null)
                {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if(d3 < d2 || d2 == 0.0D)
                    {
                        if(entity1 == ent.getRidingEntity() && !ent.canRiderInteract())
                        {
                            if(d2 == 0.0D)
                            {
                                entityTrace = entity1;
                                vec33 = movingobjectposition.hitVec;
                            }
                        }
                        else
                        {
                            entityTrace = entity1;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }

            if(entityTrace != null && (d2 < dist || rayTrace == null))
            {
                rayTrace = new RayTraceResult(entityTrace, vec33);
            }

        }

        return rayTrace;
    }

    public static RayTraceResult rayTraceBlocks(World world, double dist, Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreTransparentBlocks, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock)
    {
        if(!Double.isNaN(vec31.xCoord) && !Double.isNaN(vec31.yCoord) && !Double.isNaN(vec31.zCoord))
        {
            if(!Double.isNaN(vec32.xCoord) && !Double.isNaN(vec32.yCoord) && !Double.isNaN(vec32.zCoord))
            {
                int i = MathHelper.floor_double(vec32.xCoord);
                int j = MathHelper.floor_double(vec32.yCoord);
                int k = MathHelper.floor_double(vec32.zCoord);
                int l = MathHelper.floor_double(vec31.xCoord);
                int i1 = MathHelper.floor_double(vec31.yCoord);
                int j1 = MathHelper.floor_double(vec31.zCoord);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                IBlockState iblockstate = world.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid) && !(ignoreTransparentBlocks && isTransparent(block, iblockstate, world, blockpos)))
                {
                    RayTraceResult mop = iblockstate.collisionRayTrace(world, blockpos, vec31, vec32);

                    if(mop != null)
                    {
                        return mop;
                    }
                }

                RayTraceResult movingobjectposition2 = null;
                int k1 = (int)Math.ceil(dist + 1);

                while(k1-- >= 0)
                {
                    if(Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord))
                    {
                        return null;
                    }

                    if(l == i && i1 == j && j1 == k)
                    {
                        return returnLastUncollidableBlock ? movingobjectposition2 : null;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if(i > l)
                    {
                        d0 = (double)l + 1.0D;
                    }
                    else if(i < l)
                    {
                        d0 = (double)l + 0.0D;
                    }
                    else
                    {
                        flag2 = false;
                    }

                    if(j > i1)
                    {
                        d1 = (double)i1 + 1.0D;
                    }
                    else if(j < i1)
                    {
                        d1 = (double)i1 + 0.0D;
                    }
                    else
                    {
                        flag = false;
                    }

                    if(k > j1)
                    {
                        d2 = (double)j1 + 1.0D;
                    }
                    else if(k < j1)
                    {
                        d2 = (double)j1 + 0.0D;
                    }
                    else
                    {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.xCoord - vec31.xCoord;
                    double d7 = vec32.yCoord - vec31.yCoord;
                    double d8 = vec32.zCoord - vec31.zCoord;

                    if(flag2)
                    {
                        d3 = (d0 - vec31.xCoord) / d6;
                    }

                    if(flag)
                    {
                        d4 = (d1 - vec31.yCoord) / d7;
                    }

                    if(flag1)
                    {
                        d5 = (d2 - vec31.zCoord) / d8;
                    }

                    if(d3 == -0.0D)
                    {
                        d3 = -1.0E-4D;
                    }

                    if(d4 == -0.0D)
                    {
                        d4 = -1.0E-4D;
                    }

                    if(d5 == -0.0D)
                    {
                        d5 = -1.0E-4D;
                    }

                    EnumFacing enumfacing;

                    if(d3 < d4 && d3 < d5)
                    {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3d(d0, vec31.yCoord + d7 * d3, vec31.zCoord + d8 * d3);
                    }
                    else if(d4 < d5)
                    {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3d(vec31.xCoord + d6 * d4, d1, vec31.zCoord + d8 * d4);
                    }
                    else
                    {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3d(vec31.xCoord + d6 * d5, vec31.yCoord + d7 * d5, d2);
                    }

                    l = MathHelper.floor_double(vec31.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor_double(vec31.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor_double(vec31.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    IBlockState iblockstate1 = world.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if((!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) && !(ignoreTransparentBlocks && isTransparent(block1, iblockstate1, world, blockpos)))
                    {
                        if(block1.canCollideCheck(iblockstate1, stopOnLiquid))
                        {
                            RayTraceResult movingobjectposition1 = iblockstate1.collisionRayTrace(world, blockpos, vec31, vec32);

                            if(movingobjectposition1 != null)
                            {
                                return movingobjectposition1;
                            }
                        }
                        else
                        {
                            movingobjectposition2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
                        }
                    }
                }

                return returnLastUncollidableBlock ? movingobjectposition2 : null;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    public static void putEntityWithinAABB(Entity ent, AxisAlignedBB aabb)
    {
        if(ent.getEntityBoundingBox().maxX > aabb.maxX)
        {
            ent.posX += aabb.maxX - ent.getEntityBoundingBox().maxX;
        }
        if(ent.getEntityBoundingBox().minX < aabb.minX)
        {
            ent.posX += aabb.minX - ent.getEntityBoundingBox().minX;
        }
        if(ent.posY + ent.getEyeHeight() > aabb.maxY)
        {
            ent.posY += aabb.maxY - ent.posY - ent.getEyeHeight();
        }
        if(ent.posY < aabb.minY)
        {
            ent.posY += aabb.minY - ent.posY + 0.001D;
        }
        if(ent.getEntityBoundingBox().maxZ > aabb.maxZ)
        {
            ent.posZ += aabb.maxZ - ent.getEntityBoundingBox().maxZ;
        }
        if(ent.getEntityBoundingBox().minZ < aabb.minZ)
        {
            ent.posZ += aabb.minZ - ent.getEntityBoundingBox().minZ;
        }
    }

    public static double[] simulateMoveEntity(Entity ent, double x, double y, double z) //does not check for step height.
    {
        if(ent.noClip)
        {
            return new double[] { x, y, z };
        }
        else
        {
            double d3 = x;
            double d4 = y;
            double d5 = z;
            boolean flag = ent.onGround && ent.isSneaking() && ent instanceof EntityPlayer;

            if(flag)
            {
                for(double d6 = 0.05D; x != 0.0D && ent.worldObj.getCollisionBoxes(ent, ent.getEntityBoundingBox().offset(x, -1.0D, 0.0D)).isEmpty(); d3 = x)
                {
                    if(x < 0.05D && x >= -0.05D)
                    {
                        x = 0.0D;
                    }
                    else if(x > 0.0D)
                    {
                        x -= 0.05D;
                    }
                    else
                    {
                        x += 0.05D;
                    }
                }

                for(; z != 0.0D && ent.worldObj.getCollisionBoxes(ent, ent.getEntityBoundingBox().offset(0.0D, -1.0D, z)).isEmpty(); d5 = z)
                {
                    if(z < 0.05D && z >= -0.05D)
                    {
                        z = 0.0D;
                    }
                    else if(z > 0.0D)
                    {
                        z -= 0.05D;
                    }
                    else
                    {
                        z += 0.05D;
                    }
                }

                for(; x != 0.0D && z != 0.0D && ent.worldObj.getCollisionBoxes(ent, ent.getEntityBoundingBox().offset(x, -1.0D, z)).isEmpty(); d5 = z)
                {
                    if(x < 0.05D && x >= -0.05D)
                    {
                        x = 0.0D;
                    }
                    else if(x > 0.0D)
                    {
                        x -= 0.05D;
                    }
                    else
                    {
                        x += 0.05D;
                    }

                    d3 = x;

                    if(z < 0.05D && z >= -0.05D)
                    {
                        z = 0.0D;
                    }
                    else if(z > 0.0D)
                    {
                        z -= 0.05D;
                    }
                    else
                    {
                        z += 0.05D;
                    }
                }
            }

            List<AxisAlignedBB> list1 = ent.worldObj.getCollisionBoxes(ent, ent.getEntityBoundingBox().addCoord(x, y, z));
            AxisAlignedBB axisalignedbb = ent.getEntityBoundingBox();
            int i = 0;

            for(int j = list1.size(); i < j; ++i)
            {
                y = list1.get(i).calculateYOffset(ent.getEntityBoundingBox(), y);
            }

            ent.setEntityBoundingBox(ent.getEntityBoundingBox().offset(0.0D, y, 0.0D));
            int j4 = 0;

            for(int k = list1.size(); j4 < k; ++j4)
            {
                x = list1.get(j4).calculateXOffset(ent.getEntityBoundingBox(), x);
            }

            ent.setEntityBoundingBox(ent.getEntityBoundingBox().offset(x, 0.0D, 0.0D));
            j4 = 0;

            for(int k4 = list1.size(); j4 < k4; ++j4)
            {
                z = list1.get(j4).calculateZOffset(ent.getEntityBoundingBox(), z);
            }

            ent.setEntityBoundingBox(axisalignedbb); //restore the bounding box

            return new double[] { x, y, z };
        }
    }


    public static void addPosition(Entity living, double offset, boolean subtract, int axis)
    {
        if(subtract)
        {
            offset = -offset;
        }
        if(axis == 0) //X axis
        {
            living.lastTickPosX += offset;
            living.prevPosX += offset;
            living.posX += offset;
        }
        else if(axis == 1) //Y axis
        {
            living.lastTickPosY += offset;
            living.prevPosY += offset;
            living.posY += offset;
        }
        else if(axis == 2) //Z axis
        {
            living.lastTickPosZ += offset;
            living.prevPosZ += offset;
            living.posZ += offset;
        }
    }

    public static void relocateEntity(EnumFacing.Axis axis, Entity ent, double degree, double originX, double originY, double originZ, double destX, double destY, double destZ, float renderTick)
    {
        double rads = Math.toRadians(degree);
        if(axis == EnumFacing.Axis.Y)
        {
            //position offset
            //            double oriX = ent.posX - originX;
            //            double oriZ = ent.posZ - originZ;
            //
            //            double x1 =  oriX * Math.cos(rads) + oriZ * Math.sin(rads);
            //            double z1 = -oriX * Math.sin(rads) + oriZ * Math.cos(rads);
            //
            //            addPosition(ent, (destX - originX) + x1, false, 0);
            //            addPosition(ent, (destY - originY), false, 1);
            //            addPosition(ent, (destZ - originZ) + z1, false, 2);

            //            //motion offset
            //            double moX = ent.motionX;
            //            double moZ = ent.motionZ;
            //
            //            double x2 =  moX * Math.cos(rads) + moZ * Math.sin(rads);
            //            double z2 = -moX * Math.sin(rads) + moZ * Math.cos(rads);
            //
            //            ent.motionX = x2;
            //            ent.motionZ = z2;

            //rotation offset
            iChunUtil.proxy.adjustRotation(ent, (float)-degree, 0F);
        }
    }

    public static AxisAlignedBB rotateAABB(EnumFacing.Axis axis, AxisAlignedBB aabb, double degree, double originX, double originY, double originZ)
    {
        double rads = Math.toRadians(degree);
        if(axis == EnumFacing.Axis.X)
        {
            double oriZ = aabb.minZ - originZ;
            double oriY = aabb.minY - originY;

            double z1 = oriZ * Math.cos(rads) + oriY * Math.sin(rads);
            double y1 = -oriZ * Math.sin(rads) + oriY * Math.cos(rads);

            oriZ = aabb.maxZ - originZ;
            oriY = aabb.maxY - originY;

            double z2 = oriZ * Math.cos(rads) + oriY * Math.sin(rads);
            double y2 = -oriZ * Math.sin(rads) + oriY * Math.cos(rads);

            return new AxisAlignedBB(aabb.minX, Math.min(y1, y2) + originY, Math.min(z1, z2) + originZ, aabb.maxX, Math.max(y1, y2) + originY, Math.max(z1, z2) + originZ);
        }
        else if(axis == EnumFacing.Axis.Y)
        {
            double oriX = aabb.minX - originX;
            double oriZ = aabb.minZ - originZ;

            double x1 = oriX * Math.cos(rads) + oriZ * Math.sin(rads);
            double z1 = -oriX * Math.sin(rads) + oriZ * Math.cos(rads);

            oriX = aabb.maxX - originX;
            oriZ = aabb.maxZ - originZ;

            double x2 = oriX * Math.cos(rads) + oriZ * Math.sin(rads);
            double z2 = -oriX * Math.sin(rads) + oriZ * Math.cos(rads);

            return new AxisAlignedBB(Math.min(x1, x2) + originX, aabb.minY, Math.min(z1, z2) + originZ, Math.max(x1, x2) + originX, aabb.maxY, Math.max(z1, z2) + originZ);
        }
        else if(axis == EnumFacing.Axis.Z)
        {
            double oriX = aabb.minX - originX;
            double oriY = aabb.minY - originY;

            double x1 = oriX * Math.cos(rads) + oriY * Math.sin(rads);
            double y1 = -oriX * Math.sin(rads) + oriY * Math.cos(rads);

            oriX = aabb.maxX - originX;
            oriY = aabb.maxY - originY;

            double x2 = oriX * Math.cos(rads) + oriY * Math.sin(rads);
            double y2 = -oriX * Math.sin(rads) + oriY * Math.cos(rads);

            return new AxisAlignedBB(Math.min(x1, x2) + originX, Math.min(y1, y2) + originY, aabb.minZ, Math.max(x1, x2) + originX, Math.max(y1, y2) + originY, aabb.maxZ);
        }
        return aabb;
    }

    public static boolean destroyBlocksInAABB(Entity ent, AxisAlignedBB aabb)
    {
        int i = MathHelper.floor_double(aabb.minX);
        int j = MathHelper.floor_double(aabb.minY);
        int k = MathHelper.floor_double(aabb.minZ);
        int l = MathHelper.floor_double(aabb.maxX);
        int i1 = MathHelper.floor_double(aabb.maxY);
        int j1 = MathHelper.floor_double(aabb.maxZ);
        boolean flag = false;
        boolean flag1 = false;

        for(int k1 = i; k1 <= l; ++k1)
        {
            for(int l1 = j; l1 <= i1; ++l1)
            {
                for(int i2 = k; i2 <= j1; ++i2)
                {
                    BlockPos blockpos = new BlockPos(k1, l1, i2);
                    IBlockState iblockstate = ent.worldObj.getBlockState(blockpos);
                    Block block = ent.worldObj.getBlockState(blockpos).getBlock();

                    if(!block.isAir(iblockstate, ent.worldObj, blockpos) && iblockstate.getMaterial() != Material.FIRE)
                    {
                        if(!ent.worldObj.getGameRules().getBoolean("mobGriefing"))
                        {
                            flag = true;
                        }
                        else if(block.canEntityDestroy(iblockstate, ent.worldObj, blockpos, ent))
                        {
                            if(block != Blocks.COMMAND_BLOCK && block != Blocks.REPEATING_COMMAND_BLOCK && block != Blocks.CHAIN_COMMAND_BLOCK && block != Blocks.IRON_BARS && block != Blocks.END_GATEWAY)
                            {
                                flag1 = ent.worldObj.setBlockToAir(blockpos) || flag1;
                            }
                            else
                            {
                                flag = true;
                            }
                        }
                        else
                        {
                            flag = true;
                        }
                    }
                }
            }
        }

        if(flag1)
        {
            double d0 = aabb.minX + (aabb.maxX - aabb.minX) * (double)ent.worldObj.rand.nextFloat();
            double d1 = aabb.minY + (aabb.maxY - aabb.minY) * (double)ent.worldObj.rand.nextFloat();
            double d2 = aabb.minZ + (aabb.maxZ - aabb.minZ) * (double)ent.worldObj.rand.nextFloat();
            ent.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }

        return flag;
    }

    public static NBTTagCompound getPlayerPersistentData(EntityPlayer player) //gets the persisted NBT.
    {
        NBTTagCompound persistentTag = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentTag);
        return persistentTag;
    }

    public static NBTTagCompound getPlayerPersistentData(EntityPlayer player, String name) //gets a tag within the persisted NBT
    {
        NBTTagCompound persistentTag = getPlayerPersistentData(player).getCompoundTag(name);
        getPlayerPersistentData(player).setTag(name, persistentTag);
        return persistentTag;
    }

    public static boolean isTransparent(Block block, IBlockState state, World world, BlockPos pos)
    {
        return block.getLightOpacity(state, world, pos) != 0xff;
    }
}
