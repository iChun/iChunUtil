package me.ichun.mods.ichunutil.client.gui.bns;

import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Theme
{
    private static final transient Theme INSTANCE = new Theme();

    public transient String filename;
    public transient Block block = Blocks.SPRUCE_PLANKS;

    //defaults. Try not to change this if possible.
    public String name = "Default";
    public String author = "iChun";

    public int[] workspaceBackground = new int[] { 204, 204, 204 };
    public int[] windowBorder = new int[] { 150, 150, 150 };
    public int[] windowBackground = new int[] { 34, 34, 34 };
    public int[] tabBorder = new int[] { 255, 255, 255 };
    public int[] tabSideInactive = new int[] { 100, 100, 100 };

    public int[] elementInputBackgroundInactive = new int[] { 60, 60, 60 };
    public int[] elementInputBackgroundHover = new int[] { 70, 70, 70 };
    public int[] elementInputBorder = new int[] { 140, 140, 140 };
    public int[] elementInputUpDownHover = new int[] { 170, 170, 170 };
    public int[] elementInputUpDownClick = new int[] { 100, 100, 100 };

    public int[] elementButtonBackgroundInactive = new int[] { 60, 60, 60 };
    public int[] elementButtonBackgroundHover = new int[] { 70, 70, 70 };
    public int[] elementButtonBorder = new int[] { 140, 140, 140 };
    public int[] elementButtonClick = new int[] { 100, 100, 100 };
    public int[] elementButtonToggle = new int[] { 120, 120, 120 };
    public int[] elementButtonToggleHover = new int[] { 170, 170, 170 };

    public int[] elementProjectTabActive = new int[] { 60, 60, 60 };
    public int[] elementProjectTabHover = new int[] { 100, 100, 100 };
    public int[] elementProjectTabFont = new int[] { 140, 140, 140 };
    public int[] elementProjectTabFontChanges = new int[] { 255, 255, 255 };

    public int[] elementTreeBorder = new int[] { 100, 100, 100 };
    public int[] elementTreeScrollBar = new int[] { 34, 34, 34 };
    public int[] elementTreeScrollBarBorder = new int[] { 60, 60, 60 };

    public int[] elementTreeItemBorder = new int[] { 40, 40, 40 };
    public int[] elementTreeItemBg = new int[] { 60, 60, 60 };
    public int[] elementTreeItemBgSelect = new int[] { 100, 100, 100 };
    public int[] elementTreeItemBgHover = new int[] { 120, 120, 120 };

    public int[] fontChat = new int[] { 220, 220, 220 };
    public int[] font = new int[] { 255, 255, 255 };
    public int[] fontDim = new int[] { 150, 150, 150 };

    public String workspaceBlock = "minecraft:spruce_planks";

    public static Theme getInstance()
    {
        return INSTANCE;
    }

    public static void loadTheme(Theme themeToLoad)
    {
        loadTheme(INSTANCE, themeToLoad);
        Block block = LoaderHandler.d().getBlockFromRegistry(new ResourceLocation(INSTANCE.workspaceBlock));
        if(block != null)
        {
            INSTANCE.block = block;
        }
    }

    public static void loadTheme(Theme theme, Theme themeToLoad)
    {
        if(theme == null || themeToLoad == null || theme.getClass() != themeToLoad.getClass())
        {
            return;
        }

        Class clz = themeToLoad.getClass();
        while(clz != Object.class)
        {
            Field[] fields = clz.getDeclaredFields();
            try
            {
                for(Field f : fields)
                {
                    f.setAccessible(true);
                    if(!Modifier.isStatic(f.getModifiers()))
                    {
                        Object obj = f.get(themeToLoad);
                        if(obj != null)
                        {
                            f.set(theme, obj);
                        }
                    }
                }
            }
            catch(Exception ignored){}
            clz = clz.getSuperclass();
        }
    }

    public static Theme copyInstance()
    {
        return copyTheme(INSTANCE);
    }

    public static Theme copyTheme(Theme newTheme)
    {
        try
        {
            Theme theme = newTheme.getClass().getDeclaredConstructor().newInstance();
            loadTheme(theme, newTheme);
            return theme;
        }
        catch(Exception e)
        {
            return null;
        }
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
