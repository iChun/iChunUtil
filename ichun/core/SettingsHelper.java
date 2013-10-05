package ichun.core;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

@Deprecated //Use ConfigHandler instead
public class SettingsHelper
{
	public static int addCommentAndReturnInt(Configuration config, String cat, String s, String comment, int i, int min, int max, boolean override)
	{
		return addCommentAndReturnInt(config, cat, s, comment + "\nMin: " + min + "\nMax: " + max, Math.max(Math.min(max, i), min), override);
	}
	
	public static int addCommentAndReturnInt(Configuration config, String cat, String s, String comment, int i)
	{
		return addCommentAndReturnInt(config, cat, s, comment, i, false);
	}
	
    public static int addCommentAndReturnInt(Configuration config, String cat, String s, String comment, int i, boolean override)
    {
        Property prop = config.get(cat, s, i);

        if(override)
        {
        	prop.set(i);
        }
        
        if (!comment.equalsIgnoreCase(""))
        {
            prop.comment = comment;
        }

        return prop.getInt();
    }

    public static int addCommentAndReturnInt(Configuration config, String cat, String s, String comment, String value)
    {
    	return addCommentAndReturnInt(config, cat, s, comment, value, false);
    }
    
    public static int addCommentAndReturnInt(Configuration config, String cat, String s, String comment, String value, boolean override)
    {
        Property prop = config.get(cat, s, value);

        if(override)
        {
        	prop.set(value);
        }
        
        if(!comment.equalsIgnoreCase(""))
        {
            prop.comment = comment;
        }

        int val = 0xffffff;

        try
        {
            val = Integer.decode(prop.getString());
        }
        catch (NumberFormatException e)
        {
            iChunUtil.console("Cannot decode colour index: " + s, true);
            e.printStackTrace();
        }

        return val;
    }

    public static int addCommentAndReturnItemID(Configuration config, String cat, String s, String comment, int i)
    {
    	return addCommentAndReturnItemID(config, cat, s, comment, i, false);
    }
    
    public static int addCommentAndReturnItemID(Configuration config, String cat, String s, String comment, int i, boolean override)
    {
    	Property prop;
    	
    	if(override)
    	{
    		prop = config.get(cat, s, i);
    	}
    	else
    	{
    		prop = config.getItem(cat, s, i);
    	}

        if (!comment.equalsIgnoreCase(""))
        {
            prop.comment = comment;
        }

        return prop.getInt();
    }

    public static int addCommentAndReturnBlockID(Configuration config, String cat, String s, String comment, int i)
    {
    	return addCommentAndReturnBlockID(config, cat, s, comment, i, false);
    }
    
    public static int addCommentAndReturnBlockID(Configuration config, String cat, String s, String comment, int i, boolean override)
    {
        Property prop;
        
    	if(override)
    	{
    		prop = config.get(cat, s, i);
    	}
    	else
    	{
    		prop = config.getBlock(cat, s, i);
    	}

        if (!comment.equalsIgnoreCase(""))
        {
            prop.comment = comment;
        }

        return prop.getInt();
    }

    public static String addCommentAndReturnString(Configuration config, String cat, String s, String comment, String value)
    {
    	return addCommentAndReturnString(config, cat, s, comment, value, false);
    }
    
    public static String addCommentAndReturnString(Configuration config, String cat, String s, String comment, String value, boolean override)
    {
        Property prop = config.get(cat, s, value);

        if(override)
        {
        	prop.set(value);
        }

        if (!comment.equalsIgnoreCase(""))
        {
            prop.comment = comment;
        }

        return prop.getString();
    }
}
