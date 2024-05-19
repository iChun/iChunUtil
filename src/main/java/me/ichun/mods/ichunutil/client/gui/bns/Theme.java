package me.ichun.mods.ichunutil.client.gui.bns;

import com.google.common.io.ByteStreams;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.util.StringUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;

public class Theme
{
    //TODO add editor as part of iChunUtil when Tabula is updated
    private static final Theme INSTANCE = new Theme();

    public transient Block block = Blocks.SPRUCE_PLANKS;

    //Serialisable contents
    public String name = "Default";
    public String author = "iChun";

    public int[] workspaceBackground = new int[] { 204, 204, 204 };

    public int[] windowBorderActive = new int[] { 150, 150, 150 };
    public int[] windowBorderInactive = new int[] { 140, 140, 140 };
    public int[] windowBackground = new int[] { 34, 34, 34 };

    public int[] elementButtonBackgroundInactive = new int[] { 60, 60, 60 };
    public int[] elementButtonBackgroundActive = new int[] { 70, 70, 70 };
    public int[] elementButtonBackgroundHover = new int[] { 85, 85, 85 };
    public int[] elementButtonBorder = new int[] { 140, 140, 140 };
    public int[] elementButtonClick = new int[] { 100, 100, 100 };
    public int[] elementButtonToggle = new int[] { 120, 120, 120 };
    public int[] elementButtonToggleHover = new int[] { 170, 170, 170 };

    public int[] elementInputBackgroundInactive = new int[] { 60, 60, 60 };
    public int[] elementInputBackgroundHover = new int[] { 70, 70, 70 };
    public int[] elementInputBorder = new int[] { 140, 140, 140 };
    public int[] elementInputUpDownHover = new int[] { 170, 170, 170 };
    public int[] elementInputUpDownClick = new int[] { 100, 100, 100 };

    public int[] elementListBorder = new int[] { 100, 100, 100 };
    public int[] elementListItemBorder = new int[] { 40, 40, 40 };
    public int[] elementListItemBackground = new int[] { 60, 60, 60 };
    public int[] elementListItemBackgroundSelect = new int[] { 100, 100, 100 };
    public int[] elementListItemBackgroundHover = new int[] { 120, 120, 120 };

    public int[] elementTabHover = new int[] { 80, 80, 80 };
    public int[] elementTabBackgroundActive = new int[] { 60, 60, 60 };
    public int[] elementTabBorderActive = new int[] { 255, 255, 255 };
    public int[] elementTabBackgroundInactive = new int[] { 34, 34, 34 };
    public int[] elementTabBorderInactive = new int[] { 100, 100, 100 };

    public int[] elementScrollBar = new int[] { 34, 34, 34 };
    public int[] elementScrollBarBorder = new int[] { 60, 60, 60 };
    public int[] elementScrollBarBackground = new int[] { 60, 60, 60 };

    public int[] font = new int[] { 255, 255, 255 };
    public int[] fontLight = new int[] { 220, 220, 220 };
    public int[] fontMid = new int[] { 185, 185, 185 };
    public int[] fontDim = new int[] { 150, 150, 150 };

    public String workspaceBlock = "minecraft:spruce_planks";

    public static Theme getInstance()
    {
        return INSTANCE;
    }

    public static void loadTheme(@NotNull Theme themeToLoad)
    {
        Class clz = themeToLoad.getClass();
        while(clz != Object.class)
        {
            Field[] fields = clz.getDeclaredFields();
            try
            {
                for(Field f : fields)
                {
                    f.setAccessible(true);
                    if(!Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers()))
                    {
                        Object obj = f.get(themeToLoad);
                        if(obj != null)
                        {
                            f.set(INSTANCE, obj);
                        }
                    }
                }
            }
            catch(Exception ignored){}
            clz = clz.getSuperclass();
        }

        Block block = iChunUtil.d().getBlockFromRegistry(new ResourceLocation(INSTANCE.workspaceBlock));
        if(block != null)
        {
            INSTANCE.block = block;
        }
    }

    public static boolean loadTheme(Path path)
    {
        try
        {
            if(Files.exists(path))
            {
                InputStream con = new FileInputStream(path.toFile());
                String data = new String(ByteStreams.toByteArray(con));
                con.close();

                Theme theme = StringUtil.GSON.fromJson(data, Theme.class);

                Theme.loadTheme(theme);
                return true;
            }
            else
            {
                iChunUtil.LOGGER.error("Theme file does not exist: {}", path);
            }
        }
        catch(IOException e)
        {
            iChunUtil.LOGGER.error("Error reading theme from {}", path, e);
        }

        return false;
    }

    public static void changeColour(int[] set, int r, int g, int b)
    {
        set[0] = r;
        set[1] = g;
        set[2] = b;
    }

    public static int getAsHex(int[] set)
    {
        return (set[0] << 16) + (set[1] << 8) + (set[2]);
    }
}
