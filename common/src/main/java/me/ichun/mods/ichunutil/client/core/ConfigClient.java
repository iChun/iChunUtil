package me.ichun.mods.ichunutil.client.core;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementDropdownContextMenu;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.ichunutil.common.iChunUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;

public class ConfigClient extends ConfigBase
{
    @CategoryDivider(name = "bns")
    @Prop(min = 0, max = 2)
    public int bnsMinecraftStyle = 0;

    @Prop(min = 0, max = 50)
    public int bnsDockPadding = 0;

    @Prop(min = 1)
    public int bnsDockBorder = 8;

    @Prop(min = 1)
    public int bnsDoubleClickSpeed = 10;

    @Prop(min = 0, max = 6000) // 5 minutes before tooltips show (but why tho)
    public int bnsTooltipCooldown = 10;

    @Prop(guiElementOverride = "iChunUtil:bnsTheme")
    public String bnsTheme = "default";

    @Override
    public boolean addToSubfolder()
    {
        return true;
    }

    @Override
    public void registerGuiElementOverrides()
    {
        guiElementOverrides.put("iChunUtil:bnsTheme", (entry, item) -> {
            Field field = entry.field;
            field.setAccessible(true);
            Object o;
            try
            {
                o = field.get(this);
            }
            catch(IllegalAccessException e)
            {
                iChunUtil.LOGGER.error("Error accessing config field {} when creating config. Skipping creation of this config option.", field.getName(), e);
                return true;
            }

            ArrayList<Path> files = new ArrayList<>();
            try(Stream<Path> paths = Files.list(ResourceHelper.getThemesDir()))
            {
                files.addAll(paths.filter(path -> !Files.isDirectory(path) && path.getFileName().toString().endsWith(".json")).toList());
            }
            catch(IOException e)
            {
                iChunUtil.LOGGER.error("Error reading themes folder!", e);
            }

            Collections.sort(files);

            ElementDropdownContextMenu<?> input = new ElementDropdownContextMenu<>(item, o.toString(), files, (menu, listItem) -> {
                if(listItem.selected)
                {
                    ElementDropdownContextMenu<?> contextMenu = (ElementDropdownContextMenu<?>)menu;
                    Path path = (Path)listItem.getObject();
                    contextMenu.text = path.getFileName().toString().substring(0, path.getFileName().toString().length() - 5);//trim the ".json"

                    //update the theme here
                    if(Theme.loadTheme(path))
                    {
                        try
                        {
                            field.set(this, contextMenu.text);
                        }
                        catch(IllegalAccessException ignored){}
                    }
                }
            }).setNameProvider(o1 -> ((Path)o1).getFileName().toString().substring(0, ((Path)o1).getFileName().toString().length() - 5));
            input.setSize(80, 14);
            input.setConstraint(new Constraint(input).top(item, Constraint.Property.Type.TOP, 3).bottom(item, Constraint.Property.Type.BOTTOM, 3).right(item, Constraint.Property.Type.RIGHT, 8));
            item.addElement(input);

            return true;
        });
    }

    @Override
    public void onConfigLoaded()
    {
        Theme.loadTheme(ResourceHelper.getThemesDir().resolve(bnsTheme + ".json"));
    }

    @Override
    public void onPropertyChanged(boolean file, String name, Field field, Object oldObj, Object newObj)
    {
        if(name.equals("bnsTheme"))
        {
            Theme.loadTheme(ResourceHelper.getThemesDir().resolve(newObj + ".json"));
        }
    }

    @NotNull
    @Override
    public String getModId()
    {
        return iChunUtil.MOD_ID;
    }

    @NotNull
    @Override
    public String getConfigName()
    {
        return iChunUtil.MOD_NAME;
    }

    @Override
    public Type getConfigType()
    {
        return Type.CLIENT;
    }
}
