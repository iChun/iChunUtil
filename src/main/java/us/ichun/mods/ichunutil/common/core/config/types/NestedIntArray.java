package us.ichun.mods.ichunutil.common.core.config.types;

import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NestedIntArray
{
    public HashMap<Integer, ArrayList<Integer>> values = new HashMap<Integer, ArrayList<Integer>>();

    public NestedIntArray(HashMap<Integer, ArrayList<Integer>> vals)
    {
        values = vals;
    }

    public String serialize()
    {
        StringBuilder sb = new StringBuilder();
        boolean added = false;
        for(Map.Entry<Integer, ArrayList<Integer>> e : values.entrySet())
        {
            if(added)
            {
                sb.append(", ");
            }

            sb.append(e.getKey());

            for(int i = 0; i < e.getValue().size(); i++)
            {
                sb.append(": ");
                sb.append(e.getValue().get(i));
            }

            added = true;
        }
        return sb.toString();
    }

    public void deserialize(String s, int min, int max, int nestedMin, int nestedMax)
    {
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
                            int val = Integer.parseInt(split1[0]);
                            if(val <= max && val >= min)
                            {
                                values.put(val, new ArrayList<Integer>());
                            }
                        }
                        else
                        {
                            ArrayList<Integer> ints = new ArrayList<Integer>();
                            for(int i = 1; i < split1.length; i++)
                            {
                                try
                                {
                                    int val = Integer.parseInt(split1[i]);
                                    if(val <= nestedMax && val >= nestedMin)
                                    {
                                        ints.add(val);
                                    }
                                }
                                catch(NumberFormatException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            int val = Integer.parseInt(split1[0]);
                            if(val <= max && val >= min)
                            {
                                values.put(val, ints);
                            }
                        }
                    }
                    catch(NumberFormatException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
