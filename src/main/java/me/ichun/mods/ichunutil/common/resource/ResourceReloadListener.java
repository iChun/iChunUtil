package me.ichun.mods.ichunutil.common.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ResourceReloadListener<T> extends JsonReloadListener
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

        MinecraftForge.EVENT_BUS.addListener(this::onServerAboutToStart);
    }

    public <K extends ResourceReloadListener<T>> K setDefault(T defaultObj)
    {
        this.defaultObj = defaultObj;
        return (K)this;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> json, IResourceManager iResourceManager, IProfiler iProfiler)
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

    private void onServerAboutToStart(FMLServerAboutToStartEvent event)
    {
        event.getServer().getResourceManager().addReloadListener(this);
    }
}
