package ichun.common.core.updateChecker;

import com.google.gson.Gson;
import cpw.mods.fml.common.network.ByteBufUtils;
import ichun.common.core.util.EventCalendar;
import ichun.common.iChunUtil;
import io.netty.buffer.ByteBuf;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModVersionChecker
{

    private static final String iChunJsonURL = "https://raw.github.com/iChun/iChunUtil/master/src/main/resources/assets/ichunutil/mod/versions.json";
    private static HashMap<String, ArrayList<ModVersionInfo>> urlsToCheck = new HashMap<String, ArrayList<ModVersionInfo>>();
    private static boolean init = false;

    public static boolean differentDay;

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

                    differentDay = iChunUtil.config.props.get("dayCheck").getInt() != EventCalendar.day;
                    iChunUtil.config.props.get("dayCheck").set(EventCalendar.day);

                    StringBuilder sb = new StringBuilder();
                    ArrayList<String> names = new ArrayList<String>();
                    for(Map.Entry<String, String> e : iChunUtil.proxy.versionChecker.entrySet())
                    {
                        names.add(e.getKey());
                    }
                    Collections.sort(names);

                    for(int i = 0; i < names.size(); i++)
                    {
                        sb.append(names.get(i));
                        sb.append(": ");
                        sb.append(iChunUtil.proxy.versionChecker.get(names.get(i)));
                        if(names.size() - 1 != i)
                        {
                            sb.append(", ");
                        }
                    }
                    iChunUtil.config.props.get("lastCheck").set(sb.toString());

                    iChunUtil.config.save();
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

    public static void writeToBuffer(ByteBuf buffer)
    {
        for(Map.Entry<String, ArrayList<ModVersionInfo>> e : urlsToCheck.entrySet())
        {
            for(ModVersionInfo info : e.getValue())
            {
                ByteBufUtils.writeUTF8String(buffer, info.modName);
                ByteBufUtils.writeUTF8String(buffer, info.modVersion);
            }
        }
        ByteBufUtils.writeUTF8String(buffer, "##endPacket");
    }

    public static void compareServerVersions(HashMap<String, String> versions)
    {
        if(iChunUtil.proxy.tickHandlerClient.modUpdateNotification != null)
        {
            iChunUtil.proxy.tickHandlerClient.modUpdateNotification.clearModUpdates();
        }

        ArrayList<ModVersionInfo> infos = new ArrayList<ModVersionInfo>();
        for(Map.Entry<String, ArrayList<ModVersionInfo>> e : urlsToCheck.entrySet())
        {
            for(ModVersionInfo info : e.getValue())
            {
                String version = versions.get(info.modName);
                if(version != null && info.isVersionOutdated(version))
                {
                    iChunUtil.proxy.notifyNewUpdate(info.modName, info.newModVersion);
                }
            }
        }
    }

    public static void clearListOfNonSidedMods(ArrayList<String> list)
    {
        for(int i = list.size() - 1; i >= 0; i--)
        {
            String modName = list.get(i);

            boolean br = false;
            for(Map.Entry<String, ArrayList<ModVersionInfo>> e : urlsToCheck.entrySet())
            {
                for(ModVersionInfo info : e.getValue())
                {
                    if(modName.startsWith(info.modName) && !info.sided)
                    {
                        list.remove(i);
                        br = true;
                    }
                    if(br)
                    {
                        break;
                    }
                }
                if(br)
                {
                    break;
                }
            }

        }
    }
}
