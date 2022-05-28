package me.ichun.mods.ichunutil.common.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ResourceReloadListener<T> extends SimpleJsonResourceReloadListener
{
    private static final Gson DEFAULT_GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final Class<T> classType;

    private final Gson parser;
    private T defaultObj = null;
    public HashMap<ResourceLocation, T> objects = new HashMap<>();

    public ResourceReloadListener(String resourceFolder, Class<T> classType)
    {
        this(DEFAULT_GSON, resourceFolder, classType);
    }

    public ResourceReloadListener(Gson gsonParser, String resourceFolder, Class<T> classType)
    {
        super(gsonParser, resourceFolder);

        this.classType = classType;
        this.parser = gsonParser;

        LoaderHandler.d().registerAddReloadListener(this);
    }

    public <K extends ResourceReloadListener<T>> K setDefault(T defaultObj)
    {
        this.defaultObj = defaultObj;
        return (K)this;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> json, ResourceManager iResourceManager, ProfilerFiller iProfiler)
    {
        json.forEach((k, v) -> {
            try
            {
                objects.put(k, parser.fromJson(v, classType));
            }
            catch(Exception e)
            {
                iChunUtil.LOGGER.warn("Error parsing resource : {}", k);
            }
        });
    }

    public @Nullable T get(ResourceLocation key)
    {
        return objects.containsKey(key) ? objects.get(key) : defaultObj;
    }
}
