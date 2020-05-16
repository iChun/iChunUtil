package me.ichun.mods.ichunutil.common.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
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


    public static boolean isFakePlayer(@Nonnull ServerPlayerEntity player) //Fake Players extend ServerPlayerEntity. Please check or cast first
    {
        return player instanceof FakePlayer || player.connection == null; // || player.getName().getUnformattedComponentText().toLowerCase().startsWith("fakeplayer") || player.getName().getUnformattedComponentText().toLowerCase().startsWith("[minecraft]");
    }
}
