package me.ichun.mods.ichunutil.client.core;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowPopup;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.*;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.ichunutil.common.head.HeadHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.util.IOUtil;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

public class ConfigClient extends ConfigBase
{
    @CategoryDivider(name = "bns")
    @Prop(min = 0, max = 2)
    public int guiMinecraftStyle = 0;

    @Prop(min = 0, max = 50)
    public int guiDockPadding = 0;

    @Prop(min = 0)
    public int guiTooltipCooldown = 10;

    @Prop(min = 1)
    public int guiDockBorder = 8;

    @Prop(min = 1)
    public int guiDoubleClickSpeed = 10;

    @Prop(guiElementOverride = "iChunUtil:guiDefaultTheme")
    public String guiDefaultTheme = "default";

    @CategoryDivider(name = "clientOnly")
    public boolean easterEgg = true;

    @Prop(env = LoaderHandler.Env.FORGE) //TODO do this method in Forge!
    public boolean buttonOptionsShiftOpensMods = true;

    @Prop
    public boolean overrideToastGui = true;

    @CategoryDivider(name = "headTracking")
    @Prop(min = 0, max = 2)
    public int aggressiveHeadTracking = 1;

    @Prop(guiElementOverride = "iChunUtil:reloadHeadsButton")//we're mounting this to add a button underneath
    public boolean horseEasterEgg = true;

    @Override
    public void registerGuiElementOverrides()
    {
        guiElementOverrides.put("iChunUtil:guiDefaultTheme", (entry, item) -> {

            Field field = entry.entry.field;
            field.setAccessible(true);
            Object o;
            try
            {
                o = field.get(entry.config);
            }
            catch(IllegalAccessException e)
            {
                iChunUtil.LOGGER.error("Error accessing config field {} when creating config. Stopping config creation.", field.getName());
                e.printStackTrace();
                return true;
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
                            field.set(entry.config, contextMenu.text);

                            Theme.loadTheme(theme);
                        }
                    }
                    catch(IOException | IllegalAccessException ignored){}
                }
            }).setNameProvider(o1 -> ((File)o1).getName().substring(0, ((File)o1).getName().length() - 5));
            input.setSize(80, 14);
            input.setConstraint(new Constraint(input).top(item, Constraint.Property.Type.TOP, 3).bottom(item, Constraint.Property.Type.BOTTOM, 3).right(item, Constraint.Property.Type.RIGHT, 8));
            item.addElement(input);

            return true;
        });
        guiElementOverrides.put("iChunUtil:reloadHeadsButton", (entry, itemOri) -> {

            ElementList.Item<?> item = itemOri.parentFragment.addItem(entry).setBorderSize(0);
            item.setSelectionHandler(itemObj -> {
                if(itemObj.selected)
                {
                    for(Element<?> element : itemObj.elements)
                    {
                        if(element instanceof ElementTextWrapper || element instanceof ElementPadding)
                        {
                            continue;
                        }
                        element.parentFragment.setFocused(element);
                        element.mouseClicked(element.getLeft() + element.getWidth() / 2D, element.getTop() + element.getHeight() / 2D, 0);
                        element.mouseReleased(element.getLeft() + element.getWidth() / 2D, element.getTop() + element.getHeight() / 2D, 0);
                        break;
                    }
                }
            });
            ElementTextWrapper wrapper = new ElementTextWrapper(item).setText(I18n.get("config.ichunutil.headTracking.reload.desc"));
            wrapper.setConstraint(new Constraint(wrapper).left(item, Constraint.Property.Type.LEFT, 3).right(item, Constraint.Property.Type.RIGHT, 90));
            wrapper.setTooltip(entry.desc);
            item.addElement(wrapper);
            ElementPadding padding = new ElementPadding(item, 0, 20);
            padding.setConstraint(new Constraint(padding).right(item, Constraint.Property.Type.RIGHT, 0));
            item.addElement(padding);

            ElementButton<?> button = new ElementButton<>(item, "config.ichunutil.headTracking.reload.btn", btn ->
            {
                if(HeadHandler.hasInit())
                {
                    int count = HeadHandler.loadHeadInfos();
                    WindowPopup.popup(item.getWorkspace(), 0.6D, 0.6D, null, I18n.get("config.ichunutil.headTracking.reload.count", count));
                }
                else
                {
                    WindowPopup.popup(item.getWorkspace(), 0.6D, 0.6D, null, I18n.get("config.ichunutil.headTracking.notLoaded"));
                }
            });
            button.setTooltip(I18n.get("config.ichunutil.headTracking.reload.desc"));
            button.setSize(80, 14);
            button.setConstraint(new Constraint(button).top(item, Constraint.Property.Type.TOP, 3).bottom(item, Constraint.Property.Type.BOTTOM, 3).right(item, Constraint.Property.Type.RIGHT, 8));
            item.addElement(button);

            ElementButton<?> button1 = new ElementButton<>(item, "config.ichunutil.headTracking.reload.reextract", btn ->
            {
                try
                {
                    if(HeadHandler.hasInit())
                    {
                        InputStream in = iChunUtil.class.getResourceAsStream("/heads.zip");
                        if(in != null)
                        {
                            int extCount = IOUtil.extractFiles(HeadHandler.getHeadsDir(), in, true);

                            HeadHandler.loadHeadInfos();
                            WindowPopup.popup(item.getWorkspace(), 0.6D, 0.6D, null, I18n.get("config.ichunutil.headTracking.reload.reextract.count", extCount));
                        }
                        else
                        {
                            iChunUtil.LOGGER.error("Error extracting heads.zip.");
                            WindowPopup.popup(item.getWorkspace(), 0.6D, 0.6D, null, "Error!", "Error extracting heads.zip.");
                        }
                    }
                    else
                    {
                        WindowPopup.popup(item.getWorkspace(), 0.6D, 0.6D, null, I18n.get("config.ichunutil.headTracking.notLoaded"));
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            });
            button1.setTooltip(I18n.get("config.ichunutil.headTracking.reload.reextract.desc"));
            button1.setSize(80, 14);
            button1.setConstraint(new Constraint(button1).top(item, Constraint.Property.Type.TOP, 3).bottom(item, Constraint.Property.Type.BOTTOM, 3).right(button, Constraint.Property.Type.LEFT, 4));
            item.addElement(button1);

            return false; //we still want the button to generate, this is a hook in.
        });
    }

    @Override
    public void onConfigLoaded()
    {
        Path path = ResourceHelper.getThemesDir().resolve(guiDefaultTheme + ".json");
        if(Files.exists(path) && Files.isRegularFile(path))
        {
            try
            {
                InputStream con = new FileInputStream(path.toFile());
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
