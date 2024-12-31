package me.ichun.mods.ichunutil.loader.fabric.config.loader;

import com.google.common.base.Charsets;
import com.moandjiezana.toml.Toml;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.config.ConfigToToml;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.util.WatchServiceThread;
import me.ichun.mods.ichunutil.loader.fabric.PacketChannelFabric;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class FabricConfigLoader
{
    private static final Map<ConfigBase.Type, ArrayList<ConfigBase>> REGISTERED_CONFIGS = Collections.synchronizedMap(new EnumMap<>(ConfigBase.Type.class));
    private static final PacketChannelFabric CHANNEL = new PacketChannelFabric(ResourceLocation.fromNamespaceAndPath(iChunUtil.MOD_ID, "config"), PacketConfig.class);

    private static Path currentServerConfigPath = null;

    static
    {
        ServerLifecycleEvents.SERVER_STARTING.register(FabricConfigLoader::serverStatus);
        ServerLifecycleEvents.SERVER_STOPPED.register(FabricConfigLoader::serverStatus);
        ServerPlayConnectionEvents.JOIN.register(FabricConfigLoader::sendServerConfigs);

        if(FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT))
        {
            ClientPlayConnectionEvents.DISCONNECT.register((h, c) -> ConfigHandler.onServerDisconnect());
        }
    }

    public static <T extends ConfigBase> T registerConfig(T config)
    {
        ArrayList<ConfigBase> configBases = REGISTERED_CONFIGS.computeIfAbsent(config.getConfigType(), k -> new ArrayList<>());
        configBases.add(config);

        if(config.getConfigType() != ConfigBase.Type.SERVER) //we're not on a server, might as well figure out the file path and load up the files now
        {
            loadOrCreateConfig(config, false);
        }

        return config;
    }

    public static void loadOrCreateConfig(ConfigBase config, boolean isReload)
    {
        Path configPath = getPathForConfig(config);

        if(configPath == null)
        {
            iChunUtil.LOGGER.error("Config path is null, something went wrong here.");
            return;
        }

        try
        {
            boolean loadSuccessful = false;
            //Check if the file already exists
            if(Files.exists(configPath) && Files.isRegularFile(configPath))
            {
                //File exists, load up the config and set the values up our config object
                try
                {
                    Toml toml = new Toml().read(FileUtils.readFileToString(configPath.toFile(), Charsets.UTF_8));

                    loadSuccessful = ConfigToToml.assignValuesFromToml(config, toml, isReload);
                }
                catch(IllegalStateException | IllegalAccessException e)
                {
                    iChunUtil.LOGGER.warn("Errors detected loading up config file {}. Recreating config.", configPath, e);
                }
            }

            if(!loadSuccessful)
            {
                if(!Files.exists(configPath.getParent()))
                {
                    Files.createDirectories(configPath.getParent());
                }
                //File does not exist, we're creating a new config
                String tomlString = ConfigToToml.convertToToml(config, false);
                Files.writeString(configPath, tomlString, StandardCharsets.UTF_8);
            }

            WatchServiceThread.watchFile(configPath, FabricConfigLoader::onFileChanged);

            config.setSaveMethod(() -> {
                try
                {
                    String tomlString = ConfigToToml.convertToToml(config, false);
                    Files.writeString(configPath, tomlString, StandardCharsets.UTF_8);
                }
                catch(IOException e)
                {
                    iChunUtil.LOGGER.error("Error saving config file {}", config.getFileName(), e);
                }
            });

            if(!isReload) config.onConfigLoaded();
        }
        catch(IOException e)
        {
            iChunUtil.LOGGER.error("Something went wrong loading up config {} {}", config.getConfigName(), config.getConfigType().toString(), e);
        }

    }

    private static void onFileChanged(String fileName)
    {
        REGISTERED_CONFIGS.forEach((k, v) -> v.forEach(c -> {
            if(c.getFileName().equals(fileName))
            {
                loadOrCreateConfig(c, true);
            }
        }));
    }

    private static void serverStatus(MinecraftServer server)
    {
        if(REGISTERED_CONFIGS.containsKey(ConfigBase.Type.SERVER)) //we have some server configs, we should do something
        {
            if(server == null)//server just shut down
            {
                if(currentServerConfigPath != null)
                {
                    WatchServiceThread.stopWatchFolder(currentServerConfigPath);

                    for(ConfigBase config : REGISTERED_CONFIGS.get(ConfigBase.Type.SERVER))
                    {
                        config.setSaveMethod(null); // we can't save the file anymore, it's gone, jim
                    }

                    currentServerConfigPath = null;
                }
            }
            else
            {
                currentServerConfigPath = server.getWorldPath(LevelResource.LEVEL_DATA_FILE).resolve("/serverconfig");

                for(ConfigBase config : REGISTERED_CONFIGS.get(ConfigBase.Type.SERVER))
                {
                    //Check if there is a default config for this server config and copy it over
                    Path configPath = getPathForConfig(config);
                    if(!(Files.exists(configPath) && Files.isRegularFile(configPath))) //The file doesn't exist/isn't a normal file
                    {
                        Path defaultConfigPath = iChunUtil.d().getConfigDir().getParent().resolve("/defaultconfigs").resolve(config.getFileName());

                        if(Files.exists(defaultConfigPath) && Files.isRegularFile(defaultConfigPath)) //There is a default file
                        {
                            try
                            {
                                FileUtils.copyFile(defaultConfigPath.toFile(), configPath.toFile());
                            }
                            catch(IOException e)
                            {
                                iChunUtil.LOGGER.error("Error copying default config for config {}", config.getFileName(), e);
                            }
                        }
                    }

                    loadOrCreateConfig(config, false);
                }
            }
        }
    }

    private static void sendServerConfigs(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server)
    {
        ArrayList<ConfigBase> configBases = REGISTERED_CONFIGS.get(ConfigBase.Type.SERVER);
        if(configBases != null)
        {
            for(ConfigBase configBase : configBases)
            {
                CHANNEL.sendTo(new PacketConfig(configBase.getFileName(), ConfigToToml.convertToToml(configBase, true)), handler.getPlayer());
            }
        }
    }

    public static void receiveServerConfig(String fileName, String tomlString)
    {
        ArrayList<ConfigBase> configBases = REGISTERED_CONFIGS.get(ConfigBase.Type.SERVER);
        if(configBases != null)
        {
            for(ConfigBase configBase : configBases)
            {
                if(configBase.getFileName().equals(fileName))
                {
                    try
                    {
                        configBase.cache();

                        Toml toml = new Toml().read(tomlString);

                        ConfigToToml.assignValuesFromToml(configBase, toml, true);
                    }
                    catch(IllegalAccessException | IllegalStateException e)
                    {
                        iChunUtil.LOGGER.error("Error receiving config {} from server.", fileName, e);
                    }
                }
            }
        }
    }

    @Nullable
    public static Path getPathForConfig(ConfigBase config)
    {
        if(config.getConfigType() == ConfigBase.Type.SERVER)
        {
            return currentServerConfigPath == null ? null : currentServerConfigPath.resolve(config.getFileName());
        }
        else
        {
            return iChunUtil.d().getConfigDir().resolve(config.getFileName());
        }
    }

    //    public static class ConfigTest extends ConfigBase
    //    {
    //        public int testInt = 23;
    //
    //        @Prop(min = -6)
    //        public int testIntBoundedMin = 0;
    //
    //        @Prop(max = 9)
    //        public int testIntBoundedMax = 2;
    //
    //        @Prop(min = 0, max = 2398723)
    //        public int testIntBounded = 213;
    //
    //        public double testDoub = 22D;
    //
    //        @Prop(min = 23.2D, max = 223.23323D)
    //        public double testDoubBound = 29D;
    //
    //        public boolean testBool = false;
    //
    //        public String testString = "blah";
    //
    //        @Prop(values = {"AOKAY", "NOTOKAY", "MEH"})
    //        public String testStringForced = "AOKAY";
    //
    //        public ConfigBase.Type testEnum = Type.SERVER;
    //
    //        public List<String> listExample = Util.make(new ArrayList<>(), list -> {
    //            list.add("Stringone");
    //            list.add("stringTwo");
    //            list.add("StringDashThree");
    //        });
    //
    //
    //        @NotNull
    //        @Override
    //        public String getModId()
    //        {
    //            return "THEiChunUtil";
    //        }
    //
    //        @NotNull
    //        @Override
    //        public String getConfigName()
    //        {
    //            return "TestConfigForAll";
    //        }
    //    }
}
