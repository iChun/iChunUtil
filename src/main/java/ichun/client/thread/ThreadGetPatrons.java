package ichun.client.thread;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ichun.common.iChunUtil;
import morph.api.Ability;
import morph.common.Morph;
import morph.common.ability.AbilityHandler;
import net.minecraft.entity.EntityLivingBase;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

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
                System.out.println("patrons");
                System.out.println(json.length);
                iChunUtil.patronList = json;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
