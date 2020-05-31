package me.ichun.mods.ichunutil.common.module.tabula.legacy.project;

import com.google.common.collect.Ordering;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.TreeMap;

@Deprecated
public class ProjectInfo
{
    public static final int IDENTIFIER_LENGTH = 20;

    public String modelName;
    public String authorName;
    public int projVersion;

    public ArrayList<String> metadata;

    public int textureWidth = 64;
    public int textureHeight = 32;

    public double[] scale = new double[] { 1.0D, 1.0D, 1.0D };

    public ArrayList<CubeGroup> cubeGroups;
    public ArrayList<CubeInfo> cubes;

    public ArrayList<Animation> anims;

    public int cubeCount;

    public transient byte[] textureBytes;

    public ProjectInfo()
    {
        modelName = "";
        authorName = "";
        metadata = new ArrayList<>();
        cubeGroups = new ArrayList<>();
        cubes = new ArrayList<>();
        anims = new ArrayList<>();
    }

    public ProjectInfo(String name, String author)
    {
        this();
        modelName = name;
        authorName = author;

        cubeGroups = new ArrayList<>();
        cubes = new ArrayList<>();
    }

    public ProjectInfo repair()
    {
        while(projVersion < 4)
        {
            if(projVersion == 1)
            {
                scale = new double[] { 1D, 1D, 1D };

                for(CubeInfo info : getAllCubes())
                {
                    info.opacity = 100D;
                }
            }
            else if(projVersion == 2)
            {
                metadata = new ArrayList<>();
            }
            else if(projVersion == 3)
            {
                for(CubeGroup group : cubeGroups)
                {
                    group.metadata = new ArrayList<>();
                }
                ArrayList<CubeInfo> cubes = getAllCubes();
                for(CubeInfo info : cubes)
                {
                    info.metadata = new ArrayList<>();
                }
            }
            projVersion++;
        }
        return this;
    }

    public ArrayList<CubeInfo> getAllCubes()
    {
        ArrayList<CubeInfo> cubes = new ArrayList<>();
        addAllCubes(cubes, this.cubes);
        addAllCubesFromGroups(cubes, this.cubeGroups);
        return cubes;
    }

    public void addAllCubes(ArrayList<CubeInfo> list, ArrayList<CubeInfo> cubes)
    {
        list.addAll(cubes);
        for(CubeInfo cube : cubes)
        {
            addAllCubes(list, cube.getChildren());
        }
    }

    public void addAllCubesFromGroups(ArrayList<CubeInfo> list, ArrayList<CubeGroup> groups)
    {
        for(CubeGroup group : groups)
        {
            addAllCubes(list, group.cubes);
            addAllCubesFromGroups(list, group.cubeGroups);
        }
    }

    @Deprecated
    public static class CubeInfo
    {
        public CubeInfo(String name)
        {
            this.name = name;
            dimensions = new int[] { 1, 1, 1 };
            scale = new double[] { 1D, 1D, 1D };
            opacity = 100D;
            identifier = RandomStringUtils.randomAscii(ProjectInfo.IDENTIFIER_LENGTH);
        }

        public String name;
        public int[] dimensions = new int[3];

        public double[] position = new double[3];
        public double[] offset = new double[3];
        public double[] rotation = new double[3];

        public double[] scale = new double[3];

        public int[] txOffset = new int[2];
        public boolean txMirror = false;

        public double mcScale = 0.0D;

        public double opacity = 100D;

        public boolean hidden = false;

        public ArrayList<String> metadata = new ArrayList<>();

        public ArrayList<CubeInfo> children = new ArrayList<>();
        public String parentIdentifier;

        public String identifier;

        public ArrayList<CubeInfo> getChildren()
        {
            return children;
        }
    }

    @Deprecated
    public static class CubeGroup
    {
        public CubeGroup(String name)
        {
            this.name = name;
            identifier = RandomStringUtils.randomAscii(ProjectInfo.IDENTIFIER_LENGTH);
        }

        public ArrayList<CubeInfo> cubes = new ArrayList<>();
        public ArrayList<CubeGroup> cubeGroups = new ArrayList<>();

        public String name;

        public boolean txMirror = false;

        public boolean hidden = false;

        public ArrayList<String> metadata = new ArrayList<>();

        public String identifier;
    }

    @Deprecated
    public static class Animation
    {
        public String name;
        public String identifier;

        public boolean loops;

        public TreeMap<String, ArrayList<AnimationComponent>> sets = new TreeMap<>(Ordering.natural()); // cube identifier to animation component

        public transient int playTime;
        public transient boolean playing;

        public Animation(String name)
        {
            this.name = name;
            identifier = RandomStringUtils.randomAscii(ProjectInfo.IDENTIFIER_LENGTH);
        }

        @Deprecated
        public static class AnimationComponent
        {
            public double[] posChange = new double[3];
            public double[] rotChange = new double[3];
            public double[] scaleChange = new double[3];
            public double opacityChange = 0.0D;

            public double[] posOffset = new double[3];
            public double[] rotOffset = new double[3];
            public double[] scaleOffset = new double[3];
            public double opacityOffset = 0.0D;

            public ArrayList<double[]> progressionCoords;

            public String name;

            public int length;
            public int startKey;

            public boolean hidden;

            public String identifier;

            public AnimationComponent(String name, int length, int startKey)
            {
                this.name = name;
                this.length = length;
                this.startKey = startKey;
                identifier = RandomStringUtils.randomAscii(ProjectInfo.IDENTIFIER_LENGTH);
            }
        }
    }
}
