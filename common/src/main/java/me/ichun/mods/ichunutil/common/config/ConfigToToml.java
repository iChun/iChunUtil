package me.ichun.mods.ichunutil.common.config;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.moandjiezana.toml.Toml;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.util.StringUtil;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ConfigToToml
{
    private static final Splitter ON_LINE_BREAK = Splitter.on("\n");

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
                if(entry.prop.skip()) continue;
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
            String value = StringUtil.GSON.toJson(entry.field.get(config));
            writer.write(entry.field.getName());
            writer.write(" = ");
            writer.write(value);
        }
        catch(IllegalAccessException e)
        {
            iChunUtil.LOGGER.error("Error getting field value {} for config {}", entry.field.getName(), config.getClass().getName(), e);

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
            if(entry.prop.min() != -Double.MAX_VALUE) //has a minimum
            {
                if(entry.prop.intBool()) //is an intbool
                {
                    writer.write("0 ~ 1");
                }
                else if(entry.prop.max() != Double.MAX_VALUE) //has a maximum
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

    public static boolean assignValuesFromToml(ConfigBase config, Toml toml, boolean isReload) throws IllegalAccessException, IllegalStateException
    {
        HashMap<ConfigBase.Category.Entry, Object> entryToValue = new HashMap<>();
        HashMap<String, Object> tomlToValue = new HashMap<>();

        for(ConfigBase.Category category : config.categories)
        {
            for(ConfigBase.Category.Entry entry : category.getEntries())
            {
                if(entry.prop.skip()) continue;
                entryToValue.put(entry, entry.field.get(config));
            }
        }

        //First layer is the name of each category, each
        toml.toMap().forEach((k, v) -> {
            if(v instanceof Map)
            {
                tomlToValue.putAll((Map<? extends String, ?>)v);
            }
        });

        //We have all the entries and all the toml values, compare them to see if they are valid
        entryToValue.entrySet().removeIf(e -> {
            ConfigBase.Category.Entry entry = e.getKey();
            String fieldName = entry.field.getName();
            if(tomlToValue.containsKey(fieldName))
            {
                try
                {
                    Object oriValue = entry.field.get(config);
                    Object tomlValue = tomlToValue.get(fieldName);

                    //Check if the value is compatible with the field, if it is, assign the new value, mark for change if isReload
                    if(entry.field.getType() == int.class) //doubles are handled separately cause Number is weird
                    {
                        if(tomlValue instanceof Number)
                        {
                            int newValue = ((Number)tomlValue).intValue();
                            if(entry.prop.intBool() && newValue >= 0 && newValue <= 1 || !entry.prop.intBool() && newValue >= entry.prop.min() && newValue <= entry.prop.max())
                            {
                                entry.field.set(config, newValue);

                                if(isReload && !oriValue.equals(newValue))
                                {
                                    config.onPropertyChanged(true, fieldName, entry.field, oriValue, newValue);
                                }
                                tomlToValue.remove(fieldName);
                                return true;
                            }
                        }
                    }
                    else if(entry.field.getType() == double.class) //doubles are handled separately cause Number is weird
                    {
                        if(tomlValue instanceof Number)
                        {
                            double newValue = ((Number)tomlValue).doubleValue();
                            if(newValue >= entry.prop.min() && newValue <= entry.prop.max())
                            {
                                entry.field.set(config, newValue);

                                if(isReload && !oriValue.equals(newValue))
                                {
                                    config.onPropertyChanged(true, fieldName, entry.field, oriValue, newValue);
                                }
                                tomlToValue.remove(fieldName);
                                return true;
                            }
                        }
                    }
                    else if(entry.field.getType() == boolean.class)
                    {
                        if(tomlValue instanceof Boolean)
                        {
                            entry.field.set(config, tomlValue);

                            if(isReload && oriValue != tomlValue)
                            {
                                config.onPropertyChanged(true, fieldName, entry.field, oriValue, tomlValue);
                            }
                            tomlToValue.remove(fieldName);
                            return true;
                        }
                    }
                    else if(entry.field.getType() == String.class)
                    {
                        boolean invalid = true;
                        Object newValue = tomlValue.toString();
                        if(!(entry.prop.values().length == 1 && entry.prop.values()[0].isEmpty())) //has set values
                        {
                            for(String validValues : entry.prop.values())
                            {
                                if(newValue.equals(validValues))
                                {
                                    invalid = false;
                                    break;
                                }
                            }
                        }
                        else if(entry.prop.validator().equals("undefined") || entry.prop.validator().isEmpty()) //has no validator
                        {
                            invalid = false;
                        }
                        else
                        {
                            if(config.validate(config.getValidatorMethod(entry.prop.validator()), newValue))
                            {
                                invalid = false;
                            }
                        }

                        if(!invalid)
                        {
                            entry.field.set(config, newValue);

                            if(isReload && oriValue.equals(newValue))
                            {
                                config.onPropertyChanged(true, fieldName, entry.field, oriValue, newValue);
                            }
                            tomlToValue.remove(fieldName);
                            return true;
                        }
                    }
                    else if(entry.field.getType().isEnum())
                    {
                        Object newValue = tomlValue.toString();
                        Object[] enumConstants = entry.field.getType().getEnumConstants();
                        for(Object enumConstant : enumConstants)
                        {
                            if(enumConstant.toString().equals(newValue))
                            {
                                entry.field.set(config, enumConstant);

                                if(isReload && oriValue.equals(enumConstant))
                                {
                                    config.onPropertyChanged(true, fieldName, entry.field, oriValue, enumConstant);
                                }
                                tomlToValue.remove(fieldName);
                                return true;
                            }
                        }
                    }
                    else if(List.class.isAssignableFrom(entry.field.getType()))
                    {
                        if(tomlValue instanceof List)
                        {
                            Gson gson = StringUtil.GSON;
                            ArrayList<?> newList = new ArrayList<>((List<?>)gson.fromJson(gson.toJson(tomlValue), entry.field.getType()));

                            if(!newList.isEmpty())
                            {
                                ArrayList<Integer> newListInts = new ArrayList<>();
                                boolean isAllInts = true;
                                for(Object o : newList)
                                {
                                    if(o instanceof Double d)
                                    {
                                        if(d.intValue() == d)
                                        {
                                            newListInts.add(d.intValue());
                                            continue;
                                        }
                                    }
                                    isAllInts = false;
                                    break;
                                }
                                if(isAllInts)
                                {
                                    newList = newListInts;
                                }
                            }

                            if(!(entry.prop.validator().equals("undefined") || entry.prop.validator().isEmpty()))
                            {
                                newList.removeIf(o -> !config.validate(config.getValidatorMethod(entry.prop.validator()), o));
                            }

                            entry.field.set(config, newList);

                            if(isReload && oriValue.equals(newList))
                            {
                                config.onPropertyChanged(true, fieldName, entry.field, oriValue, tomlValue);
                            }
                            tomlToValue.remove(fieldName);
                            return true;
                        }
                    }
                }
                catch(IllegalAccessException ex)
                {
                    iChunUtil.LOGGER.error("Error with setting field {} with object {} from config {}", fieldName, tomlToValue.get(fieldName), config.getFileName(), ex);
                }
            }
            return false;
        });

        boolean errorFree = entryToValue.isEmpty() && tomlToValue.isEmpty();

        for(ConfigBase.Category.Entry entry : entryToValue.keySet())
        {
            if(tomlToValue.containsKey(entry.field.getName()))
            {
                iChunUtil.LOGGER.warn("Error getting value for {} from config {}. Config value invalid: {}.", entry.field.getName(), config.getFileName(), tomlToValue.get(entry.field.getName()));
                tomlToValue.remove(entry.field.getName());
            }
            else
            {
                iChunUtil.LOGGER.warn("Error getting value for {} from config {}. Does not exist in config.", entry.field.getName(), config.getFileName());
            }
        }

        for(String s : tomlToValue.keySet())
        {
            iChunUtil.LOGGER.warn("Discarding key {} in config {}. No such field exists in config.", s, config.getFileName());
        }

        return errorFree;
    }
}
