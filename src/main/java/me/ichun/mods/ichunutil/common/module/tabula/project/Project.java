package me.ichun.mods.ichunutil.common.module.tabula.project;

import java.util.ArrayList;

public class Project extends Identifiable //Model
{
    public static final int IDENTIFIER_LENGTH = 20;
    public static final int PROJ_VERSION = 5;

    //defaults
    public String modelName = "";
    public String author = "";
    public int projVersion = PROJ_VERSION; //TODO detect if version is old (< 5). Support? Should we support Techne?
    public ArrayList<String> notes = new ArrayList<>();

    public int texWidth = 64;
    public int texHeight = 32;

    public ArrayList<Part> parts = new ArrayList<>();

    @Override
    public Identifiable getById(String id)
    {
        for(Part part : parts)
        {
            Identifiable ident = part.getById(id);
            if(ident != null)
            {
                return ident;
            }
        }
        return identifier.equals(id) ? this : null;
    }

    public static class Part extends Identifiable //ModelRenderer
    {
        public String name = "Part";
        public ArrayList<String> notes = new ArrayList<>();

        public int texWidth = 64;
        public int texHeight = 32;
        public boolean matchProject = true;

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

        public Part(String parentIdent)
        {
            this.parentIdent = parentIdent;
            this.boxes.add(new Box(identifier));
        }

        @Override
        public Identifiable getById(String id)
        {
            for(Box part : boxes)
            {
                Identifiable ident = part.getById(id);
                if(ident != null)
                {
                    return ident;
                }
            }

            for(Part part : children)
            {
                Identifiable ident = part.getById(id);
                if(ident != null)
                {
                    return ident;
                }
            }
            return identifier.equals(id) ? this : null;
        }


        public static class Box extends Identifiable //ModelBox
        {
            public String name = "Box";

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

            public Box(String identifier)
            {
                this.parentIdent = parentIdent;
            }

            @Override
            public Identifiable getById(String id)
            {
                return identifier.equals(id) ? this : null;
            }
        }
    }

    public Part addPart()
    {
        Part part = new Part(this.identifier);
        parts.add(part);
        return part;
    }
}
