package me.ichun.mods.ichunutil.common.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
}
