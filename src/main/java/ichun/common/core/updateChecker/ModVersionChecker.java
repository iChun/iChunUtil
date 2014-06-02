package ichun.common.core.updateChecker;

import com.google.gson.Gson;
import ichun.common.iChunUtil;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModVersionChecker
{

    private static String iChunJsonURL = "https://raw.githubusercontent.com/iChun/iChunUtil/master/src/main/resources/assets/ichunutil/mod/versions.json";
    private static HashMap<String, ArrayList<ModVersionInfo>> urlsToCheck = new HashMap<String, ArrayList<ModVersionInfo>>();
    private static boolean init = false;

    /**
     * Executed in the init/load stage
     */
    public static void init()
    {
        if(!init)
        {
            new Thread("iChunUtil Mod Version Check")
            {
                @SuppressWarnings("unchecked")
                @Override
                public void run()
                {
                    try
                    {
                        for(Map.Entry<String, ArrayList<ModVersionInfo>> e : urlsToCheck.entrySet())
                        {
                            Gson gson = new Gson();
                            Reader fileIn;
                            try
                            {
                                fileIn = new InputStreamReader(new URL(e.getKey()).openStream());
                            }
                            catch(Exception e1)
                            {
                                fileIn = null;
                                e1.printStackTrace();
                            }
                            if(fileIn != null)
                            {
                                Map<String, Object> json = gson.fromJson(fileIn, Map.class);
                                for(ModVersionInfo info : e.getValue())
                                {
                                    Map<String, String> versionInfo = (Map<String, String>)json.get(info.modName);
                                    if(versionInfo != null && info.processAndReturnHasUpdate(versionInfo))
                                    {
                                        iChunUtil.proxy.notifyNewUpdate(info.modName, info.newModVersion);
                                    }
                                }
                            }
                        }
                    }
                    catch(Exception e)
                    {
                    }
                }
            }.start();
        }
        init = true;
    }

    /**
     * Register your mods if they use the same JSON formatting as I do.
     * @param url
     * @param info
     */
    public static void registerModVersionToCheck(String url, ModVersionInfo info)
    {
        ArrayList<ModVersionInfo> list = getArrayListForURL(url);
        list.add(info);
    }

    /**
     * This is for my mods, please don't use it -.-
     * @param info
     */
    public static void register_iChunMod(ModVersionInfo info)
    {
        ArrayList<ModVersionInfo> list = getArrayListForURL(iChunJsonURL);
        list.add(info);
    }

    private static ArrayList<ModVersionInfo> getArrayListForURL(String url)
    {
        ArrayList<ModVersionInfo> list = urlsToCheck.get(url);
        if(list == null)
        {
            list = new ArrayList<ModVersionInfo>();
            urlsToCheck.put(url, list);
        }
        return list;
    }
}
