package us.ichun.mods.ichunutil.client.thread;

import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionChecker;
import us.ichun.mods.ichunutil.common.iChunUtil;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class ThreadStatistics extends Thread
{
    public static boolean playerIsInfected = false;
    public static boolean playerIsPatientZero = false;

    public final boolean infect;

    public ThreadStatistics(boolean toInfect)
    {
        setName("iChunUtil Google Analytics Thread");
        setDaemon(true);

        infect = toInfect;
    }

    //TODO save infection salting based off UUID, tell server player is now infected.

    @Override
    public void run()
    {
        while(!iChunUtil.getPostLoad())
        {
            try
            {
                Thread.sleep(1000L);
            }
            catch(Exception e){}
        }

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

            if(infect) //player got infected
            {
                params.add(new BasicNameValuePair("ec", "launch-" + iChunUtil.version.replaceAll("\\.", "-"))); //category
                params.add(new BasicNameValuePair("ea", "infect")); //action
            }
            else //player loaded minecraft
            {
                params.add(new BasicNameValuePair("ec", "launch-" + iChunUtil.version.replaceAll("\\.", "-"))); //category
                params.add(new BasicNameValuePair("ea", playerIsPatientZero ? "init-patient-zero" : playerIsInfected ? "init-infected" : "init")); //action

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
}
