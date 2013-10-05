package ichun.core.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;

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
	
	public HashMap<String, ArrayList<Property>> categories = new HashMap<String, ArrayList<Property>>();
	public ArrayList<String> categoriesList = new ArrayList<String>();
	
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
				logger.log(Level.WARNING, "Tried to reference unknown property: " + s);
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
				logger.log(Level.WARNING, "Tried to reference non-int property as int: " + s);
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
				logger.log(Level.WARNING, "Tried to reference non-string property as string: " + s);
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

	
	public void createOrUpdateItemIDProperty(String cat, String catName, String propName1, String fullPropName, String comment, int i)
	{
		int min = 256;
		int max = 32000;
		boolean changable = false;
		
        Property prop;
        if(props.containsKey(propName1))
        {
        	prop = props.get(propName1);
        	prop.set(i);
        }
        else
        {
        	if(!setup)
        	{
        		prop = config.getItem(cat, propName1, i);
        	}
        	else
        	{
        		prop = config.get(cat, propName1, i);
        	}
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
	
	public void createOrUpdateIntProperty(String cat, String catName, String propName1, String fullPropName, String comment, boolean changable, int i, int min, int max)
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
	
	public void createOrUpdateStringProperty(String cat, String catName, String propName1, String fullPropName, String comment, boolean changable, String value)
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
