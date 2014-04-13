package ichun.common.core.config;


import cpw.mods.fml.common.FMLCommonHandler;
import ichun.client.keybind.KeyBind;
import ichun.common.iChunUtil;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class Config 
	implements Comparable
{

	public final Configuration config;
	public final String modId;
	public final String modName;
	public final Logger logger; //allowed to be null
	public final IConfigUser parent;
	
	public HashMap<String, Property> props = new HashMap<String, Property>();
	public HashMap<Property, String> propName = new HashMap<Property, String>();
	public HashMap<String, String> propNameToProp = new HashMap<String, String>();
	public HashMap<Property, int[]> minmax = new HashMap<Property, int[]>();
	public HashMap<Property, int[]> nestedMinmax = new HashMap<Property, int[]>();
	
	public HashMap<String, ArrayList<Property>> categories = new HashMap<String, ArrayList<Property>>();
	public ArrayList<String> categoriesList = new ArrayList<String>();
	
	public ArrayList<Property> intArrayList = new ArrayList<Property>();
	public ArrayList<Property> nestedIntArrayList = new ArrayList<Property>();
	
	public ArrayList<Property> propNeedsRestart = new ArrayList<Property>();
	public ArrayList<String> unfound = new ArrayList<String>();
	
	public HashMap<String, Object> sessionState = new HashMap<String, Object>();
	
	private boolean setup;
	
	public Config(Configuration cfg, String ModId, String ModName, Logger lg, IConfigUser configParent)
	{
		config = cfg;
		modId = ModId;
		modName = ModName;
		logger = lg;
		setup = false;
		parent = configParent;
	}
	
	public void resetSession()
	{
		sessionState.clear();
		for(Entry<String, Property> e : props.entrySet())
		{
			sessionState.put(e.getKey(), e.getValue().getType() == Type.INTEGER ? e.getValue().getInt() : e.getValue().getString());
		}
	}
	
	public void updateSession(String s, Object obj)
	{
		sessionState.put(s, obj);
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
		if(prop != null)
		{
			if(prop.getType() == Type.INTEGER)
			{
				return prop.getInt();
			}
			else if(logger != null && !unfound.contains(s))
			{
				unfound.add(s);
				logger.log(Level.WARN, "Tried to reference non-int property as int: " + s);
			}
		}
		return -2;
	}
	
	public String getString(String s)
	{
		Property prop = get(s);
		if(prop != null)
		{
			if(prop.getType() == Type.STRING)
			{
				return prop.getString();
			}
			else if(logger != null && !unfound.contains(s))
			{
				unfound.add(s);
				logger.log(Level.WARN, "Tried to reference non-string property as string: " + s);
			}
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
	
	public ArrayList<Integer> getIntArray(String s)
	{
		Property prop = get(s);
		if(prop != null)
		{
			if(prop.getType() == Type.STRING)
			{
				return parseIntArray(getString(s));
			}
			else if(logger != null && !unfound.contains(s))
			{
				unfound.add(s);
				logger.log(Level.WARN, "Tried to reference non-string property as string: " + s);
			}
		}
		return new ArrayList<Integer>();
	}
	
	public LinkedHashMap<Integer, ArrayList<Integer>> getNestedIntArray(String s)
	{
		Property prop = get(s);
		if(prop != null)
		{
			if(prop.getType() == Type.STRING)
			{
				return parseNestedIntArray(getString(s));
			}
			else if(logger != null && !unfound.contains(s))
			{
				unfound.add(s);
				logger.log(Level.WARN, "Tried to reference non-string property as string: " + s);
			}
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
		Object obj = sessionState.get(s);
		if(obj instanceof Integer)
		{
			return (Integer)obj;
		}
		return -2;
	}
	
	public String getSessionString(String s)
	{
		Object obj = sessionState.get(s);
		if(obj instanceof String)
		{
			return (String)obj;
		}
		return "";
	}
	
	public HashMap<String, ArrayList<String>> getSessionStringArray(String s)
	{
		Object obj = sessionState.get(s);
		if(obj instanceof HashMap)
		{
			return (HashMap)obj;
		}
		return new HashMap<String, ArrayList<String>>();
	}
	
	public ArrayList<Integer> getSessionIntArray(String s)
	{
		Object obj = sessionState.get(s);
		if(obj instanceof ArrayList<?>)
		{
			return (ArrayList<Integer>)obj;
		}
		return new ArrayList<Integer>();
	}
	
	public HashMap<Integer, ArrayList<Integer>> getSessionNestedIntArray(String s)
	{
		Object obj = sessionState.get(s);
		if(obj instanceof HashMap<?, ?>)
		{
			return (HashMap<Integer, ArrayList<Integer>>)obj;
		}
		return new HashMap<Integer, ArrayList<Integer>>();
	}


    //TODO createKeybindHook with SHIFT/ALT/CTRL support andd repeated presses (pulses) support
    //TODO createBooleanIntProperty
	public void createIntProperty(String cat, String catName, String propName1, String fullPropName, String comment, boolean changable, int i, int min, int max)
	{
        Property prop;
        if(props.containsKey(propName1))
        {
        	prop = props.get(propName1);
        	prop.set(i);
        }
        else
        {
        	prop = config.get(cat, propName1, i);
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
            prop.comment = comment + "\n" + (min != Integer.MIN_VALUE ? ("\nMin: " + min) : "") + (max != Integer.MAX_VALUE ? ("\nMax: " + max) : "");
        }
        
        props.put(propName1, prop);
        propName.put(prop, fullPropName);
        propNameToProp.put(fullPropName, propName1);
        minmax.put(prop, new int[] { min, max });
        
        if(!changable && !propNeedsRestart.contains(prop))
        {
        	propNeedsRestart.add(prop);
        }
        
        ArrayList<Property> categoryList;
        if(catName != null && categories.containsKey(catName) || catName == null && categories.containsKey("uncat"))
        {
        	categoryList = categories.get(catName);
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
        
        if(!setup)
        {
        	config.save();
        }
	}
	
	public void createStringProperty(String cat, String catName, String propName1, String fullPropName, String comment, boolean changable, String value)
	{
        Property prop;
        if(props.containsKey(propName1))
        {
        	prop = props.get(propName1);
        	prop.set(value);
        }
        else
        {
        	prop = config.get(cat, propName1, value);
        }

        if (!comment.equalsIgnoreCase(""))
        {
            prop.comment = comment;
        }
        
        props.put(propName1, prop);
        propName.put(prop, fullPropName);
        propNameToProp.put(fullPropName, propName1);
        
        if(!changable && !propNeedsRestart.contains(prop))
        {
        	propNeedsRestart.add(prop);
        }
        
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

        if(!setup)
        {
        	config.save();
        }
	}

    public void createKeybindProperty(String propName1, String fullPropName, String comment, int keyValue, boolean holdShift, boolean holdCtrl, boolean holdAlt, boolean canPulse, boolean pulseTime) //Custom keybinds. You don't get to define a category, and it's meant to be changed ingame.
    {
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
            config.addCustomCategoryComment("keybinds", "If you're reading this, I would strongly recommend changing the keybinds ingame.\niChunUtil uses custom keybinds. Go to the options page and hit O to show other options.");
            prop = config.get("keybinds", propName1, sb.toString());

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
                        Integer.parseInt(strings[0]);
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
            prop.comment = comment;
        }

        props.put(propName1, prop);
        propName.put(prop, fullPropName);
        propNameToProp.put(fullPropName, propName1);

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
            bind = new KeyBind(Integer.parseInt(strings[0]), keyString.contains("SHIFT"), keyString.contains("CTRL"), keyString.contains("ALT"));
        }
        catch(Exception e)
        {
            iChunUtil.console("Error parsing key for mod " + modName + ": " + fullPropName, true);
            bind = new KeyBind(keyValue, holdShift, holdCtrl, holdAlt);
        }

        iChunUtil.proxy.registerKeyBind(bind);

        if(!setup)
        {
            config.save();
        }
    }

    public void createIntArrayProperty(String cat, String catName, String propName1, String fullPropName, String comment, boolean changable, boolean nestedIntArray, String value, int[] minMax, int[] nestedMinMax) // formatting.. "int: nested int: nested int, int, int"
	{
        Property prop;
        if(props.containsKey(propName1))
        {
        	prop = props.get(propName1);
        	prop.set(value);
        }
        else
        {
        	prop = config.get(cat, propName1, value);
        }

        if (!comment.equalsIgnoreCase(""))
        {
            prop.comment = comment;
        }
        
        props.put(propName1, prop);
        propName.put(prop, fullPropName);
        propNameToProp.put(fullPropName, propName1);
        
        if(nestedIntArray)
        {
	        if(!nestedIntArrayList.contains(prop))
	        {
	        	nestedIntArrayList.add(prop);
	        }
        }
        else
        {
	        if(!intArrayList.contains(prop))
	        {
	        	intArrayList.add(prop);
	        }
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

        if(!setup)
        {
        	config.save();
        }
	}
	
	public void setup()
	{
		categoriesList.clear();
		setup = true;
		config.save();
		
		for(Entry<String, ArrayList<Property>> e : categories.entrySet())
		{
			if(!e.getKey().equals("uncat"))
			{
				categoriesList.add(e.getKey());
			}
		}
		Collections.sort(categoriesList);
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
}