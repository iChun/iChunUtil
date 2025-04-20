package me.ichun.mods.ichunutil.common.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * See Forge/NeoForge LootModifierManager for JSON use from a folder
 * @param <T> Type of object to return
 */
public class ResourceReloadListener<T> extends SimpleJsonResourceReloadListener<JsonElement>
{
    private static final Gson DEFAULT_GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final Class<T> classType;
    private final Gson parser;
    public final ResourceLocation id;
    private T defaultObj = null;
    public HashMap<ResourceLocation, T> objects = new HashMap<>();

    public ResourceReloadListener(String resourceFolder, ResourceLocation id, Class<T> classType)
    {
        this(DEFAULT_GSON, resourceFolder, id, classType);
    }

    public ResourceReloadListener(Gson gsonParser, String resourceFolder, ResourceLocation id, Class<T> classType)
    {
        super(ExtraCodecs.JSON, FileToIdConverter.json(resourceFolder));

        this.classType = classType;
        this.parser = gsonParser;
        this.id = id;

        iChunUtil.d().registerAddReloadListener(id, this);
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

    @Nullable
    public T get(ResourceLocation key)
    {
        return objects.containsKey(key) ? objects.get(key) : defaultObj;
    }
}
