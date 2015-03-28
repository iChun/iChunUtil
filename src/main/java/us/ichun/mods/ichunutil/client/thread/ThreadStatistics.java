package us.ichun.mods.ichunutil.client.thread;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;
import us.ichun.mods.ichunutil.common.core.config.ConfigBase;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import us.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import us.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionChecker;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionInfo;
import us.ichun.mods.ichunutil.common.core.util.ObfHelper;
import us.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import us.ichun.mods.ichunutil.common.iChunUtil;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
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
            HttpPost httppost = new HttpPost("http://ichun.us/infect/stats.php");

            // Request parameters and other properties.
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("event", Integer.toString(type)));
            params.add(new BasicNameValuePair("UUID", Minecraft.getMinecraft().getSession().getPlayerID().replaceAll("-", "")));
            if(type != 4) //Get infection level
            {
                params.add(new BasicNameValuePair("time", Long.toString(System.currentTimeMillis())));

                switch(type)
                {
                    case 1: //Init Event
                    {
                        params.add(new BasicNameValuePair("version", iChunUtil.version.replaceAll(" ", "").replaceAll("\\.", "_")));

                        StringBuilder sb = new StringBuilder();
                        if(!ObfHelper.obfuscation)
                        {
                            sb.append("devEnv-");
                        }
                        ArrayList<ModVersionInfo> mods = ModVersionChecker.getListOf_iChunMods();
                        for(int i = 0; i < mods.size(); i++)
                        {
                            sb.append(mods.get(i).modName.replaceAll(" ", "").replaceAll("\\.", "_") + "_" + mods.get(i).modVersion.replaceAll("\\.", "_"));
                            if(i < mods.size() - 1)
                            {
                                sb.append("-");
                            }
                        }

                        params.add(new BasicNameValuePair("mods", sb.toString()));
                        params.add(new BasicNameValuePair("level", Integer.toString(getImmunityLevel())));
                        break;
                    }
                    case 2: //Infect Event... Intentionally supposed to fall into the mutate event
                    {
                        params.add(new BasicNameValuePair("infector", (String)data[1]));
                    }
                    case 3: //Mutate Event
                    {
                        params.add(new BasicNameValuePair("level", Integer.toString((Integer)data[0])));
                        break;
                    }
                }
            }

            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

//                        System.out.println(URLEncodedUtils.format(params, HTTP.DEF_CONTENT_CHARSET.name()));
            //Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);

            if(type == 4)
            {
                HttpEntity entity = response.getEntity();

                if(entity != null)
                {
                    InputStream is = entity.getContent();
                    try
                    {
                        int lv = Integer.parseInt(IOUtils.toString(is, "UTF-8").trim());

//                        System.out.println("LEVEL_" + Integer.toString(lv));

                        if(lv >= 0)
                        {
                            ThreadStatistics.stats.reveal("statsData");
                            ThreadStatistics.stats.statsData = ThreadStatistics.getInfectionHash(lv);
                            ThreadStatistics.stats.save();
                        }
                        else
                        {
                            (new ThreadStatistics(1)).start();
                        }
                    }
                    finally
                    {
                        is.close();
                    }
                }
            }
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
        public int statsOptOut = Minecraft.getMinecraft().gameSettings.snooperEnabled ? 0 : 1;

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

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if(!(calendar.get(Calendar.YEAR) > 2015 || calendar.get(Calendar.YEAR) == 2015 && calendar.get(Calendar.DAY_OF_YEAR) >= 91))
        {
            return;
        }

        if(stats.statsOptOut == 1)
        {
            iChunUtil.console("Opting out of stat collection :(");
        }
        else
        {
            boolean isPatientZero = getImmunityLevel() == 0;
            if(stats.statsData.trim().isEmpty() || getInfectionLevel(stats.statsData) == -1)
            {
                if(isPatientZero)
                {
                    stats.reveal("statsData");
                    stats.statsData = getInfectionHash(0);
                }
                else
                {
                    stats.statsData = "";
                }
                stats.save();
            }
            String firstLaunchHash = createFirstLaunchHash();
            if(stats.statsIdentifier.trim().isEmpty() || !stats.statsIdentifier.equals(firstLaunchHash))
            {
                stats.statsIdentifier = firstLaunchHash;
                stats.save();
                (new ThreadStatistics(4)).start();
            }
        }
    }

    public static String createFirstLaunchHash()
    {
        rand.setSeed(Math.abs(Minecraft.getMinecraft().getSession().getPlayerID().hashCode() - "FirstRun".hashCode()));
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

//        if(Minecraft.getMinecraft().getSession().getUsername().equalsIgnoreCase("Corosus"))
//        {
//            return 1;
//        }

        rand.setSeed(Math.abs(mc.getSession().getPlayerID().hashCode()));

        float immunity = rand.nextFloat();

        int level = -1;
        int i = 0;

        while(immunity > 0F)
        {
            level++;
            immunity -= EntityHelperBase.RARITY[i];
            i++;
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
        if(i >= infectionHash.size())
        {
            return "CHEATER";
        }
        return infectionHash.get(i);
    }

    public static void generateInfectionHash()
    {
        infectionHash.clear();

        Minecraft mc = Minecraft.getMinecraft();

        float immunity = 1.0F;
        int i = 0;

        while(immunity > 0F)
        {
            rand.setSeed(Math.abs(mc.getSession().getPlayerID().hashCode() + (infectionHash.size() * 1000)));
            infectionHash.add(RandomStringUtils.random(20, 32, 127, false, false, null, rand));
            immunity -= EntityHelperBase.RARITY[i];
            i++;
        }
    }
}
