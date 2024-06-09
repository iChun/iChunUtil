package me.ichun.mods.ichunutil.loader.neoforge.config;

import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfigHandlerNeoForge extends ConfigHandler
{
    static
    {
        if(iChunUtil.d().isOnClient())
        {
            NeoForge.EVENT_BUS.addListener(ConfigHandlerNeoForge::onPlayerLoggedInEvent);
            NeoForge.EVENT_BUS.addListener(ConfigHandlerNeoForge::onPlayerLoggedOutEvent);
        }
    }

    private static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        onServerConnect();
    }

    private static void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event)
    {
        onServerDisconnect();
    }

    private final IdentityHashMap<ConfigBase.Category.Entry, ModConfigSpec.ConfigValue> entryToValues = new IdentityHashMap<>();

    private ModConfig modConfig; //our mod config

    public ConfigHandlerNeoForge(ConfigBase config, IEventBus bus)
    {
        super(config);

        registerListeners(bus);
    }

    @Override
    public void init()
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        boolean inCat = false;
        for(ConfigBase.Category cat : config.categories)
        {
            if(inCat)
            {
                builder.pop();
            }

            inCat = true;

            if(cat.comment != null)
            {
                builder.comment(cat.comment);
            }
            if(cat.commentKey !=  null)
            {
                builder.translation(cat.commentKey);
            }
            builder.push(cat.name);

            for(ConfigBase.Category.Entry e : cat.getEntries())
            {
                if(e.comment != null)
                {
                    builder.comment(e.comment);
                }
                if(e.commentKey != null)
                {
                    builder.translation(e.commentKey);
                }

                Prop prop = e.prop;
                if(prop.needsRestart())
                {
                    builder.worldRestart();
                }

                ModConfigSpec.ConfigValue value = null;
                Class<?> clz = e.field.getType();
                String fieldName = e.field.getName();
                try
                {
                    Object o = e.field.get(config);
                    if(clz == int.class)
                    {
                        value = builder.defineInRange(fieldName, (int)o, prop.intBool() ? 0 : prop.min() == Double.MIN_VALUE ? Integer.MIN_VALUE : (int)prop.min(), prop.intBool() ? 1 : prop.max() == Double.MAX_VALUE ? Integer.MAX_VALUE : (int)prop.max());
                    }
                    else if(clz == double.class)
                    {
                        value = builder.defineInRange(fieldName, (double)o, prop.min(), prop.max());
                    }
                    else if(clz == boolean.class)
                    {
                        value = builder.define(fieldName, (boolean)o);
                    }
                    else if(clz == String.class)
                    {
                        if(!(prop.values().length == 1 && prop.values()[0].isEmpty()))
                        {
                            value = builder.defineInList(fieldName, (String)o, Arrays.asList(prop.values()));
                        }
                        else if(prop.validator().equals("undefined") || prop.validator().isEmpty())
                        {
                            value = builder.define(fieldName, (String)o);
                        }
                        else
                        {
                            Method method = config.getValidatorMethod(prop.validator());
                            value = builder.define(fieldName, (String)o, x -> config.validate(method, x));
                        }
                    }
                    else if(clz.isEnum()) //enum!
                    {
                        value = builder.defineEnum(fieldName, (Enum)o);
                    }
                    else if(o instanceof List) //lists
                    {
                        if(prop.validator().equals("undefined") || prop.validator().isEmpty()) // no validator
                        {
                            value = builder.defineList(fieldName, (List)o, x -> true);
                        }
                        else
                        {
                            Method method = config.getValidatorMethod(prop.validator());
                            value = builder.defineList(fieldName, (List)o, x -> config.validate(method, x));
                        }
                    }
                }
                catch(IllegalAccessException | IllegalArgumentException ex)
                {
                    iChunUtil.LOGGER.error("Error with creating field {} for config {}", fieldName, config.getFileName(), ex);
                }

                if(value != null)
                {
                    entryToValues.put(e, value);
                }
                else
                {
                    throw new RuntimeException("Value should never be null");
                }
            }
        }

        if(!config.categories.isEmpty())
        {
            builder.pop();
        }

        ModLoadingContext.get().getActiveContainer().addConfig(modConfig = new ModConfig(config.getConfigType() == ConfigBase.Type.COMMON ? ModConfig.Type.COMMON : config.getConfigType() == ConfigBase.Type.CLIENT ? ModConfig.Type.CLIENT : ModConfig.Type.SERVER, builder.build(), ModLoadingContext.get().getActiveContainer(), config.getFileName()));

        config.setSaveMethod(() -> {
            ModConfigSpec spec = (ModConfigSpec)modConfig.getSpec();
            if(spec.isLoaded() && updateConfigValuesFromFields())
            {
                spec.save();
            }
        });
    }

    private void registerListeners(IEventBus bus)
    {
        bus.addListener(this::onConfigLoad);
        bus.addListener(this::onConfigReload);
    }

    private void onConfigLoad(ModConfigEvent.Loading event)
    {
        if(event.getConfig().getFileName().equals(config.getFileName()))
        {
            checkForChangesFromFile(false);
            config.onConfigLoaded();
        }
    }

    private void onConfigReload(ModConfigEvent.Reloading event)
    {
        if(event.getConfig().getFileName().equals(config.getFileName()))
        {
            checkForChangesFromFile(true);
        }
    }

    @Override
    protected void checkForChangesFromFile(boolean reload)
    {
        for(ConfigBase.Category cat : config.categories)
        {
            for(ConfigBase.Category.Entry e : cat.getEntries())
            {
                ModConfigSpec.ConfigValue configValue = entryToValues.get(e);
                Object o = configValue.get();
                try
                {
                    e.field.setAccessible(true);
                    Object oldObj = e.field.get(config);
                    if(!o.equals(oldObj)) // value has changed
                    {
                        e.field.set(config, o);
                        if(reload) config.onPropertyChanged(true, e.field.getName(), e.field, oldObj, o);
                    }
                }
                catch(IllegalAccessException | IllegalArgumentException ex)
                {
                    iChunUtil.LOGGER.error("Error with updating field {} for config value {} for config {}", e.field.getName(), o, config.getFileName(), ex);
                }
            }
        }
    }

    private boolean updateConfigValuesFromFields()
    {
        boolean dirty = false;
        for(ConfigBase.Category cat : config.categories)
        {
            for(ConfigBase.Category.Entry e : cat.getEntries())
            {
                ModConfigSpec.ConfigValue configValue = entryToValues.get(e);
                Object o = configValue.get();
                try
                {
                    e.field.setAccessible(true);
                    Object oldObj = e.field.get(config);
                    if(!o.equals(oldObj)) // value has changed
                    {
                        configValue.set(oldObj);
                        dirty = true;
                    }
                }
                catch(IllegalAccessException | IllegalArgumentException ex)
                {
                    iChunUtil.LOGGER.error("Error with updating field {} for config value {} for config {}", e.field.getName(), o, config.getFileName(), ex);
                }
            }
        }
        return dirty;
    }
}
