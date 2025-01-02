package me.ichun.mods.ichunutil.common.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import me.ichun.mods.ichunutil.client.key.KeyBind;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.Env;
import net.minecraft.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;

public abstract class ConfigBase //Configs should be created in the constructor of the mod class
    implements Comparable<ConfigBase>
{
    @Prop //this annotation is here because I am lazy. Never move this field/annotation combo. EVER. EVER EVER. EVER EVER EVER. This provides the default Prop settings.
    public static final HashMap<String, String> DEFAULT_CATEGORY_COMMENTS = Util.make(new HashMap<>(), map -> {
        map.put("general", "These configs are general configs that don't fit any other category.");
        map.put("gameplay", "These configs affect the gameplay while using the mod.");
        map.put("global", "These configs affect both servers and clients that load the mod.");
        map.put("serverOnly", "These configs affect only the server that loads the mod.");
        map.put("clientOnly", "These configs affect only the client that loads the mod.");
        map.put("block", "These configs affect the blocks in the mod.");
    });
    public static final Set<ConfigBase> CONFIGS = Collections.<ConfigBase>synchronizedSet(new TreeSet<>(Comparator.naturalOrder())); //generic required to compile. Synchronised set because concurrency when registering configs with mods

    public transient final TreeSet<Category> categories = new TreeSet<>(Comparator.naturalOrder());

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public transient HashMap<String, BiFunction<Category.Entry, ElementList.Item<?>, Boolean>> guiElementOverrides;

    @NotNull
    private transient String fileName;

    private transient Runnable saveMethod = null;

    private transient String fieldCache = null; //a toml minified version of the file, to reset to when players disconnect from servers for server configs.

    public ConfigBase(String...name) //This arg is in this way to allow for super();
    {
        CONFIGS.add(this);

        if(name.length > 0)
        {
            StringBuilder fileNameBuilder = new StringBuilder();
            for(int i = 0; i < name.length; i++)
            {
                if(!name[i].isEmpty())
                {
                    fileNameBuilder.append(name[i]);

                    if(i != name.length - 1) //not the last entry, add a slash
                    {
                        fileNameBuilder.append("/");
                    }
                }
            }
            fileName = fileNameBuilder.toString();
        }
        else
        {
            fileName = getModId() + "-" + getConfigType().toString().toLowerCase(Locale.ROOT) + ".toml";
        }

        if(addToSubfolder())
        {
            fileName = getModId() + "/" + fileName;
        }

        if(iChunUtil.d().getSide().isClient())
        {
            guiElementOverrides = new HashMap<>();

            registerGuiElementOverrides();
        }
        else if(getConfigType() == Type.CLIENT)
        {
            throw new RuntimeException("You're creating a CLIENT config on a SERVER! Bad! Mod: " + getConfigName());
        }
    }

    public void setSaveMethod(Runnable saveMethod)
    {
        this.saveMethod = saveMethod;
    }

    public String getFileName()
    {
        return fileName;
    }

    public boolean addToSubfolder()
    {
        return false;
    }

    @NotNull
    public abstract String getModId();
    @NotNull
    public abstract String getConfigName();

    public Type getConfigType()
    {
        return Type.COMMON;
    }

    public String getConfigTypeName()
    {
        return getConfigType().toString();
    }

    public enum Type //Required as usually we use Forge's
    {
        CLIENT,
        COMMON,
        SERVER
    }

    public void compile()
    {
        Map<String, String> localization;
        try(InputStream in = this.getClass().getResourceAsStream("/assets/" + getModId() + "/lang/en_us.json"))
        {
            localization = (new Gson()).fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), new TypeToken<Map<String, String>>() {}.getType());
        }
        catch(IOException | NullPointerException e)
        {
            localization = new HashMap<>();

            iChunUtil.LOGGER.warn("Error getting localization file for config {}:{}", getModId(), getConfigName(), e);
        }

        Field[] fields = this.getClass().getDeclaredFields();

        Category lastCat = null;
        for(Field field : fields)
        {
            field.setAccessible(true);
            if(isValidField(field))
            {
                //Get the field's props first.
                @NotNull Prop props;
                if(field.isAnnotationPresent(Prop.class))
                {
                    props = field.getAnnotation(Prop.class);
                }
                else
                {
                    props = ConfigBase.class.getDeclaredFields()[0].getAnnotation(Prop.class); //default
                }

                //if this prop doesn't have the current env, don't add an entry
                @NotNull Env[] envs = props.env();
                boolean pass = false;
                for(Env env : envs)
                {
                    if(env == Env.ALL || env.equals(iChunUtil.d().env()))
                    {
                        pass = true;
                        break;
                    }
                }

                if(!pass)
                {
                    continue;
                }

                //Is there a new category?
                if(field.isAnnotationPresent(CategoryDivider.class)) //new category
                {
                    CategoryDivider divider =  field.getAnnotation(CategoryDivider.class);
                    if(divider.name().isEmpty())
                    {
                        throw new RuntimeException("WHY are you defining AN EMPTY CATEGORY?!");
                    }

                    //find a category by the same name, if not, make a new one and add it to the list
                    Map<String, String> finalLocalization = localization;
                    lastCat = categories.stream().filter(cat -> cat.name.equals(divider.name())).findFirst().orElseGet(() -> {
                        String commentKey = "config." + getModId() + ".cat." + divider.name() + ".desc";
                        String comment;
                        if(!divider.comment().equals("undefined"))
                        {
                            comment = divider.comment();
                        }
                        else if(finalLocalization.containsKey(commentKey))
                        {
                            comment = finalLocalization.get(commentKey);
                        }
                        else if(DEFAULT_CATEGORY_COMMENTS.containsKey(divider.name()))
                        {
                            commentKey = "config.ichunutil.cat." + divider.name() + ".desc";
                            comment = DEFAULT_CATEGORY_COMMENTS.get(divider.name());
                        }
                        else
                        {
                            comment = null;

                            if(iChunUtil.d().isDevEnvironment())
                            {
                                iChunUtil.LOGGER.warn("Config category {} from mod {} for config {} has no localisation.", divider.name(), getModId(), getConfigName());
                            }
                        }

                        Category newCat = new Category(divider.name(), comment, commentKey, divider.showInGui(), divider.notifyIfHidden());
                        categories.add(newCat);
                        return newCat;
                    });
                }
                else if(lastCat == null)
                {
                    lastCat = new Category("general", DEFAULT_CATEGORY_COMMENTS.get("general"), "config.ichunutil.cat.general.desc", true, true);

                    categories.add(lastCat);
                }

                //We have the prop & category, now to get the comment
                String commentKey = "config." + getModId() + ".prop." + field.getName() + ".desc";
                String comment;
                if(!props.comment().equals("undefined"))
                {
                    comment = props.comment();
                }
                else if(localization.containsKey(commentKey))
                {
                    comment = localization.get(commentKey);
                }
                else
                {
                    comment = null;

                    if(iChunUtil.d().isDevEnvironment())
                    {
                        iChunUtil.LOGGER.warn("Config property {} from mod {} for config {} has no localisation.", field.getName(), getModId(), getConfigName());
                    }
                }

                Object o = null;
                try
                {
                    field.setAccessible(true);
                    o = field.get(this);
                }
                catch(IllegalAccessException | IllegalStateException e)
                {
                    iChunUtil.LOGGER.error("Error reading config {} for field {}", getConfigName(), field.getName());
                }

                if(o == null)
                {
                    throw new IllegalStateException("Field " + field.getName() + " from config " + this.getClass().getName() + " has no value!");
                }

                lastCat.addField(field, props, comment, commentKey, o);
            }
        }
    }

    public void cache()
    {
        fieldCache = ConfigToToml.convertToToml(this, true);
    }

    public void restoreFromCache()
    {
        if(fieldCache != null)
        {
            try
            {
                ConfigToToml.readTomlFromString(this, fieldCache, true);
            }
            catch(IllegalAccessException | IllegalStateException e)
            {
                iChunUtil.LOGGER.error("Error restoring config {} after disconnecting from server.", fileName, e);
            }
        }
        else
        {
            iChunUtil.LOGGER.error("Trying to restore from cache for config {} with no cache!", fileName);
        }
    }

    /**
     * Called after the field has already been changed
     * @param file true if changes were from file
     * @param name name of the config/field
     * @param field our field that was changed
     * @param oldObj old config object
     * @param newObj new config object
     */
    //NOT THREAD SAFE
    public void onPropertyChanged(boolean file, String name, Field field, Object oldObj, Object newObj){}

    /**
     * Only called when the config is loaded for the first time.
     * This is not called when the entire config is switched eg in server-type configs
     */
    //NOT THREAD SAFE
    public void onConfigLoaded(){}

    public void registerGuiElementOverrides(){}

    public void save()
    {
        saveMethod.run();
    }

    public boolean isStringValid(Category.Entry entry, String s)
    {
        boolean invalid = true;
        if(!(entry.prop.values().length == 1 && entry.prop.values()[0].isEmpty())) //has set values
        {
            for(String validValues : entry.prop.values())
            {
                if(s.equals(validValues))
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
            if(this.validate(this.getValidatorMethod(entry.prop.validator()), s))
            {
                invalid = false;
            }
        }
        return invalid;
    }

    public Method getValidatorMethod(String s)
    {
        try
        {
            Method method = this.getClass().getDeclaredMethod(s, Object.class);
            method.setAccessible(true);
            return method;
        }
        catch(NoSuchMethodException e)
        {
            throw new RuntimeException("Can't find proper validator \"" + s + "\"", e);
        }
    }

    public boolean validate(Method m, Object o)
    {
        try
        {
            return (boolean)m.invoke(this, o);
        }
        catch(IllegalAccessException | InvocationTargetException ex)
        {
            throw new RuntimeException("Error validating using method\"" + m.getName() + "\" for object " + o, ex);
        }
    }

    @Nullable
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public String getLocalisedName(Category category, boolean comment)
    {
        String suffix = comment ? ".desc" : ".name";
        String key = comment ? category.commentKey : "config." + getModId() + ".cat." + category.name + suffix;
        String localised = iChunUtil.eC().getLocalisedString(key);
        if(localised.equals(key)) //it is not localised
        {
            if(DEFAULT_CATEGORY_COMMENTS.containsKey(category.name)) //iChunUtil has this as a default category
            {
                return iChunUtil.eC().getLocalisedString("config.ichunutil.cat." + category.name + suffix);
            }

            if(comment)
            {
                if(category.comment != null)
                {
                    return category.comment;
                }

                //No comment, return null
                return null;
            }

            //We don't have a translation for it, return the category name itself
            return category.name;
        }
        return localised;
    }

    @Nullable
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public String getLocalisedName(Category.Entry entry, boolean comment)
    {
        String suffix = comment ? ".desc" : ".name";
        String key = comment ? entry.commentKey : "config." + getModId() + ".prop." + entry.field.getName() + suffix;
        String localised = iChunUtil.eC().getLocalisedString(key);
        if(localised.equals(key)) //it is not localised
        {
            if(comment)
            {
                if(entry.comment != null)
                {
                    return entry.comment;
                }

                //No comment, return null
                return null;
            }

            //We don't have a translation for it, return the category name itself
            return entry.field.getName();
        }
        return localised;
    }

    @Override
    public int compareTo(ConfigBase o)
    {
        if(this.getConfigName().equals(o.getConfigName()))
        {
            if(this.getConfigTypeName().equals(o.getConfigTypeName()))
            {
                return this.getClass().getSimpleName().compareTo(o.getClass().getSimpleName());
            }
            return this.getConfigTypeName().toLowerCase(Locale.ROOT).compareTo(o.getConfigTypeName().toLowerCase(Locale.ROOT));
        }
        return this.getConfigName().toLowerCase(Locale.ROOT).compareTo(o.getConfigName().toLowerCase(Locale.ROOT));
    }

    private static boolean isValidField(Field field)
    {
        return !Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()) && (field.getType() == int.class || field.getType() == double.class || field.getType() == boolean.class || field.getType() == String.class || field.getType().isEnum() || List.class.isAssignableFrom(field.getType()) || field.getType() == KeyBind.class);
    }

    public static class Category
        implements Comparable<Category>
    {
        @NotNull
        public final String name;
        @Nullable
        public final String comment;
        @NotNull
        public final String commentKey;

        public final boolean showInGui;

        public final boolean notifyIfHidden;

        private final LinkedHashSet<Entry> entries = new LinkedHashSet<>();

        public Category(@NotNull String name, @Nullable String comment, @NotNull String commentKey, boolean showInGui, boolean notifyIfHidden)
        {
            this.name = name;
            this.comment = comment;
            this.commentKey = commentKey;
            this.showInGui = showInGui;
            this.notifyIfHidden = notifyIfHidden;
        }

        public void addField(Field f, Prop props, String comment, String commentKey, Object defaultValue)
        {
            entries.add(new Entry(f, props, comment, commentKey, defaultValue));
        }

        public LinkedHashSet<Entry> getEntries()
        {
            return entries;
        }

        @Override
        public int compareTo(@NotNull ConfigBase.Category o)
        {
            return name.compareTo(o.name);
        }

        public static class Entry
            implements Comparable<Entry>
        {
            @NotNull
            public final Field field;
            @NotNull
            public final Prop prop;
            @Nullable
            public final String comment;
            @NotNull
            public final String commentKey;
            @NotNull
            public final Object defaultValue;

            public Entry(@NotNull Field field, @NotNull Prop prop, @Nullable String comment, @NotNull String commentKey, @NotNull Object defaultValue) {
                this.field = field;
                this.prop = prop;
                this.comment = comment;
                this.commentKey = commentKey;
                this.defaultValue = defaultValue;
            }

            @Override
            public int compareTo(@NotNull ConfigBase.Category.Entry o)
            {
                return field.getName().compareTo(o.field.getName());
            }
        }
    }

    public enum FilterType
    {
        ALLOW,
        DENY
    }
}
