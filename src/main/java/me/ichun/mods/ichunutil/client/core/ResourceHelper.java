package me.ichun.mods.ichunutil.client.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ResourceHelper
{
    public static final ResourceLocation TEX_TAMED_WOLF = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
    public static final ResourceLocation TEX_WOLF_COLLAR = new ResourceLocation("textures/entity/wolf/wolf_collar.png");


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
                workingDir = FMLPaths.CONFIGDIR.get().resolve(iChunUtil.MOD_ID);
                if(!Files.exists(workingDir)) Files.createDirectory(workingDir);

                themesDir = workingDir.resolve("themes");
                if(!Files.exists(themesDir)) Files.createDirectory(themesDir);

                File defaultTheme = new File(themesDir.toFile(), "default.json");
                if(!defaultTheme.exists()) //presume we haven't extracted anything yet
                {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonOutput = gson.toJson(new Theme());

                    try
                    {
                        FileUtils.writeStringToFile(defaultTheme, jsonOutput, StandardCharsets.UTF_8);
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
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
            }
            catch(IOException e)
            {
                iChunUtil.LOGGER.fatal("Error initialising resources!");
                e.printStackTrace();
            }
        }
    }

    public static Path getThemesDir()
    {
        return themesDir;
    }
}
