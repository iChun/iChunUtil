package me.ichun.mods.ichunutil.common.core.config;

import com.google.common.base.Splitter;
import com.google.common.collect.Ordering;
import me.ichun.mods.ichunutil.client.gui.window.element.IIdentifiable;
import me.ichun.mods.ichunutil.client.gui.window.element.IListable;
import me.ichun.mods.ichunutil.client.keybind.KeyBind;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import me.ichun.mods.ichunutil.common.core.config.annotations.StringValues;
import me.ichun.mods.ichunutil.common.core.config.types.Colour;
import me.ichun.mods.ichunutil.common.core.config.types.NestedIntArray;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.packet.mod.PacketSession;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class ConfigBase
        implements Comparable<ConfigBase>, IIdentifiable, IListable
{
    private Configuration config;

    private boolean setup;

    public TreeMap<CategoryInfo, ArrayList<PropInfo>> categories = new TreeMap<CategoryInfo, ArrayList<ConfigBase.PropInfo>>(Ordering.natural());

    public HashMap<Field, Object> session = new HashMap<Field, Object>();
    public HashMap<Field, Object> configScreen = new HashMap<Field, Object>();

    public ArrayList<Field> requiresRestart = new ArrayList<Field>();
    public ArrayList<Field> sessionProp = new ArrayList<Field>();

    //This stores the list of properties to reveal/unhide in the config screen/file
    public ArrayList<String> propsToReveal = new ArrayList<String>();

    public ConfigBase(File file)
    {
        config = new Configuration(file);
        config.load();
    }

    public abstract String getModId();
    public abstract String getModName();

    @Override
    public int compareTo(ConfigBase cfg)
    {
        return getModName().compareTo(cfg.getModName());
    }

    @Override
    public boolean equals(Object o)
    {
        return o.getClass() == this.getClass() && ((ConfigBase)o).getModName().equals(getModName());
    }

    @Override
    public String getIdentifier()
    {
        return getModName();
    }

    @Override
    public String getName()
    {
        return getModName();
    }

    @Override
    public boolean localizable()
    {
        return false;
    }

    public void read()
    {
        readFields(false);
        setCategoryComments();
    }

    public void readFields(boolean write)
    {
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field : fields)
        {
            field.setAccessible(true);
            if(!Modifier.isTransient(field.getModifiers()) && field.isAnnotationPresent(ConfigProp.class))
            {
                readProperty(field, write);
            }
        }
    }

    public void readProperty(Field field, boolean write)
    {
        ConfigProp propInfo = field.getAnnotation(ConfigProp.class);
        if(propInfo.hidden() && !propsToReveal.contains(field.getName()) || propInfo.side().isClient() && FMLCommonHandler.instance().getSide().isServer())
        {
            return;
        }
        Class clz = field.getType();
        CategoryInfo info = new CategoryInfo(clz.equals(KeyBind.class) ? "keybind" : propInfo.module().isEmpty() ? propInfo.category() : "module." + propInfo.module());
        ArrayList<ConfigBase.PropInfo> fields = null;
        for(Map.Entry<CategoryInfo, ArrayList<ConfigBase.PropInfo>> e : categories.entrySet())
        {
            CategoryInfo inf = e.getKey();
            if(inf.category.equals(info.category))
            {
                info = inf;
                fields = e.getValue();
                break;
            }
        }
        if(fields == null)
        {
            fields = new ArrayList<ConfigBase.PropInfo>();
            categories.put(info, fields);
        }
        String comment = propInfo.comment().equals("undefined") ? I18n.translateToLocal(getModId().toLowerCase() + ".config.prop." + field.getName() + ".comment") : I18n.translateToLocal(propInfo.comment());
        if(Splitter.on(".").splitToList(comment).size() >= 2 && !comment.contains(" ") && !setup) //localized but no comment?
        {
            iChunUtil.LOGGER.warn("Config property " + field.getName() + " from mod " + getModName() + " may not be localized!");
        }
        String commentLocal = I18n.translateToLocal(comment);
        List cms = Splitter.on("\\n").splitToList(commentLocal);
        String cm = "";
        for(int ll = 0; ll < cms.size(); ll++)
        {
            cm = cm + cms.get(ll);
            if(ll != cms.size() - 1)
            {
                cm = cm + "\n";
            }
        }
        comment = cm;
        try
        {
            String category = !propInfo.module().isEmpty() ? "module." + propInfo.module() + "." + propInfo.category() : propInfo.category();
            if(clz.equals(int.class))
            {
                int min = Integer.MIN_VALUE;
                int max = Integer.MAX_VALUE;
                if(field.isAnnotationPresent(IntMinMax.class))
                {
                    IntMinMax minMax =  field.getAnnotation(IntMinMax.class);
                    min = minMax.min();
                    max = minMax.max();
                }
                else if(field.isAnnotationPresent(IntBool.class))
                {
                    min = 0;
                    max = 1;
                }
                if(write)
                {
                    config.get(category, field.getName(), field.getInt(this)).set(field.getInt(this));
                    config.get(category, field.getName(), field.getInt(this)).comment = comment;
                }
                else
                {
                    field.set(this, config.getInt(field.getName(), category, field.getInt(this), min, max, comment));
                }
            }
            else if(clz.equals(int[].class))
            {
                int min = Integer.MIN_VALUE;
                int max = Integer.MAX_VALUE;
                if(field.isAnnotationPresent(IntMinMax.class))
                {
                    IntMinMax minMax =  field.getAnnotation(IntMinMax.class);
                    min = minMax.min();
                    max = minMax.max();
                }
                if(write)
                {
                    config.get(category, field.getName(), (int[])field.get(this)).set((int[])field.get(this));
                    config.get(category, field.getName(), (int[])field.get(this)).comment = comment;
                }
                else
                {
                    field.set(this, config.get(category, field.getName(), (int[])field.get(this), comment, min, max).getIntList());
                }
            }
            else if(clz.equals(NestedIntArray.class))
            {
                NestedIntArray nestedIntArray = (NestedIntArray)field.get(this);
                int min = Integer.MIN_VALUE;
                int max = Integer.MAX_VALUE;
                int nMin = Integer.MIN_VALUE;
                int nMax = Integer.MAX_VALUE;
                if(field.isAnnotationPresent(IntMinMax.class))
                {
                    IntMinMax minMax =  field.getAnnotation(IntMinMax.class);
                    min = minMax.min();
                    max = minMax.max();
                    nMin = minMax.nestedMin();
                    nMax = minMax.nestedMax();
                }
                if(write)
                {
                    config.get(category, field.getName(), nestedIntArray.serialize()).set(nestedIntArray.serialize());
                    config.get(category, field.getName(), nestedIntArray.serialize()).comment = comment;
                }
                else
                {
                    nestedIntArray.deserialize(config.getString(field.getName(), category, nestedIntArray.serialize(), comment), min, max, nMin, nMax);
                }
            }
            else if(clz.equals(Colour.class))
            {
                Colour clr = (Colour)field.get(this);
                if(write)
                {
                    config.get(category, field.getName(), clr.serialize()).set(clr.serialize());
                    config.get(category, field.getName(), clr.serialize()).comment = comment;
                }
                else
                {
                    clr.deserialize(config.getString(field.getName(), category, ((Colour)field.get(this)).serialize(), comment));
                }
            }
            else if(clz.equals(String.class))
            {
                if(write)
                {
                    config.get(category, field.getName(), (String)field.get(this)).set((String)field.get(this));
                    config.get(category, field.getName(), (String)field.get(this)).comment = comment;
                }
                else
                {
                    if(field.isAnnotationPresent(StringValues.class))
                    {
                        StringValues minMax = field.getAnnotation(StringValues.class);
                        field.set(this, config.getString(field.getName(), category, (String)field.get(this), comment, minMax.values()));
                    }
                    else
                    {
                        field.set(this, config.getString(field.getName(), category, (String)field.get(this), comment));
                    }
                }
            }
            else if(clz.equals(String[].class))
            {
                if(write)
                {
                    config.get(category, field.getName(), (String[])field.get(this)).set((String[])field.get(this));
                    config.get(category, field.getName(), (String[])field.get(this)).comment = comment;
                }
                else
                {
                    if(field.isAnnotationPresent(StringValues.class))
                    {
                        StringValues minMax = field.getAnnotation(StringValues.class);
                        field.set(this, config.getStringList(field.getName(), category, (String[])field.get(this), comment, minMax.values()));
                    }
                    else
                    {
                        field.set(this, config.getStringList(field.getName(), category, (String[])field.get(this), comment));
                    }
                }
            }
            else if(clz.equals(KeyBind.class))
            {
                KeyBind bind = (KeyBind)field.get(this);
                if(write)
                {
                    ConfigHandler.configKeybind.get("keybinds", getModId().toLowerCase() + "." + field.getName(), bind.serialize()).set(bind.serialize());
                    ConfigHandler.configKeybind.get("keybinds", getModId().toLowerCase() + "." + field.getName(), bind.serialize()).comment = comment;
                }
                else
                {
                    bind.deserialize(ConfigHandler.configKeybind.getString(getModId().toLowerCase() + "." + field.getName(), "keybinds", bind.serialize(), comment));
                    bind = iChunUtil.proxy.registerKeyBind(bind, null);
                    field.set(this, bind);
                }
            }
            else
            {
                return;
            }
            PropInfo propInfo1 = new PropInfo(!propInfo.nameOverride().isEmpty() ? propInfo.nameOverride() : I18n.translateToLocal(getModId().toLowerCase() + ".config.prop." + field.getName() + ".name"), comment, field);
            if(!fields.contains(propInfo1))
            {
                fields.add(propInfo1);
            }

            if(!propInfo.changeable() && !requiresRestart.contains(field))
            {
                requiresRestart.add(field);
            }
            if(propInfo.useSession() && !sessionProp.contains(field))
            {
                sessionProp.add(field);
            }
        }
        catch(Exception ignored){}
    }

    public void setCategoryComments()
    {
        for(Map.Entry<CategoryInfo, ArrayList<ConfigBase.PropInfo>> e : categories.entrySet())
        {
            CategoryInfo info = e.getKey();
            String cat = info.category;
            String comment;
            if(cat.equals("general") || cat.equals("gameplay") || cat.equals("globalOptions") || cat.equals("serverOptions") || cat.equals("clientOnly") || cat.equals("keybind") || cat.equals("block"))
            {
                info.name = I18n.translateToLocal(String.format("ichunutil.config.cat.%s.name", cat));
                comment = I18n.translateToLocal(String.format("ichunutil.config.cat.%s.comment", cat));
            }
            else
            {
                info.name = I18n.translateToLocal(getModId().toLowerCase() + ".config.cat." + cat + ".name");
                comment = I18n.translateToLocal(getModId().toLowerCase() + ".config.cat." + cat + ".comment");
            }
            info.comment = comment;
            config.setCategoryComment(cat, comment);
        }
    }

    public void storeSession()
    {
        for(Field field : sessionProp)
        {
            try
            {
                field.setAccessible(true);
                session.put(field, field.get(this));
            }
            catch(Exception ignored){}
        }
    }

    public void resetSession()
    {
        for(Map.Entry<Field, Object> e : session.entrySet())
        {
            try
            {
                e.getKey().setAccessible(true);
                e.getKey().set(this, e.getValue());
            }
            catch(Exception ignored){}
        }
    }

    public void sendPlayerSession(EntityPlayer player)
    {
        iChunUtil.channel.sendTo(new PacketSession(this), player);
    }

    public void updateSessionToAllPlayers()
    {
        iChunUtil.channel.sendToAll(new PacketSession(this));
    }


    public void onReceiveSession()
    {
    }

    public void onSessionChange(Field field, Object original)
    {
    }

    public void enterConfigScreen()
    {
        for(Field field : sessionProp)
        {
            try
            {
                field.setAccessible(true);
                configScreen.put(field, field.get(this));
            }
            catch(Exception ignored){}
        }
        resetSession();
    }

    public void exitConfigScreen()
    {
        storeSession();
        for(Map.Entry<Field, Object> e : configScreen.entrySet())
        {
            try
            {
                e.getKey().setAccessible(true);
                e.getKey().set(this, e.getValue());
            }
            catch(Exception ignored){}
        }
        configScreen.clear();
    }

    public void reveal(String...toReveal)
    {
        boolean add = false;
        for(String s : toReveal)
        {
            if(!propsToReveal.contains(s))
            {
                propsToReveal.add(s);
                add = true;
            }
        }
        if(add)
        {
            read();
            save();
        }
    }

    public void onConfigChange(Field field, Object original) //Nested int array and keybind original is the new var, no ori cause lazy
    {
    }

    public void setup()
    {
        setup = true;
        save(false);
    }

    public void save(boolean readFields)
    {
        if(readFields)
        {
            readFields(true);
        }
        if(setup && config.hasChanged())
        {
            config.save();
        }
    }

    public void save()
    {
        save(true);
    }

    public boolean hasSetup()
    {
        return setup;
    }

    public class CategoryInfo
            implements Comparable<CategoryInfo>, IIdentifiable, IListable
    {
        public final String category;
        public String name;
        public String comment;

        public CategoryInfo(String cat)
        {
            category = name = cat;
        }

        @Override
        public int compareTo(CategoryInfo cfg)
        {
            return category.compareTo(cfg.category);
        }

        @Override
        public String getIdentifier()
        {
            return category;
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public boolean localizable()
        {
            return false;
        }
    }

    public class PropInfo implements Comparable<PropInfo>
    {
        public final String name;
        public final String comment;
        public final Field field;

        public PropInfo(String name, String comment, Field field)
        {
            this.name = name;
            this.comment = comment;
            this.field = field;
        }

        @Override
        public boolean equals(Object o)
        {
            if(o instanceof PropInfo)
            {
                PropInfo cfg = (PropInfo)o;
                return name.equals(cfg.name) && comment.equals(cfg.comment) && field.equals(cfg.field);
            }
            return false;
        }

        @Override
        public int compareTo(PropInfo cfg)
        {
            return name.compareTo(cfg.name);
        }
    }
}
