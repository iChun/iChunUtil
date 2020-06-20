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
    @Prop
    public boolean easterEgg = true;

    @Prop
    public boolean guiStyleMinecraft = false;

    @Prop(min = 0, max = 50)
    public int guiDockPadding = 0;

    @Prop(min = 0)
    public int guiTooltipCooldown = 20;

    @Prop(min = 1)
    public int guiDockBorder = 8;

    @Prop(min = 1)
    public int guiDoubleClickSpeed = 10;

    @Prop(guiElementOverride = "iChunUtil:guiDefaultTheme")
    public String guiDefaultTheme = "default";

    @Prop
    public boolean buttonOptionsShiftOpensMods = true;

    @CategoryDivider(name = "headTracking")
    @Prop(min = 0, max = 2)
    public int aggressiveHeadTracking = 1;

    @Prop
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
