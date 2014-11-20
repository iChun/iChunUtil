package us.ichun.module.tabula.common.project.components;

import org.apache.commons.lang3.RandomStringUtils;
import us.ichun.module.tabula.common.project.ProjectInfo;

import java.util.ArrayList;

public class CubeGroup
{
    public CubeGroup(String name)
    {
        this.name = name;
        identifier = RandomStringUtils.randomAscii(ProjectInfo.IDENTIFIER_LENGTH);
    }

    public ArrayList<CubeInfo> cubes = new ArrayList<CubeInfo>();
    public ArrayList<CubeGroup> cubeGroups = new ArrayList<CubeGroup>();

    public String name;

    public boolean txMirror = false;

    public String identifier;
}
