package us.ichun.module.tabula.common.project.components;

import org.apache.commons.lang3.RandomStringUtils;
import us.ichun.module.tabula.common.project.ProjectInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class Animation
{
    public String name;
    public String identifier;

    public boolean loops;

    public HashMap<String, ArrayList<AnimationComponent>> sets = new HashMap<String, ArrayList<AnimationComponent>>(); // cube identifier to animation component

    public transient int playTime;

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
}
