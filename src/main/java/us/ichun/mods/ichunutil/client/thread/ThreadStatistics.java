package us.ichun.mods.ichunutil.client.thread;

import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;
import us.ichun.mods.ichunutil.common.core.config.ConfigBase;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import us.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import us.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionChecker;
import us.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import us.ichun.mods.ichunutil.common.iChunUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class ThreadStatistics extends Thread
{
    public int type;
    public Object[] data;

    public ThreadStatistics(int type1, Object... data1)
    {
        setName("iChunUtil Statistics Thread");
        setDaemon(true);

        type = type1;
        data = data1;
    }

    @Override
    public void run()
    {
        try
        {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost("http://www.google-analytics.com/collect");

            // Request parameters and other properties.
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("v", "1"));
            params.add(new BasicNameValuePair("tid", "UA-34318897-3"));
            params.add(new BasicNameValuePair("cid", UUIDTypeAdapter.fromString(Minecraft.getMinecraft().getSession().getPlayerID()).toString()));
            params.add(new BasicNameValuePair("t", "event"));

            //            if(infect) //player got infected
            {
                params.add(new BasicNameValuePair("ec", "launch-" + iChunUtil.version.replaceAll("\\.", "-"))); //category
                params.add(new BasicNameValuePair("ea", "infect")); //action
            }
            //            else //player loaded minecraft
            {
                params.add(new BasicNameValuePair("ec", "launch-" + iChunUtil.version.replaceAll("\\.", "-"))); //category
                //                params.add(new BasicNameValuePair("ea", playerIsPatientZero ? "init-patient-zero" : playerIsInfected ? "init-infected" : "init")); //action

                //TODO add version too
                StringBuilder sb = new StringBuilder();
                ArrayList<String> mods = ModVersionChecker.getListOf_iChunMods();
                for(int i = 0; i < mods.size(); i++)
                {
                    sb.append(mods.get(i).replaceAll(" ", ""));
                    if(i < mods.size() - 1)
                    {
                        sb.append("-");
                    }
                }

                params.add(new BasicNameValuePair("el", sb.toString())); //label
            }

            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            //Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static final ArrayList<String> infectionHash = new ArrayList<String>();
    public static final Random rand = new Random();

    public static Config stats;
    public static class Config extends ConfigBase
    {
        @ConfigProp(category = "stats")
        @IntBool
        public int statsOptOut = 0;

        @ConfigProp(category = "stats")
        public String statsIdentifier = "";

        @ConfigProp(category = "stats", hidden = true)
        public String statsData = "";

        public Config(File file)
        {
            super(file);
        }

        @Override
        public String getModId()
        {
            return "ichunutilstats";
        }

        @Override
        public String getModName()
        {
            return "iChunUtil Stats";
        }
    }

    public static void checkFirstLaunch()
    {
        stats = (Config)ConfigHandler.registerConfig(new Config(new File(ResourceHelper.getConfigFolder(), "iChunUtil_Stats.cfg")));

        if(stats.statsOptOut == 1)
        {
            iChunUtil.console("Opting out of stat collection :(");
        }
        else
        {
            boolean isPatientZero = getImmunityLevel() == 0;
            if(stats.statsData.isEmpty() || getInfectionLevel(stats.statsData) == -1)
            {
                if(isPatientZero)
                {
                    stats.statsData = getInfectionHash(0);
                    stats.reveal("statsData");
                }
                else
                {
                    stats.statsData = "";
                }
            }
            String firstLaunchHash = createFirstLaunchHash();
            if(stats.statsIdentifier.isEmpty() || !stats.statsIdentifier.equals(firstLaunchHash))
            {
                stats.statsIdentifier = firstLaunchHash;
                //TODO collect stats
            }
        }
    }

    public static String createFirstLaunchHash()
    {
        rand.setSeed(Math.abs(Minecraft.getMinecraft().getSession().getPlayerID().hashCode() - "firstRun".hashCode()));
        return RandomStringUtils.random(20, 32, 127, false, false, null, rand);
    }

    public static int getImmunityLevel()
    {
        Minecraft mc = Minecraft.getMinecraft();

        for(String s : EntityHelperBase.volunteers)
        {
            if(s.replaceAll("-", "").equalsIgnoreCase(mc.getSession().getPlayerID()))
            {
                return 0;
            }
        }

        rand.setSeed(Math.abs(mc.getSession().getPlayerID().hashCode()));

        float immunity = rand.nextFloat();

        int level = -1;
        float reduction = EntityHelperBase.RARITY;

        while(immunity > 0F)
        {
            level++;
            immunity -= reduction;
            reduction *= 2F;
        }

        return level;
    }

    public static int getInfectionLevel(String s)
    {
        if(s.isEmpty())
        {
            return -1;
        }

        if(infectionHash.isEmpty())
        {
            generateInfectionHash();
        }

        for(int i = 0; i < infectionHash.size(); i++)
        {
            if(infectionHash.get(i).equals(s))
            {
                return i;
            }
        }

        return -1;
    }

    public static String getInfectionHash(int i)
    {
        if(infectionHash.isEmpty())
        {
            generateInfectionHash();
        }
        return infectionHash.get(i);
    }

    public static void generateInfectionHash()
    {
        infectionHash.clear();

        Minecraft mc = Minecraft.getMinecraft();

        float immunity = 1.0F;

        float reduction = EntityHelperBase.RARITY;

        while(immunity > 0F)
        {
            rand.setSeed(Math.abs(mc.getSession().getPlayerID().hashCode() + (infectionHash.size() * 1000)));
            infectionHash.add(RandomStringUtils.random(20, 32, 127, false, false, null, rand));
            immunity -= reduction;
            reduction *= 2F;
        }
    }
}
