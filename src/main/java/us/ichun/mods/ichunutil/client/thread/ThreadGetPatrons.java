package us.ichun.mods.ichunutil.client.thread;

import com.google.gson.Gson;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

@SideOnly(Side.CLIENT)
public class ThreadGetPatrons extends Thread
{
    public String patronList = "https://raw.github.com/iChun/iChunUtil/master/src/main/resources/assets/ichunutil/mod/patrons.json";

    public ThreadGetPatrons()
    {
        this.setName("iChunUtil Patron Getter Thread");
        this.setDaemon(true);
    }

    @Override
    public void run()
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
                    if(s.replaceAll("-", "").equalsIgnoreCase(Minecraft.getMinecraft().getSession().getPlayerID()))
                    {
                        iChunUtil.isPatron = true;
                        iChunUtil.config.reveal("showPatronReward", "patronRewardType");
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
