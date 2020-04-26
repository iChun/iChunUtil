package me.ichun.mods.ichunutil.client.core;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementDropdownContextMenu;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

public class ConfigClient extends ConfigBase
{
    @CategoryDivider(name = "clientOnly")
    @Prop(comment = "Enables (most) Client-Side Easter Eggs for iChun's Mods")
    public boolean easterEgg = true;

    @Prop(comment = "Renders iChunUtil's GUIs (Boxes & Stuff) in a Minecraft Style instead")
    public boolean guiStyleMinecraft = false;

    @Prop(min = 0, max = 50, comment = "How much padding to add to the docked windows")
    public int guiDockPadding = 0;

    @Prop(min = 0, comment = "Number of ticks before showing a tooltip")
    public int guiTooltipCooldown = 20;

    @Prop(min = 1, comment = "Number of pixels before iChunUtil thinks you're trying to dock a window")
    public int guiDockBorder = 8;

    @Prop(min = 1, comment = "Speed, in ticks, to register a double click")
    public int guiDoubleClickSpeed = 10;

    @Prop(guiElementOverride = "iChunUtil:guiDefaultTheme", comment = "Default Theme for Boxes & Stuff")
    public String guiDefaultTheme = "default";

    //TODO localise them all
    @Prop(comment = "Override the Options button so pressing Shift when clicking it shows the Mods list.")
    public boolean buttonOptionsShiftOpensMods = true;

    @CategoryDivider(name = "headTracking")
    @Prop(min = 0, max = 2, comment = "Track the head model aggressively to try and fix improper head tracking that may be caused by mod conflicts?\n1 = All Entities\n2 = Players Only")
    public int aggressiveHeadTracking = 1;

    @Prop(comment = "Enable the easter egg where the horse/llama's \"head\" is its behind. This is an easter egg on how Hats used to put hats on Horses.")
    public boolean horseEasterEgg = true;

    @Override
    public <T extends ConfigBase> T init()
    {
        GUI_ELEMENT_OVERRIDES.put("iChunUtil:guiDefaultTheme", (value, item) -> {

            Field field = value.value.field;
            field.setAccessible(true);
            Object o;
            try
            {
                o = field.get(value.value.parent);
            }
            catch(IllegalAccessException e)
            {
                return;
            }


            ArrayList<File> files = new ArrayList<>();
            File[] themes = ResourceHelper.getThemesDir().toFile().listFiles();
            if(themes != null)
            {
                for(File file : themes)
                {
                    if(!file.isDirectory() && file.getName().endsWith(".json"))
                    {
                        files.add(file);
                    }
                }
            }
            Collections.sort(files);

            ElementDropdownContextMenu<?> input = new ElementDropdownContextMenu<>(item, o.toString(), files, (menu, listItem) ->
            {
                if(listItem.selected)
                {
                    ElementDropdownContextMenu<?> contextMenu = (ElementDropdownContextMenu<?>)menu;
                    File file = (File)listItem.getObject();
                    contextMenu.text = file.getName().substring(0, file.getName().length() - 5);//trim the ".json"

                    //update the theme here
                    try
                    {
                        InputStream con = new FileInputStream(file);
                        String data = new String(ByteStreams.toByteArray(con));
                        con.close();

                        Theme theme = (new Gson()).fromJson(data, Theme.class);

                        if(theme != null)
                        {
                            field.set(value.value.parent, contextMenu.text);

                            Theme.loadTheme(theme);
                        }
                    }
                    catch(IOException | IllegalAccessException ignored){}
                }
            }).setNameProvider(o1 -> ((File)o1).getName().substring(0, ((File)o1).getName().length() - 5));
            input.setSize(80, 14);
            input.setConstraint(new Constraint(input).top(item, Constraint.Property.Type.TOP, 3).bottom(item, Constraint.Property.Type.BOTTOM, 3).right(item, Constraint.Property.Type.RIGHT, 8));
            item.addElement(input);

        });
        return super.init();
    }

    @Override
    public void onConfigLoaded()
    {
        File file = new File(ResourceHelper.getThemesDir().toFile(), guiDefaultTheme + ".json");
        if(file.exists())
        {
            try
            {
                InputStream con = new FileInputStream(file);
                String data = new String(ByteStreams.toByteArray(con));
                con.close();

                Theme theme = (new Gson()).fromJson(data, Theme.class);

                if(theme != null)
                {
                    Theme.loadTheme(theme);
                }
            }
            catch(IOException ignored)
            {
            }
        }
    }

    @Nonnull
    @Override
    public String getModId()
    {
        return iChunUtil.MOD_ID;
    }

    @Nonnull
    @Override
    public String getConfigName()
    {
        return iChunUtil.MOD_NAME;
    }

    @Nonnull
    @Override
    public ModConfig.Type getConfigType()
    {
        return ModConfig.Type.CLIENT;
    }
}
