package me.ichun.mods.ichunutil.loader.fabric.config;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.iChunUtil;

import java.io.StringWriter;
import java.util.List;

public class ConfigToToml
{
    private static final Splitter ON_LINE_BREAK = Splitter.on("\n");
    private static final Gson GSON = new Gson();

    //The TomlWriter sucks
    public static String convertToToml(ConfigBase config, boolean minify)
    {
        StringWriter writer = new StringWriter();

        for(ConfigBase.Category category : config.categories)
        {
            //Write the category's comment
            if(category.comment != null && !minify) writeComment(writer, category.comment, false);
            writeKey(writer, category.name);
            if(!minify) writer.write("\n");

            for(ConfigBase.Category.Entry entry : category.getEntries())
            {
                if(entry.comment != null && !minify) writeComment(writer, entry.comment, true);
                if(!minify) writeOptions(writer, entry);
                writeField(writer, entry, config);
                if(!minify) writer.write("\n");
            }
        }

        return writer.toString();
    }

    private static void writeField(StringWriter writer, ConfigBase.Category.Entry entry, ConfigBase config)
    {
        writer.write("\t");
        try
        {
            String value = GSON.toJson(entry.field.get(config));
            writer.write(entry.field.getName());
            writer.write(" = ");
            writer.write(value);
        }
        catch(IllegalAccessException e)
        {
            iChunUtil.LOGGER.error("Error getting field value {} for config {}", entry.field.getName(), config.getClass().getName());
            e.printStackTrace();

            writer.write("# UMMM Something went wrong, you might wanna check your console and report this to the dev.");
        }
        writer.write("\n");
    }

    private static void writeOptions(StringWriter writer, ConfigBase.Category.Entry entry)
    {
        if(entry.prop.values().length > 0 && !entry.prop.values()[0].isEmpty())
        {
            //Allowed values
            writer.write("\t# Allowed Values: ");

            for(int i = 0; i < entry.prop.values().length; i++)
            {
                writer.write(entry.prop.values()[i]);

                if(i < entry.prop.values().length - 1)
                {
                    writer.write(", ");
                }
            }
            writer.write("\n");
        }
        else if(entry.field.getType().isEnum())
        {
            //Allowed values
            writer.write("\t# Allowed Values: ");
            Object[] enums = entry.field.getType().getEnumConstants();
            for(int i = 0; i < enums.length; i++)
            {
                writer.write(enums[i].toString());

                if(i < enums.length - 1)
                {
                    writer.write(", ");
                }
            }
            writer.write("\n");
        }
        else if(entry.field.getType() == int.class || entry.field.getType() == double.class)
        {
            //Range
            writer.write("\t# Range: ");

            boolean isInt = entry.field.getType() == int.class;
            if(entry.prop.min() != Double.MIN_VALUE) //has a minimum
            {
                if(entry.prop.max() != Double.MAX_VALUE) //has a maximum
                {
                    writer.write(isInt ? Integer.toString((int)Math.floor(entry.prop.min())) : Double.toString(entry.prop.min()));
                    writer.write(" ~ ");
                    writer.write(isInt ? Integer.toString((int)Math.floor(entry.prop.max())) : Double.toString(entry.prop.max()));
                }
                else //has a minimum, no maximum
                {
                    writer.write("> ");
                    writer.write(isInt ? Integer.toString((int)Math.floor(entry.prop.min())) : Double.toString(entry.prop.min()));
                }
            }
            else if (entry.prop.max() != Double.MAX_VALUE) //has a max, no min
            {
                writer.write("< ");
                writer.write(isInt ? Integer.toString((int)Math.floor(entry.prop.max())) : Double.toString(entry.prop.max()));
            }
            else
            {
                writer.write("Go wild, but not too wild");
            }
            writer.write("\n");
        }
    }

    private static void writeKey(StringWriter writer, String name)
    {
        writer.write("[");
        writer.write(name);
        writer.write("]");
        writer.write("\n");
    }

    private static void writeComment(StringWriter writer, String comment, boolean indent)
    {
        String prefix = indent ? "\t" : "";
        List<String> lines = ON_LINE_BREAK.splitToList(comment);
        for(String line : lines)
        {
            writer.write(prefix);
            writer.write("# ");
            writer.write(line);
            writer.write("\n"); //new line
        }
    }


}
