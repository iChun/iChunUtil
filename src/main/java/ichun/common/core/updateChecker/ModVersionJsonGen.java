package ichun.common.core.updateChecker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class ModVersionJsonGen
{
    public static void generate()
    {
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();

        Map<String, String> versions;

        versions = new HashMap<String, String>();
        map.put("Grinder", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.0");

        versions = new HashMap<String, String>();
        map.put("BackTools", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.1");

        versions = new HashMap<String, String>();
        map.put("Hats", versions);
        versions.put("1.7.10", "4.0.1");
        versions.put("1.7", "3.0.1");

        versions = new HashMap<String, String>();
        map.put("HatStand", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.0");

        versions = new HashMap<String, String>();
        map.put("iChunUtil", versions);
        versions.put("1.7.10", "4.1.2");
        versions.put("1.7", "3.3.0");

        versions = new HashMap<String, String>();
        map.put("ItFellFromTheSky", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.0");

        versions = new HashMap<String, String>();
        map.put("MobAmputation", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.1");

        versions = new HashMap<String, String>();
        map.put("MobDismemberment", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.1");

        versions = new HashMap<String, String>();
        map.put("Morph", versions);
        versions.put("1.7.10", "0.9.1");
        versions.put("1.7", "0.8.1");

        versions = new HashMap<String, String>();
        map.put("PiP", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.4");

        versions = new HashMap<String, String>();
        map.put("Photoreal", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.0");

        versions = new HashMap<String, String>();
        map.put("Shatter", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.0");

        versions = new HashMap<String, String>();
        map.put("Streak", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.0");

        versions = new HashMap<String, String>();
        map.put("Sync", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.1");

        versions = new HashMap<String, String>();
        map.put("Tabula", versions);
        versions.put("1.7.10", "4.0.2");

        versions = new HashMap<String, String>();
        map.put("Torched", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.1");

        versions = new HashMap<String, String>();
        map.put("TrailMix", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.2");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(map);

        System.out.println(jsonOutput);
    }
}
