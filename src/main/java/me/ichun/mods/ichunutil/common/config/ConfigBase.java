package me.ichun.mods.ichunutil.common.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;

public abstract class ConfigBase
        implements Comparable<ConfigBase>
{
    @Prop //this annotation is here because I am lazy. Never move this field/annotation combo. EVER. EVER EVER. EVER EVER EVER. This provides the default Prop settings.
    public static final HashMap<String, String> DEFAULT_CATEGORY_COMMENTS = Util.make(new HashMap<>(), map -> {
        map.put("general", "These options are general options that don't fit any other category.");
        map.put("gameplay", "These options affect the gameplay while using the mod.");
        map.put("global", "These options affect both servers and clients that load the mod.");
        map.put("serverOnly", "These options affect only the server that loads the mod.");
        map.put("clientOnly", "These options affect only the client that loads the mod.");
        map.put("block", "These options affect the blocks in the mod.");
    });
    public static final Set<ConfigBase> CONFIGS = Collections.<ConfigBase>synchronizedSet(new TreeSet<>(Comparator.naturalOrder())); //generic required to compile. Synchronised set because concurrency when registering configs with mods

    public final ArrayList<Category> categories = new ArrayList<>();
    public final HashMap<String, BiFunction<WorkspaceConfigs.ConfigInfo.EntryLocalised, ElementList.Item<?>, Boolean>> guiElementOverrides = new HashMap<>();

    @NotNull
    private String fileName;

    private Runnable saveMethod = null; //TODO make sure this isn't null depending on loader

    public ConfigBase()
    {
        compile();

        CONFIGS.add(this);

        fileName = getModId() + "-" + getConfigType().toString().toLowerCase(Locale.ROOT) + ".toml";

        registerGuiElementOverrides();
    }

    public ConfigBase(@NotNull String name)
    {
        compile();

        CONFIGS.add(this);

        fileName = name;

        registerGuiElementOverrides();
    }

    public void setSaveMethod(Runnable saveMethod)
    {
        this.saveMethod = saveMethod;
    }

    public String getFileName()
    {
        return fileName;
    }

    @Nonnull public abstract String getModId();
    @Nonnull public abstract String getConfigName();
    public Type getConfigType()
    {
        return Type.COMMON;
    }

    public enum Type //Required as usually we use Forge's
    {
        CLIENT,
        COMMON,
        SERVER
    }

    private void compile()
    {
        Map<String, String> localization;
        try(InputStream in = this.getClass().getResourceAsStream("/assets/" + getModId() + "/lang/en_us.json"))
        {
            localization = (new Gson()).fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), new TypeToken<Map<String, String>>() {}.getType());
        }
        catch(IOException | NullPointerException e)
        {
            localization = new HashMap<>();

            iChunUtil.LOGGER.warn("Error getting localization file for config {}:{}", getModId(), getConfigName());
            e.printStackTrace();
        }

        Field[] fields = this.getClass().getDeclaredFields();

        Category lastCat = null;
        for(Field field : fields)
        {
            field.setAccessible(true);
            if(!Modifier.isTransient(field.getModifiers()) && isValidField(field))
            {
                //Get the field's props first.
                Prop props; // should always exist
                if(field.isAnnotationPresent(Prop.class))
                {
                    props = field.getAnnotation(Prop.class);
                }
                else
                {
                    props = ConfigBase.class.getDeclaredFields()[0].getAnnotation(Prop.class);
                }

                //if this prop doesn't have the current env, don't add an entry
                if(!hasOurEnv(props.env()))
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
                            comment = DEFAULT_CATEGORY_COMMENTS.get(divider.name());
                        }
                        else
                        {
                            comment = null;

                            if(LoaderHandler.d().isDevEnvironment())
                            {
                                iChunUtil.LOGGER.warn("Config category {} from mod {} for config {} has no localisation.", divider.name(), getModId(), getConfigName());
                            }
                        }

                        Category newCat = new Category(divider.name(), comment, commentKey);
                        categories.add(newCat);
                        return newCat;
                    });
                }
                else if(lastCat == null)
                {
                    lastCat = new Category("general", DEFAULT_CATEGORY_COMMENTS.get("general"), "config.ichunutil.cat.general.desc");

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

                    if(LoaderHandler.d().isDevEnvironment())
                    {
                        iChunUtil.LOGGER.warn("Config property {} from mod {} for config {} has no localisation.", field.getName(), getModId(), getConfigName());
                    }
                }

                lastCat.addField(field, props, comment, commentKey);
            }
        }

        //Sort the categories and properties
        Collections.sort(categories);
        categories.forEach(cat -> Collections.sort(cat.getEntries()));
    }

    /**
     * @param file true if changes were from file
     * @param name name of the config/field
     * @param field our field that was changed
     * @param oldObj old config object
     * @param newObj new config object
     */
    //NOT THREAD SAFE
    public void onPropertyChanged(boolean file, String name, Field field, Object oldObj, Object newObj){}

    //NOT THREAD SAFE
    public void onConfigLoaded(){}

    public void registerGuiElementOverrides(){}

    public void save()
    {
        saveMethod.run();
    }

    private boolean hasOurEnv(LoaderHandler.Env[] propEnvs)
    {
        for(LoaderHandler.Env propEnv : propEnvs)
        {
            if(propEnv == LoaderHandler.Env.ALL || propEnv == LoaderHandler.getEnv())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(ConfigBase o)
    {
        if(getConfigName().equals(o.getConfigName()))
        {
            return Integer.compare(getConfigType().ordinal(), o.getConfigType().ordinal());
        }
        return getConfigName().compareTo(o.getConfigName());
    }

    private static boolean isValidField(Field field)
    {
        return field.getType() == int.class || field.getType() == double.class || field.getType() == boolean.class || field.getType() == String.class || field.getType().isEnum() || List.class.isAssignableFrom(field.getType());
    }

    public static class Category
            implements Comparable<Category>
    {
        @NotNull
        public final String name;
        @Nullable
        public final String comment;
        @Nullable
        public final String commentKey;

        private final ArrayList<Entry> entries = new ArrayList<>();

        public Category(@NotNull String name, @Nullable String comment, @Nullable String commentKey)
        {
            this.name = name;
            this.comment = comment;
            this.commentKey = commentKey;
        }

        public void addField(Field f, Prop props, String comment, String commentKey)
        {
            entries.add(new Entry(f, props, comment, commentKey));
        }

        public ArrayList<Entry> getEntries()
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
            @Nullable
            public final String commentKey;

            public Entry(@NotNull Field field, @NotNull Prop prop, @Nullable String comment, @Nullable String commentKey) {
                this.field = field;
                this.prop = prop;
                this.comment = comment;
                this.commentKey = commentKey;
            }

            @Override
            public int compareTo(@NotNull ConfigBase.Category.Entry o)
            {
                return field.getName().compareTo(o.field.getName());
            }
        }
    }

    public static enum FilterType
    {
        ALLOW,
        DENY
    }
}
