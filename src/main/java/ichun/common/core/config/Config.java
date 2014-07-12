package ichun.common.core.config;

import com.google.common.base.Splitter;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import ichun.client.keybind.KeyBind;
import ichun.common.iChunUtil;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;

public class Config
        implements Comparable
{

    public static int printKey = -1;
    public static String curMod = "sync";

    public static Configuration configKeybind;

    public final Configuration config;
    public final String modId;
    public final String modName;
    public final Logger logger; //allowed to be null
    public final IConfigUser parent;

    public String currentCat;
    public String currentCatName;

    public HashMap<String, Property> props = new HashMap<String, Property>();
    public HashMap<Property, String> propName = new HashMap<Property, String>();
    public HashMap<String, String> propNameToProp = new HashMap<String, String>();
    public HashMap<Property, int[]> minmax = new HashMap<Property, int[]>();
    public HashMap<Property, int[]> nestedMinmax = new HashMap<Property, int[]>();
    public ArrayList<Property> sessionProps = new ArrayList<Property>();

    public HashMap<String, ArrayList<Property>> categories = new HashMap<String, ArrayList<Property>>();
    public ArrayList<String> categoriesList = new ArrayList<String>();

    public HashMap<Property, EnumPropType> propType = new HashMap<Property, EnumPropType>();

    public HashMap<String, KeyBind> keyBindMap = new HashMap<String, KeyBind>();

    public ArrayList<Property> propNeedsRestart = new ArrayList<Property>();
    public ArrayList<String> unfound = new ArrayList<String>();

    public EnumMap<Side, HashMap<String, Object>> sessionState = new EnumMap<Side, HashMap<String, Object>>(Side.class) {{ put(Side.CLIENT, new HashMap<String, Object>()); put(Side.SERVER, new HashMap<String, Object>()); }};

    private boolean setup;

    //TODO read if config changed then save?
    //If you're reading this and you're confused, I don't blame you.. I did not put much javadoc in my source.
    //The best way for you to understand how to use this kind of config is by looking at how my other mods implement it.
    //I would recommend you to check out Morph on my GitHub. It uses the int, intbool, string and keybind configs with proper implementation, as well as the "Session" feature provided in configs.
    //If there is any issue feel free to leave one on the GitHub issues page. I will attend to when I can.
    //-iChun
    public Config(Configuration cfg, String ModId, String ModName, Logger lg, IConfigUser configParent)
    {
        config = cfg;
        modId = ModId;
        modName = ModName;
        logger = lg;
        setup = false;
        parent = configParent;

        currentCat = "general";
        currentCatName = StatCollector.translateToLocal("ichun.config.cat.general.name");
    }

    public void resetSession()
    {
        sessionState.get(FMLCommonHandler.instance().getEffectiveSide()).clear();
        for(Property prop : sessionProps)
        {
            sessionState.get(FMLCommonHandler.instance().getEffectiveSide()).put(prop.getName(), prop.getType() == Type.INTEGER ? prop.getInt() : prop.getString());
        }
    }

    public void updateSession(String s, Object obj)
    {
        sessionState.get(FMLCommonHandler.instance().getEffectiveSide()).put(s, obj);
    }

    public Property get(String s)
    {
        if(!props.containsKey(s) && !unfound.contains(s))
        {
            unfound.add(s);
            if(logger != null)
            {
                logger.log(Level.WARN, "Tried to reference unknown property: " + s);
            }
        }
        return props.get(s);
    }

    public int getInt(String s)
    {
        Property prop = get(s);
        if(prop != null && prop.getType() == Type.INTEGER)
        {
            return prop.getInt();
        }
        else if(logger != null && !unfound.contains(s))
        {
            unfound.add(s);
            logger.log(Level.WARN, "Tried to reference non-int property as int: " + s);
        }
        return -2;
    }

    public String getString(String s)
    {
        Property prop = get(s);
        if(prop != null && prop.getType() == Type.STRING)
        {
            return prop.getString();
        }
        else if(logger != null && !unfound.contains(s))
        {
            unfound.add(s);
            logger.log(Level.WARN, "Tried to reference non-string property as string: " + s);
        }
        return "";
    }

    public HashMap<String, ArrayList<String>> getStringArray(String s)
    {
        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

        String s1 = getString(s);

        String[] split = s1.split(", *");

        if(split.length > 0 && !split[0].equalsIgnoreCase(""))
        {
            for(String splits : split)
            {
                String[] split1 = splits.split(": *");
                if(split1.length > 0 && !split1[0].equalsIgnoreCase(""))
                {
                    if(split1.length == 1)
                    {
                        map.put(split1[0], new ArrayList<String>());
                    }
                    else
                    {
                        ArrayList<String> strings = new ArrayList<String>();
                        for(int i = 1; i < split1.length; i++)
                        {
                            strings.add(split1[i]);
                        }
                        map.put(split1[0], strings);
                    }
                }
            }
        }
        return map;
    }

    public KeyBind getKeyBind(String s)
    {
        KeyBind bind = keyBindMap.get(s);
        if(bind != null)
        {
            return bind;
        }
        bind = new KeyBind(0, false, false, false, false);
        keyBindMap.put(s, bind);

        if(logger != null && !unfound.contains(s))
        {
            unfound.add(s);
            logger.log(Level.WARN, "Tried to reference non-existent keybind: " + s);
        }

        return bind;
    }

    public ArrayList<Integer> getIntArray(String s)
    {
        Property prop = get(s);
        if(prop != null && prop.getType() == Type.STRING)
        {
            return parseIntArray(getString(s));
        }
        else if(logger != null && !unfound.contains(s))
        {
            unfound.add(s);
            logger.log(Level.WARN, "Tried to reference non-string property as string: " + s);
        }
        return new ArrayList<Integer>();
    }

    public LinkedHashMap<Integer, ArrayList<Integer>> getNestedIntArray(String s)
    {
        Property prop = get(s);
        if(prop != null && prop.getType() == Type.STRING)
        {
            return parseNestedIntArray(getString(s));
        }
        else if(logger != null && !unfound.contains(s))
        {
            unfound.add(s);
            logger.log(Level.WARN, "Tried to reference non-string property as string: " + s);
        }
        return new LinkedHashMap<Integer, ArrayList<Integer>>();
    }

    public ArrayList<Integer> parseIntArray(String s)
    {
        ArrayList<Integer> list = new ArrayList<Integer>();

        String[] split = s.split(", *");

        if(split.length > 0 && !split[0].equalsIgnoreCase(""))
        {
            for(String splits : split)
            {
                try
                {
                    Integer i = Integer.parseInt(splits);
                    list.add(i);
                }
                catch(NumberFormatException e)
                {
                    if(logger != null)
                    {
                        logger.log(Level.WARN, "Could not parse following as int: " + splits);
                    }
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public LinkedHashMap<Integer, ArrayList<Integer>> parseNestedIntArray(String s)
    {
        LinkedHashMap<Integer, ArrayList<Integer>> map = new LinkedHashMap<Integer, ArrayList<Integer>>();

        String[] split = s.split(", *");

        if(split.length > 0 && !split[0].equalsIgnoreCase(""))
        {
            for(String splits : split)
            {
                String[] split1 = splits.split(": *");
                if(split1.length > 0 && !split1[0].equalsIgnoreCase(""))
                {
                    try
                    {
                        if(split1.length == 1)
                        {
                            map.put(Integer.parseInt(split1[0]), new ArrayList<Integer>());
                        }
                        else
                        {
                            ArrayList<Integer> ints = new ArrayList<Integer>();
                            for(int i = 1; i < split1.length; i++)
                            {
                                try
                                {
                                    ints.add(Integer.parseInt(split1[i]));
                                }
                                catch(NumberFormatException e)
                                {
                                    if(logger != null)
                                    {
                                        logger.log(Level.WARN, "Could not parse following as (nested) int: " + split1[i]);
                                    }
                                    e.printStackTrace();
                                }
                            }
                            map.put(Integer.parseInt(split1[0]), ints);
                        }
                    }
                    catch(NumberFormatException e)
                    {
                        if(logger != null)
                        {
                            logger.log(Level.WARN, "Could not parse following as int: " + split1[0]);
                        }
                        e.printStackTrace();
                    };
                }
            }
        }
        return map;
    }

    public int getSessionInt(String s)
    {
        Object obj = sessionState.get(FMLCommonHandler.instance().getEffectiveSide()).get(s);
        if(obj instanceof Integer)
        {
            return (Integer)obj;
        }
        return -2;
    }

    public String getSessionString(String s)
    {
        Object obj = sessionState.get(FMLCommonHandler.instance().getEffectiveSide()).get(s);
        if(obj instanceof String)
        {
            return (String)obj;
        }
        return "";
    }

    public HashMap<String, ArrayList<String>> getSessionStringArray(String s)
    {
        Object obj = sessionState.get(FMLCommonHandler.instance().getEffectiveSide()).get(s);
        if(obj instanceof HashMap)
        {
            return (HashMap)obj;
        }
        return new HashMap<String, ArrayList<String>>();
    }

    public ArrayList<Integer> getSessionIntArray(String s)
    {
        Object obj = sessionState.get(FMLCommonHandler.instance().getEffectiveSide()).get(s);
        if(obj instanceof ArrayList<?>)
        {
            return (ArrayList<Integer>)obj;
        }
        return new ArrayList<Integer>();
    }

    public HashMap<Integer, ArrayList<Integer>> getSessionNestedIntArray(String s)
    {
        Object obj = sessionState.get(FMLCommonHandler.instance().getEffectiveSide()).get(s);
        if(obj instanceof HashMap<?, ?>)
        {
            return (HashMap<Integer, ArrayList<Integer>>)obj;
        }
        return new HashMap<Integer, ArrayList<Integer>>();
    }

    private void addToCategory(String catName, Property prop)
    {
        ArrayList<Property> categoryList;
        if(catName != null && categories.containsKey(catName) || catName == null && categories.containsKey("uncat"))
        {
            categoryList = categories.get(catName != null ? catName : "uncat");
        }
        else
        {
            categoryList = new ArrayList<Property>();
            categories.put(catName != null ? catName : "uncat", categoryList);
        }
        if(!categoryList.contains(prop))
        {
            categoryList.add(prop);
        }
        if(setup && !categoriesList.contains(catName))
        {
            categoriesList.add(catName);
            Collections.sort(categoriesList);
        }
    }

    public int createIntProperty(String propName1, String fullPropName, String comment, boolean changable, boolean isSessionProp, int i, int min, int max) //returns the config property
    {
        if(modId.equalsIgnoreCase(curMod) && printKey == 2)
            System.out.println(modId.toLowerCase() + ".config.prop." + propName1 + ".name=" + fullPropName);
        if(modId.equalsIgnoreCase(curMod) && printKey == 3)
            System.out.println(modId.toLowerCase() + ".config.prop." + propName1 + ".comment=" + comment);
        if(modId.equalsIgnoreCase(curMod) && printKey == 4)
            System.out.println("config.createIntProperty(\"" + propName1 + "\", \"" + modId.toLowerCase() + ".config.prop." + propName1 + ".name\", " + "\"" + modId.toLowerCase() + ".config.prop." + propName1 + ".comment\", " + Boolean.toString(changable) + ", " + Boolean.toString(isSessionProp) + ", " + Integer.toString(i) + ", " + (min == Integer.MIN_VALUE ? "Integer.MIN_VALUE" : Integer.toString(min)) + ", " + (max == Integer.MAX_VALUE ? "Integer.MAX_VALUE" : Integer.toString(max)) + ");");

        Property prop;
        if(props.containsKey(propName1))
        {
            prop = props.get(propName1);
            prop.set(i);
        }
        else
        {
            prop = config.get(currentCat, propName1, i);
            if(prop.getInt() > max)
            {
                prop.set(max);
            }
            if(prop.getInt() < min)
            {
                prop.set(min);
            }
        }

        if (!comment.equalsIgnoreCase(""))
        {
            List cms = Splitter.on("\\n").splitToList(StatCollector.translateToLocal(comment));
            String cm = "";
            for(int ll = 0; ll < cms.size(); ll++)
            {
                cm = cm + cms.get(ll);
                if(ll != cms.size() - 1)
                {
                    cm = cm + "\n";
                }
            }
            prop.comment = cm + "\n" + (min != Integer.MIN_VALUE ? ("\nMin: " + min) : "") + (max != Integer.MAX_VALUE ? ("\nMax: " + max) : "");
        }

        props.put(propName1, prop);
        propName.put(prop, fullPropName);
        propNameToProp.put(fullPropName, propName1);
        minmax.put(prop, new int[] { min, max });

        propType.put(prop, EnumPropType.INT);

        if(!changable && !propNeedsRestart.contains(prop))
        {
            propNeedsRestart.add(prop);
        }
        if(isSessionProp && !sessionProps.contains(prop))
        {
            sessionProps.add(prop);
        }

        addToCategory(currentCatName, prop);

        save();

        return prop.getInt();
    }

    public int createIntBoolProperty(String propName1, String fullPropName, String comment, boolean changable, boolean isSessionProp, boolean flag) //returns the config property
    {
        if(modId.equalsIgnoreCase(curMod) && printKey == 2)
            System.out.println(modId.toLowerCase() + ".config.prop." + propName1 + ".name=" + fullPropName);
        if(modId.equalsIgnoreCase(curMod) && printKey == 3)
            System.out.println(modId.toLowerCase() + ".config.prop." + propName1 + ".comment=" + comment);
        if(modId.equalsIgnoreCase(curMod) && printKey == 4)
            System.out.println("config.createIntBoolProperty(\"" + propName1 + "\", \"" + modId.toLowerCase() + ".config.prop." + propName1 + ".name\", " + "\"" + modId.toLowerCase() + ".config.prop." + propName1 + ".comment\", " + Boolean.toString(changable) + ", " + Boolean.toString(isSessionProp) + ", " + Boolean.toString(flag) + ");");

        Property prop;
        if(props.containsKey(propName1))
        {
            prop = props.get(propName1);
            prop.set(flag ? 1 : 0);
        }
        else
        {
            prop = config.get(currentCat, propName1, flag ? 1 : 0);
            if(prop.getInt() > 1)
            {
                prop.set(1);
            }
            if(prop.getInt() < 0)
            {
                prop.set(0);
            }
        }

        if (!comment.equalsIgnoreCase(""))
        {
            List cms = Splitter.on("\\n").splitToList(StatCollector.translateToLocal(comment));
            String cm = "";
            for(int ll = 0; ll < cms.size(); ll++)
            {
                cm = cm + cms.get(ll);
                if(ll != cms.size() - 1)
                {
                    cm = cm + "\n";
                }
            }
            prop.comment = cm + "\n\nMin: 0\nMax: 1";
        }

        props.put(propName1, prop);
        propName.put(prop, fullPropName);
        propNameToProp.put(fullPropName, propName1);
        minmax.put(prop, new int[] { 0, 1 });

        propType.put(prop, EnumPropType.INT_BOOL);

        if(!changable && !propNeedsRestart.contains(prop))
        {
            propNeedsRestart.add(prop);
        }
        if(isSessionProp && !sessionProps.contains(prop))
        {
            sessionProps.add(prop);
        }

        addToCategory(currentCatName, prop);

        save();

        return prop.getInt();
    }

    public int createColourProperty(String propName1, String fullPropName, String comment, boolean changable, boolean isSessionProp, int colour) //returns the config val
    {
        if(modId.equalsIgnoreCase(curMod) && printKey == 2)
            System.out.println(modId.toLowerCase() + ".config.prop." + propName1 + ".name=" + fullPropName);
        if(modId.equalsIgnoreCase(curMod) && printKey == 3)
            System.out.println(modId.toLowerCase() + ".config.prop." + propName1 + ".comment=" + comment);
        if(modId.equalsIgnoreCase(curMod) && printKey == 4)
            System.out.println("config.createColourProperty(\"" + propName1 + "\", \"" + modId.toLowerCase() + ".config.prop." + propName1 + ".name\", " + "\"" + modId.toLowerCase() + ".config.prop." + propName1 + ".comment\", " + Boolean.toString(changable) + ", " + Boolean.toString(isSessionProp) + ", " + Integer.toHexString(colour) + ");");

        Property prop;
        if(props.containsKey(propName1))
        {
            prop = props.get(propName1);
            prop.set("#" + Integer.toHexString(colour));
        }
        else
        {
            prop = config.get(currentCat, propName1, "#" + Integer.toHexString(colour));

            try
            {
                Integer.decode(prop.getString().trim());
            }
            catch(NumberFormatException e)
            {
                if(logger != null)
                {
                    logger.log(Level.WARN, "Could not parse following as colour code, setting as default: " + prop.getString());
                }
                e.printStackTrace();

                prop.set("#" + Integer.toHexString(colour));
            }
        }

        if (!comment.equalsIgnoreCase(""))
        {
            List cms = Splitter.on("\\n").splitToList(StatCollector.translateToLocal(comment));
            String cm = "";
            for(int ll = 0; ll < cms.size(); ll++)
            {
                cm = cm + cms.get(ll);
                if(ll != cms.size() - 1)
                {
                    cm = cm + "\n";
                }
            }
            prop.comment = cm;
        }

        props.put(propName1, prop);
        propName.put(prop, fullPropName);
        propNameToProp.put(fullPropName, propName1);

        propType.put(prop, EnumPropType.COLOUR);

        if(!changable && !propNeedsRestart.contains(prop))
        {
            propNeedsRestart.add(prop);
        }
        if(isSessionProp && !sessionProps.contains(prop))
        {
            sessionProps.add(prop);
        }

        addToCategory(currentCatName, prop);

        save();

        return Integer.decode(prop.getString().trim());
    }

    public String createStringProperty(String propName1, String fullPropName, String comment, boolean changable, boolean isSessionProp, String value) //returns the config val
    {
        if(modId.equalsIgnoreCase(curMod) && printKey == 2)
            System.out.println(modId.toLowerCase() + ".config.prop." + propName1 + ".name=" + fullPropName);
        if(modId.equalsIgnoreCase(curMod) && printKey == 3)
            System.out.println(modId.toLowerCase() + ".config.prop." + propName1 + ".comment=" + comment);
        if(modId.equalsIgnoreCase(curMod) && printKey == 4)
            System.out.println("config.createStringProperty(\"" + propName1 + "\", \"" + modId.toLowerCase() + ".config.prop." + propName1 + ".name\", " + "\"" + modId.toLowerCase() + ".config.prop." + propName1 + ".comment\", " + Boolean.toString(changable) + ", " + Boolean.toString(isSessionProp) + ", \"" + value + "\");");

        Property prop;
        if(props.containsKey(propName1))
        {
            prop = props.get(propName1);
            prop.set(value);
        }
        else
        {
            prop = config.get(currentCat, propName1, value);
        }

        if (!comment.equalsIgnoreCase(""))
        {
            List cms = Splitter.on("\\n").splitToList(StatCollector.translateToLocal(comment));
            String cm = "";
            for(int ll = 0; ll < cms.size(); ll++)
            {
                cm = cm + cms.get(ll);
                if(ll != cms.size() - 1)
                {
                    cm = cm + "\n";
                }
            }
            prop.comment = cm;
        }

        props.put(propName1, prop);
        propName.put(prop, fullPropName);
        propNameToProp.put(fullPropName, propName1);

        propType.put(prop, EnumPropType.STRING);

        if(!changable && !propNeedsRestart.contains(prop))
        {
            propNeedsRestart.add(prop);
        }
        if(isSessionProp && !sessionProps.contains(prop))
        {
            sessionProps.add(prop);
        }

        addToCategory(currentCatName, prop);

        save();

        return prop.getString();
    }

    public Property createKeybindProperty(String propName1, String fullPropName, String comment, int keyValue, boolean holdShift, boolean holdCtrl, boolean holdAlt, boolean canPulse, int pulseTime, boolean ignoreHold) //Custom keybinds. You don't get to define a category, and it's meant to be changed ingame.
    {
        if(modId.equalsIgnoreCase(curMod) && printKey == 2)
            System.out.println(modId.toLowerCase() + ".config.prop." + propName1 + ".name=" + fullPropName);
        if(modId.equalsIgnoreCase(curMod) && printKey == 3)
            System.out.println(modId.toLowerCase() + ".config.prop." + propName1 + ".comment=" + comment);
        if(modId.equalsIgnoreCase(curMod) && printKey == 4)
            System.out.println("config.createKeybindProperty(\"" + propName1 + "\", \"" + modId.toLowerCase() + ".config.prop." + propName1 + ".name\", " + "\"" + modId.toLowerCase() + ".config.prop." + propName1 + ".comment\", " + Integer.toString(keyValue) + ", " + Boolean.toString(holdShift) + ", " + Boolean.toString(holdCtrl) + ", " + Boolean.toString(holdAlt) + ", " + Boolean.toString(canPulse) + ", " + Integer.toString(pulseTime) + ", " + Boolean.toString(ignoreHold) + ");");

        Property prop;

        StringBuilder sb = new StringBuilder();

        sb.append(keyValue);

        if(holdShift)
        {
            sb.append(":SHIFT");
        }
        if(holdCtrl)
        {
            sb.append(":CTRL");
        }
        if(holdAlt)
        {
            sb.append(":ALT");
        }

        if(props.containsKey(propName1))
        {
            prop = props.get(propName1);
            prop.set(sb.toString());
        }
        else
        {
            List cms = Splitter.on("\\n").splitToList(StatCollector.translateToLocal("ichun.config.keybind.comment"));
            String cm = "";
            for(int ll = 0; ll < cms.size(); ll++)
            {
                cm = cm + cms.get(ll);
                if(ll != cms.size() - 1)
                {
                    cm = cm + "\n";
                }
            }
            configKeybind.addCustomCategoryComment("keybinds", cm);
            prop = configKeybind.get("keybinds." + modName, propName1, sb.toString());

            if(prop.getString() != sb.toString())
            {
                String[] strings = prop.getString().split(":");
                if(strings.length == 0)
                {
                    iChunUtil.console("Invalid keybind for mod " + modName + ": " + fullPropName, true);
                    prop.set(sb.toString());
                }
                else
                {
                    try
                    {
                        Integer.parseInt(strings[0].trim());
                    }
                    catch(NumberFormatException e)
                    {
                        iChunUtil.console("Invalid key for mod " + modName + ": " + fullPropName, true);
                        prop.set(sb.toString());
                    }
                }
            }
        }

        if (!comment.equalsIgnoreCase(""))
        {
            List cms = Splitter.on("\\n").splitToList(StatCollector.translateToLocal(comment));
            String cm = "";
            for(int ll = 0; ll < cms.size(); ll++)
            {
                cm = cm + cms.get(ll);
                if(ll != cms.size() - 1)
                {
                    cm = cm + "\n";
                }
            }
            prop.comment = cm;
        }

        props.put(propName1, prop);
        propName.put(prop, fullPropName);
        propNameToProp.put(fullPropName, propName1);

        propType.put(prop, EnumPropType.KEYBIND);

        ArrayList<Property> categoryList;
        if(categories.containsKey("Key Binds"))
        {
            categoryList = categories.get("Key Binds");
        }
        else
        {
            categoryList = new ArrayList<Property>();
            categories.put("Key Binds", categoryList);
        }
        if(!categoryList.contains(prop))
        {
            categoryList.add(prop);
        }

        String keyString = prop.getString();
        String[] strings = keyString.split(":");
        KeyBind bind;
        try
        {
            bind = new KeyBind(Integer.parseInt(strings[0].trim()), keyString.contains("SHIFT"), keyString.contains("CTRL"), keyString.contains("ALT"), ignoreHold);
        }
        catch(Exception e)
        {
            iChunUtil.console("Error parsing key for mod " + modName + ": " + fullPropName, true);
            bind = new KeyBind(keyValue, holdShift, holdCtrl, holdAlt, ignoreHold);
        }

        bind.setPulse(canPulse, pulseTime);

        keyBindMap.put(propName1, iChunUtil.proxy.registerKeyBind(bind, null));

        save();

        return prop;
    }

    public void createIntArrayProperty(String propName1, String fullPropName, String comment, boolean changable, boolean isSessionProp, boolean nestedIntArray, String value, int[] minMax, int[] nestedMinMax) // formatting.. "int: nested int: nested int, int, int"
    {
        Property prop;
        if(props.containsKey(propName1))
        {
            prop = props.get(propName1);
            prop.set(value);
        }
        else
        {
            prop = config.get(currentCat, propName1, value);
        }

        if (!comment.equalsIgnoreCase(""))
        {
            List cms = Splitter.on("\\n").splitToList(StatCollector.translateToLocal(comment));
            String cm = "";
            for(int ll = 0; ll < cms.size(); ll++)
            {
                cm = cm + cms.get(ll);
                if(ll != cms.size() - 1)
                {
                    cm = cm + "\n";
                }
            }
            prop.comment = cm;
        }

        props.put(propName1, prop);
        propName.put(prop, fullPropName);
        propNameToProp.put(fullPropName, propName1);

        if(nestedIntArray)
        {
            propType.put(prop, EnumPropType.NESTED_INT_ARRAY);
        }
        else
        {
            propType.put(prop, EnumPropType.INT_ARRAY);
        }

        if(minMax == null || minMax.length != 2)
        {
            minMax = new int[] { (minMax != null && minMax.length > 0 ? minMax[0] : Integer.MIN_VALUE), (minMax != null && minMax.length > 1 ? minMax[1] : Integer.MAX_VALUE) };
        }
        minmax.put(prop, minMax);

        if(nestedIntArray)
        {
            if(nestedMinMax == null || nestedMinMax.length != 2)
            {
                nestedMinMax = new int[] { (nestedMinMax != null && nestedMinMax.length > 0 ? nestedMinMax[0] : Integer.MIN_VALUE), (nestedMinMax != null && nestedMinMax.length > 1 ? nestedMinMax[1] : Integer.MAX_VALUE) };
            }
            nestedMinmax.put(prop, nestedMinMax);
        }


        if(!changable && !propNeedsRestart.contains(prop))
        {
            propNeedsRestart.add(prop);
        }
        if(isSessionProp && !sessionProps.contains(prop))
        {
            sessionProps.add(prop);
        }

        addToCategory(currentCatName, prop);

        save();

        //does not return value;
    }

    public void setCurrentCategory(String cat, String catName, String comment)
    {
        currentCat = cat;
        currentCatName = StatCollector.translateToLocal(catName);
        List cms = Splitter.on("\\n").splitToList(StatCollector.translateToLocal(comment));
        String cm = "";
        for(int ll = 0; ll < cms.size(); ll++)
        {
            cm = cm + cms.get(ll);
            if(ll != cms.size() - 1)
            {
                cm = cm + "\n";
            }
        }
        config.addCustomCategoryComment(currentCat, cm);

        if(modId.equalsIgnoreCase(curMod) && printKey == 0)
            System.out.println(modId.toLowerCase() + ".config.cat." + cat + ".name=" + catName);
        if(modId.equalsIgnoreCase(curMod) && printKey == 1)
            System.out.println(modId.toLowerCase() + ".config.cat." + cat + ".comment=" + comment);
        if(modId.equalsIgnoreCase(curMod) && printKey == 4)
            System.out.println("config.setCurrentCategory(\"" + cat + "\", \"" + modId.toLowerCase() + ".config.cat." + cat + ".name\", \"" + modId.toLowerCase() + ".config.cat." + cat + ".comment\");");
    }

    public void setup()
    {
        categoriesList.clear();
        setup = true;
        save();

        for(Entry<String, ArrayList<Property>> e : categories.entrySet())
        {
            if(!e.getKey().equals("uncat"))
            {
                categoriesList.add(e.getKey());
            }
        }
        Collections.sort(categoriesList);
    }

    public void save()
    {
        if(setup && config.hasChanged())
        {
            config.save();
        }
    }

    @Override
    public int compareTo(Object arg0)
    {
        if(arg0 instanceof Config)
        {
            Config cfg = (Config)arg0;
            return modName.compareTo(cfg.modName);
        }
        return 0;
    }

    public EnumPropType getPropType(Property prop)
    {
        EnumPropType type = propType.get(prop);
        if(propType == null)
        {
            iChunUtil.console("Property has no type: " + prop.getName(), true);
            return EnumPropType.UNDEFINED;
        }
        return type;
    }

    public static enum EnumPropType
    {
        UNDEFINED,
        STRING,
        INT,
        INT_BOOL,
        INT_ARRAY,
        NESTED_INT_ARRAY,
        KEYBIND,
        COLOUR//screw the american english system.
    }
}