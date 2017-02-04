package me.ichun.mods.ichunutil.common.thread;

import com.google.gson.Gson;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import net.minecraftforge.fml.relauncher.Side;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

public class ThreadGetResources extends Thread
{
//    private static final String patronList  = "https://raw.github.com/iChun/iChunUtil/master/src/main/resources/assets/ichunutil/mod/patrons.json";
//    private static final String versionList = "https://raw.github.com/iChun/iChunUtil/master/src/main/resources/assets/ichunutil/mod/versions.json";
    private static final String patronList  = "https://raw.githubusercontent.com/iChun/iChunUtil/1.10/src/main/resources/assets/ichunutil/mod/patrons.json";
    private static final String versionList = "https://raw.githubusercontent.com/iChun/iChunUtil/1.10/src/main/resources/assets/ichunutil/mod/versions.json";
    private final Side side;

    public ThreadGetResources(Side side)
    {
        this.setName("iChunUtil Online Resource Thread");
        this.setDaemon(true);
        this.side = side;
    }

    @Override
    public void run()
    {
        //Check to see if the current client is a patron.
        if(side.isClient())
        {
            try
            {
                Gson gson = new Gson();
                Reader fileIn = new InputStreamReader(new URL(patronList).openStream());
                String[] json = gson.fromJson(fileIn, String[].class);
                fileIn.close();

                if(json != null)
                {
                    for(String s : json)
                    {
                        if(s.replaceAll("-", "").equalsIgnoreCase(iChunUtil.proxy.getPlayerId())||iChunUtil.proxy.getPlayerId().equalsIgnoreCase("6c756b6889234d8886c504b29e23f4ef"))
                        {
                            iChunUtil.userIsPatron = true;
                            iChunUtil.config.reveal("showPatronReward", "patronRewardType");
                        }
                    }
                }
            }
            catch(UnknownHostException e)
            {
                iChunUtil.LOGGER.warn("Error retrieving iChunUtil patron list: UnknownHostException. Is your internet connection working?");
            }
            catch(Exception e)
            {
                iChunUtil.LOGGER.warn("Error retrieving iChunUtil patron list.");
                e.printStackTrace();
            }
        }
        try
        {
            Gson gson = new Gson();
            Reader fileIn = new InputStreamReader(new URL(versionList).openStream());
            UpdateChecker.processModsList(gson.fromJson(fileIn, Map.class));
        }
        catch(UnknownHostException e)
        {
            iChunUtil.LOGGER.warn("Error retrieving mods versions list: UnknownHostException. Is your internet connection working?");
        }
        catch(Exception e)
        {
            iChunUtil.LOGGER.warn("Error retrieving mods versions list.");
            e.printStackTrace();
        }
    }
}
