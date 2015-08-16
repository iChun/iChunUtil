package us.ichun.mods.ichunutil.common.core.config;

import com.google.common.base.Splitter;
import com.google.common.collect.Ordering;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import us.ichun.mods.ichunutil.client.keybind.KeyBind;
import us.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import us.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import us.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import us.ichun.mods.ichunutil.common.core.config.annotations.StringValues;
import us.ichun.mods.ichunutil.common.core.config.types.Colour;
import us.ichun.mods.ichunutil.common.core.config.types.NestedIntArray;
import us.ichun.mods.ichunutil.common.core.packet.mod.PacketSession;
import us.ichun.mods.ichunutil.common.iChunUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class ConfigBase
        implements Comparable
{
    private Configuration config;

    public boolean setup;

    public ArrayList<String> propsToReveal = new ArrayList<String>();

    public ArrayList<Field> unchangable = new ArrayList<Field>();
    public ArrayList<Field> sessionProp = new ArrayList<Field>();

    public TreeMap<CategoryInfo, ArrayList<ConfigBase.PropInfo>> categories = new TreeMap<CategoryInfo, ArrayList<ConfigBase.PropInfo>>(Ordering.natural());

    public HashMap<Field, Object> session = new HashMap<Field, Object>();
    public HashMap<Field, Object> configScreen = new HashMap<Field, Object>();

    //TODO remove the bloody unhide param. It's annoying af.
    public ConfigBase(File file, String... unhide)
    {
        config = new Configuration(file);
        config.load();

        for(String s : unhide)
        {
            propsToReveal.add(s);
        }
    }

    public abstract String getModId();
    public abstract String getModName();

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
        CategoryInfo info = new CategoryInfo(clz.equals(KeyBind.class) ? "keybind" : propInfo.category());
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
        String comment = propInfo.comment().equals("undefined") ? StatCollector.translateToLocal(getModId().toLowerCase() + ".config.prop." + field.getName() + ".comment") : StatCollector.translateToLocal(propInfo.comment());
        if(Splitter.on(".").splitToList(comment).size() >= 2 && !comment.contains(" ") && !setup) //localized but no comment?
        {
            iChunUtil.logger.warn("Config property " + field.getName() + " from mod " + getModName() + " may not be localized!");
        }
        String commentLocal = StatCollector.translateToLocal(comment);
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
                    config.get(propInfo.category(), field.getName(), field.getInt(this)).set(field.getInt(this));
                    config.get(propInfo.category(), field.getName(), field.getInt(this)).comment = comment;
                }
                else
                {
                    field.set(this, config.getInt(field.getName(), propInfo.category(), field.getInt(this), min, max, comment));
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
                    config.get(propInfo.category(), field.getName(), (int[])field.get(this)).set((int[])field.get(this));
                    config.get(propInfo.category(), field.getName(), (int[])field.get(this)).comment = comment;
                }
                else
                {
                    field.set(this, config.get(propInfo.category(), field.getName(), (int[])field.get(this), comment, min, max).getIntList());
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
                    config.get(propInfo.category(), field.getName(), nestedIntArray.serialize()).set(nestedIntArray.serialize());
                    config.get(propInfo.category(), field.getName(), nestedIntArray.serialize()).comment = comment;
                }
                else
                {
                    nestedIntArray.deserialize(config.getString(field.getName(), propInfo.category(), nestedIntArray.serialize(), comment), min, max, nMin, nMax);
                }
            }
            else if(clz.equals(Colour.class))
            {
                Colour clr = (Colour)field.get(this);
                if(write)
                {
                    config.get(propInfo.category(), field.getName(), clr.serialize()).set(clr.serialize());
                    config.get(propInfo.category(), field.getName(), clr.serialize()).comment = comment;
                }
                else
                {
                    clr.deserialize(config.getString(field.getName(), propInfo.category(), ((Colour)field.get(this)).serialize(), comment));
                }
            }
            else if(clz.equals(String.class))
            {
                if(write)
                {
                    config.get(propInfo.category(), field.getName(), (String)field.get(this)).set((String)field.get(this));
                    config.get(propInfo.category(), field.getName(), (String)field.get(this)).comment = comment;
                }
                else
                {
                    if(field.isAnnotationPresent(StringValues.class))
                    {
                        StringValues minMax = field.getAnnotation(StringValues.class);
                        field.set(this, config.getString(field.getName(), propInfo.category(), (String)field.get(this), comment, minMax.values()));
                    }
                    else
                    {
                        field.set(this, config.getString(field.getName(), propInfo.category(), (String)field.get(this), comment));
                    }
                }
            }
            else if(clz.equals(String[].class))
            {
                if(write)
                {
                    config.get(propInfo.category(), field.getName(), (String[])field.get(this)).set((String[])field.get(this));
                    config.get(propInfo.category(), field.getName(), (String[])field.get(this)).comment = comment;
                }
                else
                {
                    if(field.isAnnotationPresent(StringValues.class))
                    {
                        StringValues minMax = field.getAnnotation(StringValues.class);
                        field.set(this, config.getStringList(field.getName(), propInfo.category(), (String[])field.get(this), comment, minMax.values()));
                    }
                    else
                    {
                        field.set(this, config.getStringList(field.getName(), propInfo.category(), (String[])field.get(this), comment));
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
            PropInfo propInfo1 = new PropInfo(!propInfo.nameOverride().isEmpty() ? propInfo.nameOverride() : StatCollector.translateToLocal(getModId().toLowerCase() + ".config.prop." + field.getName() + ".name"), comment, field);
            if(!setup && !fields.contains(propInfo1))
            {
                fields.add(propInfo1);
            }

            if(!propInfo.changeable() && !unchangable.contains(field))
            {
                unchangable.add(field);
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
                info.name = StatCollector.translateToLocal(String.format("ichunutil.config.cat.%s.name", cat));
                comment = StatCollector.translateToLocal(String.format("ichunutil.config.cat.%s.comment", cat));
            }
            else
            {
                info.name = StatCollector.translateToLocal(getModId().toLowerCase() + ".config.cat." + cat + ".name");
                comment = StatCollector.translateToLocal(getModId().toLowerCase() + ".config.cat." + cat + ".comment");
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
        iChunUtil.channel.sendToPlayer(new PacketSession(this), player);
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

    @Override
    public int compareTo(Object arg0)
    {
        if(arg0 instanceof ConfigBase)
        {
            ConfigBase cfg = (ConfigBase)arg0;
            return getModName().compareTo(cfg.getModName());
        }
        return 0;
    }

    public class CategoryInfo
            implements Comparable
    {
        public final String category;
        public String name;
        public String comment;

        public CategoryInfo(String cat)
        {
            category = name = cat;
        }

        @Override
        public int compareTo(Object arg0)
        {
            if(arg0 instanceof CategoryInfo)
            {
                CategoryInfo cfg = (CategoryInfo)arg0;
                return category.compareTo(cfg.category);
            }
            return 0;
        }
    }

    public class PropInfo implements Comparable
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
        public int compareTo(Object o)
        {
            if(o instanceof PropInfo)
            {
                PropInfo cfg = (PropInfo)o;
                return name.compareTo(cfg.name);
            }
            return 0;

        }
    }

    @Override
    public boolean equals(Object o)
    {
        return o.getClass() == this.getClass() && ((ConfigBase)o).getModName().equals(getModName());
    }
}
