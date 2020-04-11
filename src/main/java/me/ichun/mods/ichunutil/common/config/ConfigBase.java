package me.ichun.mods.ichunutil.common.config;

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

    private boolean init;
    private ModConfig config; //our mod config

    public ConfigBase()
    {
        this(ModLoadingContext.get().getActiveContainer().getModId() + ".toml");
    }

    public ConfigBase(@Nonnull String pathName)
    {
        this.fileName = pathName;
        configs.add(this);
    }

    public <T extends ConfigBase> T init()
    {
        init = true;

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

        if(!lastCat[0].isEmpty()) //we pushed a name. I needs to be popped.
        {
            configBuilder.pop();
        }

        //Taken from ModLoadingContext.get().registerConfig
        ModLoadingContext.get().getActiveContainer().addConfig(config = new ModConfig(getConfigType(), configBuilder.build(), ModLoadingContext.get().getActiveContainer(), fileName));
        //End registerConfig

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigReload);

        loadConfig();

        return (T)this;
    }

    public <T extends ConfigBase> T reveal(String...name)
    {
        reveal.addAll(Arrays.asList(name));
        return (T)this;
    }

    private void loadConfig() //massive reflective method here. Sorry cpw!
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
            if(!divider.name().isEmpty())
            {
                if(!lastCat[0].isEmpty())
                {
                    builder.pop();
                }
                lastCat[0] = divider.name();
                if(!divider.comment().equals("undefined")) //not undefined //TODO if ever localizable, localize these
                {
                    builder.comment(divider.comment());
                }
                else if(divider.name().equals("general")) //undefined, but we're in a set category, default to generics
                {
                    builder.comment("These options are general options that don't fit any other category.");
                }
                else if(divider.name().equals("gameplay")) //undefined, but we're in a set category, default to generics
                {
                    builder.comment("These options affect the gameplay while using the mod.");
                }
                builder.push(lastCat[0]);
            }
            else
            {
                throw new RuntimeException("WHY are you defining AN EMPTY CATEGORY?!");
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
        else if(iChunUtil.isDevEnvironemnt())
        {
            iChunUtil.LOGGER.warn("Property is not commented: " + fieldName); //TODO switch this over to localization?
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
    public @Nonnull ModConfig.Type getConfigType()
    {
        return ModConfig.Type.COMMON;
    }

    private void onConfigLoad(ModConfig.Loading event)
    {
        if(event.getConfig().getFileName().equals(fileName))
        {
            checkForChanges();
            onConfigLoaded();
        }
    }

    private void onConfigReload(ModConfig.Reloading event)
    {
        if(event.getConfig().getFileName().equals(fileName))
        {
            checkForChanges();
            onConfigLoaded();
        }
    }

    /**
     * @param file true if changes were from file
     * @param name name of the config/field
     * @param field our field that was changed
     * @param oldObj old config object
     * @param newObj new config object
     */
    public void onPropertyChanged(boolean file, String name, Field field, Object oldObj, Object newObj){}

    public void onConfigLoaded(){}

    private void checkForChanges()
    {
        values.forEach(ValueWrapper::checkForChange);
    }

    public boolean hasInit()
    {
        return init;
    }

    public void save()
    {
        boolean save = false;
        for(ValueWrapper value : values)
        {
            save = value.save() || save;
        }
        if(save)
        {
            config.getSpec().save();
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
                    Object old = lastObj;
                    lastObj = configValue.get();
                    field.setAccessible(true);
                    field.set(parent, lastObj);
                    parent.onPropertyChanged(true, field.getName(), field, old, lastObj);
                }
                catch(IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }

        private boolean save() //returns true if the value has changed
        {
            try
            {
                field.setAccessible(true);
                Object o = field.get(parent);
                if(o != lastObj)
                {
                    Object old = lastObj;
                    lastObj = (T)o;
                    configValue.set(lastObj);
                    parent.onPropertyChanged(false, field.getName(), field, old, lastObj);
                    return true;
                }
            }
            catch(IllegalAccessException e)
            {
                e.printStackTrace();
            }
            return false;
        }
    }
}
