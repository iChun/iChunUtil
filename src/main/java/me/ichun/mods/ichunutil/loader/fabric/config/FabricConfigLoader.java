package me.ichun.mods.ichunutil.loader.fabric.config;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.moandjiezana.toml.Toml;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import me.ichun.mods.ichunutil.loader.fabric.PacketChannelFabric;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FabricConfigLoader
{
    private static final Map<ConfigBase.Type, ArrayList<ConfigBase>> REGISTERED_CONFIGS = Collections.synchronizedMap(new EnumMap<>(ConfigBase.Type.class));
    private static final Map<Path, WatchServiceThread> WATCH_SERVICES = Collections.synchronizedMap(new HashMap<>());
    private static final PacketChannelFabric CHANNEL = new PacketChannelFabric(new ResourceLocation(iChunUtil.MOD_ID, "config"), PacketConfig.class);

    private static Path currentServerConfigPath = null;

    static
    {
        ServerLifecycleEvents.SERVER_STARTING.register(FabricConfigLoader::serverStatus);
        ServerLifecycleEvents.SERVER_STOPPED.register(FabricConfigLoader::serverStatus);
        ServerPlayConnectionEvents.JOIN.register(FabricConfigLoader::sendServerConfigs);
        Runtime.getRuntime().addShutdownHook(new Thread(FabricConfigLoader::terminateWatchServices));
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

                    loadSuccessful = assignValuesFromToml(config, toml, isReload);
                }
                catch(IllegalStateException | IllegalAccessException e)
                {
                    iChunUtil.LOGGER.warn("Errors detected loading up config file " + configPath + ". Recreating config.", e);
                }
            }

            if(!loadSuccessful)
            {
                if(!Files.exists(configPath.getParent()))
                {
                    Files.createDirectories(configPath.getParent());
                }
                //File does not exist, we're creating a new config
                String tomlString = ConfigToToml.convertToToml(config);
                FileUtils.writeStringToFile(configPath.toFile(), tomlString, Charsets.UTF_8);
            }

            synchronized(WATCH_SERVICES)
            {
                WatchServiceThread watchServiceThread = WATCH_SERVICES.computeIfAbsent(configPath.getParent(), k -> {
                    WatchServiceThread thread = new WatchServiceThread(configPath.getParent(), FabricConfigLoader::onFileChanged);
                    thread.start();
                    return thread;
                });
                watchServiceThread.addFileToWatch(config.getFileName());
            }

            config.setSaveMethod(() -> {
                try
                {
                    String tomlString = ConfigToToml.convertToToml(config);
                    FileUtils.writeStringToFile(configPath.toFile(), tomlString, Charsets.UTF_8);
                }
                catch(IOException e)
                {
                    iChunUtil.LOGGER.error("Error saving config file " + config.getFileName(), e);
                }
            });

            config.onConfigLoaded();
        }
        catch(IOException e)
        {
            iChunUtil.LOGGER.error("Something went wrong loading up config " + config.getConfigName() + " " +  config.getConfigType().toString(), e);
        }

    }

    private static void onFileChanged(String fileName)
    {
        synchronized(REGISTERED_CONFIGS)
        {
            REGISTERED_CONFIGS.forEach((k, v) -> v.forEach(c -> {
                if(c.getFileName().equals(fileName))
                {
                    loadOrCreateConfig(c, true);
                }
            }));
        }
    }

    private static boolean assignValuesFromToml(ConfigBase config, Toml toml, boolean isReload) throws IllegalAccessException, IllegalStateException
    {
        HashMap<ConfigBase.Category.Entry, Object> entryToValue = new HashMap<>();
        HashMap<String, Object> tomlToValue = new HashMap<>();

        for(ConfigBase.Category category : config.categories)
        {
            for(ConfigBase.Category.Entry entry : category.getEntries())
            {
                entryToValue.put(entry, entry.field.get(config));
            }
        }

        //First layer is the name of each category, each
        toml.toMap().forEach((k, v) -> {
            if(v instanceof Map)
            {
                tomlToValue.putAll((Map<? extends String, ?>)v);
            }
        });

        //We have all the entries and all the toml values, compare them to see if they are valid
        entryToValue.entrySet().removeIf(e -> {
            ConfigBase.Category.Entry entry = e.getKey();
            if(tomlToValue.containsKey(entry.field.getName()))
            {
                try
                {
                    Object oriValue = entry.field.get(config);
                    Object tomlValue = tomlToValue.get(entry.field.getName());

                    //Check if the value is compatible with the field, if it is, assign the new value, mark for change if isReload
                    if(entry.field.getType() == int.class) //doubles are handled separately cause Number is weird
                    {
                        if(tomlValue instanceof Number)
                        {
                            int newValue = ((Number)tomlValue).intValue();
                            if(newValue >= entry.prop.min() && newValue <= entry.prop.max())
                            {
                                entry.field.set(config, newValue);

                                if(isReload && !oriValue.equals(newValue))
                                {
                                    config.onPropertyChanged(true, entry.field.getName(), entry.field, oriValue, newValue);
                                }
                                tomlToValue.remove(entry.field.getName());
                                return true;
                            }
                        }
                    }
                    else if(entry.field.getType() == double.class) //doubles are handled separately cause Number is weird
                    {
                        if(tomlValue instanceof Number)
                        {
                            double newValue = ((Number)tomlValue).doubleValue();
                            if(newValue >= entry.prop.min() && newValue <= entry.prop.max())
                            {
                                entry.field.set(config, newValue);

                                if(isReload && !oriValue.equals(newValue))
                                {
                                    config.onPropertyChanged(true, entry.field.getName(), entry.field, oriValue, newValue);
                                }
                                tomlToValue.remove(entry.field.getName());
                                return true;
                            }
                        }
                    }
                    else if(entry.field.getType() == boolean.class)
                    {
                        if(tomlValue instanceof Boolean)
                        {
                            entry.field.set(config, tomlValue);

                            if(isReload && oriValue != tomlValue)
                            {
                                config.onPropertyChanged(true, entry.field.getName(), entry.field, oriValue, tomlValue);
                            }
                            tomlToValue.remove(entry.field.getName());
                            return true;
                        }
                    }
                    else if(entry.field.getType() == String.class)
                    {
                        boolean invalid = true;
                        Object newValue = tomlValue.toString();
                        if(!(entry.prop.values().length == 1 && entry.prop.values()[0].isEmpty())) //has set values
                        {
                            for(String validValues : entry.prop.values())
                            {
                                if(newValue.equals(validValues))
                                {
                                    invalid = false;
                                    break;
                                }
                            }
                        }
                        else if(entry.prop.validator().equals("undefined") || entry.prop.validator().isEmpty()) //has no validator
                        {
                            invalid = false;
                        }
                        else
                        {
                            Method method = config.getClass().getDeclaredMethod(entry.prop.validator(), Object.class);
                            method.setAccessible(true);
                            if((boolean)method.invoke(config, newValue))
                            {
                                invalid = false;
                            }
                        }

                        if(!invalid)
                        {
                            entry.field.set(config, newValue);

                            if(isReload && oriValue.equals(newValue))
                            {
                                config.onPropertyChanged(true, entry.field.getName(), entry.field, oriValue, newValue);
                            }
                            tomlToValue.remove(entry.field.getName());
                            return true;
                        }
                    }
                    else if(entry.field.getType().isEnum())
                    {
                        Object newValue = tomlValue.toString();
                        Object[] enumConstants = entry.field.getType().getEnumConstants();
                        for(Object enumConstant : enumConstants)
                        {
                            if(enumConstant.toString().equals(newValue))
                            {
                                entry.field.set(config, enumConstant);

                                if(isReload && oriValue.equals(enumConstant))
                                {
                                    config.onPropertyChanged(true, entry.field.getName(), entry.field, oriValue, enumConstant);
                                }
                                tomlToValue.remove(entry.field.getName());
                                return true;
                            }
                        }
                    }
                    else if(List.class.isAssignableFrom(entry.field.getType()))
                    {
                        if(tomlValue instanceof List)
                        {
                            Gson gson = new Gson();
                            Object newList = new ArrayList<>((List<?>)gson.fromJson(gson.toJson(tomlValue), entry.field.getType()));
                            entry.field.set(config, newList);

                            if(isReload && oriValue.equals(newList))
                            {
                                config.onPropertyChanged(true, entry.field.getName(), entry.field, oriValue, tomlValue);
                            }
                            tomlToValue.remove(entry.field.getName());
                            return true;
                        }
                    }
                }
                catch(IllegalAccessException | NoSuchMethodException | InvocationTargetException ex)
                {
                    ex.printStackTrace();
                }
            }
            return false;
        });

        boolean errorFree = entryToValue.isEmpty() && tomlToValue.isEmpty();

        for(ConfigBase.Category.Entry entry : entryToValue.keySet())
        {
            if(tomlToValue.containsKey(entry.field.getName()))
            {
                iChunUtil.LOGGER.warn("Error getting value for {} from config {}. Config value invalid: {}.", entry.field.getName(), config.getFileName(), tomlToValue.get(entry.field.getName()));
                tomlToValue.remove(entry.field.getName());
            }
            else
            {
                iChunUtil.LOGGER.warn("Error getting value for {} from config {}. Does not exist in config.", entry.field.getName(), config.getFileName());
            }
        }

        for(String s : tomlToValue.keySet())
        {
            iChunUtil.LOGGER.warn("Discarding key {} in config {}. No such field exists in config.", s, config.getFileName());
        }

        return errorFree;
    }

    private static void serverStatus(MinecraftServer server)
    {
        if(REGISTERED_CONFIGS.containsKey(ConfigBase.Type.SERVER)) //we have some server configs, we should do something
        {
            if(server == null)//server just shut down
            {
                if(currentServerConfigPath != null)
                {
                    synchronized(WATCH_SERVICES)
                    {
                        WatchServiceThread watchServiceThread = WATCH_SERVICES.get(currentServerConfigPath);
                        if(watchServiceThread != null)
                        {
                            watchServiceThread.stopThread();
                            WATCH_SERVICES.remove(currentServerConfigPath);
                        }
                    }

                    for(ConfigBase config : REGISTERED_CONFIGS.get(ConfigBase.Type.SERVER))
                    {
                        config.setSaveMethod(null); // we can't save the file anymore, it's gone, jim
                    }

                    currentServerConfigPath = null;
                }
            }
            else
            {
                currentServerConfigPath = server.getWorldPath(LevelResource.DATAPACK_DIR).resolve("../serverconfig"); //Grab the parent dir, and go to the server config folder.

                for(ConfigBase config : REGISTERED_CONFIGS.get(ConfigBase.Type.SERVER))
                {
                    //Check if there is a default config for this server config and copy it over
                    Path configPath = getPathForConfig(config);
                    if(!(Files.exists(configPath) && Files.isRegularFile(configPath))) //The file doesn't exist/isn't a normal file
                    {
                        Path defaultConfigPath = LoaderHandler.d().getConfigDir().resolve("../defaultconfigs").resolve(config.getFileName());

                        if(Files.exists(defaultConfigPath) && Files.isRegularFile(defaultConfigPath)) //There is a default file
                        {
                            try
                            {
                                FileUtils.copyFile(defaultConfigPath.toFile(), configPath.toFile());
                            }
                            catch(IOException e)
                            {
                                iChunUtil.LOGGER.error("Error copying default config for config " + config.getFileName(), e);
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
                CHANNEL.sendTo(new PacketConfig(configBase.getFileName(), ConfigToToml.convertToToml(configBase)), handler.getPlayer());
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
                        Toml toml = new Toml().read(tomlString);

                        assignValuesFromToml(configBase, toml, true);
                    }
                    catch(IllegalAccessException | IllegalStateException e)
                    {
                        iChunUtil.LOGGER.error("Error receiving config " + fileName + " from server.", e);
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
            return LoaderHandler.d().getConfigDir().resolve(config.getFileName());
        }
    }

    private static void terminateWatchServices()
    {
        synchronized(WATCH_SERVICES)
        {
            WATCH_SERVICES.forEach((k, v) -> v.stopThread());
            WATCH_SERVICES.clear();
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
