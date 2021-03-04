package me.ichun.mods.ichunutil.common.head;

import com.google.gson.*;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class HeadHandler
{
    public static final HashMap<Class<? extends LivingEntity>, String> MODEL_OFFSET_HELPERS_JSON = new HashMap<>();
    public static final HashMap<Class<? extends LivingEntity>, HeadInfo<?>> MODEL_OFFSET_HELPERS = new HashMap<>();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
            .registerTypeAdapter(HeadInfo.class, new HeadInfo.Serializer())
            .create();
    public static final HashSet<String> IMC_HEAD_INFO = new HashSet<>();
    public static final int HEAD_INFO_VERSION = 1;


    public static BooleanSupplier acidEyesBooleanSupplier = () -> false;

    @Nullable
    public static HeadInfo<?> getHelper(Class<? extends LivingEntity> clz)
    {
        if(MODEL_OFFSET_HELPERS.containsKey(clz))
        {
            return MODEL_OFFSET_HELPERS.get(clz);
        }
        HeadInfo<?> helper = null;
        Class clzz = clz.getSuperclass();
        if(clzz != LivingEntity.class)
        {
            helper = getHelper(clzz);
            if(helper != null)
            {
                helper = GSON.fromJson(GSON.toJson(helper), helper.getClass());
            }
        }
        MODEL_OFFSET_HELPERS.put(clz, helper);
        return helper;
    }

    @Nullable
    public static String getHelperJson(Class<? extends LivingEntity> clz)
    {
        if(MODEL_OFFSET_HELPERS_JSON.containsKey(clz))
        {
            return MODEL_OFFSET_HELPERS_JSON.get(clz);
        }
        String json = null;
        Class clzz = clz.getSuperclass();
        if(clzz != LivingEntity.class)
        {
            json = getHelperJson(clzz);
        }
        MODEL_OFFSET_HELPERS_JSON.put(clz, json);
        return json;
    }

    private static Path headDir;

    private static boolean init;
    public static boolean hasInit() { return init; }
    public static void init() //should be initialised in FMLLoadCompleteEvent stage
    {
        if(!init)
        {
            init = true;

            try
            {
                Path workingDir = FMLPaths.CONFIGDIR.get().resolve(iChunUtil.MOD_ID);
                if(!Files.exists(workingDir)) Files.createDirectory(workingDir);

                headDir = workingDir.resolve("head");
                if(!Files.exists(headDir)) Files.createDirectory(headDir);

                File extractedMarker = new File(headDir.toFile(), HEAD_INFO_VERSION + ".extracted");
                if(!extractedMarker.exists()) //presume we haven't extracted anything yet
                {
                    InputStream in = iChunUtil.class.getResourceAsStream("/heads.zip");
                    if(in != null)
                    {
                        ZipInputStream zipStream = new ZipInputStream(in);
                        ZipEntry entry = null;

                        while((entry = zipStream.getNextEntry()) != null)
                        {
                            File file = new File(headDir.toFile(), entry.getName());
                            if(file.exists() && file.length() > 3L)
                            {
                                continue;
                            }
                            FileOutputStream out = new FileOutputStream(file);

                            byte[] buffer = new byte[8192];
                            int len;
                            while((len = zipStream.read(buffer)) != -1)
                            {
                                out.write(buffer, 0, len);
                            }
                            out.close();
                        }
                        zipStream.close();
                    }

                    FileUtils.writeStringToFile(extractedMarker, "", StandardCharsets.UTF_8);
                }

                loadHeadInfos();

                //                MinecraftForge.EVENT_BUS.register(new SerialiserHelper());
            }
            catch(IOException e)
            {
                iChunUtil.LOGGER.fatal("Error initialising HeadInfo resources!");
                e.printStackTrace();
            }
        }
    }

    public static Path getHeadsDir()
    {
        return headDir;
    }

    public static int loadHeadInfos()
    {
        MODEL_OFFSET_HELPERS_JSON.clear();
        MODEL_OFFSET_HELPERS.clear();

        ArrayList<File> files = new ArrayList<>();
        File[] headHelpers = getHeadsDir().toFile().listFiles();
        if(headHelpers != null)
        {
            for(File file : headHelpers)
            {
                if(!file.isDirectory() && file.getName().endsWith(".json"))
                {
                    files.add(file);
                }
            }
        }

        int count = 0;
        for(File file : files)
        {
            try
            {
                String json = FileUtils.readFileToString(file, "UTF-8");
                if(readHeadInfoJson(json))
                {
                    count++;
                }
                else
                {
                    iChunUtil.LOGGER.error("Error reading HeadInfo file, no forClass: {}", file);
                }
            }
            catch(IOException | JsonSyntaxException e)
            {
                iChunUtil.LOGGER.error("Error reading HeadInfo file: {}", file);
                e.printStackTrace();
            }
            catch(ClassNotFoundException e)
            {
                iChunUtil.LOGGER.error("Class not found for HeadInfo file: {}", file);
            }
        }

        iChunUtil.LOGGER.info("Loaded {} HeadInfo object(s)", count);

        if(!IMC_HEAD_INFO.isEmpty())
        {
            count = 0;
            for(String s : IMC_HEAD_INFO)
            {
                try
                {
                    if(readHeadInfoJson(s))
                    {
                        count++;
                    }
                    else
                    {
                        iChunUtil.LOGGER.error("Error reading IMC HeadInfo file: {}", s);
                    }
                }
                catch(JsonSyntaxException e)
                {
                    iChunUtil.LOGGER.error("Error reading IMC HeadInfo file: {}", s);
                    e.printStackTrace();
                }
                catch(ClassNotFoundException e)
                {
                    iChunUtil.LOGGER.error("Class not found for IMC HeadInfo file: {}", s);
                }
            }
            iChunUtil.LOGGER.info("Loaded {} IMC HeadInfo object(s)", count);
        }

        return count;
    }

    public static boolean readHeadInfoJson(String json) throws ClassNotFoundException, JsonSyntaxException
    {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        if(jsonObject.has("forClass"))
        {
            String className = jsonObject.get("forClass").getAsString();

            Class clz = Class.forName(className);

            MODEL_OFFSET_HELPERS_JSON.put(clz, json);

            if(FMLEnvironment.dist.isClient())
            {
                if(!loadHeadInfo(clz, json))
                {
                    MODEL_OFFSET_HELPERS_JSON.remove(clz);
                }
            }
            return true;
        }
        return false;
    }

    public static boolean loadHeadInfo(Class clz, String json)
    {
        try
        {
            HeadInfo info = GSON.fromJson(json, HeadInfo.class);
            MODEL_OFFSET_HELPERS.put(clz, info);
        }
        catch(Throwable t)
        {
            iChunUtil.LOGGER.error("Error deserializing HeadInfo for {}", clz.getName());
            t.printStackTrace();
        }
        return false;
    }


    //SERIALISING STUFF

    public static void serializeHeadInfos()
    {
        for(Map.Entry<Class<? extends LivingEntity>, HeadInfo<?>> e : HeadHandler.MODEL_OFFSET_HELPERS.entrySet())
        {
            if(e.getKey() == PlayerEntity.class)
            {
                e.getValue().hasStrippedInfo = true;
            }

            e.getValue().forClass = e.getKey().getName();

            File file = new File(HeadHandler.getHeadsDir().toFile(), e.getKey().getSimpleName() + ".json");
            try
            {
                String json = HeadHandler.GSON.toJson(e.getValue(), HeadInfo.class);
                FileUtils.writeStringToFile(file, json, "UTF-8");
            }
            catch(IOException ignored){}
            catch(Throwable e1)
            {
                e1.printStackTrace();
                break;
            }
        }
    }

    public static class SerialiserHelper
    {
        public boolean shiftKeyDown;

        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event)
        {
            if(event.phase == TickEvent.Phase.END)
            {
                if(shiftKeyDown && !Screen.hasShiftDown())
                {
                    System.out.println("dump");
                    HeadHandler.serializeHeadInfos();
                }
                shiftKeyDown = Screen.hasShiftDown();
            }
        }
    }
}
