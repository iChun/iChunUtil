package me.ichun.mods.ichunutil.common.module.tabula.project;

import java.util.ArrayList;

public class Project //Model
{
    public static final int IDENTIFIER_LENGTH = 20;
    public static final int PROJ_VERSION = 5;

    //defaults
    public String modelName = "";
    public String author = "";
    public int projVersion = PROJ_VERSION; //TODO detect if version is old (< 5). Not supported anymore.
    public ArrayList<String> notes = new ArrayList<>();

    public int texWidth = 64;
    public int texHeight = 32;

    public ArrayList<Part> parts = new ArrayList<>();

    public static class Part //ModelRenderer
    {
        public String name = "Part";
        public ArrayList<String> notes = new ArrayList<>();

        public int texWidth = 64;
        public int texHeight = 32;

        public int texOffX;
        public int texOffY;

        //position
        public float rotPX;
        public float rotPY;
        public float rotPZ;

        //angles
        public float rotAX;
        public float rotAY;
        public float rotAZ;

        public boolean mirror;

        public ArrayList<Box> boxes = new ArrayList<>();
        public ArrayList<Part> children = new ArrayList<>();

        public Part()
        {
            boxes.add(new Box());
        }

        public static class Box //ModelBox
        {
            //the old offsets.
            public float posX;
            public float posY;
            public float posZ;

            public float dimX = 1;
            public float dimY = 1;
            public float dimZ = 1;

            public float expandX;
            public float expandY;
            public float expandZ;
        }
    }
}
