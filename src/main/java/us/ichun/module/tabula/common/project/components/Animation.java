package us.ichun.module.tabula.common.project.components;

import org.apache.commons.lang3.RandomStringUtils;
import us.ichun.module.tabula.common.project.ProjectInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Animation
{
    public String name;
    public String identifier;

    public boolean loops;

    public HashMap<String, ArrayList<AnimationComponent>> sets = new HashMap<String, ArrayList<AnimationComponent>>(); // cube identifier to animation component

    public transient int playTime;
    public transient boolean playing;

    public Animation(String name)
    {
        this.name = name;
        identifier = RandomStringUtils.randomAscii(ProjectInfo.IDENTIFIER_LENGTH);
    }

    public void createAnimComponent(String cubeIdent, String name, int length, int pos)
    {
        ArrayList<AnimationComponent> set = sets.get(cubeIdent);
        if(set == null)
        {
            set = new ArrayList<AnimationComponent>();
            sets.put(cubeIdent, set);
        }

        set.add(new AnimationComponent(name, length, pos));
    }

    public void update()
    {
        if(playing)
        {
            playTime++;
            int lastTick = 0;
            for(Map.Entry<String, ArrayList<AnimationComponent>> e : sets.entrySet())
            {
                for(AnimationComponent comp : e.getValue())
                {
                    if(comp.startKey + comp.length > lastTick)
                    {
                        lastTick = comp.startKey + comp.length;
                    }
                }
            }

            if(playTime > lastTick)
            {
                if(loops)
                {
                    playTime = 0;
                }
                else
                {
                    stop();
                }
            }
        }
    }

    public void play()
    {
        if(!playing)
        {
            playing = true;
            playTime = 0;
        }
    }

    public void stop()
    {
        playing = false;
    }
}
