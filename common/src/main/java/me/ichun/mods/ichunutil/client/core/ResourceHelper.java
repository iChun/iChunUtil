package me.ichun.mods.ichunutil.client.core;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.util.StringUtil;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ResourceHelper
{
    public static final ResourceLocation TEX_TAMED_WOLF = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
    public static final ResourceLocation TEX_WOLF_COLLAR = new ResourceLocation("textures/entity/wolf/wolf_collar.png");
    public static final ResourceLocation TEX_PIG = new ResourceLocation("textures/entity/pig/pig.png");

    private static Path workingDir;
    private static Path themesDir;

    private static boolean init;

    public static void init()
    {
        if(!init)
        {
            init = true;

            try
            {
                workingDir = iChunUtil.d().getConfigDir().resolve(iChunUtil.MOD_ID);
                if(!Files.exists(workingDir)) Files.createDirectory(workingDir);

                themesDir = workingDir.resolve("themes");
                if(!Files.exists(themesDir)) Files.createDirectory(themesDir);

                Path defaultTheme = themesDir.resolve("default.json");
                if(!Files.exists(defaultTheme)) //presume we haven't extracted anything yet
                {
                    String jsonOutput = StringUtil.GSON_PRETTY.toJson(new Theme());

                    try
                    {
                        Files.writeString(defaultTheme, jsonOutput, StandardCharsets.UTF_8);
                    }
                    catch(IOException e)
                    {
                        iChunUtil.LOGGER.error("Error writing default theme to themes folder!", e);
                    }

                    InputStream in = iChunUtil.class.getResourceAsStream("/themes.zip");
                    if(in != null)
                    {
                        ZipInputStream zipStream = new ZipInputStream(in);
                        ZipEntry entry = null;

                        while((entry = zipStream.getNextEntry()) != null)
                        {
                            File file = new File(themesDir.toFile(), entry.getName());
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
                }

                if(iChunUtil.d().isDevEnvironment() && iChunUtil.d().env().isFabric())
                {
                    createFabricLoaderDependenciesOverride();
                }
            }
            catch(IOException e)
            {
                throw new RuntimeException("Error initialising resources!", e);
            }
        }
    }

    private static void createFabricLoaderDependenciesOverride() throws IOException
    {
        Path file = iChunUtil.d().getConfigDir().resolve("fabric_loader_dependencies.json");
        if(!Files.exists(file))
        {
            LinkedHashMap<String, Object> json = new LinkedHashMap<>();
            json.put("version", 1);
            TreeMap<String, Object> modIdToDep = new TreeMap<>(Comparator.naturalOrder());
            json.put("overrides", modIdToDep);

            ArrayList<String> modsToOverride = new ArrayList<>();
            modsToOverride.add("betterthanbunnies");
            modsToOverride.add("betterthanllamas");
            modsToOverride.add("deathcounter");
            modsToOverride.add("ding");
            modsToOverride.add("dogslie");
            modsToOverride.add("limitedlives");
            modsToOverride.add("partyparrots");
            modsToOverride.add("serverpause");

            for(String s : modsToOverride)
            {
                TreeMap<String, Object> depToDef = new TreeMap<>();
                modIdToDep.put(s, depToDef);
                TreeMap<String, String> def = new TreeMap<>();
                depToDef.put("-depends", def);
                def.put("ichunutil", "IGNORED");
            }

            Files.writeString(file, StringUtil.GSON_PRETTY.toJson(json), StandardCharsets.UTF_8);
        }
    }

    public static Path getThemesDir()
    {
        return themesDir;
    }
}
