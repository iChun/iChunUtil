package me.ichun.mods.ichunutil.common.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.*;

public abstract class ConfigBase
    implements Comparable<ConfigBase>
{
    @Prop //this annotation is here because I am lazy. Never move this field/annotation combo. EVER. EVER EVER. EVER EVER EVER.
    public static final Set<ConfigBase> configs = Collections.<ConfigBase>synchronizedSet(new TreeSet<>(Comparator.naturalOrder())); //generic required to compile

    private final @Nonnull String fileName;
    private final @Nonnull HashSet<ValueWrapper> values = new HashSet<>();
    private final @Nonnull HashSet<String> reveal = new HashSet<>();

    private ModConfig config; //our mod config

    public ConfigBase(@Nonnull String pathName)
    {
        this.fileName = pathName;
    }

    public <T extends ConfigBase> T init()
    {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();

        Field[] fields = this.getClass().getDeclaredFields();
        String[] lastCat = new String[] { "" };
        for(Field field : fields)
        {
            field.setAccessible(true);
            if(!Modifier.isTransient(field.getModifiers()) && isValidField(field))
            {
                createWrapper(field, configBuilder, lastCat);
            }
        }

        if(!lastCat[0].isEmpty()) //we pushed a category. I needs to be popped.
        {
            configBuilder.pop();
        }

        //Taken from ModLoadingContext.get().registerConfig
        ModLoadingContext.get().getActiveContainer().addConfig(config = new ModConfig(getConfigType(), configBuilder.build(), ModLoadingContext.get().getActiveContainer(), fileName));
        //End registerConfig

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigReload);

        configs.add(this);

        loadConfig();

        return (T)this;
    }

    public <T extends ConfigBase> T reveal(String...name)
    {
        reveal.addAll(Arrays.asList(name));
        return (T)this;
    }

    private void loadConfig() //massive reflective method here.
    {
        if(iChunUtil.getLoadingStage().ordinal() > ModLoadingStage.CONSTRUCT.ordinal() && getConfigType() != ModConfig.Type.SERVER)
        {
            iChunUtil.LOGGER.info("Missed config load window. Force loading config: {}", fileName);
            try
            {
                Method method = ConfigTracker.class.getDeclaredMethod("openConfig", ModConfig.class, Path.class);
                method.setAccessible(true);
                method.invoke(ConfigTracker.INSTANCE, config, FMLPaths.CONFIGDIR.get());
            }
            catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
            {
                iChunUtil.LOGGER.fatal("Uh oh. We failed to load the config. This is bad. CRASH!");
                throw new RuntimeException(e); //we can't fail! something is up
            }
        }
    }

    private void createWrapper(Field field, ForgeConfigSpec.Builder builder, String[] lastCat)
    {
        if(field.isAnnotationPresent(CategoryDivider.class))
        {
            CategoryDivider divider =  field.getAnnotation(CategoryDivider.class);
            if(!divider.category().isEmpty())
            {
                if(!lastCat[0].isEmpty())
                {
                    builder.pop();
                }
                lastCat[0] = divider.category();
                builder.comment(divider.comment()).push(lastCat[0]);
            }
        }

        //handle the field itself

        //get the default value
        Object o;
        try
        {
            o = field.get(this);
        }
        catch(IllegalAccessException e)
        {
            return;
        }

        //get the name for our localization
        String fieldName = field.getName();
        Class clz = field.getType();

        Prop props; // should always exist
        if(field.isAnnotationPresent(Prop.class))
        {
            props = field.getAnnotation(Prop.class);
        }
        else
        {
            props = ConfigBase.class.getDeclaredFields()[0].getAnnotation(Prop.class);
        }

        if(props.hidden() && !reveal.contains(fieldName)) //this is a hidden property that hasn't been revealed. Do not create it.
        {
            return;
        }

        if(!props.comment().equals("undefined"))
        {
            builder.comment(props.comment());
        }
        builder.translation("config." + getModId() + ".prop." + fieldName + ".desc");

        //define
        ForgeConfigSpec.ConfigValue value = null;
        if(props.needsRestart())
        {
            builder.worldRestart();
        }

        if(clz == int.class)
        {
            value = builder.defineInRange(fieldName, (int)o, props.min() == Double.MIN_VALUE ? Integer.MIN_VALUE : (int)props.min(), props.max() == Double.MAX_VALUE ? Integer.MAX_VALUE : (int)props.max());
        }
        else if(clz == double.class)
        {
            value = builder.defineInRange(fieldName, (double)o, props.min(), props.max());
        }
        else if(clz == boolean.class)
        {
            value = builder.define(fieldName, (boolean)o);
        }
        else if(clz == String.class)
        {
            if(!(props.values().length == 1 && props.values()[0].isEmpty()))
            {
                value = builder.defineInList(fieldName, (String)o, Arrays.asList(props.values()));
            }
            else if(props.validator().equals("undefined") || props.validator().isEmpty())
            {
                value = builder.define(fieldName, (String)o);
            }
            else
            {
                try
                {
                    Method method = this.getClass().getDeclaredMethod(props.validator(), Object.class);
                    method.setAccessible(true);
                    value = builder.define(fieldName, (String)o, x -> {
                        try
                        {
                            return (boolean)method.invoke(this, x);
                        }
                        catch(IllegalAccessException | InvocationTargetException e)
                        {
                            throw new RuntimeException("Can't find proper validator \"" + props.validator() + "\" for field " + fieldName);
                        }
                    });
                }
                catch(NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if(clz.isEnum()) //enum!
        {
            value = builder.defineEnum(fieldName, (Enum)o);
        }
        else if(o instanceof List) //lists
        {
            if(props.validator().equals("undefined") || props.validator().isEmpty()) // no validator
            {
                value = builder.defineList(fieldName, (List)o, x -> true);
            }
            else
            {
                try
                {
                    Method method = this.getClass().getDeclaredMethod(props.validator(), Object.class);
                    method.setAccessible(true);
                    value = builder.defineList(fieldName, (List)o, x -> {
                        try
                        {
                            return (boolean)method.invoke(this, x);
                        }
                        catch(IllegalAccessException | InvocationTargetException e)
                        {
                            throw new RuntimeException("Can't find proper validator \"" + props.validator() + "\" for field " + fieldName);
                        }
                    });
                }
                catch(NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
            }
        }

        if(value != null)
        {
            values.add(new ValueWrapper(this, value, field));
        }
        else
        {
            throw new RuntimeException("Value should never be null");
        }
    }

    public abstract @Nonnull String getModId();
    public abstract @Nonnull String getConfigName();
    public ModConfig.Type getConfigType()
    {
        return ModConfig.Type.COMMON;
    }

    private void onConfigLoad(ModConfig.Loading event)
    {
        if(event.getConfig().getFileName().equals(fileName));
        {
            checkForChanges();
        }
    }

    private void onConfigReload(ModConfig.ConfigReloading event)
    {
        if(event.getConfig().getFileName().equals(fileName));
        {
            checkForChanges();
        }
    }

    private void checkForChanges()
    {
        values.forEach(ValueWrapper::checkForChange);
    }

    public void save()
    {
        ForgeConfigSpec.ConfigValue configValue = null;
        for(ValueWrapper value : values)
        {
            ForgeConfigSpec.ConfigValue valueValue = value.save();
            if(valueValue != null)
            {
                configValue = valueValue;
            }
        }
        if(configValue != null)
        {
            configValue.save();
        }
    }

    @Override
    public int compareTo(ConfigBase o)
    {
        return getConfigName().compareTo(o.getConfigName());
    }

    private static boolean isValidField(Field field)
    {
        return field.isAnnotationPresent(Prop.class) || field.getType() == int.class || field.getType() == double.class || field.getType() == boolean.class || field.getType() == String.class || field.getType().isEnum() || List.class.isAssignableFrom(field.getType());
    }

    private static class ValueWrapper<T>
    {
        private final ConfigBase parent;
        private final @Nonnull ForgeConfigSpec.ConfigValue<T> configValue;
        private final @Nonnull Field field;

        private T lastObj = null;

        private ValueWrapper(ConfigBase config, ForgeConfigSpec.ConfigValue<T> configValue, Field field)
        {
            this.parent = config;
            this.configValue = configValue;
            this.field = field;
        }

        private void checkForChange()
        {
            if(configValue.get() != lastObj)
            {
                try
                {
                    lastObj = configValue.get();
                    field.setAccessible(true);
                    field.set(parent, lastObj);
                }
                catch(IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }

        private ForgeConfigSpec.ConfigValue save()
        {
            try
            {
                field.setAccessible(true);
                Object o = field.get(parent);
                if(o != lastObj)
                {
                    lastObj = (T)o;
                    configValue.set(lastObj);
                    return configValue;
                }
            }
            catch(IllegalAccessException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

}
